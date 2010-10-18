package com.greplin.robotron;

public class SimpleKeycode implements Keycode {
	private final int main;
	
	public SimpleKeycode(int m) {
		this.main = m;
	}
	
	@Override
	public int getMain() {
		return this.main;
	}

	@Override
	public Integer getModifier() {
		return null;
	}

}
