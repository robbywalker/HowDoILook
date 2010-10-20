package com.greplin.robotron.keycode;


public class ModifiedKeycode extends Keycode {
	private final int main;
	private final int modifier;
	
	public ModifiedKeycode(int m, int mod) {
		this.main = m;
		this.modifier = mod;
	}
	
	@Override
	public int getMain() {
		return this.main;
	}

	@Override
	public Integer getModifier() {
		return this.modifier;
	}
}
