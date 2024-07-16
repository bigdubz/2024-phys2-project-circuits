package com.mygdx.main;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.main.utils.Point;

public abstract class Component extends Actor {

    Main main;
    public Point pos1;
    public Point pos2;
    public boolean selected;
    public boolean previewing = true;

    Component(Main main) {
        this.main = main;
        this.pos1 = this.main.lePoint();
        this.main.mainScreen.addActor(this);
    }

    protected ShapeRenderer msr() {
        return main.sr;
    }

    public void setSelected(boolean val) {
        selected = val;
    }

    public abstract void previewPos2(Point pos2);


    public boolean setPos2(Point pos2) {
        if (!pos2.equals(pos1) && (pos1.y - pos2.y == 0 || pos1.x - pos2.x == 0)) {
            this.pos2 = pos2;
            previewing = false;
            return true;
        }
        remove();
        return false;
    }

}
