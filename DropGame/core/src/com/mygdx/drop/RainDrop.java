package com.mygdx.drop;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class RainDrop {
    public Rectangle rectangle;
    public int type;

    public RainDrop(Rectangle rectangle) {
        this.rectangle = rectangle;
        type = MathUtils.random(0, 5);
    }
}
