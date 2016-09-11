package de.mrtroble;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class MButton extends Button{

	public MButton(String s) {
		super(s);
		this.setFont(MainAWP.FONT);
		this.setBackground(BackgroundUtil.getColered(Color.WHITESMOKE, 15, 0));
	}
	
}
