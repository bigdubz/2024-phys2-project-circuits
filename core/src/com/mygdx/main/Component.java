package com.mygdx.main;

public abstract class Component {

    private int posX;
    private int posY;

    Component() {

    }


    public abstract void place();
    public int getX() {
        return this.posX;
    }
    public int getY() {
        return this.posY;
    }
}
