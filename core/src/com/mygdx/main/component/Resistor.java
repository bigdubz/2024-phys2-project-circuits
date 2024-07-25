package com.mygdx.main.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Point;

public class Resistor extends Component{

    float resistance;

    public Resistor(Main main) {
        super(main);
    }

    @Override
    public void act(float delta) {
        if (!previewing) {
            checkConnected();
        }
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
            msr().setColor(0, 1, 1, parentAlpha);
        }
        if (pos2 != null) {
            msr().rectLine(pos1.x, pos1.y, pos2.x, pos2.y, 20);
            msr().circle(pos2.x, pos2.y, 10);
        }
        msr().circle(pos1.x, pos1.y, 10);
    }

    @Override
    public void action() {

    }

    @Override
    protected boolean valid(Point pnt) {
        float w = Math.abs(pnt.x - pos1.x);
        float h = Math.abs(pnt.y - pos1.y);
        currValid = w*h == 0 && w + h == 200;
        return currValid;
    }

    @Override
    protected void placed() {

    }

    @Override
    public void checkConnected() {

    }
}
