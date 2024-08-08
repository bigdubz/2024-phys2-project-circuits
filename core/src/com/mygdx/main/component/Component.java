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
    public float resistance; // ohms

    Component(Main main) {
        this.main = main;
        this.pos1 = this.main.elPunto();
        this.main.mainScreen.addActor(this);
        this.rect = new Rect(this.pos1);
        this.con1 = new Array<>();
        this.con2 = new Array<>();
    }

    protected ShapeRenderer msr() {
        return main.sr;
    }

    void drawArrow(float length) {
        float width = 4;
        Point mid = new Point((pos1.x + pos2.x) * 0.5f, (pos1.y + pos2.y) * 0.5f);
        Point other = toPnt.equals(pos1) ? pos2 : pos1;
        msr().circle(mid.x, mid.y, width*0.5f);
        if (toPnt.y < other.y) {
            msr().rectLine(mid.x, mid.y, mid.x-length, mid.y+length, width);
            msr().rectLine(mid.x, mid.y, mid.x+length, mid.y+length, width);
        } else if (toPnt.y > other.y) {
            msr().rectLine(mid.x, mid.y, mid.x-length, mid.y-length, width);
            msr().rectLine(mid.x, mid.y, mid.x+length, mid.y-length, width);
        } else if (toPnt.x > other.x) {
            msr().rectLine(mid.x, mid.y, mid.x-length, mid.y+length, width);
            msr().rectLine(mid.x, mid.y, mid.x-length, mid.y-length, width);
        } else {
            msr().rectLine(mid.x, mid.y, mid.x+length, mid.y+length, width);
            msr().rectLine(mid.x, mid.y, mid.x+length, mid.y-length, width);
        }
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

    public abstract void action(int key);

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

    public Point getPos1() {
        return pos1;
    }

    public Point getPos2() {
        return pos2;
    }

    public boolean setDirection(Array<Component> direction, Point point) {
        this.to = direction;
        this.toPnt = point;
        return main.mainScreen.getCircuit().setDir(this);
    }

    protected abstract void setTerminals();
}
