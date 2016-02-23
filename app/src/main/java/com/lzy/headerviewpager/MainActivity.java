package com.lzy.headerviewpager;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pagerHeader:
                startActivity(new Intent(this, PagerHeaderActivity.class));
                break;
            case R.id.parallaxHeader:
                startActivity(new Intent(this, ParallaxHeaderActivity.class));
                break;
        }
    }
}
