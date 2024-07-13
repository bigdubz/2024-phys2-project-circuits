package com.mygdx.main;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.utils.Point;


public class Wire extends Component {

    boolean previewing = true;
    public Point pos2;
    boolean split = false;

    public Wire(Main main) {
        super(main);
    }

    public Wire(Main main, Point pos1, Point pos2) {
        super(main);
        this.pos = pos1;
        this.pos2 = pos2;
        this.previewing = false;
        this.split = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (previewing) {
            msr().setColor(1, 1, 0, parentAlpha);
        }
        else {
            if (selected) {
                msr().setColor(0, 1, 1, parentAlpha);
            } else {
                msr().setColor(0, 1, 0, parentAlpha);
            }
        }
        if (pos2 != null) {
            msr().rectLine(pos.x, pos.y, pos2.x, pos2.y, 10);
            msr().circle(pos2.x, pos2.y, 5);
        }
        msr().setColor(0, 0, 1, parentAlpha);
        msr().circle(pos.x, pos.y, 5);
    }

    @Override
    public void act(float delta) {
        if (!previewing && !split) {
            split();
            split = true;
        }
    }

    public void previewPos2(Point pos2) {
        if (pos2.notEqual(pos) && (pos.y - pos2.y == 0 || pos.x - pos2.x == 0)) {
            this.pos2 = pos2;
        }
    }

    public boolean setPos2(Point pos2) {
        if (pos2.notEqual(pos) && (pos.y - pos2.y == 0 || pos.x - pos2.x == 0)) {
            this.pos2 = pos2;
            previewing = false;
            return true;
        }
        remove();
        return false;
    }

    private void split() {
        int dx = (int) (pos.x - pos2.x);
        int dy = (int) (pos.y - pos2.y);
        boolean horizontal = dx != 0;
        int direction = (dx + dy) < 0 ? 1 : -1;
        setPos2(new Point(horizontal ? this.pos.x + direction * main.tileSize : this.pos.x,
                horizontal ? this.pos.y : this.pos.y + direction * main.tileSize));
        Point prev = new Point(this.pos2);
        int totalWires = (int) ((Math.abs(dx) + Math.abs(dy)) * main.tileSizeInverse) - 1;
        for (int i = 0; i < totalWires; i += 1) {
            Point pos2 = new Point(horizontal ? prev.x + direction * main.tileSize : prev.x,
                    horizontal ? prev.y : prev.y + direction * main.tileSize);
            main.mainScreen.addComponent(new Wire(main, prev, pos2), 0);
            prev = new Point(pos2);
        }
    }

}
