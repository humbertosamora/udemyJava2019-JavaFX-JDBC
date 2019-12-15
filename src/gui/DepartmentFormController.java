package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> listeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;
	
	@FXML
	private Label lbErrorName;
	
	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}
	
	public void txtNameRequestFocus() {
		txtName.requestFocus();
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {

		try {
			
			if (entity == null) throw new IllegalStateException("Entity wal null!");
			
			if (service == null) throw new IllegalStateException("Service wal null!");
			
			entity = getFormData();
			service.saveOrupdate(entity);
			
			listeners.forEach(DataChangeListener::onDataChange);
			
			Utils.currentStage(event).close();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErros());
		}
		catch(Exception e) {
			Alerts.showAlert("Department save error!", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 35);
		lbErrorName.setText("");
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("DepartmentFormController: Entity wal null!");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private Department getFormData() {
		Department obj = new Department();
		
		ValidationException e = new ValidationException("Validation error.");
		
		obj.setId(Utils.parseToInt(txtId.getText()));
				
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			e.addError("name", "Field can't be empty.");
		} else {
			obj.setName(txtName.getText().trim().replaceAll("\\s+", " ").toUpperCase());
		}
		
		if (e.getErros().size() > 0) throw e;
		
		return obj;
	}
	
	private void setErrorMessages(Map<String, String> erros) {
		lbErrorName.setText( erros.get("name") == null ? "" :  erros.get("name")); 
	}
}
