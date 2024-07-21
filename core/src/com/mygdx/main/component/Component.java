package com.mygdx.main.component;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;

public abstract class Component extends Actor {

    protected Main main;
    protected Point pos1;
    protected Point pos2;
    protected Rect rect;
    protected boolean currValid = false;
    public boolean previewing = true;
    public boolean selected = false;

    Component(Main main) {
        this.main = main;
        this.pos1 = this.main.lePoint();
        this.main.mainScreen.addActor(this);
        this.rect = new Rect(this.pos1);
    }

    protected ShapeRenderer msr() {
        return main.sr;
    }

    public Component setSelected(boolean val) {
        selected = val;
        return this;
    }

    public void setPos1(Point pos1) {
        this.pos1 = pos1;
        rect.setP1(pos1);
    }

    public void previewPos2(Point pos2) {
        valid(pos2); // check
        this.pos2 = pos2;
        rect.setP2(this.pos2);
    }

    public boolean setPos2(Point pos2) {
        if (valid(pos2)) {
            this.pos2 = pos2;
            previewing = false;
            rect.setP2(this.pos2);
            placed();
            return true;
        }
        remove();
        return false;
    }

    public void setPos2(Point pos2, boolean ignore) {
        this.pos2 = pos2;
        previewing = false;
        rect.setP2(this.pos2);
    }

    public boolean equals(Component other) {
        return (pos1.equals(other.pos1) || pos1.equals(other.pos2)) &&
                (pos2.equals(other.pos1) || pos2.equals(other.pos2)) &&
                this.getClass() == other.getClass();
    }

    public abstract void action();

    protected abstract boolean valid(Point pnt);

    /**
     * Invoked when the component is placed validly
     */
    protected abstract void placed();

    public abstract boolean checkSelected(Rect slcRect);

    public abstract void checkConnected();
}
