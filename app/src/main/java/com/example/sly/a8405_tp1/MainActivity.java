package com.example.sly.a8405_tp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.sly.a8405_tp1.ui.AbstractBaseActivity;
import com.example.sly.a8405_tp1.ui.SetupActivity;
import com.example.sly.a8405_tp1.utility.DrawingView;
import com.example.sly.a8405_tp1.model.Game;
import com.example.sly.a8405_tp1.ui.GridActivity;

public class MainActivity extends AbstractBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        dv = new DrawingView(this, mPaint);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startButtonClicked(View v){
        popToast("Starting new game");
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Game.setIsStarted(false);
        this.startActivity(intent);
    }

    public void rulesButtonClicked(View v){
        popToast("Rules Clicked");
    }

    public void exitButtonClicked(View v){
        popToast("Application Closed");
        finish();
    }
}
