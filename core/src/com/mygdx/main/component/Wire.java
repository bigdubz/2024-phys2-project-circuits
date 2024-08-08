package com.mygdx.main.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;


public class Wire extends Component {

    boolean split = false;

    public Wire(Main main) {
        super(main);
        resistance = 0;
    }

    public Wire(Main main, Point pos1, Point pos2) {
        this(main);
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
        if (toPnt != null) {
            msr().setColor(1, 1, 0, 1);
            drawArrow(15);
        }
    }

    @Override
    public void act(float delta) {
        if (!previewing) {
            checkConnected();
        }
    }

    @Override
    protected boolean valid(Point pnt) {
        float w = Math.abs(pnt.x - pos1.x);
        float h = Math.abs(pnt.y - pos1.y);
        currValid = w*h == 0 && w + h >= 100;
        return currValid;
    }

    @Override
    protected void placed() {
        if (!split) {
            split = true;
            split();
        }
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
    protected void setTerminals() {
        term1 = pos1.getRect();
        term2 = pos2.getRect();
    }

    @Override
    public void action(int key) {

    }
}
