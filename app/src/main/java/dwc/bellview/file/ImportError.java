package dwc.bellview.file;

public class ImportError {
	private long row = -1;
    private String errorType = "";
    private String textValue = "";
    /** Creates a new instance of ImportError */
    public ImportError() {
    }

    /**
     * Returns the row in the data source that the error occured.
     * @return row in which error occurred.
     */
    public long getRow() {
        return row;
    }

    /**
     * Returns the row in the data source that the error occured.
     * @param row row in which error occurred.
     */
    public void setRow(long row) {
        this.row = row;
    }

    /**
     * Returns the type of error.
     * @return type of error.
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * Set the type of error.
     * @param errorType type of error.
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * Returns a text description of the error.
     * @return description of error.
     */
    public String getTextValue() {
        return textValue;
    }

    /**
     * Sets a text description of the error.
     * @param textValue description of error.
     */
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ImportError [Row: ").append(this.getRow());
        sb.append(", Type of error: ").append(this.getErrorType());
        sb.append(", Value: ").append(this.getTextValue()).append("]");
        return sb.toString();
    }
}
