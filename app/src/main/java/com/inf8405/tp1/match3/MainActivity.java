package com.inf8405.tp1.match3;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.addView(rulesMsg, layoutParams);
        popUpWindow.setContentView(containerLayout);
        setContentView(mainLayout);
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
        } else {
            closePopUpWindow();
        }
    }

    public void mainOnClick(View v){
        closePopUpWindow();
    }

    public void exitButtonClicked(View v){
        popToast("Application Closed");
        killApp();
    }

    private void closePopUpWindow(){
        isClicked = true;
        popUpWindow.dismiss();
    }

    private void killApp(){
        // Termine l activity actuel
        finish();
        // Termine le programme
        System.exit(0);
        // Termine le process par PID
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
