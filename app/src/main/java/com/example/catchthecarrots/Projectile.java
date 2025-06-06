package com.example.catchthecarrots;

import java.util.Random;

public class Projectile extends AnimatedEntity{

    Random random;
    int velY;

    public Projectile() {
        random = new Random();
    }

    void goVertical(){
        posY += velY;
    }

    void spawnAtTop(){
        posY = -600;
        posX = random.nextInt(GameView.dWidth - getEntityWidth());
    }

}
