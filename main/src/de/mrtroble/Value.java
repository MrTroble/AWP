package de.mrtroble;

public class Value {
	
	public final String name;
	private int count = 1;

	public Value(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
