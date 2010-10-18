package com.greplin.robotron;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public abstract class Browser {
	protected abstract String getAppName();
	protected abstract Collection<Keycode> getKeysToOpenNewTab();
	protected abstract Collection<Keycode> getKeysToGoToUrlBar();
	
	protected boolean started;
	protected Process process;
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
		this.start();
	}

	public void runJs(String s) {
		if(!this.startedP()) {
			throw new RobotronException("You can't go to a url in a browser that isn't started!");
		}
		this.pressKeys(this.getKeysToGoToUrlBar());
		List<Keycode> backspace = new LinkedList<Keycode>();
		backspace.add(new SimpleKeycode(KeyEvent.VK_BACK_SPACE));
		this.pressKeys(backspace);
		this.pressKeys(Utility.stringToIntList("javascript:"));
		this.pressKeys(Utility.stringToIntList(s));
		this.pressEnter();
	}
	
	private void pressKeys(Collection<Keycode> keys) {
		for(Keycode k : keys) {
			if(k.getModifier() != null) {
				this.r.keyPress(k.getModifier());
			}
			this.r.keyPress(k.getMain());
			this.r.keyRelease(k.getMain());
			if(k.getModifier() != null) {
				this.r.keyRelease(k.getModifier());
			}
			Utility.wrappedSleep(10); //otherwise some keys are missed
		}
	}

	public  void goToUrl(String url) {
		if(!this.startedP()) {
			throw new RobotronException("You can't go to a url in a browser that isn't started!");
		}
		this.pressKeys(getKeysToOpenNewTab());
		this.pressKeys(this.getKeysToGoToUrlBar());
		this.pressKeys(Utility.stringToIntList(url));
		this.pressEnter();
		Utility.wrappedSleep(20000); //always wait 5s after going to a new URL
	}
	
	private void pressEnter() {
		this.r.keyPress(KeyEvent.VK_ENTER);
	}
	
	public void quit() {
		if(!this.startedP()) {
			throw new RobotronException("You can't quit a browser that isn't started");
		}
	}
	
	private void start() {
		if(this.startedP()) {
			throw new RobotronException("You can't start a browser that's already started");
		}
		
		Runtime rt = Runtime.getRuntime();
		String[] cmd = {"open", "-a", getAppName()};
		Process p;
		try {
			p = rt.exec(cmd);
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
		this.process = p;
	}
	
	//TODO: Actually make this do something
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
