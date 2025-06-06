package com.example.catchthecarrots;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class AnimatedEntity extends Entity{
    protected ArrayList<Bitmap> allFrames = new ArrayList<>();

    int animateCountdown = 0;
    int currentFrame = 0;
    boolean doneOneLoop = false;

    public void updateAnimation(){
        int animationLength = allFrames.size();
        if(animationLength <= 1) return;

        //  To slow down the animation
        animateCountdown++;
        if(animateCountdown >= 4){
            animateCountdown = 0;
            currentFrame++;
            if(currentFrame >= animationLength) {
                currentFrame = 0;
                doneOneLoop = true;
            }
            entityImage = allFrames.get(currentFrame);
        }
    }

    public boolean finishOneLoop(){
        return doneOneLoop;
    }
}
