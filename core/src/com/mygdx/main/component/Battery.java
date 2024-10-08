package com.mygdx.main.component;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Line;
import com.mygdx.main.utils.Point;

public class Battery extends Component {

    int voltage; // Volts
    public Component pos_term;
    public Component neg_term;
    Line positive;
    Line negative;

    public Battery(Main main) {
        super(main);
        resistance = 0;
        voltage = 12;
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
            msr().setColor(0.75f, 1, 0.5f, parentAlpha);
        }
        if (pos2 != null) {
            rect.draw(msr());
        }
        if (positive != null) {
            msr().setColor(1,0,0,1);
            positive.draw(msr(), 10);
            msr().setColor(0,0,1,1);
            negative.draw(msr(), 10);
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
        currValid = w*h != 0 && w + h == 300;
        return currValid;
    }

    @Override
    protected void placed() {
    }


    @Override
    public void checkConnected() {
        for (Component comp : main.components()) {
            if (comp == this || comp.previewing) continue;
            if (positive.getRect().overlaps(comp.rect.getExpanded())) {
                pos_term = comp;
            }
            else if (negative.getRect().overlaps(comp.rect.getExpanded())) {
                neg_term = comp;
            }
        }
        if (!main.components().contains(pos_term, true)) {
            pos_term = null;
        }
        if (!main.components().contains(neg_term, true)) {
            neg_term = null;
        }
    }

    public boolean setDir() {
        if (pos_term.pos1.getRect().overlaps(positive.getRect())) {
            return pos_term.setDirection(pos_term.con2, pos_term.pos2);
        } else {
            return pos_term.setDirection(pos_term.con1, pos_term.pos1);
        }
    }

    @Override
    protected void setTerminals() {
        float w = Math.abs(pos2.x - pos1.x);
        float h = Math.abs(pos2.y - pos1.y);
        if (w > h) {
            setPos1(new Point(pos1.x, pos1.y - 0.5f*main.tileSize));
            setPos2(new Point(pos2.x, pos2.y - 0.5f*main.tileSize), true);
            positive = new Line(
                    new Point(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y)),
                    new Point(Math.min(pos1.x, pos2.x), Math.max(pos1.y, pos2.y))
            );
            negative = new Line(
                    new Point(Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y)),
                    new Point(Math.max(pos1.x, pos2.x), Math.min(pos1.y, pos2.y))
            );
            return;
        }

        setPos1(new Point(pos1.x + 0.5f*main.tileSize, pos1.y));
        setPos2(new Point(pos2.x + 0.5f*main.tileSize, pos2.y), true);
        positive = new Line(
                new Point(Math.min(pos1.x, pos2.x), Math.max(pos1.y, pos2.y)),
                new Point(Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y))
        );
        negative = new Line(
                new Point(Math.max(pos1.x, pos2.x), Math.min(pos1.y, pos2.y)),
                new Point(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y))
        );
    }

    @Override
    public void action(int key) {
        if (key == Input.Keys.E) {
            Line temp = new Line(positive);
            positive = new Line(negative);
            negative = new Line(temp);
        }
        else if (key == Input.Keys.J) {
            voltage++;
        }
        else if (key == Input.Keys.K) {
            voltage--;
        }
    }

    public int getVoltage() {
        return voltage;
    }
}
