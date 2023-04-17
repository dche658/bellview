package dwc.bellview;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dwc.bellview.export.PdfReportWriter;
import dwc.bellview.file.AbstractFileImporter;
import dwc.bellview.file.CSVFileImporter;
import dwc.bellview.file.FileImporter;
import dwc.bellview.file.XSSFFileImporter;
import dwc.bellview.model.BellviewModel;
import dwc.bellview.model.DataElement;
import dwc.bellview.model.DistributionParameters;
import dwc.bellview.model.HistogramBin;
import dwc.bellview.model.Report;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.stage.Stage;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/*
 * FXML Controller for the applications main scene graph 
 */
public class FXMLController {

	private static final Logger logger = LoggerFactory.getLogger(FXMLController.class);

	private static final String DEFAULT_FILE_IMPORT_DIRECTORY = "defaultFileImportDirectory";

	private static final String DEFAULT_EXPORT_DIRECTORY = "defaultExportDirectory";

	@FXML
	private Label statusLabel;

	@FXML
	private TextField analyte;

	@FXML
	private TextField units;

	@FXML
	private TextField histogramMin;

	@FXML
	private TextField histogramMax;

	@FXML
	private TextField histogramBinWidth;

	@FXML
	private TextField refIntRange;

	@FXML
	private Spinner<Integer> regIndexStart;

	@FXML
	private Spinner<Integer> regIndexEnd;

	@FXML
	private TableView<Map<String, Object>> histogramTable;

	@FXML
	private TextArea reportArea;

	@FXML
	private VBox analysisVBox;

	@FXML
	private BarChart<String, Number> residualsChart;

	@FXML
	private MenuItem exportPdfMenuItem;
	
	@FXML
	private ProgressBar progressBar;

	private LineChart<Number, Number> histogramChart;

	private LineChart<Number, Number> bhattacharyaChart;

	private final BellviewModel model = new BellviewModel();

	private final Report report = new Report();

	private Preferences preferences = Preferences.userNodeForPackage(App.class);

	private Stage stage;

	public FXMLController() {
		report.setModel(model);
	}

	public void initialize() {
		histogramTable.getColumns().clear();
		TableColumn<Map<String, Object>, Object> col0 = new TableColumn<>("Index");
		col0.setCellValueFactory(
				new Callback<CellDataFeatures<Map<String, Object>, Object>, ObservableValue<Object>>() {

					@Override
					public ObservableValue<Object> call(CellDataFeatures<Map<String, Object>, Object> param) {
						Map<String, Object> map = param.getValue();
						Object value = map.get("index");
						return new ReadOnlyObjectWrapper<Object>(value);
					}

				});
		TableColumn<Map<String, Object>, Object> col1 = new TableColumn<>("Bin");
		col1.setCellValueFactory(
				new Callback<CellDataFeatures<Map<String, Object>, Object>, ObservableValue<Object>>() {

					@Override
					public ObservableValue<Object> call(CellDataFeatures<Map<String, Object>, Object> param) {
						Map<String, Object> map = param.getValue();
						Object value = map.get("bin");
						return new ReadOnlyObjectWrapper<Object>(value);
					}

				});
		TableColumn<Map<String, Object>, Object> col2 = new TableColumn<>("Count");
		col2.setCellValueFactory(
				new Callback<CellDataFeatures<Map<String, Object>, Object>, ObservableValue<Object>>() {

					@Override
					public ObservableValue<Object> call(CellDataFeatures<Map<String, Object>, Object> param) {
						Map<String, Object> map = param.getValue();
						Object value = map.get("count");
						return new ReadOnlyObjectWrapper<Object>(value);
					}

				});
		TableColumn<Map<String, Object>, Object> col3 = new TableColumn<>("Delta Log Y");
		col3.setCellValueFactory(
				new Callback<CellDataFeatures<Map<String, Object>, Object>, ObservableValue<Object>>() {

					@Override
					public ObservableValue<Object> call(CellDataFeatures<Map<String, Object>, Object> param) {
						Map<String, Object> map = param.getValue();
						Object value = map.get("dly");
						return new ReadOnlyObjectWrapper<Object>(value);
					}

				});
		histogramTable.getColumns().add(col0);
		histogramTable.getColumns().add(col1);
		histogramTable.getColumns().add(col2);
		histogramTable.getColumns().add(col3);

		NumberAxis xAxis1 = new NumberAxis();
		xAxis1.setForceZeroInRange(false);
		xAxis1.setLabel("Value");
		NumberAxis yAxis1 = new NumberAxis();
		yAxis1.setLabel("Count");

		histogramChart = new LineChart<Number, Number>(xAxis1, yAxis1);
		histogramChart.setLegendVisible(false);
		histogramChart.getData().add(new XYChart.Series<Number, Number>());
		histogramChart.getData().add(new XYChart.Series<Number, Number>());
		histogramChart.setTitle("Frequency");
		histogramChart.setAnimated(false);

		NumberAxis xAxis2 = new NumberAxis();
		xAxis2.setForceZeroInRange(false);
		xAxis2.setLabel("Value");
		NumberAxis yAxis2 = new NumberAxis();
		yAxis2.setLabel("Delta log(y)");
		bhattacharyaChart = new LineChart<Number, Number>(xAxis2, yAxis2);
		bhattacharyaChart.setLegendVisible(false);
		bhattacharyaChart.getData().add(new XYChart.Series<Number, Number>());
		bhattacharyaChart.getData().add(new XYChart.Series<Number, Number>());
		bhattacharyaChart.setTitle("Bhattacharya Plot");
		bhattacharyaChart.setAnimated(false);
		analysisVBox.getChildren().clear();
		analysisVBox.getChildren().add(histogramChart);
		analysisVBox.getChildren().add(bhattacharyaChart);

		residualsChart.getData().add(new XYChart.Series<String, Number>());

		regIndexStart.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
		regIndexEnd.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));

		logger.debug("FXMLControler initialized");
		statusLabel.setText("Initialised");
	}

	/*
	 * Event handler for processing data after it has been read
	 */
	private void processData(FileImporter importer) {
		logger.debug("Importing data to histogram");
		model.getHistogram().clear();
		model.setData(importer.getDataItems());
		model.setImportErrors(importer.getImportErrors());
		buildGenderList();
		updateHistogram();
	}
	
	private void buildGenderList() {
		SortedSet<String> set = new TreeSet<>();
		model.getData().forEach(d -> {
			if (d.getSex()==null || d.getSex().length()==0) {
				if (!set.contains(DataElement.SEX_UNKNOWN)) {
					set.add(DataElement.SEX_UNKNOWN);
				}
			} else if(!set.contains(d.getSex())) {
				set.add(d.getSex());
			}
		});
		model.getGenderList().clear();
		model.getGenderList().addAll(set);
	}

	private void updateHistogram() {
		DescriptiveStatistics statistics = model.buildStatistics();
		model.getHistogram().setMinValue(statistics.getMin());
		histogramMin.setText(String.valueOf(model.getHistogram().getMinValue()));
		model.getHistogram().setMaxValue(statistics.getMax());
		histogramMax.setText(String.valueOf(model.getHistogram().getMaxValue()));
		model.getHistogram().setBinWidth(model.getHistogram().getDefaultBinWidth());
		histogramBinWidth.setText(String.valueOf(model.getHistogram().getBinWidth()));
		model.addDataToHistogram();
		histogramTable.setItems(model.getHistogramTableData());
		buildIndexList();
		updateHistogramChart();
	}

	/*
	 * Build the index list for the spinners based on the histogram
	 */
	private void buildIndexList() {
		logger.debug("Building index list");
		int min = 0;
		int max = model.getHistogram().getElements().size() - 1;
		SpinnerValueFactory.IntegerSpinnerValueFactory startFactory = (SpinnerValueFactory.IntegerSpinnerValueFactory) regIndexStart
				.getValueFactory();
		startFactory.setMin(min);
		startFactory.setMax(max);
		startFactory.setValue(min);

		SpinnerValueFactory.IntegerSpinnerValueFactory endFactory = (SpinnerValueFactory.IntegerSpinnerValueFactory) regIndexEnd
				.getValueFactory();
		endFactory.setMin(min);
		endFactory.setMax(max);
		endFactory.setValue(max);
	}

	/*
	 * Action to be performed by the build histogram button
	 */
	public void buildHistogram() {
		logger.debug("Building histogram");
		model.getHistogram().clear();
		model.getHistogram().setMinValue(Double.valueOf(histogramMin.getText()));
		model.getHistogram().setMaxValue(Double.valueOf(histogramMax.getText()));
		model.getHistogram().setBinWidth(Double.valueOf(histogramBinWidth.getText()));
		model.addDataToHistogram();
		histogramTable.setItems(model.getHistogramTableData());
		buildIndexList();
		updateHistogramChart();
	}

	public void updateHistogramChart() {
		logger.debug("Updating histogram chart");
		if (model == null)
			throw new RuntimeException("This is unexpected. The model is null");
		histogramChart.getData().get(0).getData().clear();
		histogramChart.getData().get(1).getData().clear();
		histogramChart.getData().get(1).getData().add(new XYChart.Data<Number,Number>(model.getHistogram().getElements().get(0).getBinMidPoint(),0));
		bhattacharyaChart.getData().get(0).getData().clear();
		bhattacharyaChart.getData().get(1).getData().clear();
		bhattacharyaChart.getData().get(1).getData().add(new XYChart.Data<Number,Number>(model.getHistogram().getElements().get(0).getBinMidPoint(),0));
		model.getHistogram().getElements().forEach(bin -> {
			XYChart.Data<Number, Number> data = new XYChart.Data<Number, Number>(bin.getBinMidPoint(), bin.getCount());
			histogramChart.getData().get(0).getData().add(data);
			bhattacharyaChart.getData().get(0).getData()
					.add(new XYChart.Data<Number, Number>(bin.getBinMidPoint(), bin.getDeltaLogY()));
		});
	}

	/**
	 * Perform regression analysis on the selected segment
	 */
	public void analyze() {
		logger.debug("Begin analysis");
		model.setStartIndexValue(regIndexStart.getValue());
		model.setEndIndexValue(regIndexEnd.getValue());
		bhattacharyaChart.getData().get(1).getData().clear();
		histogramChart.getData().get(1).getData().clear();
		try {
			// Calculate the regression
			SimpleRegression reg = model.calculateRegression();

			// Calculate the distribution parameters from the slope and intercept
			DistributionParameters dist = model.getDistributionParameters(reg);

			// Create a normal distribution for charting
			NormalDistribution nd = new NormalDistribution(dist.getMean(), dist.getStandardDeviation());

			// Update the bhattarcharya plot with the regression line
			HistogramBin bin = model.getHistogram().getElements().get(model.getStartIndex().get());
			bhattacharyaChart.getData().get(1).getData()
					.add(new XYChart.Data<Number, Number>(bin.getBinMidPoint(), reg.predict(bin.getBinMidPoint())));
			bin = model.getHistogram().getElements().get(model.getEndIndex().get());
			bhattacharyaChart.getData().get(1).getData()
					.add(new XYChart.Data<Number, Number>(bin.getBinMidPoint(), reg.predict(bin.getBinMidPoint())));

			// Update the histogram chart with the normal distribution plot
			HistogramBin b = model.getHistogram().getBinContaining(dist.getMean());
			if (b != null) {
				int n = b.getCount();
				double lowerLimit = model.getHistogram().getElements().get(0).getBinValue();
				double minus3sd = dist.getMean() - (3 * dist.getStandardDeviation());
				lowerLimit = minus3sd < lowerLimit ? lowerLimit : minus3sd;
				HistogramBin topElement = model.getHistogram().getElements()
						.get(model.getHistogram().getElements().size() - 1);
				double upperLimit = topElement.getBinValue() + topElement.getBinWidth();
				double plus3sd = dist.getMean() + (3 * dist.getStandardDeviation());
				upperLimit = plus3sd > upperLimit ? upperLimit : plus3sd;
				int dataSize = 100;
				double increment = (upperLimit - lowerLimit) / dataSize;
				double xVal, yVal;
				int total = (int) (n / nd.density(dist.getMean()));
				for (int i = 0; i < dataSize; i++) {
					xVal = lowerLimit + (i * increment);
					yVal = total * nd.density(xVal);
					histogramChart.getData().get(1).getData().add(new XYChart.Data<Number, Number>(xVal, yVal));
				}
			}

			// Update the residuals chart.
			residualsChart.getData().get(0).getData().clear();
			for (int i = model.getStartIndex().get(); i < model.getEndIndex().get(); i++) {
				b = model.getHistogram().getElements().get(i);
				Double v = Math.log10(b.getBinMidPoint());
				String x = String.valueOf(Precision.round(b.getBinMidPoint(), 2 - v.intValue()));
				residualsChart.getData().get(0).getData()
						.add(new XYChart.Data<String, Number>(x, b.getDeltaLogY() - reg.predict(b.getBinMidPoint())));
			}

			// Update report area
			report.getAnalyte().setName(analyte.getText());
			report.getAnalyte().setUnits(units.getText());
			report.setRegression(reg);
			report.setDistributionParameters(dist);
			reportArea.setText(report.toString());

			// Enable the export menu
			exportPdfMenuItem.setDisable(false);

		} catch (BellviewException | RuntimeException ex) {
			logger.error("Unexpected error during analysis and reporting", ex);
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setContentText("An unexpected error occured. Caused by: " + ex.getMessage());
			a.show();
		}
	}

	public void dataTransformMenuAction(ActionEvent evt) {
		
		try {
			//Load the transform dialog scene and add to a dialog pane
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("transformDialog.fxml"));
			Dialog<ButtonType> transformDlg = new Dialog<ButtonType>();
			transformDlg.setDialogPane(loader.load());
			transformDlg.setTitle(BellviewUtils.getMessage("label.transforms"));
			
			DataTransformDialogController controller = (DataTransformDialogController) loader.getController();
			controller.setDataTransform(model.getDataTransform());
			
			//Open the dialog and wait until OK before updating the histogram
			Optional<ButtonType> buttonType = transformDlg.showAndWait();
			if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
				model.setDataTransform(controller.getDataTransform());
				logger.debug("Setting data transform to {}",model.getDataTransform().getName());
				updateHistogram();
			}
		} catch (IOException ex) {
			String msg = "Unexpected error opening data transform dialog. ";
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText(msg + ex.getMessage());
			logger.error(msg, ex);
			alert.show();
		}
	}

	/**
	 * Write the report to a PDF document
	 * @param e Action Event
	 */
	public void exportPdfMenuAction(ActionEvent e) {
		try {
			report.getAnalyte().setName(analyte.getText());
			report.getAnalyte().setUnits(units.getText());
			if (report.getAnalyte().getName() == null || report.getAnalyte().getName().length() == 0)
				throw new BellviewException("Analyte name is required");
			if (report.getAnalyte().getUnits() == null || report.getAnalyte().getUnits().length() == 0)
				throw new BellviewException("Units must be specified");
			String directoryString = preferences.get(DEFAULT_EXPORT_DIRECTORY, ".");
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setInitialDirectory(new File(directoryString).getParentFile());
			File selectedDir = chooser.showDialog(stage);
			preferences.put(DEFAULT_EXPORT_DIRECTORY, selectedDir.getAbsolutePath());
			StringBuilder filename = new StringBuilder();
			filename.append(report.getAnalyte().getName().toLowerCase()).append("-rpt.pdf");
			File file = new File(selectedDir, filename.toString());
			SnapshotParameters parameters = new SnapshotParameters();
			
			//Write the report to file
			PdfReportWriter writer = new PdfReportWriter(report, model, histogramChart.snapshot(parameters, null),
					bhattacharyaChart.snapshot(parameters, null), residualsChart.snapshot(parameters, null));
			logger.debug("Writing to {}", file.getAbsolutePath());
			writer.writeReport(file);
			
			//Display dialog with location of saved file
			Alert notif = new Alert(Alert.AlertType.INFORMATION);
			notif.setContentText("File saved as " + file.getAbsolutePath());
			notif.show();
		} catch (BellviewException ex) {
			logger.error("Error writing PDF", ex);
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setContentText("Unable to export PDF.\n" + ex.getMessage());
			a.show();
		}
	}

	/**
	 * Open the filter dialog
	 * @param evt Action Event
	 */
	public void filterDataMenuAction(ActionEvent evt) {
		try {
			//Load and display the dialog
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("filterDialog.fxml"));
			Dialog<ButtonType> dlg = new Dialog<ButtonType>();
			dlg.setDialogPane(loader.load());
			dlg.setTitle(BellviewUtils.getMessage("label.filters"));
			FilterDialogController controller = (FilterDialogController) loader.getController();
			controller.setModel(model);
			Optional<ButtonType> buttonType = dlg.showAndWait();
			if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
				
				//filter data based on criteria provided in the dialog
				controller.filterData();
				
				//Update status label
				StringBuilder sb = new StringBuilder("Record count ");
				sb.append(controller.getRecordCount());
				sb.append(" [");
				sb.append(model.getFilter().toString());
				sb.append("]");
				this.statusLabel.setText(sb.toString());
			}
			
			//build the histogram using the filtered data
			buildHistogram();
		} catch (IOException ex) {
			String msg = "Unexpected error opening data filter dialog. ";
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText(msg + ex.getMessage());
			logger.error(msg, ex);
			alert.show();
		}
	}

	public void importDataAction(ActionEvent e) {
		try {
			//Open a file chooser and select the file containing user data
			String directoryString = preferences.get(DEFAULT_FILE_IMPORT_DIRECTORY, ".");
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(directoryString));
			fileChooser.setTitle("Open Resource File");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.csv", "*.txt"),
					new ExtensionFilter("Excel Files", "*.xlsx"));
			File selectedFile = fileChooser.showOpenDialog(stage);
			model.getData().clear();
			model.getHistogram().clear();
			if (selectedFile != null) {
				logger.debug("Selected file: " + selectedFile.getAbsolutePath());
				preferences.put(DEFAULT_FILE_IMPORT_DIRECTORY, selectedFile.getParentFile().getAbsolutePath());
				
				//load and display the file import dialog
				Dialog<ButtonType> fileImportDlg = new Dialog<ButtonType>();
				
				//There must be a better way of doing this. At the moment use the AbstractFileImporter and
				//AbstraceImportControllers as base classes so that the same event handlers can be used and
				//the load sample data method called for either type of file importer.
				AbstractFileImporter fileImporter;
				AbstractImportController importController;
				if (selectedFile.getName().endsWith("xlsx")) {
					
					//create an excel importer if the file type is xlsx
					XSSFFileImporter excelImporter = new XSSFFileImporter();
					excelImporter.setFile(selectedFile);
					logger.debug("Using excel importer");
					
					//load and display the excel file import dialog
					final FXMLLoader loader = new FXMLLoader(getClass().getResource("excelImportDialog.fxml"));
					fileImportDlg.setDialogPane(loader.load());
					ExcelImportController eic = (ExcelImportController) loader.getController();
					eic.setFileImporter(excelImporter);
					//load the sheet names
					eic.configure();
					fileImporter = excelImporter;
					importController = eic;
				} else {
					
					//otherwise use a csv file importer
					CSVFileImporter csvImporter = new CSVFileImporter();
					csvImporter.setFile(selectedFile);
					logger.debug("Using delimited text importer");
					
					//load and display the CSV file import dialog
					final FXMLLoader loader = new FXMLLoader(getClass().getResource("fileImportDialog.fxml"));
					fileImportDlg.setDialogPane(loader.load());
					FileImportController fic = (FileImportController) loader.getController();
					fic.setFileImporter(csvImporter);
					fileImporter = csvImporter;
					importController = fic;
				}
				
				progressBar.visibleProperty().bind(fileImporter.runningProperty());
				fileImporter.addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, new EventHandler<WorkerStateEvent>() {

					@Override
					public void handle(WorkerStateEvent event) {
						statusLabel.setText(BellviewUtils.getMessage("msg.importtask.started"));
						//progressBar.setVisible(true);
						logger.debug("Begin importing from CSV");
					}
				});
				fileImporter.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, new EventHandler<WorkerStateEvent>() {

					@Override
					public void handle(WorkerStateEvent event) {
						statusLabel.setText("Loading data...");
						
					}});
				fileImporter.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {

					@Override
					public void handle(WorkerStateEvent event) {
						Long numberOfRowsImported = fileImporter.getNumberOfRowsImported();
						Object[] args = {numberOfRowsImported, fileImporter.getImportErrors().size()};
						String msg = MessageFormat.format(BellviewUtils.getMessage("msg.importtask.finished"), args);
						processData(fileImporter);
						//progressBar.setVisible(false);
						statusLabel.setText(msg);
						
					}});
				importController.loadSampleData();

				Optional<ButtonType> btn = fileImportDlg.showAndWait();
				if (btn.isPresent()) {
					// logger.debug("Button selected: "+btn.get());
					if (ButtonType.CANCEL == btn.get()) {
						fileImportDlg.close();
					} else if (ButtonType.OK == btn.get()) {
						this.analyte.setText(fileImporter.getValueColumnName());
						Thread th = new Thread(fileImporter);
				        th.setDaemon(true);
				        th.start();
					}
				}
				exportPdfMenuItem.setDisable(true);
			} else {
				logger.debug("No file selected");
			}
		} catch (IOException | BellviewException ex) {
			logger.error("Error while loading file import dialog", ex);
		}
	}

	/**
	 * Display the About Dialog
	 * @param evt Action Event
	 */
	public void aboutMenuAction(ActionEvent evt) {
		StringBuilder msg = new StringBuilder();
		msg.append(BellviewUtils.getMessage("label.version")).append(": ").append(BellviewUtils.getMessage("application.version")).append(" ");
		msg.append(BellviewUtils.getMessage("label.build")).append(": ").append(BellviewUtils.getMessage("application.build.time")).append("\n");
		msg.append(BellviewUtils.getMessage("label.javafx.version")).append(": ").append(System.getProperty("javafx.runtime.version")).append("\n");
		msg.append(BellviewUtils.getMessage("label.java.version")).append(": ").append(System.getProperty("java.version")).append("\n\n");
		msg.append(BellviewUtils.getMessage("label.author")).append(": ").append(BellviewUtils.getMessage("application.author"));
		Alert about = new Alert(Alert.AlertType.NONE, msg.toString(), ButtonType.OK);
		about.setTitle(BellviewUtils.getMessage("label.about"));
		about.show();
	}

	/**
	 * Set the javafx stage. This must be set soon after the application is created.
	 * @param stage JavaFX stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Action to exit the application
	 * @param evt Action Event
	 */
	public void quit(ActionEvent evt) {
		logger.info(BellviewUtils.getMessage("msg.application.closed"));
		Platform.exit();
	}
}
