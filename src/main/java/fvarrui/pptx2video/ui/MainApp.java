package fvarrui.pptx2video.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private MainController mainController;
	
	public static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		MainApp.primaryStage = primaryStage;

		mainController = new MainController();
		
		primaryStage.setTitle("PowerPoint To Video");
		primaryStage.setScene(new Scene(mainController.getRoot()));
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image("/images/video-icon-16x16.png"));
		primaryStage.getIcons().add(new Image("/images/video-icon-24x24.png"));
		primaryStage.getIcons().add(new Image("/images/video-icon-32x32.png"));
		primaryStage.getIcons().add(new Image("/images/video-icon-48x48.png"));
		primaryStage.getIcons().add(new Image("/images/video-icon-64x64.png"));
		primaryStage.show();
		
	}
	
	public static void info(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(primaryStage.getTitle() + " :: Info");
		alert.setHeaderText(message);
		alert.showAndWait();
	}
	
	public static void error(String header, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(primaryStage);
		alert.setTitle(primaryStage.getTitle() + " :: Error");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	public static void error(String header) {
		error(header, "");
	}
	
	public static void error(String header, Throwable e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(primaryStage);
		alert.setTitle(primaryStage.getTitle() + " :: Error");
		alert.setHeaderText(header);
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}
	
	public static boolean confirm(String header, String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(primaryStage.getTitle() + " :: Confirm");
		alert.setHeaderText(header);
		alert.setContentText(message);
		return alert.showAndWait().get().equals(ButtonType.OK);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
