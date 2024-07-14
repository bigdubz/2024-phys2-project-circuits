package com.mygdx.main.utils;

public class Line {

    Point pnt1;
    Point pnt2;

    public Line(Point pnt1, Point pnt2) {
        this.pnt1 = pnt1;
        this.pnt2 = pnt2;
    }

    public boolean intersects(Line line) {
        return this.intersects(line.pnt1, line.pnt2);
    }

    public boolean intersects(Point pnt1, Point pnt2) {
        return ((this.pnt1.y <= pnt1.y && this.pnt1.y >= pnt2.y || this.pnt1.y >= pnt1.y && this.pnt1.y <= pnt2.y) &&
                (pnt1.x >= this.pnt1.x && pnt1.x <= this.pnt2.x || pnt1.x <= this.pnt1.x && pnt1.x >= this.pnt2.x)) ||
                ((this.pnt1.x <= pnt1.x && this.pnt1.x >= pnt2.x || this.pnt1.x >= pnt1.x && this.pnt1.x <= pnt2.x) &&
                (pnt1.y >= this.pnt1.y && pnt1.y <= this.pnt2.y || pnt1.y <= this.pnt1.y && pnt1.y >= this.pnt2.y));
    }

    public boolean equals(Line line) {
        return (line.pnt1.equals(this.pnt1) || line.pnt1.equals(this.pnt2)) &&
                (line.pnt2.equals(this.pnt1) || line.pnt2.equals(this.pnt2));
    }
}
