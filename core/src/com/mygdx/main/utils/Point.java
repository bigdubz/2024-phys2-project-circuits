package com.mygdx.main.utils;

public class Point {

    public float x;
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Point point2) {
        return point2.x == x && point2.y == y;
    }
}
