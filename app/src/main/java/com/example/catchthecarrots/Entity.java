package com.example.catchthecarrots;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Entity {

    Bitmap entityImage;
    float posX, posY;

    public void setPos(float posX, float posY){
        this.posX = posX;
        this.posY = posY;
    }


    public boolean collideWithThisEntity(Entity collider){
        if(collider == null) return false;

        return posX <= collider.posX + collider.getEntityWidth()
                && posX + getEntityWidth() >= collider.posX
                && posY <= collider.posY + collider.getEntityHeight()
                && posY + getEntityHeight() >= collider.posY;
    }

    public void drawOnThisCanvas(Canvas canvas){
        canvas.drawBitmap(entityImage, posX, posY, null);
    }

    public int getEntityWidth(){
        if(entityImage == null) return 0;
        return entityImage.getWidth();
    }

    public int getEntityHeight(){
        if(entityImage == null) return 0;
        return entityImage.getHeight();
    }

}
