package dwc.bellview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dwc.bellview.file.ColumnHeader;
import dwc.bellview.file.FileImporter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class FileImportController extends AbstractImportController {
	private static final Logger logger = LoggerFactory.getLogger(FileImportController.class);

	@FXML
	private CheckBox commaCheckBox;

	@FXML
	private CheckBox tabCheckBox;

	@FXML
	private CheckBox otherCheckBox;

	@FXML
	private CheckBox firstRowHeadingCheckBox;

	@FXML
	private TextField sepTextField;

	@FXML
	private ComboBox<ColumnHeader> valueComboBox;
	
	@FXML
	private ComboBox<ColumnHeader> sexComboBox;
	
	@FXML
	private ComboBox<ColumnHeader> ageComboBox;

	@FXML
	private TableView<Map<String, String>> dataTableView;

	private FileImporter fileImporter;
	
	private String delimiters = ",";

	public FileImportController() {
	}

	
	public void initialize() {

	}
	
	//action listener for when first row heading checkbox is toggled
	public void firstRowHeadingChanged() {
		fileImporter.setFirstRowHeadings(this.firstRowHeadingCheckBox.isSelected());
		loadSampleData();
	}
	
	//action listener for when comma checkbox is toggled
	public void commaCheckboxChanged() {
		updateDelimiters(",", commaCheckBox.isSelected());
		loadSampleData();
	}
	
	public void tabCheckboxChanged() {
		updateDelimiters("\t", tabCheckBox.isSelected());
		loadSampleData();
	}
	
	public void otherCheckboxChanged() {
		if (sepTextField.getText()!=null && sepTextField.getText().length()>0) {
			updateDelimiters(sepTextField.getText(), otherCheckBox.isSelected());
			loadSampleData();
		}
	}
	
	public void sepTextFieldChanged() {
		if (otherCheckBox.isSelected()) {
			if (sepTextField.getText()!=null && sepTextField.getText().length()>0) {
				updateDelimiters(sepTextField.getText(), otherCheckBox.isSelected());
				loadSampleData();
			}
		}
	}
	
	public void valueColumnChanged() {
		fileImporter.setColumnIndex(valueComboBox.getSelectionModel().getSelectedItem().getIndex());
		fileImporter.setValueColumnName(valueComboBox.getSelectionModel().getSelectedItem().getLabel());
	}

	public void sexColumnChanged() {
		fileImporter.setSexColumnIndex(sexComboBox.getSelectionModel().getSelectedItem().getIndex());
	}

	public void ageColumnChanged() {
		fileImporter.setAgeColumnIndex(ageComboBox.getSelectionModel().getSelectedItem().getIndex());
	}
	
	private void updateDelimiters(String c, boolean add) {
		
		if (add && !this.delimiters.contains(c)) {
			//add delimiter if not present
			StringBuilder sb = new StringBuilder(delimiters);
			sb.append(c);
			delimiters = sb.toString();
		} else if (!add && this.delimiters.contains(c)) {
			//remove this delimiter if present
			StringBuilder sb = new StringBuilder(delimiters);
			sb.deleteCharAt(delimiters.indexOf(c));
			delimiters = sb.toString();
		}
		fileImporter.setFieldDelimiters(delimiters.toCharArray());
	}

	@Override
	public void loadSampleData() {
		try {
			if (fileImporter == null)
				throw new RuntimeException("FileImporter is null");
			dataTableView.getColumns().clear();
			fileImporter.setFirstRowHeadings(firstRowHeadingCheckBox.isSelected());
			logger.debug("Use first row as heading is " + fileImporter.isFirstRowHeadings());
			fileImporter.setFieldDelimiters(getDelimiters());

			// Clear the column selection combobox
			valueComboBox.getItems().clear();
			sexComboBox.getItems().clear();
			ageComboBox.getItems().clear();
			
			sexComboBox.getItems().add(new ColumnHeader(-1,"Skip"));
			ageComboBox.getItems().add(new ColumnHeader(-1,"Skip"));
			
			// Load sample data and display in tableview
			List<String[]> sampleData = fileImporter.getSampleData();
			ObservableList<Map<String, String>> dataList = FXCollections.<Map<String, String>>observableArrayList();
			
			
			//Build column headings
			String[] headings;
			if (fileImporter.isFirstRowHeadings()) {
				headings = sampleData.get(0);
				sampleData.remove(0);
			} else {
				headings = new String[sampleData.get(0).length];
				for (int i = 0; i < headings.length; i++) {
					headings[i] = "X" + i;
				}
			}
			
			//Map sample data for display in table
			sampleData.forEach(row -> {
				Map<String, String> m = new HashMap<>();
				for (int i = 0; i < headings.length; i++) {
					m.put(headings[i], row[i]);
				}
				dataList.add(m);
			});
			for (int i = 0; i < headings.length; i++) {
				final String heading = headings[i];
				TableColumn<Map<String, String>, String> col = new TableColumn<>(heading);
				col.setCellValueFactory(
						new Callback<CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> param) {
								Map<String, String> map = param.getValue();
								String value = map.get(heading);
								return new ReadOnlyStringWrapper(value);
							}

						});
				dataTableView.getColumns().add(col);
				logger.debug("Adding column " + heading);
				valueComboBox.getItems().add(new ColumnHeader(i,heading));
				sexComboBox.getItems().add(new ColumnHeader(i,heading));
				ageComboBox.getItems().add(new ColumnHeader(i,heading));
			}

			dataTableView.setItems(dataList);
			valueComboBox.getSelectionModel().selectFirst();
			sexComboBox.getSelectionModel().selectFirst();
			ageComboBox.getSelectionModel().selectFirst();
		} catch (BellviewException ex) {
			logger.error("Exception reading sample data", ex);
		}
	}

	public ColumnHeader getSelectedColumnHeader() {
		return valueComboBox.getSelectionModel().getSelectedItem();
	}

	public ComboBox<ColumnHeader> getValueComboBox() {
		return valueComboBox;
	}


	public ComboBox<ColumnHeader> getSexComboBox() {
		return sexComboBox;
	}


	public ComboBox<ColumnHeader> getAgeComboBox() {
		return ageComboBox;
	}


	private char[] getDelimiters() {
		List<String> list = new ArrayList<>();
		if (commaCheckBox.isSelected())
			list.add(",");
		if (tabCheckBox.isSelected())
			list.add("\t");
		if (otherCheckBox.isSelected())
			list.add(sepTextField.getText(0, 1));
		char[] delimiters = new char[list.size()];
		for (int i = 0; i < list.size(); i++) {
			delimiters[i] = list.get(i).charAt(0);
		}
		return delimiters;
	}

	public void close() {
		this.sepTextField.getScene().getWindow().hide();
	}

	public FileImporter getFileImporter() {
		return fileImporter;
	}

	public void setFileImporter(FileImporter fileImporter) {
		this.fileImporter = fileImporter;
	}
}
