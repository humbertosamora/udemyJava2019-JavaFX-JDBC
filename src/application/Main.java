package application;

import java.io.IOException;

import db.DB;
import db.DBException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		try {
			DB.getConnection("coursejdbc.properties");
		}
		catch(DBException e){
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void stop() throws Exception {
		try {
			DB.closeConnection();
		}
		catch(DBException e){
			System.out.println(e.getMessage());
		}
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
	
	public static void setMainScene(Scene mainScene) {
		Main.mainScene = mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
