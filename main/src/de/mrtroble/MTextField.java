package de.mrtroble;

import javafx.scene.control.TextField;

public class MTextField extends TextField{

	public MTextField(String s) {
		this.setPromptText(s);
		this.setFont(MainAWP.FONT);
	}
	
}
