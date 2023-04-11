package dwc.bellview.file;

import java.io.File;
import java.util.List;

import dwc.bellview.BellviewException;
import dwc.bellview.model.DataElement;
import dwc.bellview.transform.DataTransform;

public interface FileImporter {

	/* Required to read excel files
	 * For CSV files just return an empty list;
	 */
	List<String> getSheetNames();

	List<ColumnHeader> getColumnHeadings();

	List<String[]> getSampleData() throws BellviewException;

	void setFile(File file) throws BellviewException;

	boolean isFirstRowHeadings();

	void setFirstRowHeadings(boolean firstRowHeadings);

	char[] getFieldDelimiters();

	void setFieldDelimiters(char[] fieldDelimiters);

	int getColumnIndex();

	void setColumnIndex(int columnIndex);
	
	public int getSexColumnIndex();
	
	public void setSexColumnIndex(int index);
	
	public int getAgeColumnIndex();
	
	public void setAgeColumnIndex(int index);
	
	public void setValueColumnName(String name);
	
	public String getValueColumnName();

	DataTransform getDataTransform();

	void setDataTransform(DataTransform dataTransform);

	List<ImportError> getImportErrors();

	List<DataElement> getDataItems();


	Long getNumberOfRowsImported();

}