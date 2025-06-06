package com.example.catchthecarrots;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View{
    static int dWidth, dHeight;
    static float groundPosY;
    final long UPDATE_MILIS = 10;
    final int minSpawnInterval = 500;
    private final List<Blade> allBlades = new ArrayList<>();
    private final List<Carrot> allCarrots = new ArrayList<>();
    private final List<Explo> allExplos = new ArrayList<>();

    Bitmap skyBackground;
    Context context;
    int spawnInterval = 2000;
    int spawnCarrotInterval = 2500;
    Handler mainHandler;
    Runnable mainRunnable;

    Paint scorePaint = new Paint();

    float TEXT_SIZE = 130;
    float oldX;
    float oldBunnyPosX;
    int points = 0;
    int pointIncrease = 1;
    final int maximumIncrease = 50;
    int life = 3;
    int carrotCount = 0;

    Random random;
    Entity bunny, ground;
    Bitmap bunnyIcon;
    private SoundPool soundPool;

    public GameView(Context context) {
        super(context);
        this.context = context;
        random = new Random();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dWidth = displayMetrics.widthPixels;
        dHeight = displayMetrics.heightPixels;

        initEntities();
        initAllHandlerRunnable();
        initAudio();

        scorePaint.setColor(Color.rgb(255, 245, 166));
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        scorePaint.setTypeface(ResourcesCompat.getFont(context, R.font.minecraft));

        changeSkyBackground();
    }

    private void initEntities() {
        ground = new Entity();
        bunny = new Entity();

        skyBackground = BitmapFactory.decodeResource(getResources(), R.drawable.sky_sunset);
        ground.entityImage = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        bunny.entityImage = BitmapFactory.decodeResource(getResources(), R.drawable.bunny);

        Bitmap originalBunnyIcon = bunny.entityImage;
        int iconWidth = originalBunnyIcon.getWidth() / 2;
        int iconHeight = originalBunnyIcon.getHeight() / 2;
        bunnyIcon = Bitmap.createScaledBitmap(originalBunnyIcon, iconWidth, iconHeight, false);

        ground.posY = dHeight - ground.getEntityHeight() + 100;
        groundPosY = ground.posY;
        bunny.setPos((float) (dWidth / 2 - bunny.getEntityWidth() / 2)
                , ground.posY - bunny.getEntityHeight());

        Blade.increaseVelY = 0;
    }

    public void changeSkyBackground() {
        SharedPreferences prefs = context.getSharedPreferences("my_pref", Context.MODE_PRIVATE);
        int backgroundResId = prefs.getInt("selected_background", R.drawable.sky_day);

        if (backgroundResId == R.drawable.sky_day) {
            scorePaint.setColor(Color.rgb(50, 50, 50));
        } else if (backgroundResId == R.drawable.sky_sunset) {
            scorePaint.setColor(Color.rgb(255, 245, 166));
        } else if (backgroundResId == R.drawable.sky_night) {
            scorePaint.setColor(Color.rgb(200, 200, 200));
        } else {
            scorePaint.setColor(Color.rgb(255, 245, 166));
        }

        skyBackground = BitmapFactory.decodeResource(getResources(), backgroundResId);
        invalidate(); // Redraw the view with the new background
    }

    private void initAllHandlerRunnable() {
        mainHandler = new Handler(Looper.getMainLooper());
        mainRunnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
                mainHandler.postDelayed(this, UPDATE_MILIS);
            }
        };
        mainHandler.postDelayed(mainRunnable, UPDATE_MILIS);

        Runnable bladeRunnable = new Runnable() {
            @Override
            public void run() {
                spawnBlade();
                mainHandler.postDelayed(this, spawnInterval);
            }
        };
        Runnable carrotRunnable = new Runnable() {
            @Override
            public void run() {
                spawnCarrot();
                mainHandler.postDelayed(this, spawnCarrotInterval);
            }
        };
        Runnable intervalRunnable = new Runnable() {
            @Override
            public void run() {
                updateSpawnInterval();
                mainHandler.postDelayed(this, 2000);
            }
        };

        mainHandler.post(bladeRunnable);
        mainHandler.post(carrotRunnable);
        mainHandler.post(intervalRunnable);
    }

    private void initAudio() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.load(context, R.raw.eat_crunch_0, 1);
        soundPool.load(context, R.raw.eat_crunch_1, 1);
        soundPool.load(context, R.raw.blade_break_0, 1);
        soundPool.load(context, R.raw.game_over, 1);
        soundPool.load(context, R.raw.blade_hit, 1);
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(skyBackground, 0, 0, null);
        ground.drawOnThisCanvas(canvas);
        bunny.drawOnThisCanvas(canvas);

        for (int i = 0; i < allBlades.size(); i++) {
            Blade blade = allBlades.get(i);
            blade.drawOnThisCanvas(canvas);
            blade.updateAnimation();
            blade.goVertical();

            if (blade.collideWithThisEntity(ground) || blade.collideWithThisEntity(bunny)){
                if (blade.collideWithThisEntity(bunny)) {
                    life--;
                    shakeScreen();
                    playSFX(5);
                    if (life == 0) {
                        playSFX(4);
                        Intent intent = new Intent(context, GameOver.class);
                        intent.putExtra("points", points);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }
                Explo bladeExplo = new Explo(context, "Blade");
                bladeExplo.setPos(blade.posX + (float) blade.getEntityWidth() / 2
                        - bladeExplo.getEntityWidth()/2, blade.posY - 50);
                allExplos.add(bladeExplo);
                allBlades.remove(blade);
            }
        }
        for (int i = 0; i < allCarrots.size(); i++) {
            Carrot carrot = allCarrots.get(i);
            carrot.drawOnThisCanvas(canvas);
            carrot.updateAnimation();
            carrot.goVertical();

            if (carrot.collideWithThisEntity(bunny)) {
                String carrotExploType = "Carrot";
                if(carrot.isGolden){
                    points += pointIncrease*2;
                    carrotExploType = "Gold Carrot";
                }else{
                    points += pointIncrease;
                }
                if(pointIncrease < maximumIncrease) pointIncrease++;
                carrotCount++;
                playSFX(random.nextInt(2) + 1);

                Explo carrotExplo = new Explo(context, carrotExploType);
                carrotExplo.setPos(carrot.posX + (float) carrot.getEntityWidth() / 2
                        - (float) carrotExplo.getEntityWidth() / 2, carrot.posY);

                allExplos.add(carrotExplo);
                allCarrots.remove(carrot);
            } else if (carrot.collideWithThisEntity(ground)) {
                allCarrots.remove(carrot);
            }
        }
        for (int i = 0; i < allExplos.size(); i++) {
            Explo explo = allExplos.get(i);
            explo.drawOnThisCanvas(canvas);
            explo.updateAnimation();
            if (explo.doneOneLoop) allExplos.remove(explo);
        }

        for (int i = 0; i < life; i++) {
            float bunnyIconX = dWidth - (i + 1) * (bunnyIcon.getWidth() + 20);
            float bunnyIconY = 20; // Vị trí y cố định
            canvas.drawBitmap(bunnyIcon, bunnyIconX, bunnyIconY, null);
        }
        canvas.drawText("" + points, 20, TEXT_SIZE, scorePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            oldX = event.getX();
            oldBunnyPosX = bunny.posX;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            float shift = oldX - touchX;
            float newRabbitX = oldBunnyPosX - shift;
            if (newRabbitX <= 0) bunny.posX = 0;
            else if (newRabbitX >= dWidth - bunny.getEntityWidth()) {
                bunny.posX = dWidth - bunny.getEntityWidth();
            } else bunny.posX = (int) newRabbitX;
        }
        return true;
    }

    private void spawnBlade() {
        Blade newBlade = new Blade(context);
        allBlades.add(newBlade);
    }

    private void spawnCarrot() {
        Carrot newCarrot = new Carrot(context);
        allCarrots.add(newCarrot);
    }

    private void updateSpawnInterval() {
        spawnInterval -= 100;
        if (spawnInterval <= minSpawnInterval) {
            spawnInterval = minSpawnInterval;
            return;
        }
        Blade.increaseVelY++;
    }

    private void playSFX(int sfxIndex) {
        soundPool.play(sfxIndex, 1, 1, 1, 0, 1);
    }
    public void shakeScreen() {
        // Create a TranslateAnimation that moves the view along the x-axis back and forth
        TranslateAnimation shake = new TranslateAnimation(-10, 10, 0, 0);
        shake.setDuration(100); // Set the duration of each shake
        shake.setRepeatCount(5); // Set the number of shakes
        shake.setRepeatMode(Animation.REVERSE); // Move in the reverse direction for back and forth motion

        // Start the animation on the GameView
        this.startAnimation(shake);
    }

}
