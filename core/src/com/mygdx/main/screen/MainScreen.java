package com.mygdx.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.main.Component;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;
import com.mygdx.main.Wire;
import com.mygdx.main.utils.Rect;


public class MainScreen implements Screen {

    Main main;
    OrthographicCamera cam;
    ScreenViewport viewport;
    Stage stage;
    Component selectedComponent;
    Array<Component> components;
    Rect selectionRect;
    boolean selection = false;
    boolean slctdCompPlaced = false;


    public MainScreen(Main main) {
        this.main = main;
        this.cam = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.cam);
        this.stage = new Stage();
        this.components = new Array<>();
        this.selectionRect = new Rect();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();
        draw();
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

    void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) addComponent(new Wire(main));
        if (selectedComponent != null && !slctdCompPlaced) {
            if (Gdx.input.isKeyPressed(Input.Keys.F)) ((Wire) selectedComponent).previewPos2(main.lePoint());
            else {
                if (((Wire) selectedComponent).setPos2(main.lePoint())) {
                    slctdCompPlaced = true;
                }
            }
        }

        // drag camera
        if (Gdx.input.isKeyPressed(Input.Keys.TAB))
            cam.translate(
                    -Gdx.input.getDeltaX() * cam.zoom,
                    Gdx.input.getDeltaY() * cam.zoom
            );

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            startSelection();
        }
        if (selection) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                rectSelect();
            } else {
                clearSelection();
            }
        }
    }

    void draw() {
        msr().setProjectionMatrix(viewport.getCamera().combined);
        viewport.apply();

        msr().begin();
        drawGrid();
        msr().end();

        msr().begin(ShapeRenderer.ShapeType.Filled);
        if (!selection) {
            Point cursor = main.lePoint();
            msr().setColor(1, 0, 0, 1);
            msr().circle(cursor.x, cursor.y, 5);
        }
        stage.draw();
        msr().end();

        if (selection) {
            msr().begin();
            msr().setColor(1, 1, 1, 1);
            selectionRect.draw(msr());
            msr().end();
        }
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

    private void startSelection() {
        selection = true;
        int mouseX = Gdx.input.getX() + (int) (getCam().position.x - Gdx.graphics.getWidth()*0.5f);
        int mouseY = -Gdx.input.getY() + (int) (getCam().position.y + Gdx.graphics.getHeight()*0.5f);
        Point mouse = new Point(mouseX, mouseY);
        selectionRect.setP1(mouse);
    }

    private void rectSelect() {
        int mouseX = Gdx.input.getX() + (int) (cam.position.x - Gdx.graphics.getWidth()*0.5f);
        int mouseY = -Gdx.input.getY() + (int) (cam.position.y + Gdx.graphics.getHeight()*0.5f);
        Point mouse = new Point(mouseX, mouseY);
        selectionRect.setP2(mouse);
        selectionRect.updateCoords();

    }

    private void clearSelection() {
        selection = false;
        selectionRect.clear();
    }

    // msr: Main.ShapeRenderer
    private ShapeRenderer msr() {
        return main.sr;
    }

    private void addComponent(Component component) {
        selectedComponent = component;
        components.add(component);
        slctdCompPlaced = false;
    }

    public OrthographicCamera getCam() {
        return this.cam;
    }

    public void addActor(Actor actor) {
        stage.addActor(actor);
    }

    private void deselect() {
        selectedComponent = null;
        slctdCompPlaced = false;
    }
}
