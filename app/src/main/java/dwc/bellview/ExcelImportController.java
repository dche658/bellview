package dwc.bellview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dwc.bellview.file.ColumnHeader;
import dwc.bellview.file.XSSFFileImporter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class ExcelImportController extends AbstractImportController {

	private static final Logger logger = LoggerFactory.getLogger(ExcelImportController.class);

	@FXML
	private CheckBox firstRowHeadingCheckBox;

	@FXML
	private ComboBox<String> sheetComboBox;

	@FXML
	private ComboBox<ColumnHeader> valueComboBox;

	@FXML
	private ComboBox<ColumnHeader> sexComboBox;

	@FXML
	private ComboBox<ColumnHeader> ageComboBox;

	@FXML
	private TableView<Map<String, String>> dataTableView;

	private XSSFFileImporter fileImporter;

	public ExcelImportController() {
	}

	// Called by FXML loader
	public void initialize() {

	}

	/*
	 * Sheet selection change listener
	 */
	public void sheetSelectionChanged() {
		fileImporter.setSelectedSheetIndex(sheetComboBox.getSelectionModel().getSelectedIndex());
		loadSampleData();
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

	// Call this after inserting model and file importer
	public void configure() {
		if (fileImporter == null)
			throw new RuntimeException("File importer is null. This should never happen!");
		sheetComboBox.getItems().addAll(fileImporter.getSheetNames());
		sheetComboBox.getSelectionModel().select(0);

	}

	public void loadSampleData() {
		try {
			if (fileImporter == null)
				throw new RuntimeException("FileImporter is null");
			dataTableView.getColumns().clear();
			fileImporter.setFirstRowHeadings(firstRowHeadingCheckBox.isSelected());
			
			// Clear the column selection combobox
			valueComboBox.getItems().clear();
			sexComboBox.getItems().clear();
			ageComboBox.getItems().clear();

			sexComboBox.getItems().add(new ColumnHeader(-1, "Skip"));
			ageComboBox.getItems().add(new ColumnHeader(-1, "Skip"));

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
			fileImporter.setColumnIndex(valueComboBox.getSelectionModel().getSelectedItem().getIndex());
			fileImporter.setSexColumnIndex(sexComboBox.getSelectionModel().getSelectedItem().getIndex());
			fileImporter.setAgeColumnIndex(ageComboBox.getSelectionModel().getSelectedItem().getIndex());
		} catch (BellviewException ex) {
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setContentText("An error occurred loading the sample data. " + ex.getMessage());
			a.show();
			logger.error("Error loading sample data", ex);
		}

	}

	public void setFileImporter(XSSFFileImporter fileImporter) {
		this.fileImporter = fileImporter;
	}

}
