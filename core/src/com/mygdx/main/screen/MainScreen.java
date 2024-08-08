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
import com.mygdx.main.Circuit;
import com.mygdx.main.component.Battery;
import com.mygdx.main.component.Component;
import com.mygdx.main.Main;
import com.mygdx.main.component.Resistor;
import com.mygdx.main.utils.Point;
import com.mygdx.main.component.Wire;
import com.mygdx.main.utils.Rect;


public class MainScreen implements Screen {

    private final Main main;
    private final OrthographicCamera cam;
    private final ScreenViewport viewport;
    private final ScreenViewport uiport;
    private final Stage stage;
    private final Array<String> allTypes;
    private final Array<Component> components;
    private final Array<Component> slctdComponents;
    private final Array<Component> visited;
    private Component selectedComponent;
    private String selectedType;
    private Circuit testCircuit;
    private final Rect selectionRect;
    private boolean selection = false;
    private boolean slctdCompPlaced = false;
    private int typeIndex = 0;

    public MainScreen(Main main) {
        this.main = main;

        this.cam = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.cam);

        this.uiport = new ScreenViewport();

        this.stage = new Stage();

        this.components = new Array<>();
        this.slctdComponents = new Array<>();
        this.visited = new Array<>();

        this.allTypes = new Array<>();
        this.allTypes.add("Wire");
        this.allTypes.add("Battery");
        this.allTypes.add("Resistor");

        this.selectionRect = new Rect();
        this.selectedType = allTypes.get(0);
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

        // test
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            createCircuit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            System.out.println(main.elPunto().x + ", " + main.elPunto().y);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            for (int i = 0; i < components.size; i++) {
                Component comp = components.get(i);
                comp.checkConnected();
                comp.to = null;
                comp.toPnt = null;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            visited.clear();
            for (int i = 0; i < components.size; i++) {
                Component comp = components.get(i);
                if (!(comp instanceof Battery)) continue;
                ((Battery) comp).setDir();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && testCircuit != null) {
            double r = testCircuit.getTotalResistance();
            System.out.println(r);
        }
        // end test

        // select next component
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            selectNextType();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            clearSelected();
            switch (selectedType) {
                case "Wire":
                    addComponent(new Wire(main));
                    break;
                case "Battery":
                    addComponent(new Battery(main));
                    break;
                case "Resistor":
                    addComponent(new Resistor(main));
                    break;
            }
        }

        // add component
        if (selectedComponent != null && !slctdCompPlaced) {
            if (Gdx.input.isKeyPressed(Input.Keys.F)) selectedComponent.previewPos2(main.elPunto());
            else {
                placeComponent(selectedComponent);
            }
        }

        // use action key on selected component
        if (slctdComponents.size == 1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                slctdComponents.pop().setSelected(false).action();
            }
        }

        // delete components
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            for (Component component : slctdComponents) {
                component.remove();
                components.removeValue(component, true);
            }
            slctdComponents.clear();
        }

        // drag camera
        if (Gdx.input.isButtonPressed(1))
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
                selection = false;
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
            Point cursor = main.elPunto();
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

        // draw ui (will make a UI class if this gets long enough)
        uiport.apply(true);
        msb().setProjectionMatrix(uiport.getCamera().combined);
        main.sb.begin();
        drawText(selectedType, 100, 100);
        main.sb.end();
    }

    // optimized abomination to draw grid :sob:
    void drawGrid() {
        float clr = 0.1f;
        msr().setColor(clr,clr,clr,1);
        for (float i = (int) ((cam.position.x - Gdx.graphics.getWidth()*0.5f)*main.tileSizeInverse)*main.tileSize;
             i < (cam.position.x + Gdx.graphics.getWidth()*0.5 + main.tileSize); i += main.tileSize) {
            msr().line(
                    i, cam.position.y - Gdx.graphics.getHeight()*0.5f,
                    i, cam.position.y + Gdx.graphics.getHeight()*0.5f
            );
        }

        for (float i = (int) ((cam.position.y - Gdx.graphics.getHeight()*0.5f)*main.tileSizeInverse) * main.tileSize;
             i < (cam.position.y + Gdx.graphics.getHeight()*0.5f) + main.tileSize; i += main.tileSize) {
            msr().line(
                    cam.position.x - Gdx.graphics.getWidth()*0.5f, i,
                    cam.position.x + Gdx.graphics.getWidth()*0.5f, i
            );
        }
    }

    private void startSelection() {
        selection = true;
        selectionRect.setP1(main.getMouse());
    }

    private void rectSelect() {
        selectionRect.setP2(main.getMouse());
        for (Component comp : components) {
            if (comp.previewing) continue;
            boolean select = comp.checkSelected(selectionRect);
            if (select) {
                if (!comp.selected) {
                    slctdComponents.add(comp);
                }
            } else {
                if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    slctdComponents.removeValue(comp, true);
                }
            }
            comp.setSelected(select);
        }
    }

    private void clearSelected() {
        for (Component comp : components) {
            comp.setSelected(false);
        }
    }

    private void selectNextType() {
        selectedType = allTypes.get(++typeIndex % allTypes.size);
    }

    private void placeComponent(Component component) {
        if (selectedComponent.setPos2(main.elPunto())) {
            if (checkExistent(component)) {
                removeComponent(component);
            }
            slctdCompPlaced = true;
        } else {
            removeComponent(component);
        }
    }

    public boolean checkExistent(Component component) {
        for (Component comp : components) {
            if (comp != component && comp.equals(component)) {
                return true;
            }
        }
        return false;
    }

    private void addComponent(Component component) {
        selectedComponent = component;
        components.add(component);
        slctdCompPlaced = false;
    }

    public void addComponent(Component component, int ignore) {
        if (!checkExistent(component)) {
            components.add(component);
        } else {
            component.remove();
        }
    }

    private void createCircuit() {
        testCircuit = new Circuit(main, components);
    }

    public void removeComponent(Component component) {
        component.remove();
        components.removeValue(component, true);
    }

    private void drawText(String text, float x, float y) {
        main.font.draw(msb(), text, x, y);
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

    public Array<Component> getComponents() {
        return components;
    }

    public Circuit getCircuit() {
        return testCircuit;
    }
}
