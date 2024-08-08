package com.mygdx.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.main.component.Component;
import com.mygdx.main.screen.MainScreen;
import com.mygdx.main.utils.Point;


public class Main extends Game {

	public ShapeRenderer sr;
	public SpriteBatch sb;
	public BitmapFont font;
	public final int tileSize = 100; // px
	public final float tileSizeInverse = 1f / tileSize;
	public MainScreen mainScreen;

	@Override
	public void create () {
		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);
		sb = new SpriteBatch();
		font = new BitmapFont();
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
		sb.dispose();
		font.dispose();
	}

	public Point elPunto() {
		int mapX = getMx();
		int mapY = getMy();
		float x = MathUtils.floor(mapX*tileSizeInverse)*tileSize;
		float y = MathUtils.floor(mapY*tileSizeInverse)*tileSize;
		int xmod100 = mapX % tileSize;
		if ((mapX >= 0 && xmod100 >= tileSize*0.5f) || (mapX < 0 && xmod100 >= -tileSize*0.5f && xmod100 != 0)) {
			x += tileSize;
		}

		int ymod100 = mapY % tileSize;
		if ((mapY >= 0 && ymod100 >= tileSize*0.5f) || (mapY < 0 && ymod100 >= -tileSize*0.5f && ymod100 != 0)) {
			y += tileSize;
		}

		return new Point(x, y);
	}

	private int getMx() {
		return Gdx.input.getX() + (int) (mainScreen.getCam().position.x - Gdx.graphics.getWidth()*0.5f);
	}

	private int getMy() {
		return -Gdx.input.getY() + (int) (mainScreen.getCam().position.y + Gdx.graphics.getHeight()*0.5f);
	}

	public Point getMouse() {
		return new Point(getMx(), getMy());
	}

	public Array<Component> components() {
		return mainScreen.getComponents();
	}
}
