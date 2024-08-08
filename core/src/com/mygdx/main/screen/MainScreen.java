package com.mygdx.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;


public class MainScreen implements Screen {

    private final Main main;
    private final OrthographicCamera cam;
    private final ScreenViewport viewport;
    private final ScreenViewport uiport;
    private final Stage stage;
    private final Array<String> allTypes;
    private final Array<Component> components;
    private final Array<Component> slctdComponents;
    private Component selectedComponent;
    private String selectedType;
    private Circuit mainCircuit;
    private final Rect selectionRect;
    private boolean selection = false;
    private boolean slctdCompPlaced = false;
    private int typeIndex = 0;
    private long msgTime = System.currentTimeMillis();
    private String message = "";
    private Color msgColor;
    private final GlyphLayout layout;

    public MainScreen(Main main) {
        this.main = main;

        this.cam = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.cam);

        this.uiport = new ScreenViewport();

        this.stage = new Stage();

        this.components = new Array<>();
        this.slctdComponents = new Array<>();

        this.allTypes = new Array<>();
        this.allTypes.add("Wire");
        this.allTypes.add("Battery");
        this.allTypes.add("Resistor");

        this.selectionRect = new Rect();
        this.selectedType = allTypes.get(0);

        this.msgColor = Color.WHITE;

        this.layout = new GlyphLayout();
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

        // Set circuit
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            createCircuit();
            resetCompsDir();
            if (!mainCircuit.start()) {
                msgTime = System.currentTimeMillis();
                message = "Error occurred during circuit creation! (probably open circuit)";
                msgColor = Color.RED;
                layout.setText(main.font, message);
            } else {
                mainCircuit.getEquivalentResistance();
            }
        }

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
                slctdComponents.pop().setSelected(false).action(Input.Keys.E);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
                slctdComponents.pop().setSelected(false).action(Input.Keys.J);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                slctdComponents.pop().setSelected(false).action(Input.Keys.K);
            }

            // show current component internal resistance
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                msgTime = System.currentTimeMillis();
                msgColor = Color.YELLOW;
                message = "Component internal resistance: " +
                        slctdComponents.pop().setSelected(false).resistance + " Ohms";
                layout.setText(main.font, message);
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

        // draw components and cursor
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

        // <-- draw UI -->

        uiport.apply(true);
        msb().setProjectionMatrix(uiport.getCamera().combined);
        main.sb.begin();
        main.font.setColor(1,1,1,1);

        // draw current selected component
        drawText(selectedType, 100, 100);

        // draw cursor coordinates
        Point pnt = main.elPunto();
        drawText("(" + Math.round(pnt.x) + ", " + Math.round(pnt.y) + ")",
                100, uiport.getScreenHeight() - 100);

        // draw message
        long duration = 3000;
        if (System.currentTimeMillis() - msgTime <= duration) {
            main.font.setColor(msgColor);
            drawText(message, (uiport.getScreenWidth()-layout.width)*0.5f, uiport.getScreenHeight() - 100);
        }

        // draw circuit stats
        if (mainCircuit != null && mainCircuit.canShow) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);

            String v = "Battery voltage: " + mainCircuit.voltage + " Volt(s)";
            layout.setText(main.font, v);
            drawText(v, uiport.getScreenWidth() - 100 - layout.width,
                    uiport.getScreenHeight() - 100 - layout.height);

            String r = "Circuit equivalent resistance: " + df.format(mainCircuit.resistance) + " Ohm(s)";
            layout.setText(main.font, r);
            drawText(r, uiport.getScreenWidth() - 100 - layout.width,
                    uiport.getScreenHeight() - 130 - layout.height);

            String cu = mainCircuit.current == 1/0. ? "-1" : df.format(mainCircuit.current);
            String c = "Main Current: " + (Objects.equals(cu, "-1") ? "NA " : cu) + " Ampere(s)";
            layout.setText(main.font, c);
            drawText(c, uiport.getScreenWidth() - 100 - layout.width,
                    uiport.getScreenHeight() - 160 - layout.height);
        }
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
        mainCircuit = new Circuit(main, components);
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
        return mainCircuit;
    }

    private void resetCompsDir() {
        for (int i = 0; i < components.size; i++) {
            Component comp = components.get(i);
            comp.checkConnected();
            comp.to = null;
            comp.toPnt = null;
        }
    }
}
