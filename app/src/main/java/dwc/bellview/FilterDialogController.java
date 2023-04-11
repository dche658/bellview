package dwc.bellview;

import java.util.List;

import dwc.bellview.model.BellviewModel;
import dwc.bellview.model.DataElement;
import dwc.bellview.model.Filter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FilterDialogController {
	
	private static final String ANY = "Any";
	
	@FXML
	private ComboBox<String> sexComboBox;
	
	@FXML
	private TextField ageLowTextField;
	
	@FXML
	private TextField ageHighTextField;
	
	private int recordCount;
	
	private BellviewModel model;
	
	public FilterDialogController() {
		
	}
	
	public void filterData() {
		Filter filter = model.getFilter();
		filter.reset();
		String sex = sexComboBox.getSelectionModel().getSelectedItem();
		String v;
		if (sex.equals(ANY)) {
			filter.setSex(null);
		} else {
			filter.setSex(sex);
		}
		try {
			v = ageLowTextField.getText();
			if (v==null || v.length()==0) {
				filter.setAgeLow(null);
			} else {
				Double ageLow = Double.valueOf(ageLowTextField.getText());
				filter.setAgeLow(ageLow);
			}
			v = ageHighTextField.getText();
			if (v==null || v.length()==0) {
				filter.setAgeHigh(null);
			} else {
				Double ageHigh = Double.valueOf(ageHighTextField.getText());
				filter.setAgeHigh(ageHigh);
			}
			List<DataElement> filteredItems =filter.apply(model.getData());
			model.setSubset(filteredItems);
			recordCount = filteredItems.size();
			
			//calculate transformed values in case data was filtered before transform was called.
			model.applyTransform();
		} catch (NumberFormatException nfe) {
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setContentText("Age must be entered as a number");
			a.showAndWait();
		}		
	}

	public BellviewModel getModel() {
		return model;
	}

	public void setModel(BellviewModel model) {
		this.model = model;
		sexComboBox.getItems().clear();
		sexComboBox.getItems().add(ANY);
		sexComboBox.getItems().addAll(model.getGenderList());
		if (model.getFilter().getSex() != null && sexComboBox.getItems().contains(model.getFilter().getSex())) {
			sexComboBox.getSelectionModel().select(model.getFilter().getSex());
		} else {
			sexComboBox.getSelectionModel().select(ANY);
		}
		if (model.getFilter().getAgeLow() != null) {
			this.ageLowTextField.setText(String.valueOf(model.getFilter().getAgeLow()));
		}
		if (model.getFilter().getAgeHigh() != null) {
			this.ageHighTextField.setText(String.valueOf(model.getFilter().getAgeHigh()));
		}
	}
	
	

	public int getRecordCount() {
		return recordCount;
	}
}
