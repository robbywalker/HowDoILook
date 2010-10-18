package com.greplin.robotron;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import com.greplin.robotron.Keycode;
import com.greplin.robotron.SimpleKeycode;
import com.greplin.robotron.ModifiedKeycode;


public class Utility {
	private Utility() {
		throw new IllegalStateException("Bad coder!");
	}

	public static<A,B> List<B> map(List<A> data, UnaryFunction<A,B> mapper) {
		List<B> res = new LinkedList<B>();
		for(A a : data) {
			res.add(mapper.run(a));
		}
		return res;
	}
	
	public static final UnaryFunction<Character, Keycode> charToCodeUnary =
		new UnaryFunction<Character, Keycode>() {
			@Override
			public Keycode run(Character a) {
				return Utility.charToCode(a);
			}
	};

	public static Keycode charToCode(char c) {
		boolean shift = true;
		int keyCode;

		switch (c) {
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
			keyCode = (int)c;
			shift = false;
		}
		if (shift)
			return new ModifiedKeycode(keyCode, KeyEvent.VK_SHIFT);
		else
			return new SimpleKeycode(keyCode);
	}
	
	public static List<Keycode> charListToIntList(List<Character> chars) {
		return Utility.map(chars, charToCodeUnary);
	}

	public static List<Keycode> stringToIntList(String s) {
		s = s.toUpperCase();
		List<Character> chars = new LinkedList<Character>();
		for(char c : s.toCharArray()) {
			chars.add(c);
		}
		return Utility.charListToIntList(chars);
	}

	public static void wrappedSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RobotronException("Sleep interrupted");
		}
	}

}
