package com.greplin.robotron;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.greplin.robotron.keycode.Keycode;
import com.greplin.robotron.keycode.ModifiedKeycode;

public class Chrome extends Browser {
	private static final String APP_NAME = "Google Chrome.app";
	
	@Override
	protected String getAppName() {
		return APP_NAME;
	}

	@Override
	protected Collection<Keycode> getKeysToOpenNewTab() {
		List<Keycode> res = new LinkedList<Keycode>();
		res.add(new ModifiedKeycode(KeyEvent.VK_T, KeyEvent.VK_META));
		return res;
	}

	@Override
	protected Collection<Keycode> getKeysToGoToUrlBar() {
		List<Keycode> res = new LinkedList<Keycode>();
		res.add(new ModifiedKeycode(KeyEvent.VK_L, KeyEvent.VK_META));
		return res;	}	
}
