package com.mygdx.main.utils;

public class Line {

    Point pnt1;
    Point pnt2;
    boolean horizontal;

    public Line(Point pnt1, Point pnt2) {
        this.pnt1 = pnt1;
        this.pnt2 = pnt2;
        this.horizontal = getHorizontal(pnt1, pnt2);
    }

    public Line(Line line) {
        this(line.pnt1, line.pnt2);
    }

    public boolean intersects(Line line) {
        return this.intersects(line.pnt1, line.pnt2);
    }

    private boolean getHorizontal(Point pnt1, Point pnt2) {
        return pnt1.y - pnt2.y == 0;
    }

    public boolean intersects(Point pnt1, Point pnt2) {
        return (horizontal && !getHorizontal(pnt1, pnt2) && this.pnt1.y >= pnt1.y && this.pnt2.y < pnt1.y) ||
                (!horizontal && getHorizontal(pnt1, pnt2) && this.pnt1.x >= pnt1.x && this.pnt2.x < pnt1.x);
    }
}
