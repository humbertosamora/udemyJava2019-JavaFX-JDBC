package gui;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	
	private DepartmentService service;
	
	private ObservableList<Department> tableList;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private TableColumn<Department, Department> tableColumnEdit;
	
	@FXML
	private TableColumn<Department, Department> tableColumnRemove;
	
	@FXML
	private Button	buttonNew;
	
	@FXML
	public void onButtonNewAction(ActionEvent event) {
		createDialogForm("/gui/DepartmentForm.fxml", Utils.currentStage(event), new Department());
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Scene mainScene = Main.getMainScene();
		tableViewDepartment.prefHeightProperty().bind(mainScene.heightProperty());
		tableViewDepartment.prefWidthProperty().bind(mainScene.widthProperty());
		
		tableColumnEdit.setText("");
		tableColumnEdit.setSortable(false);
		
		tableColumnRemove.setText("");
		tableColumnRemove.setSortable(false);
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("DepartmentListController error: service was null!");
		}
		
		tableList = FXCollections.observableArrayList(service.findAll());
		tableViewDepartment.setItems(tableList);
		
		initEditButtons();
		initRemoveButtons();
	}
	
	public void createDialogForm(String url, Stage parentStage, Department obj) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
			
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.updateFormData();
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			
			Scene dialogScene = new Scene(pane);
			Stage dialogStage = new Stage();
							
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(dialogScene);
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setOnShown(event -> controller.txtNameRequestFocus());
			dialogStage.showAndWait();
			
		} catch (IOException e) {
			Alerts.showAlert("Error loading view", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	@Override
	public void onDataChange() {
		updateTableView();
	}
	
	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm("/gui/DepartmentForm.fxml", Utils.currentStage(event), obj));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	private void removeEntity(Department obj) {
		
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if (result.get() == ButtonType.OK) {
			
			try {
				if (obj == null || obj.getName() == null || obj.getName().length() == 0) {
					throw new InvalidParameterException("Department name invalid!");
				}
				
				if (service == null) throw new IllegalStateException("Service wal null!");
				
				service.remove(obj);
				updateTableView();
			}
			catch(Exception e) {
				Alerts.showAlert("Remove department error!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
}
