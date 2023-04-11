package dwc.bellview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dwc.bellview.transform.DataTransform;
import dwc.bellview.transform.LogTransform;
import dwc.bellview.transform.NoneTransform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;

public class DataTransformDialogController {
	
	private static final Logger LOG = LoggerFactory.getLogger(DataTransformDialogController.class);
	
	@FXML
	private RadioButton rbNone;
	
	@FXML
	private RadioButton rbLog;
	
	@FXML
	private TextField constant;
	
	private DataTransform dataTransform;
	
	public DataTransformDialogController() {
		
	}
	
	/**
	 * Post constructor initialization
	 */
	@FXML
	public void initialize() {
		rbNone.setUserData("none");
		rbLog.setUserData("log");
		
		//Limit text to numbers
		constant.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					if (rbLog.isSelected() && constant.getText()!=null && constant.getText().length()>0) {
                    	dataTransform = new LogTransform(Double.valueOf(constant.getText()));
                    	LOG.debug("Log transform selected with constant = {}", constant.getText());
                    }
				} catch (NumberFormatException nfe) {
					Alert err = new Alert(Alert.AlertType.ERROR);
                	err.setContentText("Constant must be a number only");
                	err.show();
				}

			}});
		constant.setText("0.0");
		rbNone.getToggleGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue.getUserData().equals("log")) {
					if (constant.getText()!= null && constant.getText().length()>0) {
						dataTransform = new LogTransform(Double.valueOf(constant.getText()));
						LOG.debug("Log transform selected with constant = {}", constant.getText());
					} else {
						dataTransform = new LogTransform();
						LOG.debug("Log transform selected with constant = 0");
					}
				} else {
					dataTransform = new NoneTransform();
					LOG.debug("None transform selected");
				}
				
			}});
	}
	
	public void setDataTransform(DataTransform transform) {
		if (transform.getName().equals(DataTransform.NO_TRANSFORM)) {
			rbNone.setSelected(true);
			LOG.debug("none data transform");
		} else if (transform.getName().equals(DataTransform.LOG_TRANSFORM)) {
			LogTransform lt = (LogTransform) transform;
			constant.setText(lt.getConstant().toString());
			rbLog.setSelected(true);
			LOG.debug("log data transform with c={}",constant.getText());
		} else {
			rbNone.setSelected(true);
			transform = new NoneTransform();
		}
		this.dataTransform = transform;
	}
	
	public DataTransform getDataTransform() {
		return dataTransform;
	}
	
	

}
