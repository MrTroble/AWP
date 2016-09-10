package de.mrtroble;

import de.mrtroble.assets.Assets;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;

public class MainAWP extends Application{
	
	public static final Pane root = new Pane();
	public static final Font FONT = Font.loadFont(Assets.getResourceIO("CALIBRIB.TTF"), 30);
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage st) throws Exception {
		Scene sc = new Scene(root,1200,800);
		st.setScene(sc);
		st.initStyle(StageStyle.UNDECORATED);
				
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(15);
		pane.setVgap(15);
		
		pane.add(new MLabel("Datenbank"), 0, 0);
		
		MTextField url = new MTextField("URL");
		pane.add(url, 0,1);
		
		MTextField username = new MTextField("Username");
		pane.add(username, 0,2);
		
		MPassword pw = new MPassword();
		pane.add(pw, 0,3);
		
		MButton con = new MButton("Connect"); 
		con.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(!username.getText().isEmpty() && !url.getText().isEmpty() && !pw.getText().isEmpty()){
					initConnectionPhase(st, sc);
				}
			}
		});
		pane.add(con, 0,4);
		
		StackPane pns = new StackPane(pane);
		pns.setAlignment(Pos.CENTER);
		pns.setBackground(BackgroundUtil.getColered(Color.DARKGRAY, 0, 0));
		pns.setPrefSize(sc.getWidth(), sc.getHeight());
		
		root.getChildren().add(pns);
		
		st.show();
	}
	
	private static void initConnectionPhase(Stage st,Scene sc){
		root.getChildren().clear();
		Scene s = new Scene(root,sc.getWidth(),sc.getHeight());
		s.setFill(Color.DARKGRAY);
		st.setScene(s);
	}
}
