package com.example.sly.a8405_tp1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.sly.a8405_tp1.utility.DrawingView;
import com.example.sly.a8405_tp1.model.Game;
import com.example.sly.a8405_tp1.ui.GridActivity;

public class MainActivity extends AppCompatActivity {

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


    public void startButtonClicked(View v){
        popToast("Starting new game");
        Intent intent = new Intent(getApplicationContext(), GridActivity.class);
        Game.setIsStarted(false);
        intent.putExtra("gridColumns", 8);
        startActivity(intent);

    }

    public void rulesButtonClicked(View v){
        popToast("Rules Clicked");

    }

    public void exitButtonClicked(View v){
        popToast("Application Closed");
        finish();
    }

    private void popToast(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
