package com.mygdx.main.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Line;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;


public class Wire extends Component {

    boolean split = false;
    Component con1;
    Component con2;
    Rect term1;
    Rect term2;

    public Wire(Main main) {
        super(main);
    }

    @Override
    public void action() {

    }

    public Wire(Main main, Point pos1, Point pos2) {
        super(main);
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.rect.setPos(this.pos1, this.pos2);
        setTerminals();
        this.previewing = false;
        this.split = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (previewing) {
            if (!currValid) {
                msr().setColor(1, 0, 0, parentAlpha);
            }
            else {
                msr().setColor(0.75f, 0.75f, 0, parentAlpha);
            }
        }
        else if (selected) {
            msr().setColor(1, 0.5f, 0, parentAlpha);
        } else {
            msr().setColor(0, 1, 0, parentAlpha);
        }
        if (pos2 != null) {
            msr().rectLine(pos1.x, pos1.y, pos2.x, pos2.y, 10);
            msr().circle(pos2.x, pos2.y, 5);
        }
        msr().circle(pos1.x, pos1.y, 5);
    }

    @Override
    public void act(float delta) {
        if (!previewing) {
            if (!split) {
                split();
                split = true;
            }
            checkConnected();
        }
    }

    @Override
    protected boolean valid(Point pnt) {
        currValid = !pnt.equals(pos1) && (pos1.y - pnt.y == 0 || pos1.x - pnt.x == 0);
        return currValid;
    }

    @Override
    protected void placed() {
        setTerminals();
    }

    private void split() {
        int dx = (int) (pos1.x - pos2.x);
        int dy = (int) (pos1.y - pos2.y);
        boolean horizontal = dx != 0;
        int direction = (dx + dy) < 0 ? 1 : -1;
        Point np2 = new Point(horizontal ? this.pos1.x + direction * main.tileSize : this.pos1.x,
                horizontal ? this.pos1.y : this.pos1.y + direction * main.tileSize);
        setPos2(np2);
        this.rect.setP2(np2);
        if (main.mainScreen.checkExistent(this)) {
            main.mainScreen.removeComponent(this);
        }
        Point prev = new Point(this.pos2);
        int totalWires = (int) ((Math.abs(dx) + Math.abs(dy)) * main.tileSizeInverse) - 1;
        for (int i = 0; i < totalWires; i += 1) {
            Point pos2 = new Point(horizontal ? prev.x + direction * main.tileSize : prev.x,
                    horizontal ? prev.y : prev.y + direction * main.tileSize);
            main.mainScreen.addComponent(new Wire(main, prev, pos2), 0);
            prev = new Point(pos2);
        }
    }

    @Override
    public boolean checkSelected(Rect slcRect) {
        return slcRect.overlaps(this.rect);
    }

    @Override
    public void checkConnected() {
        if (con1 != null && con2 != null) {
            return;
        }
        for (Component comp : main.components()) {
            if (comp != this) {
                if (con1 == null && comp.rect.overlaps(term1)) {
                    con1 = comp;
                } else if (con2 == null && comp.rect.overlaps(term2)) {
                    con2 = comp;
                }
            }
        }
    }

    public Line getLine() {
        return new Line(pos1, pos2);
    }

    private void setTerminals() {
        term1 = new Rect(pos1.x-5, pos1.y-5, 10, 10);
        term2 = new Rect(pos2.x-5, pos2.y-5, 10, 10);
    }
}
