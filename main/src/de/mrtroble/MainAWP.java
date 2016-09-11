package de.mrtroble;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Scanner;

import de.mrtroble.assets.Assets;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;

public class MainAWP extends Application{
	
	public static final Pane root = new Pane();
	public static final Font FONT = Font.loadFont(Assets.getResourceIO("CALIBRIB.TTF"), 30);
	public static final File FOLDER = new File(System.getProperty("user.dir") + "/awpo"),
			                 OPTIONS = new File(FOLDER.getAbsolutePath() + "/option.txt");
	public static String[] arg = new String[] {"","",""};
	
	public static void main(String[] args) {
		FOLDER.mkdir();
		if(OPTIONS.exists()){
			try {
				Scanner scr = new Scanner(OPTIONS);
				int i = 0;
				while(scr.hasNextLine()){
					if(i >= arg.length)break;
					arg[i] = scr.nextLine();
					i++;
				}
				scr.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.setProperty("http.agent", "Chrome");
		CookieHandler.setDefault(new CookieManager());
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
		url.setText(arg[0]);
		pane.add(url, 0,1);
		
		MTextField username = new MTextField("Username");
		username.setText(arg[1]);
		pane.add(username, 0,2);
		
		MPassword pw = new MPassword();
		pw.setText(arg[2]);
		pane.add(pw, 0,3);
		
		MButton con = new MButton("Connect"); 
		con.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(!username.getText().isEmpty() && !url.getText().isEmpty() && !pw.getText().isEmpty()){
						initConnectionPhase(st, sc, url.getText(),username.getText(),pw.getText());
				}else{
					Alert al = new Alert(AlertType.ERROR);
					al.initStyle(StageStyle.UNDECORATED);
					al.setTitle("ERROR");
					al.setHeaderText("Couldn't connect");
					String st = (username.getText().isEmpty() ? "Username needed\n":"") + (url.getText().isEmpty() ? "URL needed\n":"") + (pw.getText().isEmpty() ? "Password needed\n":"");
					al.setContentText(st);
					al.showAndWait();
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
	
	private static void initConnectionPhase(Stage st,Scene sc,String url,String name,String pw){
		OPTIONS.delete();
		try {
			OPTIONS.createNewFile();
			FileWriter wr = new FileWriter(OPTIONS);
			wr.write(url + String.format("%n") + name + String.format("%n") + pw);
			wr.flush();
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Connection cn = connectToServices(url,name,pw);
		sc.setRoot(new Group());
		root.getChildren().clear();
		Scene s = new Scene(root,sc.getWidth(),sc.getHeight());
		s.setFill(Color.DARKGRAY);
		st.setScene(s);
	}
	
	private static Connection connectToServices(String url, String name, String pw) {	 
		Connection con = null;
		System.out.println(url);
	    AsyncMysql mysql = new AsyncMysql(url, 3306, "analytics", pw, name);
		mysql.query("SELECT * FROM data WHERE uuid=\"b12598d5-c8ac-4a3d-a301-7ab7b4754864\";", rs -> {
				System.out.println("---");
				try {
					while (rs.next()) {
						System.out.println(rs.getString("data"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}});		  
           return con;
	}
}
