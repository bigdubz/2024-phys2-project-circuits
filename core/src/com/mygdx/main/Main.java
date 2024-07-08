package com.mygdx.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
		mainScreen.start();
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


	public Point lePoint() {
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		int mapX = mouseX + (int) (mainScreen.getCam().position.x - Gdx.graphics.getWidth()*0.5f);
		int mapY = -mouseY + (int) (mainScreen.getCam().position.y + Gdx.graphics.getHeight()*0.5f);

		float x = MathUtils.floor(mapX*tileSizeInverse)*tileSize;
		float y = MathUtils.floor(mapY*tileSizeInverse)*tileSize;

		int xmod100 = mapX % 100;
		if ((mapX >= 0 && xmod100 >= 50) || (mapX < 0 && xmod100 >= -50 && xmod100 != 0)) {
			x += tileSize;
		}

		int ymod100 = mapY % 100;
		if ((mapY >= 0 && ymod100 >= 50) || (mapY < 0 && ymod100 >= -50 && ymod100 != 0)) {
			y += tileSize;
		}


		return new Point(x, y);
	}
}
