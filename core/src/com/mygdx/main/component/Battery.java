package com.mygdx.main.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;

public class Battery extends Component {

    Rect rect;
    int Voltage; // Volts

    Battery(Main main) {
        super(main);
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

        }
    }

    @Override
    public void previewPos2(Point pos2) {

    }
}
