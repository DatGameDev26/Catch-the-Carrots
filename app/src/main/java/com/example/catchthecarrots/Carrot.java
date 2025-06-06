package com.example.catchthecarrots;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Carrot extends Projectile{
    boolean isGolden;
    public Carrot(Context context) {
        super();
        if(random.nextInt(10) <= 1) isGolden = true;

        if(isGolden){
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_carrot_0));
            entityImage = allFrames.get(0);

            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_carrot_1));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_carrot_2));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_carrot_3));
        }else{
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.carrot_0));
            entityImage = allFrames.get(0);

            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.carrot_1));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.carrot_2));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.carrot_3));
        }
        spawnAtTop();
        velY = 7;
    }
}
