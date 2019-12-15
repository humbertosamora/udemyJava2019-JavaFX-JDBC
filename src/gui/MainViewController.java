package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSeller() {
		Consumer<SellerListController> initializeAction = c -> {
			if (c != null) {
				c.setSellerService(new SellerService());
				c.updateTableView();
			}
		};
		
		loadView("/gui/SellerList.fxml", initializeAction);
	}
	
	@FXML
	public void onMenuItemDepartment() {
		
		Consumer<DepartmentListController> initializeAction = c -> {
			if (c != null) {
				c.setDepartmentService(new DepartmentService());
				c.updateTableView();
			}
		};
		
		loadView("/gui/DepartmentList.fxml", initializeAction);
	}
	
	@FXML
	public void onMenuItemAbout() {
		loadView("/gui/About.fxml", null);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	public synchronized <T> void loadView(String url, Consumer<T> initializeAction) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
			Node node = loader.load();
			
			if (initializeAction != null) {
				T controller = loader.getController();
				initializeAction.accept(controller);
			}
			
			// Retrieves mainBox node
			VBox mainBox = (VBox) ((ScrollPane) Main.getMainScene().getRoot()).getContent();
			
			// Preserves menuItem from mainBox
			Node menuItem = mainBox.getChildren().get(0);
			
			// Removes all items from menuBox.
			mainBox.getChildren().clear();
			
			// Re-add menuItem and add "node" item
			mainBox.getChildren().add(menuItem);
			mainBox.getChildren().add(node);
		}
		catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view!", e.getMessage(), AlertType.ERROR);
		}
	}
	
}
