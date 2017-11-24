package com.sonar.view;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class SonarMainClass extends Application{

	public static void main(String[] args) {
		Application.launch(args);		 

	}

	@Override
	public void start(Stage primaryStage) throws Exception {	
		TabPane  pane = (TabPane)FXMLLoader.load(SonarMainClass.class.getResource("/Sonar.fxml"));		
		primaryStage.setScene(new Scene(pane));		
		primaryStage.setTitle("Sonar BITO Trading App");
		primaryStage.show();
		
	}
}
