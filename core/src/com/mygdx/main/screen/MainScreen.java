package com.mygdx.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.main.Component;
import com.mygdx.main.Main;
import com.mygdx.main.Point;
import com.mygdx.main.Wire;

public class MainScreen implements Screen {

    Main main;
    OrthographicCamera cam;
    ScreenViewport viewport;
    Stage stage;
    Component selectedComponent;


    public MainScreen(Main main) {
        this.main = main;
        this.cam = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.cam);
        this.stage = new Stage();
    }

    public void start() {
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) addComponent(new Wire(main));

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) ((Wire) selectedComponent).setPos2(main.lePoint());

        // drag camera
        if (Gdx.input.isKeyPressed(Input.Keys.TAB))
            cam.translate(
                    -Gdx.input.getDeltaX() * cam.zoom,
                    Gdx.input.getDeltaY() * cam.zoom
            );

        msr().setProjectionMatrix(viewport.getCamera().combined);
        viewport.apply();

        msr().begin();
        drawGrid();
        msr().end();

        msr().begin(ShapeRenderer.ShapeType.Filled);
        Point lpnt = main.lePoint();
		msr().setColor(1,0,0,1);
		msr().circle(lpnt.x, lpnt.y, 5);
        stage.draw();
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


    // msr: Main.ShapeRenderer
    private ShapeRenderer msr() {
        return main.sr;
    }

    private void addComponent(Component component) {
        selectedComponent = component;
    }

    public OrthographicCamera getCam() {
        return this.cam;
    }

    public void addActor(Actor actor) {
        stage.addActor(actor);
    }
}
