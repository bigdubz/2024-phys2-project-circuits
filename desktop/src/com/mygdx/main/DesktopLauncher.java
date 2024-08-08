package com.mygdx.main;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class DesktopLauncher {

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1400, 900);
		config.setForegroundFPS(165);
		config.setTitle("Physics Project");
		new Lwjgl3Application(new Main(), config);
	}
}
