package com.mygdx.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.main.Component;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Line;
import com.mygdx.main.utils.Point;
import com.mygdx.main.Wire;
import com.mygdx.main.utils.Rect;


public class MainScreen implements Screen {

    Main main;
    OrthographicCamera cam;
    ScreenViewport viewport;
    OrthographicCamera uicam;
    ScreenViewport uiport;
    Stage stage;
    Component selectedComponent;
    String selectedType;
    Array<Component> components;
    Array<Component> slctdComponents;
    Rect selectionRect;
    boolean selection = false;
    boolean slctdCompPlaced = false;


    public MainScreen(Main main) {
        this.main = main;
        this.cam = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.cam);
        this.uicam = new OrthographicCamera();
        this.uiport = new ScreenViewport(this.uicam);
        this.stage = new Stage();
        this.components = new Array<>();
        this.slctdComponents = new Array<>();
        this.selectionRect = new Rect();
        this.selectedType = "Wire";
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();
        stage.act();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiport.update(width, height);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            switch (selectedType) {
                case "Wire":
                    addComponent(new Wire(main));
                    break;
                case "Battery":
                    // add battery
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectedType);
            }
        }
        if (selectedComponent != null && !slctdCompPlaced) {
            if (Gdx.input.isKeyPressed(Input.Keys.F)) ((Wire) selectedComponent).previewPos2(main.lePoint());
            else {
                if (((Wire) selectedComponent).setPos2(main.lePoint())) {
                    slctdCompPlaced = true;
                }
            }
        }

        // delete components
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            for (Component component : slctdComponents) {
                component.remove();
                components.removeValue(component, true);
            }
        }

        // drag camera
        if (Gdx.input.isKeyPressed(Input.Keys.TAB))
            cam.translate(
                    -Gdx.input.getDeltaX() * cam.zoom,
                    Gdx.input.getDeltaY() * cam.zoom
            );

        // select components
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            startSelection();
        }
        if (selection) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                rectSelect();
            } else {
                stopSelection();
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

        // draw ui
        uiport.apply(true);
        main.sb.begin();
        drawText(selectedType, 100, 100);
        main.sb.end();
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
        selectionRect.setP1(main.getMouse());
    }

    private void rectSelect() {
        selectionRect.setP2(main.getMouse());
        for (Component component : components) {
            if (component instanceof Wire) {
                Wire wire = (Wire) component;
                boolean select = selectionRect.checkIntersection(new Line(wire.pos1, wire.pos2)) ||
                        selectionRect.checkInside(new Line(wire.pos1, wire.pos2));
                if (select) {
                    if (!wire.selected) {
                        slctdComponents.add(wire);
                    }
                } else {
                    slctdComponents.removeValue(wire, true);
                }
                component.setSelected(select);
            }
        }
    }

    private void stopSelection() {
        selection = false;
        selectionRect.clear();
    }

    private void addComponent(Component component) {
        selectedComponent = component;
        components.add(component);
        slctdCompPlaced = false;
    }

    @SuppressWarnings("unused")
    public void addComponent(Component component, int unused) {
        components.add(component);
    }

    private void drawText(String text, Point pos) {
        drawText(text, pos.x, pos.y);
    }

    private void drawText(String text, float x, float y) {
        main.font.draw(main.sb, text, x, y);
    }

    public OrthographicCamera getCam() {
        return this.cam;
    }

    public void addActor(Actor actor) {
        stage.addActor(actor);
    }
    // msr: Main.ShapeRenderer
    private ShapeRenderer msr() {
        return main.sr;
    }

    // msb: Main.SpriteBatch
    private SpriteBatch msb() {
        return main.sb;
    }
}
