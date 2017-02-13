package com.inf8405.tp1.match3;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import com.inf8405.tp1.match3.ui.SetupActivity;

public class MainActivity extends AbstractBaseActivity {
    private CoordinatorLayout mainLayout;
    private TextView rulesMsg;
    private LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (CoordinatorLayout)findViewById(R.id.activity_main);
        rulesMsg = new TextView(this);
        rulesMsg.setText(R.string.rules);
        rulesMsg.setBackgroundColor(Color.WHITE);

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
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
        this.startActivity(intent);
    }

    public void rulesButtonClicked(View v){
        popToast("Rules Clicked");
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.rules)).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).show();
    }

    public void exitButtonClicked(View v){
        popToast("Application Closed");
        killApp();
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
