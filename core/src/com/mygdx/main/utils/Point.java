package com.mygdx.main.utils;

public class Point {

    public float x;
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point pos) {
        this(pos.x, pos.y);
    }

    public boolean equals(Point point2) {
        return point2.x == x && point2.y == y;
    }

    public Rect getRect() {
        return new Rect(x-5, y-5, 10, 10);
    }

    public float distance(Point p2) {
        return (float) Math.sqrt((x - p2.x)*(x - p2.x) + (y - p2.y)*(y - p2.y));
    }
}
