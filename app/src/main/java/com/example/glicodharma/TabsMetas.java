package com.example.glicodharma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class TabsMetas extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    MyViewPagerAdapter myViewPagerAdapter;

    String _USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_metas);

        Intent intent = getIntent();
        _USERNAME = intent.getStringExtra("username");
        SharedPreferences sharedPreferences = getSharedPreferences("salvarUsername", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", _USERNAME);
        editor.apply();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        myViewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager.setAdapter(myViewPagerAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("MISSÕES"));
        tabLayout.addTab(tabLayout.newTab().setText("MEDALHAS"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }
}