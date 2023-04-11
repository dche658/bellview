package dwc.bellview.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dwc.bellview.BellviewException;
import dwc.bellview.model.DataElement;
import dwc.bellview.transform.DataTransform;
import dwc.bellview.transform.NoneTransform;
import javafx.concurrent.Task;

public abstract class AbstractFileImporter extends Task<Long> implements FileImporter {
	
	/* File to be read */
	protected File file;
	
	protected boolean firstRowHeadings = true;
    protected char[] fieldDelimiters = null;
    private int columnIndex = 0;
    private int sexColumnIndex = -1;
    private int ageColumnIndex = -1;
    private final List<ImportError> importErrors = new ArrayList<>();
    private DataTransform dataTransform = new NoneTransform();
    private List<DataElement> dataItems = new ArrayList<>(100000);
    private Long numberOfRowsImported = 0l;
    private String valueColumnName = "";
  
    public AbstractFileImporter() {
    	fieldDelimiters = new char[1];
    	fieldDelimiters[0] = ',';
    }
    
    /* Required to read excel files
     * For CSV files just return an empty list;
     */
    @Override
	public List<String> getSheetNames() {
    	return new ArrayList<>();
    }
    
    @Override
	public abstract List<ColumnHeader> getColumnHeadings();

    @Override
	public abstract List<String[]> getSampleData() throws BellviewException;
    
    protected void writeToDatabase(double[] buffer, String[] sexBuffer, double[] ageBuffer, int numberOfRows) {
        for (int i = 0; i < numberOfRows; i++) {
            double tv = dataTransform == null ? buffer[i] : dataTransform.transform(buffer[i]);
            DataElement element = new DataElement(buffer[i], tv);
            element.setSex(sexBuffer[i]);
            element.setAge(ageBuffer[i]);
            this.dataItems.add(element);
        }
    }
	
    public File getFile() {
		return file;
	}
	@Override
	public void setFile(File file) throws BellviewException {
		if (file == null) throw new BellviewException("File is null");
		this.file = file;
	}
	@Override
	public boolean isFirstRowHeadings() {
		return firstRowHeadings;
	}
	@Override
	public void setFirstRowHeadings(boolean firstRowHeadings) {
		this.firstRowHeadings = firstRowHeadings;
	}
	@Override
	public char[] getFieldDelimiters() {
		return fieldDelimiters;
	}
	@Override
	public void setFieldDelimiters(char[] fieldDelimiters) {
		this.fieldDelimiters = fieldDelimiters;
	}
	@Override
	public String getValueColumnName() {
		return valueColumnName;
	}
	@Override
	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	@Override
	public int getColumnIndex() {
		return columnIndex;
	}
	@Override
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	public int getSexColumnIndex() {
		return sexColumnIndex;
	}

	public void setSexColumnIndex(int sexColumnIndex) {
		this.sexColumnIndex = sexColumnIndex;
	}

	public int getAgeColumnIndex() {
		return ageColumnIndex;
	}

	public void setAgeColumnIndex(int ageColumnIndex) {
		this.ageColumnIndex = ageColumnIndex;
	}

	@Override
	public DataTransform getDataTransform() {
		return dataTransform;
	}

	@Override
	public void setDataTransform(DataTransform dataTransform) {
		this.dataTransform = dataTransform;
	}

	@Override
	public List<ImportError> getImportErrors() {
		return importErrors;
	}

	@Override
	public List<DataElement> getDataItems() {
		return dataItems;
	}

	@Override
	public Long getNumberOfRowsImported() {
		return numberOfRowsImported;
	}

	public void setNumberOfRowsImported(Long numberOfRowsImported) {
		this.numberOfRowsImported = numberOfRowsImported;
	}
	
}
