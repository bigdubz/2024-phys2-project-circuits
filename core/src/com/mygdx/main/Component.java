package com.mygdx.main;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Component extends Actor {

    Main main;
    Point pos;

    Component(Main main) {
        this.main = main;
        this.pos = this.main.lePoint();
        this.main.mainScreen.addActor(this);
    }

    protected ShapeRenderer msr() {
        return main.sr;
    }
}
