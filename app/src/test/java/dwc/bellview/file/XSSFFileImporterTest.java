package dwc.bellview.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import dwc.bellview.BellviewException;

public class XSSFFileImporterTest {
	
	File file = new File("src/test/resources/testdata.xlsx");
	
	@Test
	public void readSampleData() {
		XSSFFileImporter importer = new XSSFFileImporter();
		try {
			importer.setFile(file);
			importer.setFirstRowHeadings(true);
			List<String[]> data = importer.getSampleData();
			data.forEach(cols -> {
				StringBuilder sb = new StringBuilder("[");
				for (int i=0;i<cols.length;i++) {
					if (i>0) sb.append(";");
					sb.append(cols[i]);
				}
				sb.append("]");
				System.out.println(sb.toString());
			});
			List<ColumnHeader> headings = importer.getColumnHeadings();
			headings.forEach(h -> {
				System.out.println(h.getIndex()+":"+h.getLabel());
			});
		} catch (BellviewException ex) {
			fail(ex);
			ex.printStackTrace(System.err);
		}
	}
	@Test
	public void readData() {
		try {
			
			XSSFFileImporter importer = new XSSFFileImporter();
			importer.setFile(file);
			importer.setColumnIndex(3);
			importer.setSexColumnIndex(1);
			importer.setAgeColumnIndex(2);
			importer.setFirstRowHeadings(true);
			importer.call();
			List<ImportError> errors = importer.getImportErrors();
			assertTrue(!importer.getDataItems().isEmpty());
			assertEquals(53554, importer.getDataItems().size());
			errors.forEach(e -> System.err.println(e.getTextValue()+" at "+e.getRow()));
		} catch (Exception be) {
			fail(be);
		}
	}
}
