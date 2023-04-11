package dwc.bellview.file;

import dwc.bellview.BellviewException;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Douglas Chesher
 */
public class XSSFFileImporter extends AbstractFileImporter {

	private static final Logger logger = LoggerFactory.getLogger(XSSFFileImporter.class);

	private XSSFWorkbook workbook;

	private int selectedSheetIndex = 0;

	public XSSFFileImporter() {

	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public List<String[]> getSampleData() throws BellviewException {
		List<String[]> sampleData = new ArrayList<>();
		int numberOfRows = 10;
		int numberOfColumns = 0;
		int rowCount = 0;
		int colCount;
		Sheet sheet = getWorkbook().getSheetAt(this.selectedSheetIndex);
		Row row = sheet.getRow(0);
		if (row != null) {
			for (Cell cell : row) {
				if (!cell.getCellType().equals(CellType.BLANK)) {
					numberOfColumns += 1;
				} else {
					break;
				}
			}

			for (Row r : sheet) {
				if (rowCount >= numberOfRows) {
					break;
				}
				colCount = 0;
				String[] vals = new String[numberOfColumns];
				sampleData.add(vals);
				for (Cell cell : r) {
					if (colCount > numberOfColumns) {
						break;
					}
					switch (cell.getCellType()) {
					case STRING:
						vals[colCount] = cell.getStringCellValue();
						break;
					case NUMERIC:
						vals[colCount] = String.valueOf(cell.getNumericCellValue());
						break;
					case FORMULA:
						try {
							vals[colCount] = String.valueOf(cell.getNumericCellValue());
						} catch (IllegalStateException ise) {
							logger.warn("Error getting numeric value from formula " + ise.getMessage());
							vals[colCount] = cell.getStringCellValue();
						}
						break;
					case BOOLEAN:
						vals[colCount] = String.valueOf(cell.getBooleanCellValue());
					default:
						vals[colCount] = "";
						break;
					}
					colCount += 1;
				}
				rowCount += 1;
			}
		} else {
			String[] rowdata = { "", "", "", "" };
			sampleData.add(rowdata);
		}
		return sampleData;
	}

	@Override
	public List<String> getSheetNames() {
		List<String> names = new ArrayList<>();
		for (Sheet sheet : getWorkbook()) {
			names.add(sheet.getSheetName());
		}
		close();
		return names;
	}

	@Override
	public List<ColumnHeader> getColumnHeadings() {
		List<ColumnHeader> headings = new ArrayList<>();
		Sheet sheet = getWorkbook().getSheetAt(selectedSheetIndex);
		Row row = sheet.getRow(0);
		int columnCount = 0;
		for (Cell cell : row) {
			if (cell.getCellType().equals(CellType.BLANK)) {
				break;
			} else {
				ColumnHeader header = new ColumnHeader(columnCount, "");
				headings.add(header);
				if (this.firstRowHeadings) {
					switch (cell.getCellType()) {
					case STRING:
						header.setLabel(cell.getStringCellValue());
						break;
					case NUMERIC:
						header.setLabel(String.valueOf(cell.getNumericCellValue()));
						break;
					case BOOLEAN:
						header.setLabel(String.valueOf(cell.getBooleanCellValue()));
						break;
					case FORMULA:
						header.setLabel(String.valueOf(cell.getNumericCellValue()));
						break;
					default:
						break;
					}
				} else {
					header.setLabel(String.valueOf(columnCount));
				}

			}
			columnCount += 1;
		}
		return headings;
	}

	private String getCellValueAsString(Cell cell) {
		String val = "";
		if (cell != null) {
			switch (cell.getCellType()) {
			case STRING:
				val = cell.getStringCellValue();
				break;
			case NUMERIC:
				val = String.valueOf(cell.getNumericCellValue());
				break;
			case BOOLEAN:
				val = String.valueOf(cell.getBooleanCellValue());
				break;
			case FORMULA:
				val = String.valueOf(cell.getNumericCellValue());
				break;
			default:
				break;
			}
		}
		return val;
	}

	protected Long doInBackground() {
		logger.info("Reading file {}",this.getFile().getAbsolutePath());
		long rowsRead = 0;
		long rowsImported = 0;
		Sheet sheet = this.getWorkbook().getSheetAt(selectedSheetIndex);
		logger.debug("Selected sheet: {}", sheet.getSheetName());
		int BUFFER_SIZE = 1024;
		double[] buffer = new double[BUFFER_SIZE];
		String[] sexBuffer = new String[BUFFER_SIZE];
		double[] ageBuffer = new double[BUFFER_SIZE];
		int bufferIndex = 0;
		long rowIndex = 0;
		for (Row datarow : sheet) {
			//logger.debug("Row index {}", rowIndex);
			if (!(rowIndex == 0 && this.isFirstRowHeadings())) {
				if (bufferIndex == BUFFER_SIZE) {
					writeToDatabase(buffer, sexBuffer, ageBuffer, bufferIndex);
					//logger.debug("Row " + rowsImported);
					bufferIndex = 0;
				}
				// Get value from selected column

				Cell cell = datarow.getCell(getColumnIndex());

				if (cell != null) {
					try {
						// if number format exception thrown then skip the row
						buffer[bufferIndex] = cell.getNumericCellValue();
						// Get sex from selected column
						String sex = "U";
						if (getSexColumnIndex() >= 0) {
							Cell sexCell = datarow.getCell(getSexColumnIndex());
							sex = this.getCellValueAsString(sexCell);
						}
						if (sex == null || sex.length() == 0)
							sex = "U";
						//logger.debug("Sex {}", sex);
						sexBuffer[bufferIndex] = sex;

						Double age = -1.0;
						if (getAgeColumnIndex() >= 0) {
							Cell ageCell = datarow.getCell(getAgeColumnIndex());
							try {
								age = ageCell.getNumericCellValue();
								//logger.debug("Age {}", age);
							} catch (NumberFormatException | IllegalStateException nfe) {
								logger.debug("Number format exception processing age at row {}", rowIndex);
								// use default value if not a number
								age = -0.1;
							}
						}
						ageBuffer[bufferIndex] = age;

						bufferIndex += 1;
						rowsImported += 1;
					} catch (NumberFormatException | IllegalStateException ex) {
						ImportError err = new ImportError();
						err.setRow(rowsRead);
						err.setErrorType(ex.getMessage());
						err.setTextValue("");
						getImportErrors().add(err);
						logger.debug("Number format exception thrown at row {}", rowIndex);
					}

				} else {
					logger.debug("Cell at row {} column {} is null", rowIndex, getColumnIndex());
				}
			}
			rowsRead += 1;
			rowIndex += 1;
		}

		if (bufferIndex > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Writing " + bufferIndex + " bytes of buffer to database");
			}
			writeToDatabase(buffer, sexBuffer, ageBuffer, bufferIndex);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Number of rows imported " + rowsImported);
		}
		close();
		return rowsImported;
	}

	/**
	 * @return the selectedSheetIndex
	 */
	public int getSelectedSheetIndex() {
		return selectedSheetIndex;
	}

	/**
	 * @param selectedSheetIndex the selectedSheetIndex to set
	 */
	public void setSelectedSheetIndex(int selectedSheetIndex) {
		this.selectedSheetIndex = selectedSheetIndex;
	}

	/**
	 * @return the workbook
	 */
	public XSSFWorkbook getWorkbook() {
		try {
			if (file == null)
				throw new RuntimeException("File is null");
			if (workbook == null)
				workbook = new XSSFWorkbook(file);
		} catch (InvalidFormatException | IOException | RuntimeException e) {
			logger.error("Error while reading {} with message {}", file.getName(), e.getMessage(), e);
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setContentText("Error while opening workbook. " + e.getMessage());
			a.show();
		}
		return workbook;
	}

	private void close() {
		try {
			if (workbook != null) {
				workbook.close();
				workbook = null;
				logger.debug("Workbook closed");
			}
		} catch (IOException ioe) {
			logger.error("Error while closing excel workbook");
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setContentText("Error while closing workbook. " + ioe.getMessage());
			a.show();
		}
	}

	@Override
	protected Long call() throws Exception {
		Long rowsImported = doInBackground();
		setNumberOfRowsImported(rowsImported);
		return rowsImported;
	}

}
