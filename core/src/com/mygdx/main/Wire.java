package com.mygdx.main;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.utils.Point;

public class Wire extends Component {

    boolean previewing = true;
    Point pos2;

    public Wire(Main main) {
        super(main);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (previewing) {
            msr().setColor(1, 1, 0, parentAlpha);
        }
        else {
            msr().setColor(0, 1, 0, parentAlpha);
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

    }

    public void previewPos2(Point pos2) {
        if (!pos2.equals(pos) && (pos.y - pos2.y == 0 || pos.x - pos2.x == 0)) {
            this.pos2 = pos2;
        }
    }

    public boolean setPos2(Point pos2) {
        if (!pos2.equals(pos) && (pos.y - pos2.y == 0 || pos.x - pos2.x == 0)) {
            this.pos2 = pos2;
            previewing = false;
            return true;
        }
        remove();
        return false;
    }
}
