package com.example.catchthecarrots;

import android.content.Context;
import android.graphics.BitmapFactory;


import java.util.Random;

public class Blade extends Projectile{

    static int increaseVelY;

    public Blade(Context context) {
        super();
        random = new Random();
        allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_blade_0));
        entityImage = allFrames.get(0);
        allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_blade_1));

        spawnAtTop();
        velY = 10;
    }

    @Override
    void goVertical(){
        super.goVertical();
        posY += increaseVelY;
    }


}
