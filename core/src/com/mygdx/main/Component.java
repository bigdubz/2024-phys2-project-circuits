package com.mygdx.main;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.main.utils.Point;

public abstract class Component extends Actor {

    Main main;
    public Point pos;
    public boolean selected;

    Component(Main main) {
        this.main = main;
        this.pos = this.main.lePoint();
        this.main.mainScreen.addActor(this);
    }

    protected ShapeRenderer msr() {
        return main.sr;
    }

    public void setSelected(boolean val) {
        selected = val;
    }
}
