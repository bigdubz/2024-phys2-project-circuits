package com.mygdx.main.component;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;

public abstract class Component extends Actor {

    Main main;
    public Point pos1;
    public Point pos2;
    public boolean selected;
    public boolean previewing = true;
    Rect rect;

    Component(Main main) {
        this.main = main;
        this.pos1 = this.main.lePoint();
        this.main.mainScreen.addActor(this);
        this.rect = new Rect(this.pos1);
    }

    protected ShapeRenderer msr() {
        return main.sr;
    }

    public void setSelected(boolean val) {
        selected = val;
    }

    public void previewPos2(Point pos2) {
        if (valid(pos2)) {
            this.pos2 = pos2;
        }
    }

    public boolean setPos2(Point pos2) {
        if (valid(pos2)) {
            this.pos2 = pos2;
            previewing = false;
            rect.setP2(this.pos2);
            return true;
        }
        remove();
        return false;
    }

    public boolean equals(Component other) {
        return (pos1.equals(other.pos1) || pos1.equals(other.pos2)) &&
                (pos2.equals(other.pos1) || pos2.equals(other.pos2)) &&
                this.getClass() == other.getClass();
    }

    protected abstract boolean valid(Point pnt);

    public abstract boolean checkSelected(Rect slcRect);
}
