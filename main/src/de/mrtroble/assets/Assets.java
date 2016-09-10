package de.mrtroble.assets;

import java.io.InputStream;
import java.net.URL;

public class Assets {

	public static InputStream getResourceIO(String st) {
		return Assets.class.getResourceAsStream(st);
	}
	
	public static URL getResource(String st) {
		return Assets.class.getResource(st);
	}
	
}
