package com.example.glicodharma;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.SharedPreferences;
import android.util.Pair;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    //Variáveis
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animações
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        SharedPreferences toLogado = getSharedPreferences("toLogado", MODE_PRIVATE);
        boolean logado = toLogado.getBoolean("logado", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(logado){
                    Intent intent = new Intent(MainActivity.this, Dashboard.class);
                    intent.putExtra("whatToDo", "recuperarDados");

                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View,String>(image, "logo_img");
                    pairs[1] = new Pair<View,String>(logo, "logo_text");
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, Login.class);

                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View,String>(image, "logo_img");
                    pairs[1] = new Pair<View,String>(logo, "logo_text");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                    finish();
                }
            }
        },SPLASH_SCREEN);
    }
}