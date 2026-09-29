import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GenericDataReaderIntoMapTest {

    @Test
    public void testMergeMultipleKeysIntoSingleKeyWithMergedValues() {
        GenericDataReaderIntoMap dataReader = new GenericDataReaderIntoMap("datafiles/TopSongs5000Edited.csv");

        // Test key lookup for artist with multiple songs (e.g., Elton John)
        String value = dataReader.keyValueLookup("Elton John");
        assertNotNull(value, "Value for 'Elton John' should not be null.");

        // Verify multiple lines are merged together separated by newlines
        assertTrue(value.contains("Line 10: Candle in the Wind '97"), "Should contain line 10 song");
        assertTrue(value.contains("Line 221: Crocodile Rock"), "Should contain line 221 song");
        assertTrue(value.contains("Line 4843: Empty Garden (Hey Hey Johnny)"), "Should contain line 4843 song");

        // Verify line count indicates multiple values were merged into a single key
        String[] lines = value.split("\n");
        assertTrue(lines.length > 1, "Merged value should contain multiple lines for multiple song entries.");
    }

    @Test
    public void testMergeMultipleKeysCustomData(@TempDir Path tempDir) throws IOException {
        File csvFile = tempDir.resolve("test_data.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("Artist,Song\n");
            writer.write("Artist A,Song 1\n");
            writer.write("Artist B,Song 2\n");
            writer.write("Artist A,Song 3\n");
        }

        GenericDataReaderIntoMap dataReader = new GenericDataReaderIntoMap(csvFile.getAbsolutePath());
        assertEquals("Artist", dataReader.keyTitle);
        assertEquals("Song", dataReader.valueTitle);

        String artistAValues = dataReader.keyValueLookup("Artist A");
        assertNotNull(artistAValues, "Merged values for 'Artist A' should not be null.");
        String expectedArtistA = "Line 1: Song 1\nLine 3: Song 3";
        assertEquals(expectedArtistA, artistAValues);

        String artistBValues = dataReader.keyValueLookup("Artist B");
        assertEquals("Line 2: Song 2", artistBValues);
    }

    @Test
    public void testLowerAndHigherKeyForMissingKey() {
        GenericDataReaderIntoMap dataReader = new GenericDataReaderIntoMap("datafiles/TopSongs5000Edited.csv");

        // Query for a key not in the dataset, e.g. "Bon Jovy"
        String nearbyResult = dataReader.keyValueNearby("Bon Jovy");
        assertNotNull(nearbyResult, "Result for missing key query should not be null.");

        // Expected output check based on lowerKey and higherKey
        assertTrue(nearbyResult.contains("The entered key Bon Jovy was not in the map."));
        assertTrue(nearbyResult.contains("Lower key is: Bon Jovi"));
        assertTrue(nearbyResult.contains("Higher key is: Bone Thugs-n-Harmony"));
    }

    @Test
    public void testLowerAndHigherKeyCustomData(@TempDir Path tempDir) throws IOException {
        File csvFile = tempDir.resolve("test_nearby.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("Key,Value\n");
            writer.write("Apple,Fruit 1\n");
            writer.write("Banana,Fruit 2\n");
            writer.write("Cherry,Fruit 3\n");
        }

        GenericDataReaderIntoMap dataReader = new GenericDataReaderIntoMap(csvFile.getAbsolutePath());

        // Search for "Blueberry" which falls between "Banana" and "Cherry"
        String result = dataReader.keyValueNearby("Blueberry");
        assertNotNull(result, "Result for missing key query should not be null.");
        assertTrue(result.contains("The entered key Blueberry was not in the map."));
        assertTrue(result.contains("Lower key is: Banana"));
        assertTrue(result.contains("Higher key is: Cherry"));
    }
}
