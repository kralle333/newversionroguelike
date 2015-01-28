package com.brimstonetower.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.brimstonetower.game.TheBrimstoneTowerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 960;
		config.height=540;
		new LwjglApplication(TheBrimstoneTowerGame.getGameInstance(), config);
	}
}
