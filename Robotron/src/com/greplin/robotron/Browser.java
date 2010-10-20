package com.greplin.robotron;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.imageio.ImageIO;

import com.greplin.robotron.keycode.Keycode;
import com.greplin.robotron.keycode.ModifiedKeycode;
import com.greplin.robotron.keycode.SimpleKeycode;

public abstract class Browser {
	protected abstract String getAppName();
	protected abstract Collection<Keycode> getKeysToOpenNewTab();
	protected abstract Collection<Keycode> getKeysToGoToUrlBar();
	
	protected Process P;
	protected boolean started;
	protected Robot r;
	
	public boolean startedP() {
		return this.started;
	}
	
	public Browser() {
		started = false;
		try {
			this.r = new Robot();
		} catch (AWTException e) {
			throw new RobotronException("Unable to start Robot");
		}
	}

	public void runJs(String s) {
		if(!this.startedP()) {
			throw new RobotronException("You can't go to a url in a browser that isn't started!");
		}
		this.pressKeys(this.getKeysToGoToUrlBar());
		this.pressKeys(KeyEvent.VK_BACK_SPACE);

		//taken from the jQuery bookmarklet trick at http://benalman.com/projects/run-jquery-code-bookmarklet/
		//it ensures that jQuery is loaded on the page before running my JS. If it isn't already loaded,
		//it loads it from Google's CDN safely (only overwrite $ in a closure my code is run in
		StringBuilder jsCmd = new StringBuilder();
		jsCmd.append("javascript:(function(e,a,g,h,f,c,b,d){if(!(f=e.jQuery)||g>f.fn.jquery||h(f)){c=" +
				"a.createElement(\"script\");c.type=\"text/javascript\";c.src=" +
				"\"http://ajax.googleapis.com/ajax/libs/jquery/\"+g+\"/jquery.min.js\";c.onload=" +
				"c.onreadystatechange=function(){if(!b&&(!(d=this.readyState)||d==\"loaded\"||d==\"complete\"))" +
				"{h((f=e.jQuery).noConflict(1),b=1);f(c).remove()}};a.documentElement.childNodes[0].appendChild(c)}})" +
				"(window,document,\"1.3.2\",function($,L){");
		jsCmd.append("alert(\"hello%20world\");");
		jsCmd.append("});");
		this.pressKeys(jsCmd.toString());
		this.pressKeys(KeyEvent.VK_ENTER);
	}
	
	//In the case of strings, I'm guaranteed that every key being entered is a 'printable' character
	//So, I can use the optimization of dumping the string's contents into the system clipboard
	//and then just pasting (with Cmd-v) the contents of the clipboard
	//This makes a noticeable improvement when running a big JS command, since having the robot type
	//is really slow (with the hundreds of extra chars to guarantee jQuery is loaded)
	private void pressKeys(String s) {
		String oldContents = Utility.getClipboardContents(); //don't clobber the old clipboard (I hate when apps do that)
		Utility.setClipboardContents(s);
		this.pressKeys(new ModifiedKeycode(KeyEvent.VK_V, KeyEvent.VK_META));
		Utility.setClipboardContents(oldContents);
	}
	
	private void pressKeys(int i) {
		this.pressKeys(new SimpleKeycode(i));
	}
	
	private void pressKeys(Keycode k) {
		this.pressKeys(Arrays.asList(k));
	}
	
	private void pressKeys(Collection<? extends Keycode> keys) {
		if(!this.startedP()) {
			throw new RobotronException("You can't press keys in a browser that isn't started!");
		}
		for(Keycode k : keys) {
			if(k.getModifier() != null) {
				this.r.keyPress(k.getModifier());
			}
			this.r.keyPress(k.getMain());
			this.r.keyRelease(k.getMain());
			if(k.getModifier() != null) {
				this.r.keyRelease(k.getModifier());
			}
			Utility.wrappedSleep(5); //otherwise some keys are missed
		}
	}

	public  void goToUrl(String url) {
		if(!this.startedP()) {
			throw new RobotronException("You can't go to a url in a browser that isn't started!");
		}
		this.pressKeys(getKeysToOpenNewTab());
		this.pressKeys(getKeysToGoToUrlBar());
		this.pressKeys(url);
		this.pressKeys(KeyEvent.VK_ENTER);
		Utility.wrappedSleep(5000); //always wait 5s after going to a new URL
	}
		
	public void quit() {
		if(!this.startedP()) {
			throw new RobotronException("You can't quit a browser that isn't started");
		}
//		Keycode quitKeys = new ModifiedKeycode(KeyEvent.VK_Q, KeyEvent.VK_META);
//		this.pressKeys(quitKeys);
		this.P.exitValue();
		this.started = false;
	}
	
	public void start() {
		if(this.startedP()) {
			throw new RobotronException("You can't start a browser that's already started");
		}
		
		Runtime rt = Runtime.getRuntime();
		String[] cmd = {"open", "-a", getAppName()};
		try {
			this.P = rt.exec(cmd); //this returns the process object for 'open' not of the browser
		} catch (IOException e) {
			throw new RobotronException("Unable to start " + getAppName());
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			throw new RobotronException("Unable to sleep while waiting for " + getAppName() + " to start");
		}
		
		//the browser hasn't exited yet (which it shouldn't!) - so all is good
		this.started = true;
	}
		
	//TODO: Actually make this do something smart
	private Rectangle getScreenResolution() {
		return new Rectangle(1280, 800);
	}
	
	public RenderedImage takeScreenShot() {
		if(!this.startedP()) {
			throw new RobotronException("You can't take a screenshot of a stopped browser");
		}
		return this.r.createScreenCapture(this.getScreenResolution());
	}
	
	public static void writeImageToFile(RenderedImage r, String filename) {
		try {
			ImageIO.write(r, "png", new File(filename));
		} catch (IOException e) {
			throw new RobotronException("Unable to save screenshot to " + filename);
		}
	}
	
	public void takeAndWriteScreenshot(String filename) {
		Browser.writeImageToFile(this.takeScreenShot(), filename);
	}	
}
