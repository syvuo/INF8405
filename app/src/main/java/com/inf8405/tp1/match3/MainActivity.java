package com.inf8405.tp1.match3;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import com.inf8405.tp1.match3.ui.SetupActivity;

public class MainActivity extends AbstractBaseActivity {
    private PopupWindow popUpWindow;
    private CoordinatorLayout mainLayout;
    private boolean isClicked = true;
    private TextView rulesMsg;
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout containerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        containerLayout = new LinearLayout(this);
        mainLayout = (CoordinatorLayout)findViewById(R.id.activity_main);
        popUpWindow = new PopupWindow(this);
        rulesMsg = new TextView(this);
        rulesMsg.setText(R.string.rules);
        rulesMsg.setBackgroundColor(Color.WHITE);

        layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.addView(rulesMsg, layoutParams);
        popUpWindow.setContentView(containerLayout);
        setContentView(mainLayout);
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
        closePopUpWindow();
        this.startActivity(intent);
    }

    public void rulesButtonClicked(View v){
        popToast("Rules Clicked");
        if (isClicked) {
            isClicked = false;
            popUpWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
            //popUpWindow.update(5, 10, 320, 90);
        } else {
            closePopUpWindow();
        }
    }

    public void mainOnClick(View v){
        closePopUpWindow();
    }

    public void exitButtonClicked(View v){
        popToast("Application Closed");
        finish();
    }

    private void closePopUpWindow(){
        isClicked = true;
        popUpWindow.dismiss();
    }
}
