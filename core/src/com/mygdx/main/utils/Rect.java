package com.mygdx.main.utils;


import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Rect extends Rectangle {

    Point p1;
    Point p2;

    public Rect() {
    }

    public void draw(ShapeRenderer msr) {
        msr.rect(x, y, width, height);
    }

    public void updateCoords() {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        setWidth(Math.abs(dx));
        setHeight(Math.abs(dy));
        if (dx <= 0 && dy >= 0) {
            setX(p1.x);
            setY(p2.y);
        } else if (dx < 0 && dy < 0) {
            setX(p1.x);
            setY(p1.y);
        } else if (dx > 0 && dy > 0) {
            setX(p2.x);
            setY(p2.y);
        } else {
            setX(p2.x);
            setY(p1.y);
        }
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
        updateCoords();
    }

    public void clear() {
        setWidth(0);
        setHeight(0);
    }

    public boolean checkIntersection(Line line) {
        // down
        Line l1 = new Line(
                new Point(
                        getX(), getY()
                ),
                new Point(
                        getX() + getWidth(), getY()
                )
        );

        // left
        Line l2 = new Line(
                new Point(
                        getX(), getY()
                ),
                new Point(
                        getX(), getY() + getHeight()
                )
        );

        // right
        Line l3 = new Line(
                new Point(
                        getX() + getWidth(), getY()
                ),
                new Point(
                        getX() + getWidth(), getY() + getHeight()
                )
        );

        // up
        Line l4 = new Line(
                new Point(
                        getX(), getY() + getHeight()
                ),
                new Point(
                        getX() + getWidth(), getY() + getHeight()
                )
        );

        return l1.intersects(line) || l2.intersects(line) || l3.intersects(line) || l4.intersects(line);
    }

    public boolean checkInside(Line line) {
        return (line.pnt1.x >= getX() && line.pnt1.x <= getX() + getWidth() &&
                line.pnt1.y >= getY() && line.pnt1.y <= getY() + getHeight()) &&
                (line.pnt2.x >= getX() && line.pnt2.x <= getX() + getWidth() &&
                line.pnt2.y >= getY() && line.pnt2.y <= getY() + getHeight());
    }
}
