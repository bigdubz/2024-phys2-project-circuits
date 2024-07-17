package com.mygdx.main.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;

public class Battery extends Component {

    int Voltage; // Volts

    public Battery(Main main) {
        super(main);
        rect = new Rect(this.pos1);
    }

    @Override
    public void previewPos2(Point pos2) {
        if (valid(pos2)) {
            this.pos2 = pos2;
            rect.setP2(pos2);
        }
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
                msr().setColor(0.5f, 1, 0.5f, parentAlpha);
            }
        }
        if (pos2 != null) {
            rect.draw(msr());
        }
    }

    @Override
    protected boolean valid(Point pnt) {
        return !pnt.equals(pos1) && !(pos1.y - pnt.y == 0 || pos1.x - pnt.x == 0);
    }

    @Override
    public boolean checkSelected(Rect slcRect) {
        return slcRect.overlaps(this.rect);
    }
}
