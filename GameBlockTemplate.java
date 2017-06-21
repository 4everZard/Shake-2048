package com.a2xia.shake2048;


import android.content.Context;
import android.widget.ImageView;

public abstract class GameBlockTemplate extends ImageView{

    public GameBlockTemplate(Context gbCTX){
        super(gbCTX);
    }

    public abstract boolean setDestination(GameLoop.eDir myDir);

    public abstract void move();

}
