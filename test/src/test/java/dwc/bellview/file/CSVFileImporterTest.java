package dwc.bellview.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.Test;

import dwc.bellview.BellviewException;


public class CSVFileImporterTest {

	@Test
	public void readData() {
		try {
			File file = new File("src/test/resources/testdata.csv");
			CSVFileImporter importer = new CSVFileImporter();
			importer.setFile(file);
			importer.setColumnIndex(3);
			importer.setFirstRowHeadings(true);
			importer.run();
			assertTrue(!importer.getDataItems().isEmpty());
			assertEquals(53556, importer.getDataItems().size());
		} catch (BellviewException be) {
			fail(be);
		}
	}
}
