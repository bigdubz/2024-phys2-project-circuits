package com.mygdx.main.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Line;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;


public class Wire extends Component {

    boolean split = false;

    public Wire(Main main) {
        super(main);
    }

    public Wire(Main main, Point pos1, Point pos2) {
        super(main);
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.rect.setPos(this.pos1, this.pos2);
        this.previewing = false;
        this.split = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (previewing) {
            msr().setColor(0.75f, 0.75f, 0, parentAlpha);
        }
        else {
            if (selected) {
                msr().setColor(1, 0.5f, 0, parentAlpha);
            } else {
                msr().setColor(0, 1, 0, parentAlpha);
            }
        }
        if (pos2 != null) {
            msr().rectLine(pos1.x, pos1.y, pos2.x, pos2.y, 10);
            msr().circle(pos2.x, pos2.y, 5);
        }
        msr().circle(pos1.x, pos1.y, 5);
    }

    @Override
    public void act(float delta) {
        if (!previewing && !split) {
            split();
            split = true;
        }
    }

    @Override
    protected boolean valid(Point pnt) {
        return !pnt.equals(pos1) && (pos1.y - pnt.y == 0 || pos1.x - pnt.x == 0);
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

    public boolean equals(Wire wire) {
        return new Line(wire.pos1, wire.pos2).equals(new Line(this.pos1, this.pos2));
    }

    @Override
    public boolean checkSelected(Rect slcRect) {
        return slcRect.overlaps(this.rect);
    }
}
