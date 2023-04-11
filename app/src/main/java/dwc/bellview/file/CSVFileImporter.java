package dwc.bellview.file;


import dwc.bellview.BellviewException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileDataSource is used to read data from a delimited file. It assumes that
 * all data is in a single column.
 *
 * @author Douglas Chesher
 */
public class CSVFileImporter extends AbstractFileImporter {

    private static final Logger logger = LoggerFactory.getLogger(CSVFileImporter.class);

    private BufferedReader reader = null;

    public CSVFileImporter() {
    }


    @Override
    public List<String[]> getSampleData() throws BellviewException {
        List<String[]> sampleData = new ArrayList<>();
        try {
            int numberOfRows = 10;
            int count = 0;
            String[] data;
            while ((data = readLine()) != null && (count < numberOfRows)) {
                sampleData.add(data);
                count += 1;
            }
        } catch (IOException ioe) {
            throw new BellviewException(ioe.getMessage(), ioe);
        } finally {
            close();
        }
        return sampleData;
    }

    /**
     * Read one line from the input stream and return this as an array of
     * strings. Returns null when the end of the stream has been reached.
     *
     * @return
     * @throws java.io.IOException
     */
    public String[] readLine() throws IOException {
        String line = getReader().readLine();
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < getFieldDelimiters().length; i++) {
            String str = Integer.toHexString(Character.codePointAt(getFieldDelimiters(), i));
            regex.append("\\x");
            if (str.length() == 1) {
                regex.append("0");
            }
            regex.append(str);
        }
        if (line != null) {
            return line.split(regex.toString());
        } else {
            return null;
        }
    }

    private BufferedReader getReader() throws FileNotFoundException {
        if (reader == null) {
            reader = new BufferedReader(new FileReader(file));
        }
        return reader;
    }

    protected Long doInBackground() throws BellviewException {
    	logger.info("Reading file {}",this.getFile().getAbsolutePath());
        Long rowsImported = 0l;
        long row = 0;
        try {
        	
            String[] v;
            int BUFFER_SIZE = 1024;
            double[] buffer = new double[BUFFER_SIZE];
            String[] sexBuffer = new String[BUFFER_SIZE];
            double[] ageBuffer = new double[BUFFER_SIZE];
            int bufferIndex = 0;
            logger.debug("Starting file import");
            do {
                v = readLine();
                //logger.debug("Reading row {}", row);
                //this.updateMessage("Reading row "+row);
                if (v != null) {
                    if (v.length > this.getColumnIndex() && !(row == 0 && this.isFirstRowHeadings())) {
                        try {
                            if (bufferIndex == BUFFER_SIZE) {
                                writeToDatabase(buffer, sexBuffer, ageBuffer, bufferIndex);
                                bufferIndex = 0;
                            }
                            buffer[bufferIndex] = Double.parseDouble(v[getColumnIndex()]);
                            if(getSexColumnIndex()>=0) {
                            	sexBuffer[bufferIndex] = v[getSexColumnIndex()];
                            } else {
                            	sexBuffer[bufferIndex] = "U";
                            }
                            if (getAgeColumnIndex()>=0) {
                            	try {
                            		ageBuffer[bufferIndex] = Double.parseDouble(v[getAgeColumnIndex()]);
                            	} catch (NumberFormatException ex) {
                            		ageBuffer[bufferIndex] = -1.0;
                            	}
                            } else {
                            	ageBuffer[bufferIndex] = -1.0;
                            }
                            bufferIndex += 1;
                        } catch (NumberFormatException e) {
                            ImportError err = new ImportError();
                            err.setRow(row);
                            err.setErrorType(e.getMessage());
                            err.setTextValue(v[getColumnIndex()]);
                            getImportErrors().add(err);
                            if (logger.isDebugEnabled()) {
                                logger.debug(e.getMessage());
                            }
                        }
                    }
                    row += 1;
                } else {
                    writeToDatabase(buffer, sexBuffer, ageBuffer, bufferIndex);
                }
            } while (!(v == null || this.isCancelled()));
            if (isFirstRowHeadings()) {
                row = row - 1;
            }
            rowsImported = row - getImportErrors().size();
            this.setNumberOfRowsImported(rowsImported);
        } catch (IOException ex) {
            throw new BellviewException(ex);
        } finally {
            close();
        }
        
        return rowsImported;
    }

    public void close() throws BellviewException {
        try {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        } catch (IOException e) {
            String msg = "IOException occured while closing the file reader.";
            logger.error(msg);
            throw new BellviewException(msg, e);
        }
        logger.trace("Closed file reader");
    }

    @Override
    public List<ColumnHeader> getColumnHeadings() {
        List<ColumnHeader> headings = new ArrayList<>();
        int colCount = 0;
        try {
            String[] row = this.readLine();
            for (String item : row) {
                ColumnHeader header;
                if (isFirstRowHeadings()) {
                    header = new ColumnHeader(colCount, item);
                } else {
                    header = new ColumnHeader(colCount, String.valueOf(colCount));
                }
                headings.add(header);
                colCount += 1;
            }
        } catch (IOException ioe) {
            logger.error("IOException reading headings", ioe);
        } finally {
            try {
                close();
            } catch (BellviewException ae) {
                logger.error("Exception closing CSV file reader", ae);
            }
        }
        return headings;
    }

	@Override
	protected Long call() throws Exception {
		logger.debug("CSV Import Task called");
		Long rowsImported = 0l;
		try {
			rowsImported = doInBackground();
			setNumberOfRowsImported(rowsImported);
		} catch (BellviewException be) {
			logger.error("Error while importing data",be);
		}
		return rowsImported;
	}

}
