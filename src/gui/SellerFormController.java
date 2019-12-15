package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	
	private Seller entity;
	
	private SellerService service;
	
	private DepartmentService departmentService;
	
	private ObservableList<Department> departmentList;
	
	private List<DataChangeListener> listeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> cbDepartment;
	
	@FXML
	private Label lbErrorName;
	
	@FXML
	private Label lbErrorEmail;
	
	@FXML
	private Label lbErrorBirthDate;
	
	@FXML
	private Label lbErrorBaseSalary;
	
	@FXML
	private Label lbErrorDepartment;
	
	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
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
			Alerts.showAlert("Seller save error!", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 60);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Constraints.setTextFieldDouble(txtBaseSalary);
		
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
		
		lbErrorName.setText("");
		lbErrorEmail.setText("");
		lbErrorBaseSalary.setText("");
		lbErrorBirthDate.setText("");
		lbErrorDepartment.setText("");
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("SellerFormController: Entity wal null!");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		
		if (entity.getBirthdate() != null) {
			dpBirthDate.setValue(LocalDateTime.ofInstant(entity.getBirthdate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}
		
		if (entity.getDepartment() != null) {
			cbDepartment.setValue(entity.getDepartment());
		} else {
			cbDepartment.getSelectionModel().selectFirst();
		}
	}
	
	private Seller getFormData() {
		Seller obj  = new Seller();
		
		ValidationException e = new ValidationException("Validation error.");
		
		obj.setId(Utils.parseToInt(txtId.getText()));
				
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			e.addError("name", "Field can't be empty.");
		} else {
			obj.setName(txtName.getText().trim().replaceAll("\\s+", " ").toUpperCase());
		}

		if(txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			e.addError("email", "Field can't be empty.");
		} else {
			obj.setEmail(txtEmail.getText().trim().replaceAll("\\s+", " ").toUpperCase());
		}
		
		if(dpBirthDate.getValue() == null) {
			e.addError("birthdate", "Field can't be empty.");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthdate(Date.from(instant));
		}
		
		if(txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			e.addError("baseSalary", "Field can't be empty.");
		} else {
			obj.setBaseSalary(Utils.parseToDouble(txtBaseSalary.getText()));
		}
		
		if(cbDepartment.getValue() == null) {
			e.addError("department", "Field can't be empty.");
		} else {
			obj.setDepartment(cbDepartment.getValue());
		}
		
		if (e.getErros().size() > 0) throw e;
		
		return obj;
	}
	
	private void setErrorMessages(Map<String, String> erros) {
		lbErrorName.setText(erros.get("name") == null ? "" :  erros.get("name")); 
		lbErrorEmail.setText(erros.get("email") == null ? "" :  erros.get("email"));
		lbErrorBaseSalary.setText(erros.get("baseSalary") == null ? "" :  erros.get("baseSalary"));
		lbErrorBirthDate.setText(erros.get("birthdate") == null ? "" :  erros.get("birthdate"));
		lbErrorDepartment.setText(erros.get("department") == null ? "" :  erros.get("department"));
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cbDepartment.setCellFactory(factory);
		cbDepartment.setButtonCell(factory.call(null));
	}
	
	public void loadDepartments() {
		if (departmentService == null) {
			throw new IllegalStateException("SellerFormController: Service wal null!");
		}
		
		departmentList = FXCollections.observableArrayList(departmentService.findAll());
		cbDepartment.setItems(departmentList);
	}
	
}
