package com.mygdx.main;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Wire extends Component {

    boolean fullyPlaced = false;
    Point pos2;

    public Wire(Main main) {
        super(main);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (fullyPlaced) {
            msr().setColor(0, 1, 0, parentAlpha);
            msr().rectLine(pos.x, pos.y, pos2.x, pos2.y, 10);
            msr().circle(pos2.x, pos2.y, 5);
            msr().setColor(0, 0, 1, parentAlpha);
            msr().circle(pos.x, pos.y, 5);
        } else {
            msr().setColor(0, 0, 1, parentAlpha);
            msr().circle(pos.x, pos.y, 5);
        }
    }

    @Override
    public void act(float delta) {

    }

    public void setPos2(Point pos2) {
        if (!pos2.equals(pos) && (pos.y - pos2.y == 0 || pos.x - pos2.x == 0)) {
            this.pos2 = pos2;
            fullyPlaced = true;
        }
    }
}
