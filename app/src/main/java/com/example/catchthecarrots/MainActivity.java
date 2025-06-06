package com.example.catchthecarrots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView selectedBackgroundTextView;
    private Button dayButton;
    private Button sunsetButton;
    private Button nightButton;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button backgroundButton = findViewById(R.id.backgroundButton);
        dayButton = findViewById(R.id.background_day);
        sunsetButton = findViewById(R.id.background_sunset);
        nightButton = findViewById(R.id.background_night);
        selectedBackgroundTextView = findViewById(R.id.selectedBackgroundTextView);

        ImageButton exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBackgroundButtonsVisibility();
            }
        });


        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedBackground(R.drawable.sky_day);
            }
        });

        sunsetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedBackground(R.drawable.sky_sunset);
            }
        });

        nightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedBackground(R.drawable.sky_night);
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        displayHighScore();

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    private void toggleBackgroundButtonsVisibility() {
        if (dayButton.getVisibility() == View.VISIBLE) {
            dayButton.setVisibility(View.GONE);
            sunsetButton.setVisibility(View.GONE);
            nightButton.setVisibility(View.GONE);
        } else {
            dayButton.setVisibility(View.VISIBLE);
            sunsetButton.setVisibility(View.VISIBLE);
            nightButton.setVisibility(View.VISIBLE);
        }
    }

    private void saveSelectedBackground(int resourceId) {
        SharedPreferences prefs = getSharedPreferences("my_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("selected_background", resourceId);
        editor.apply();

        String backgroundName;
        if (resourceId == R.drawable.sky_day) {
            backgroundName = "Day";
        } else if (resourceId == R.drawable.sky_sunset) {
            backgroundName = "Sunset";
        } else if (resourceId == R.drawable.sky_night) {
            backgroundName = "Night";
        } else {
            backgroundName = "Unknown"; // Trường hợp mặc định
        }

        selectedBackgroundTextView.setText("Selected background: " + backgroundName);
        selectedBackgroundTextView.setVisibility(View.VISIBLE);

        handler.removeCallbacksAndMessages(null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectedBackgroundTextView.setVisibility(View.GONE);
            }
        }, 3000);

        toggleBackgroundButtonsVisibility();
    }

    public void displayHighScore() {
        TextView tvCurrentHighScore = findViewById(R.id.currentHighScore);
        SharedPreferences sharedPreferences = getSharedPreferences("my_pref", 0);

        int highest = sharedPreferences.getInt("highest", 0);

        if (highest > 0) {
            tvCurrentHighScore.setText("Best Score: " + highest);
            tvCurrentHighScore.setVisibility(View.VISIBLE);
        } else {
            tvCurrentHighScore.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, MusicService.class));
    }

    public void startGame(View view) {
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MusicService.class));
    }
}
