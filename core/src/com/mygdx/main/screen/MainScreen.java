package com.mygdx.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.main.Main;

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
        msr().setProjectionMatrix(viewport.getCamera().combined);
        viewport.apply();

        // drag camera
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
             cam.translate(
                -Gdx.input.getDeltaX() * cam.zoom,
                Gdx.input.getDeltaY() * cam.zoom
        );

        // optimized abomination to draw grid :sob:
        msr().begin();
        for (float i = ((int) (cam.position.x*main.tileSizeInverse)*main.tileSize - Gdx.graphics.getWidth()*0.5f);
             i < (cam.position.x + Gdx.graphics.getWidth()*0.5); i += main.tileSize)
            msr().line(
                    i,
                    cam.position.y - Gdx.graphics.getHeight()*0.5f,
                    i,
                    cam.position.y + Gdx.graphics.getHeight()*0.5f
            );

        for (float i = ((int) (cam.position.y*main.tileSizeInverse)*main.tileSize - Gdx.graphics.getHeight()*0.5f);
             i < (cam.position.y + Gdx.graphics.getHeight()*0.5f); i += main.tileSize)
            msr().line(
                    cam.position.x - Gdx.graphics.getWidth()*0.5f,
                    i,
                    cam.position.x + Gdx.graphics.getWidth()*0.5f,
                    i
            );
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

    ShapeRenderer msr() {
        return main.sr;
    }
}
