package com.mygdx.main.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.main.Main;
import com.mygdx.main.utils.Line;
import com.mygdx.main.utils.Point;
import com.mygdx.main.utils.Rect;

public class Battery extends Component {

    int Voltage; // Volts
    Wire pos_term;
    Wire neg_term;
    Line positive;
    Line negative;

    public Battery(Main main) {
        super(main);
    }

    @Override
    public void previewPos2(Point pos2) {
        valid(pos2); // check
        this.pos2 = pos2;
        rect.setP2(this.pos2);
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
        currValid = !pnt.equals(pos1) && !(pos1.y - pnt.y == 0 || pos1.x - pnt.x == 0) && (w != h);
        return currValid;
    }

    @Override
    protected void placed() {
        float w = Math.abs(pos2.x - pos1.x);
        float h = Math.abs(pos2.y - pos1.y);
        if (w > h) {
            if ((h * main.tileSizeInverse) % 2 == 1) {
                setPos1(new Point(pos1.x, pos1.y - 0.5f*main.tileSize));
                setPos2(new Point(pos2.x, pos2.y - 0.5f*main.tileSize), true);
            }
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

        if ((w * main.tileSizeInverse) % 2 == 1) {
            setPos1(new Point(pos1.x + 0.5f*main.tileSize, pos1.y));
            setPos2(new Point(pos2.x + 0.5f*main.tileSize, pos2.y), true);
        }
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
    public boolean checkSelected(Rect slcRect) {
        return slcRect.overlaps(this.rect);
    }

    @Override
    public void checkConnected() {
        if (pos_term != null && neg_term != null) {
            return;
        }
        for (Component comp : main.components()) {
            if (comp instanceof Wire) {
                Rect z = positive.getRect();
                Rect x = comp.rect.getExpanded();
                System.out.println("positive rect -> [" + z.x + ", " + z.y + ", " + z.width + ", " + z.height + "]");
                System.out.println("comp rect -> [" + x.x + ", " + x.y + ", " + x.width + ", " + x.height + "]");
                System.out.println("overlapped: " + z.overlaps(x));
                if (pos_term == null && positive.getRect().overlaps(comp.rect.getExpanded())) {
                    pos_term = (Wire) comp;
                }
                else if (negative.getRect().overlaps(comp.rect.getExpanded())) {
                    neg_term = (Wire) comp;
                }
            }
        }
    }

    @Override
    public void action() {
        Line temp = new Line(positive);
        positive = new Line(negative);
        negative = new Line(temp);
    }
}
