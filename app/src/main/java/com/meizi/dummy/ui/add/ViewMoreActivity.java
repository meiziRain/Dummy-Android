package com.meizi.dummy.ui.add;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.meizi.dummy.R;

public class ViewMoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar背景颜色
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setElevation(0);//去除标题栏阴影.
        setContentView(R.layout.activity_view_more);
    }
}
