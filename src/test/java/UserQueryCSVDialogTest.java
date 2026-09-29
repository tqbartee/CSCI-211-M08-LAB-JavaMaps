import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UserQueryCSVDialogTest {

    @Test
    public void testUserQueryCSVDialogClassExistsAndHasMainMethod() {
        // 1. Verify that UserQueryCSVDialog class exists
        Class<?> clazz = null;
        try {
            clazz = Class.forName("UserQueryCSVDialog");
        } catch (ClassNotFoundException e) {
            fail("UserQueryCSVDialog class does not exist.");
        }
        assertNotNull(clazz, "UserQueryCSVDialog class should be present.");

        // 2. Verify main method exists and is public static void main(String[])
        try {
            Method mainMethod = clazz.getMethod("main", String[].class);
            assertNotNull(mainMethod, "main method should exist.");
            assertTrue(Modifier.isPublic(mainMethod.getModifiers()), "main method must be public.");
            assertTrue(Modifier.isStatic(mainMethod.getModifiers()), "main method must be static.");
            assertEquals(void.class, mainMethod.getReturnType(), "main method must return void.");
        } catch (NoSuchMethodException e) {
            fail("UserQueryCSVDialog must have a public static void main(String[] args) method.");
        }
    }

    @Test
    public void testUserQueryCSVDialogFieldsAndComponents() throws Exception {
        Class<?> clazz = Class.forName("UserQueryCSVDialog");

        // Verify presence of GenericDataReaderIntoMap integration and UI component fields
        boolean hasDataReaderField = false;
        boolean hasSwingComponentField = false;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(GenericDataReaderIntoMap.class) || 
                field.getType().getName().contains("GenericDataReaderIntoMap")) {
                hasDataReaderField = true;
            }
            if (javax.swing.JComponent.class.isAssignableFrom(field.getType()) || 
                javax.swing.JFrame.class.isAssignableFrom(field.getType())) {
                hasSwingComponentField = true;
            }
        }

        assertTrue(hasDataReaderField, "UserQueryCSVDialog should contain a field for GenericDataReaderIntoMap.");
        assertTrue(hasSwingComponentField, "UserQueryCSVDialog should contain Swing UI component fields.");
    }

    @Test
    public void testUserQueryCSVDialogCoreFunctionality() throws Exception {
        Class<?> clazz = Class.forName("UserQueryCSVDialog");
        String csvPath = "datafiles/TopSongs5000Edited.csv";
        Object dialog = null;

        // Try single-string argument constructor first, or no-arg constructor
        Constructor<?> filenameCtor = null;
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            if (ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0] == String.class) {
                filenameCtor = ctor;
                break;
            }
        }

        try {
            if (filenameCtor != null) {
                filenameCtor.setAccessible(true);
                dialog = filenameCtor.newInstance(csvPath);
            } else {
                Constructor<?> defaultCtor = clazz.getDeclaredConstructor();
                defaultCtor.setAccessible(true);
                dialog = defaultCtor.newInstance();
            }
        } catch (Throwable t) {
            fail("Failed to instantiate UserQueryCSVDialog in headless environment: " + t.getMessage());
        }

        assertNotNull(dialog, "UserQueryCSVDialog instance should be created.");

        // Access internal fields via reflection for testing UI components
        Field dataReaderField = findFieldByType(clazz, GenericDataReaderIntoMap.class);
        assertNotNull(dataReaderField, "Could not find GenericDataReaderIntoMap field in UserQueryCSVDialog.");
        dataReaderField.setAccessible(true);
        GenericDataReaderIntoMap dataReader = (GenericDataReaderIntoMap) dataReaderField.get(dialog);
        assertNotNull(dataReader, "GenericDataReaderIntoMap should be initialized in UserQueryCSVDialog.");

        Field inputFieldRef = findFieldByType(clazz, JTextField.class);
        assertNotNull(inputFieldRef, "Could not find JTextField in UserQueryCSVDialog.");
        inputFieldRef.setAccessible(true);
        JTextField inputField = (JTextField) inputFieldRef.get(dialog);
        assertNotNull(inputField, "JTextField component should be initialized.");

        Field resultsAreaRef = findFieldByType(clazz, JTextArea.class);
        assertNotNull(resultsAreaRef, "Could not find JTextArea in UserQueryCSVDialog.");
        resultsAreaRef.setAccessible(true);
        JTextArea resultsArea = (JTextArea) resultsAreaRef.get(dialog);
        assertNotNull(resultsArea, "JTextArea component should be initialized.");

        Method handleUserInputMethod = findMethodByName(clazz, "handleUserInput");
        assertNotNull(handleUserInputMethod, "Could not find handleUserInput method in UserQueryCSVDialog.");
        handleUserInputMethod.setAccessible(true);

        // Test 1: Query an existing key ("Elton John")
        inputField.setText("Elton John");
        handleUserInputMethod.invoke(dialog);

        String resultText = resultsArea.getText();
        assertTrue(resultText.contains("Elton John"), "Results area should display requested key.");
        assertTrue(resultText.contains("Candle in the Wind '97"), "Results area should display lookup value.");

        // Clear results
        resultsArea.setText("");

        // Test 2: Query a missing key ("Bon Jovy") to trigger nearby key lookup
        inputField.setText("Bon Jovy");
        handleUserInputMethod.invoke(dialog);

        String nearbyText = resultsArea.getText();
        assertTrue(nearbyText.contains("Bon Jovy"), "Results area should reference searched missing key.");
        assertTrue(nearbyText.contains("Lower key is: Bon Jovi"), "Results area should show lower key.");
        assertTrue(nearbyText.contains("Higher key is: Bone Thugs-n-Harmony"), "Results area should show higher key.");
    }

    private Field findFieldByType(Class<?> clazz, Class<?> type) {
        for (Field f : clazz.getDeclaredFields()) {
            if (type.isAssignableFrom(f.getType())) {
                return f;
            }
        }
        return null;
    }

    private Method findMethodByName(Class<?> clazz, String name) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
}
