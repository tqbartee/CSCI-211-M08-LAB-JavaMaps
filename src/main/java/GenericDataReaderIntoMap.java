import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;

public class GenericDataReaderIntoMap {
    // TODO: Change below from HashMap to TreeMap for the nearby commands
    // TODO: Also change Map to NavigableMap
    // These are necessary for the lowerKey and higherKey calls later
    // in the application to work (they need the NavigableMap interface
    // and the TreeMap because HashMap does not maintain fully
    // sorted keys)
    // Note the definition of the object keyValuePairs
    // as being an interface type (either Map or NavigableMap)
    public NavigableMap<String, String> keyValuePairs = new TreeMap<>();
    public String keyTitle;
    public String valueTitle;

    public GenericDataReaderIntoMap(String fileName) {
        // Read the CSV file data into the map
        String line;
        String csvSplitBy = ",";
        int lineno = 0;
        String data[];
        try (BufferedReader br =
                     new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                if (lineno == 0) {
                    // Save the header
                    data = line.split(",", 2);
                    if (data.length == 2) {
                        keyTitle = data[0];
                        valueTitle = data[1];
                    }
                }
                else if (lineno > 0) {
                    data = line.split(",", 2);
                    if (data.length == 2) {
                        String keyInput = data[0].trim();
                        String valueInput = "Line " + lineno + ": " + data[1].trim();
                        // TODO: resolve issue of multiple values per key
                        // Note how the original version only stores one song
                        // (the highest number) for Elton John and all artists
                        // See slides (before/after) and tests to get expected format
                        // Pseudocode: see if the key is in the Map
                        // If so then add this value to valueInput
                        keyValuePairs.put(keyInput,
                                valueInput);
                    }
                }
                lineno++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Static method for key-value pair lookups
    public void keyValueLookupCommandLine() {
        // Test operation with user input
        Scanner symbolscanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a key (" +
                    this.keyTitle +
                    ") or 'quit' to exit: ");
            String key = symbolscanner.nextLine().trim();
            if (key.equalsIgnoreCase("quit")) {
                break;
            }
            String value = (String) this.keyValuePairs.get(key);
            if (value != null) {
                System.out.println("Value (" +
                        this.valueTitle +
                        "): " + value);
            } else {
                System.out.println("Key not found.");
            }
        }
        System.out.println("Thank you for using the Key-Value Lookup program");
    }

    // Static method for key-value pair lookups
    public String keyValueLookup(String keyString) {
        // Test - Create the loaded Map object and
        // Interact with user
        String keyStringTrim = keyString.trim();
        String value = (String) keyValuePairs.get(keyStringTrim);
        return value;
    }

    public String keyValueNearby(String keyString) {
        String lower = " ";
        String higher = " ";
        // TODO: get the lower and higher key for a given input key
        // we will use this to clue the user into nearby keys if they
        // submit something that missed (no key)
        // produce the output shown in the slides (see before/after)
        // and expected by the unit tests


        String returnString;
        returnString = new String
                ("The entered key " + keyString + " was not in the map."
                        + "\n" + "Lower key is: " + lower + "\n"
                        + "Higher key is: " + higher);
        return returnString;  // or construct a response with both nearby keys
    }

    public static void test() {
        String fileName = "datafiles/TopSongs5000Edited.csv";
        GenericDataReaderIntoMap ourGenericDataReaderIntoMap =
                new GenericDataReaderIntoMap(fileName);
        ourGenericDataReaderIntoMap.keyValueLookupCommandLine();
    }

    public static void main(String[] args) {
        test();
    }
}