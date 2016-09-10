package de.mrtroble;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

public class BackgroundUtil {
	
	public static Background getColered(Paint p,double corn,double ins){
		return new Background(new BackgroundFill(p, new CornerRadii(corn), new Insets(ins)));
	}

}
