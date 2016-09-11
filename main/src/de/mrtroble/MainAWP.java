package de.mrtroble;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.BiConsumer;

import de.mrtroble.assets.Assets;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.*;
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
		sc.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ESCAPE)){
					System.exit(0);
				}
			}
		});
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
	
	public static HashMap<String,ArrayList<Value>> tabels = new HashMap<String,ArrayList<Value>>();
	public static int igt = 0;

	private void initConnectionPhase(Stage st,Scene sc,String url,String name,String pw){
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
		sc.setRoot(new Group());
		root.getChildren().clear();
		Scene s = new Scene(root,sc.getWidth(),sc.getHeight());
		s.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ESCAPE)){
					System.exit(0);
				}
			}
		});
		s.setFill(Color.DARKGRAY);
		st.setScene(s);
		
		StackPane std = new StackPane();
		std.setAlignment(Pos.CENTER);
		std.setBackground(BackgroundUtil.getColered(Color.DARKGRAY, 0, 0));
		std.setPrefSize(sc.getWidth(), sc.getHeight());
		
		GridPane pn = new GridPane();
		pn.setHgap(15);
		pn.setVgap(15);
		pn.setAlignment(Pos.TOP_CENTER);
		pn.setPrefSize(500, 500);
				
		ScrollPane scr = new ScrollPane(pn);
		scr.setStyle("-fx-background-color: darkgray;");
		scr.setHbarPolicy(ScrollBarPolicy.NEVER);
		scr.setPrefSize(500, 500);
		scr.setMaxSize(scr.getPrefWidth(), scr.getPrefHeight());
		std.getChildren().add(scr);
		
		AsyncMysql cn = connectToServices(url,name,pw);
		cn.query("SELECT * FROM data;", rs -> {
			try {
				while (rs.next()) {
					String[] str = rs.getString("data").split("\n");
				    for(String stri : str){
					if(!stri.contains("=>"))continue;
					String[] sr = stri.split("=>");
					String nam = sr[0].replace("[", "/~").split("/~")[1].replace("]", "~/").split("~/")[0];
					String val = sr[1];
					if(val.startsWith(" ")){
							val = val.replaceFirst(" ", "").replace("	", "").replace("\n", "");
					}
					while(val.endsWith(" ")){
							val = val.substring(0, val.length() - 1);
					}
					ArrayList<Value> tab;
					if(!tabels.containsKey(nam)){
							tabels.put(nam, new ArrayList<Value>());
					}
					tab = tabels.get(nam);
					Iterator<Value> itar = tab.iterator();
					boolean b = false;
					while(itar.hasNext()){
							Value vl = itar.next();
							if(vl.name.equals(val)){
								vl.setCount(vl.getCount() + 1);
							    b = true;
								break;
							}
					}
					if(!b){
						   tab.add(new Value(val));
					}
					}
				}
				tabels.forEach(new BiConsumer<String, ArrayList<Value>>() {

					@Override
					public void accept(String t, ArrayList<Value> u) {
						u.sort(new Comparator<Value>() {

							@Override
							public int compare(Value v1, Value v2) {
								if(v1.getCount() > v2.getCount())return -1;
								if(v1.getCount() < v2.getCount())return 1;
								return 0;
							}
						});
						Platform.runLater(new Runnable() {
							
							public void run() {
								MButton btn = new MButton(t);
								btn.setOnAction(new EventHandler<ActionEvent>() {
									
									@Override
									public void handle(ActionEvent event) {
										dialog(t, u,sc);
									}
								});
								pn.add(btn, 0, igt);
								igt++;
						    }
						});
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
			});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});		  
		root.getChildren().add(std);
	}
	private static int ic = 0;
	private static void dialog(String nam,ArrayList<Value> val,Scene sc){
		Stage st = new Stage(StageStyle.UNDECORATED);
		st.setResizable(false);
		StackPane pn = new StackPane();
		pn.setAlignment(Pos.TOP_CENTER);
		pn.setPrefSize(sc.getWidth(), sc.getHeight());
		Scene sce = new Scene(pn,sc.getWidth(),sc.getHeight());
		sce.getStylesheets().add(Assets.getResource("main.css").toExternalForm());
		st.setScene(sce);
		
		sce.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ESCAPE)){
					st.close();
				}
			}
		});
		
		GridPane gpn = new GridPane();
		gpn.setHgap(15);
		gpn.setVgap(15);
		
		GridPane list = new GridPane();
		list.setHgap(15);
		list.setVgap(15);
		
		ScrollPane scro = new ScrollPane(gpn);
		
		ScrollPane lisP = new ScrollPane(list);
		lisP.setStyle("-fx-border-radius: 0;");
		lisP.setMaxWidth(900);
		lisP.setMinWidth(900);
		lisP.setVbarPolicy(ScrollBarPolicy.NEVER);
		
		GridPane co = new GridPane();
		co.setHgap(15);
		co.setVgap(15);
		co.setMaxWidth(100);
		
		gpn.add(lisP, 0, 0);
		gpn.add(co, 1, 0);
		
		scro.setPrefSize(1000, 800);
		scro.setMaxSize(scro.getPrefWidth(), scro.getPrefHeight());
		scro.setHbarPolicy(ScrollBarPolicy.NEVER);
		ic = 0;
		list.add(new MLabel("Value"), 0, 0);
		co.add(new MLabel("Value"), 0, 0);
		ic++;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Iterator<Value> vl = val.iterator();
				while(vl.hasNext()){
					Value vls = vl.next();
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						EventHandler<MouseEvent> evt = new EventHandler<MouseEvent>() {
							
							@Override
							public void handle(MouseEvent event) {
								if(event.getButton().equals(MouseButton.PRIMARY)){
									Alert al = new Alert(AlertType.INFORMATION);
									al.setTitle("Value view");
									al.setHeaderText(vls.name.replace(";", "\n"));
									al.setContentText("Found " + vls.getCount() + " times in data");
									al.showAndWait();
								}
							}
						};
						MLabel lab = new MLabel(vls.name);
						lab.setOnMouseClicked(evt);
						MLabel cont = new MLabel("" + vls.getCount());
						cont.setOnMouseClicked(evt);
						list.add(lab, 0, ic);
						co.add(cont, 0, ic);
						ic++;
					}
				});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
			}
		}).start();
		
		pn.getChildren().add(scro);
		st.show();
	}
	
	private static AsyncMysql connectToServices(String url, String name, String pw) {	 
		 return new AsyncMysql(url, 3306, "analytics", pw, name);
	}
}
