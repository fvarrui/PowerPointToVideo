package fvarrui.pptx2video.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;

import fvarrui.pptx2video.utils.DesktopUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController implements Initializable {
	
	private StringProperty input = new SimpleStringProperty();
	private StringProperty output = new SimpleStringProperty();
	private BooleanProperty generatingVideo = new SimpleBooleanProperty(false);
	
	private FileChooser inputDialog, outputDialog;
	
	private ConvertionTask task = null;
	
    @FXML
    private StackPane root;

    @FXML
    private BorderPane generatePane;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button chooseInputButton;

    @FXML
    private TextField outputTextField;

    @FXML
    private Button chooseOutputButton;

    @FXML
    private Button generateButton;

    @FXML
    private GridPane loadingPane;

    @FXML
    private Label progressLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button cancelButton;
	
	public MainController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		File userHome = new File(System.getProperty("user.home"));
		
		inputDialog = new FileChooser();
		inputDialog.setInitialDirectory(userHome);
		inputDialog.getExtensionFilters().add(new ExtensionFilter("PowerPoint", "*.pptx"));
		inputDialog.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
 		
		outputDialog = new FileChooser();
		outputDialog.setInitialDirectory(userHome);
		outputDialog.getExtensionFilters().add(new ExtensionFilter("MP4", "*.mp4"));
		outputDialog.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
		
		inputTextField.textProperty().bindBidirectional(input);
		outputTextField.textProperty().bindBidirectional(output);
		
		generateButton.disableProperty().bind(input.isEmpty().or(output.isEmpty()));
		
		cancelButton.textProperty().bind(Bindings.when(generatingVideo).then("Cancel").otherwise("Close"));
		
		generatePane.disableProperty().bind(loadingPane.visibleProperty());
		
		loadingPane.setVisible(false);
	}

	public Parent getRoot() {
		return root;
	}
	
    @FXML
    void onBalabolkaLinkAction(ActionEvent event) {
    	DesktopUtils.browse("http://www.cross-plus-a.com/balabolka.htm");
    }

    @FXML
    void onChooseInputAction(ActionEvent event) {
    	if (input.isNotEmpty().get()) {
    		File currentFile = new File(input.get());
    		inputDialog.setInitialDirectory(currentFile.getParentFile());
    		inputDialog.setInitialFileName(currentFile.getName());
    	}
     	File inputFile = inputDialog.showOpenDialog(MainApp.primaryStage);
    	if (inputFile != null) {
    		input.set(inputFile.getAbsolutePath());
			if (output.isEmpty().get()) {
				File outputFile = new File(inputFile.getParentFile(), FilenameUtils.removeExtension(inputFile.getName()) + ".mp4");
				output.set(outputFile.getAbsolutePath());
			}
    	}
    }

    @FXML
    void onChooseOutputAction(ActionEvent event) {
    	if (output.isNotEmpty().get()) {
    		File currentFile = new File(output.get());
    		outputDialog.setInitialDirectory(currentFile.getParentFile());
    		outputDialog.setInitialFileName(currentFile.getName());
    	}
    	File outputFile = outputDialog.showSaveDialog(MainApp.primaryStage);
    	if (outputFile != null) {
    		output.set(outputFile.getAbsolutePath());
    	}
    }

    @FXML
    void onDeveloperLinkAction(ActionEvent event) {
    	DesktopUtils.browse("https://github.com/fvarrui");
    }

    @FXML
    void onFfmpegLinkAction(ActionEvent event) {
    	DesktopUtils.browse("https://ffmpeg.org/");
    }

    @FXML
    void onGenerateVideoAction(ActionEvent event) {

    	File inputFile = new File(input.get());
    	File outputFile = new File(output.get());
    	
    	// check if input file (pptx) exists
    	if (!inputFile.exists()) {
    		MainApp.error("Input file " + inputFile.getName() + " doesn't exist.");
    		return;
    	}
    	
    	// check if output file exists and is a directory
    	if (outputFile.exists() && outputFile.isDirectory()) {
    		MainApp.error("Output file " + inputFile.getName() + " is a directory.");
    		return;
    	}
    	
    	// check if output file (mp4) exists, and if so, ask if it must be overwritten
    	if (outputFile.exists() && !MainApp.confirm(
    			"Output file " + outputFile.getName() + " exists.", 
    			"Do you want to ovewrite it?"
    			)) {
    		return;
    	}
    	
    	task = new ConvertionTask(inputFile, outputFile);
    	task.setOnFailed(e -> {
    		MainApp.error("Video generation failed", e.getSource().getException());
    		loadingPane.setVisible(false);
    	});
    	task.setOnScheduled(e -> {
    		loadingPane.setVisible(true);
    	});
    	
    	progressLabel.textProperty().bind(task.messageProperty());
    	
    	progressBar.progressProperty().bind(task.progressProperty());
    	
    	generatingVideo.bind(task.runningProperty());

    	new Thread(task).start();
    	
    }
    
    @FXML
    void onCancelAction(ActionEvent event) {
    	
    	if (task.isRunning())
    		task.cancel();
    	
    	loadingPane.setVisible(false);
    	
    }


}
