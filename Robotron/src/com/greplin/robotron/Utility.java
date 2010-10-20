package com.greplin.robotron;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;


public class Utility {
	private Utility() {
		throw new IllegalStateException("Non-instantiable class");
	}

	public interface UnaryFunction<A, B> {
		B run(A a);
	}

	public static<A,B> ArrayList<B> map(List<A> data, UnaryFunction<A,B> mapper) {
		ArrayList<B> res = new ArrayList<B>(data.size());
		for(A a : data) {
			res.add(mapper.run(a));
		}
		return res;
	}

	public static void wrappedSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RobotronException("Sleep interrupted");
		}
	}
	
	public static void setClipboardContents(String str) {
		StringSelection ss = new StringSelection(str); 
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); 	
	}
	
	public static String getClipboardContents() { 
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null); 
		String res = "";
		try { 
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) { 
				res = (String)t.getTransferData(DataFlavor.stringFlavor); 
			} 
		} catch (Exception e) { 
			throw new RobotronException("I can't read from the clipboard!");
		}
		return res;
	}
}
