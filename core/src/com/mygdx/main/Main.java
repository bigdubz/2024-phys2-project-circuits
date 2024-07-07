package com.mygdx.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.main.screen.MainScreen;


public class Main extends Game {
	public ShapeRenderer sr;
	public final int tileSize = 100; // px
	public final float tileSizeInverse = 1f / tileSize;

	MainScreen mainScreen;

	@Override
	public void create () {
		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);

		mainScreen = new MainScreen(this);
		setScreen(mainScreen);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		super.render();
	}
	
	@Override
	public void dispose () {
		sr.dispose();
	}
}
