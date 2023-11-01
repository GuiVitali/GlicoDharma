package com.example.glicodharma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TelaInicialInfo extends AppCompatActivity {

    ViewPager mSLideViewPager;

    LinearLayout mDotLayout;

    Button backbtn, nextbtn, skipbtn;

    TextView[] dots;

    ViewPagerAdapterInfo viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial_info);

        backbtn = findViewById(R.id.btnVoltar);
        nextbtn = findViewById(R.id.btnProximo);
        skipbtn = findViewById(R.id.btnPular);

        Intent intent = getIntent();
        String whatToDo = intent.getStringExtra("whatToDo");
        String username = intent.getStringExtra("username");
        String nome = intent.getStringExtra("nome");
        String email = intent.getStringExtra("email");
        String telefone = intent.getStringExtra("telefone");
        String senha = intent.getStringExtra("senha");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getitem(0) > 0){

                    mSLideViewPager.setCurrentItem(getitem(-1),true);

                }

            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getitem(0) < 3)
                    mSLideViewPager.setCurrentItem(getitem(1),true);
                else {

                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("nome", nome);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    intent.putExtra("telefone", telefone);
                    intent.putExtra("senha", senha);
                    intent.putExtra("whatToDo", "Login");
                    startActivity(intent);

                }

            }
        });

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("nome", nome);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                intent.putExtra("telefone", telefone);
                intent.putExtra("senha", senha);
                intent.putExtra("whatToDo", "Login");
                startActivity(intent);

            }
        });

        mSLideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapterInfo(this);

        mSLideViewPager.setAdapter(viewPagerAdapter);

        setUpindicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);

    }

    public void setUpindicator(int position){

        dots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0 ; i < dots.length ; i++){

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#C4C4C4"));
            mDotLayout.addView(dots[i]);

        }

        dots[position].setTextColor(Color.parseColor("#50C2C9"));

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setUpindicator(position);

            if (position > 0){

                backbtn.setVisibility(View.VISIBLE);

            }else {

                backbtn.setVisibility(View.INVISIBLE);

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getitem(int i){

        return mSLideViewPager.getCurrentItem() + i;
    }

}