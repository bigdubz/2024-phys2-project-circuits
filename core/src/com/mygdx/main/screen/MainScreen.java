package com.mygdx.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.main.Main;
import com.mygdx.main.Point;

public class MainScreen implements Screen {

    Main main;
    OrthographicCamera cam;
    ScreenViewport viewport;

    public MainScreen(Main main) {
        this.main = main;
        this.cam = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.cam);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // drag camera
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            cam.translate(
                    -Gdx.input.getDeltaX() * cam.zoom,
                    Gdx.input.getDeltaY() * cam.zoom
            );

        msr().setProjectionMatrix(viewport.getCamera().combined);
        viewport.apply();

        msr().begin();
        drawGrid();


        lePoint(Gdx.input.getX(), Gdx.input.getY());
        msr().end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    // optimized abomination to draw grid :sob:
    void drawGrid() {
        float clr = 0.5f;
        msr().setColor(clr,clr,clr,1);
        for (float i = (int) ((cam.position.x - Gdx.graphics.getWidth()*0.5f)*main.tileSizeInverse)*main.tileSize;
             i < (cam.position.x + Gdx.graphics.getWidth()*0.5 + main.tileSize); i += main.tileSize)
            msr().line(
                    i,
                    cam.position.y - Gdx.graphics.getHeight() * 0.5f,
                    i,
                    cam.position.y + Gdx.graphics.getHeight() * 0.5f
            );

        for (float i = (int) ((cam.position.y - Gdx.graphics.getHeight()*0.5f)*main.tileSizeInverse) * main.tileSize;
             i < (cam.position.y + Gdx.graphics.getHeight()*0.5f) + main.tileSize; i += main.tileSize)
            msr().line(
                    cam.position.x - Gdx.graphics.getWidth() * 0.5f,
                    i,
                    cam.position.x + Gdx.graphics.getWidth() * 0.5f,
                    i
            );
    }

    Point lePoint(int mouseX, int mouseY) {
        int mapX = mouseX + (int) (cam.position.x - Gdx.graphics.getWidth()*0.5f);
        int mapY = -mouseY + (int) (cam.position.y + Gdx.graphics.getHeight()*0.5f);

        float x = MathUtils.floor(mapX * main.tileSizeInverse)*main.tileSize;
        float y = MathUtils.floor(mapY*main.tileSizeInverse)*main.tileSize;

        int xmd100 = mapX % 100;
        if (mapX >= 0 && xmd100 >= 50) x += main.tileSize;
        else if (mapX < 0 && xmd100 >= -50 && xmd100 != 0) x += main.tileSize;

        int ymd100 = mapY % 100;
        if (mapY >= 0 && ymd100 >= 50) y += main.tileSize;
        else if (mapY < 0 && ymd100 >= -50 && ymd100 != 0)y += main.tileSize;

        msr().setColor(1,0,0,1);
        msr().circle(x, y, 5);

        return new Point(x, y);
    }

    // msr: Main.ShapeRenderer
    ShapeRenderer msr() {
        return main.sr;
    }
}
