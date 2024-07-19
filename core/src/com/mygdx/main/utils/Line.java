package com.mygdx.main.utils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Line {

    public Point pnt1;
    public Point pnt2;

    public Line(Point pnt1, Point pnt2) {
        this.pnt1 = pnt1;
        this.pnt2 = pnt2;
    }

    public void draw(ShapeRenderer msr, float width) {
        msr.rectLine(pnt1.x, pnt1.y, pnt2.x, pnt2.y, width);
    }
}
