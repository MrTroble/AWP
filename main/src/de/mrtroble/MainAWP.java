package de.mrtroble;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

public class MainAWP extends Application{
	
	public static final Group root = new Group();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage st) throws Exception {
		Scene sc = new Scene(root);
		st.setScene(sc);
		sc.setFill(Color.GRAY);
		
		st.initStyle(StageStyle.UNDECORATED);
		
		st.show();
	}
	
}
