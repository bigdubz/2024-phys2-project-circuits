package com.mygdx.main.component;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;


public abstract class Component extends Actor {

    protected Main main;
    protected Point pos1;
    protected Point pos2;
    protected Rect rect;
    protected Rect term1;
    protected Rect term2;
    public Array<Component> con1;
    public Array<Component> con2;
    public Array<Component> to;
    public Point toPnt;
    protected boolean currValid = false;
    public boolean previewing = true;
    public boolean selected = false;
    public float current; // amps
    public float resistance; // ohms

    Component(Main main) {
        this.main = main;
        this.pos1 = this.main.lePoint();
        this.main.mainScreen.addActor(this);
        this.rect = new Rect(this.pos1);
        this.con1 = new Array<>();
        this.con2 = new Array<>();
        this.to = new Array<>();
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
            setTerminals();
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
        return (pos1.getRect().overlaps(other.rect) && pos2.getRect().overlaps(other.rect)) ||
                (other.pos1.getRect().overlaps(this.rect) && other.pos2.getRect().overlaps(this.rect));
    }

    public abstract void action();

    protected abstract boolean valid(Point pnt);

    /**
     * Invoked when the component is placed validly
     */
    protected abstract void placed();

    public boolean checkSelected(Rect slcRect) {
        return slcRect.overlaps(this.rect);
    }

    public void checkConnected() {
        for (Component comp : main.components()) {
            if (comp != this) {
                if (con1.size < 3  && comp.rect.overlaps(term1) && !con1.contains(comp, true)) {
                    con1.add(comp);
                } else if (con2.size < 3 && comp.rect.overlaps(term2) && !con2.contains(comp, true)) {
                    con2.add(comp);
                }
            }
        }
        for (Component con : con1) {
            if (!main.components().contains(con, true)) {
                con1.removeValue(con, true);
            }
        }
        for (Component con : con2) {
            if (!main.components().contains(con, true)) {
                con2.removeValue(con, true);
            }
        }
    }

    // NEED SOMETING LIKE A PATHFINDER IN ADDITION TO THIS
    public void setDirection(Array<Component> direction, Point point) {
        if (!this.to.isEmpty()) return;
        this.to = direction;
        this.toPnt = point;
        for (Component comp : this.to) {
            if (comp instanceof Battery) continue;
            if (comp.pos1.equals(this.toPnt)) {
                comp.setDirection(comp.con2, comp.pos2);
            } else {
                comp.setDirection(comp.con1, comp.pos1);
            }
        }
    }

    protected abstract void setTerminals();
}
