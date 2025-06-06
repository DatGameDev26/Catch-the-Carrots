package com.example.catchthecarrots;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Explo extends AnimatedEntity{

    public Explo(Context context, String whichExplo) {

        if(whichExplo.equals("Carrot"))
        {
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.carrot_explo_0));
            entityImage = allFrames.get(0);
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.carrot_explo_1));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.carrot_explo_2));
        }
        else if(whichExplo.equals("Gold Carrot"))
        {
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_carrot_explo_0));
            entityImage = allFrames.get(0);
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_carrot_explo_1));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_carrot_explo_2));
        }
        else
        {
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_blade_explo_0));
            entityImage = allFrames.get(0);
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_blade_explo_1));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_blade_explo_2));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_blade_explo_3));
            allFrames.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_blade_explo_4));
        }


    }
}