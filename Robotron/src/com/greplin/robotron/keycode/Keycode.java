package com.greplin.robotron.keycode;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import com.greplin.robotron.Utility;

public abstract class Keycode {
	public static final Utility.UnaryFunction<Character, ? extends Keycode> charToCodeUnary =
		new Utility.UnaryFunction<Character, Keycode>() {
		@Override
		public Keycode run(Character a) {
			return Keycode.charToCode(a);
		}
	};

	public abstract int getMain();
	public abstract Integer getModifier();
	
	@Override
	public String toString() {
		return "Keycode [getMain()=" + getMain() + ", getModifier()="
				+ getModifier() + "]";
	}
	public static List<? extends Keycode> stringToKeycodeList(String s) {
		List<Character> chars = new LinkedList<Character>();
		for(char c : s.toCharArray()) {
			chars.add(c);
		}
		return charListToKeycodeList(chars);
	}
	public static List<? extends Keycode> charListToKeycodeList(List<Character> chars) {
		return Utility.map(chars, charToCodeUnary);
	}
	public static Keycode charToCode(char c) {
		boolean shift = true;
		int keyCode;
	
		switch (c) {
		case '"':
			keyCode = KeyEvent.VK_QUOTE;
			break;
		case '\'':
			keyCode = KeyEvent.VK_QUOTE;
			shift = false;
			break;
		case '~':
			keyCode = (int)'`';
			break;
		case '!':
			keyCode = (int)'1';
			break;
		case '@':
			keyCode = (int)'2';
			break;
		case '#':
			keyCode = (int)'3';
			break;
		case '$':
			keyCode = (int)'4';
			break;
		case '%':
			keyCode = (int)'5';
			break;
		case '^':
			keyCode = (int)'6';
			break;
		case '&':
			keyCode = (int)'7';
			break;
		case '*':
			keyCode = (int)'8';
			break;
		case '(':
			keyCode = (int)'9';
			break;
		case ')':
			keyCode = (int)'0';
			break;
		case ':':
			keyCode = (int)';';
			break;
		case '_':
			keyCode = (int)'-';
			break;
		case '+':
			keyCode = (int)'=';
			break;
		case '|':
			keyCode = (int)'\\';
			break;
		case '?':
			keyCode = (int)'/';
			break;
		case '{':
			keyCode = (int)'[';
			break;
		case '}':
			keyCode = (int)']';
			break;
		case '<':
			keyCode = (int)',';
			break;
		case '>':
			keyCode = (int)'.';
			break;
		default:
			if(Character.isUpperCase(c)) {
				shift = true;
				keyCode = (int)c;
			}
			else {
				shift = false;
				keyCode = (int)Character.toUpperCase(c);
			}
		}
		if (shift)
			return new ModifiedKeycode(keyCode, KeyEvent.VK_SHIFT);
		else
			return new SimpleKeycode(keyCode);
	}
}
