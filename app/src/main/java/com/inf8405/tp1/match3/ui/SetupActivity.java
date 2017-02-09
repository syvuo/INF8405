package com.inf8405.tp1.match3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inf8405.tp1.match3.R;
import com.inf8405.tp1.match3.model.Game;

/**
 * Created by Lam on 1/28/2017.
 */

public class SetupActivity extends AbstractBaseActivity {

    private TextView rulesMsg;
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout containerLayout;
    private PopupWindow popUpWindow;
    private boolean isClicked = true;
    private LinearLayout setupLevelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_menu);
        setupLevelLayout = (LinearLayout)findViewById(R.id.level);
    }

    public void onClick(View v){
        Button btn = (Button)v;
        Intent intent = new Intent(SetupActivity.this, GridActivity.class);
        int levelAllowed = Game.getInstance().getGameLevel();
        int btnLevel = Integer.valueOf(btn.getText().toString().substring(btn.getText().length() - 1));
        if(btnLevel <= levelAllowed){
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //gameMatch3.setIsStarted(false);
            intent.putExtra("level", btn.getText());
            startActivity(intent);
        }
        else {
            popUpWindow = new PopupWindow(this);
            containerLayout = new LinearLayout(this);
            rulesMsg = new TextView(this);
            rulesMsg.setText(setupLevelLayout.getResources().getString(R.string.allowed_level) + " " + ++levelAllowed);
            rulesMsg.setBackgroundColor(Color.WHITE);

            layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            containerLayout.setOrientation(LinearLayout.VERTICAL);
            containerLayout.addView(rulesMsg, layoutParams);
            popUpWindow.setContentView(containerLayout);
            if (isClicked) {
                isClicked = false;
                popUpWindow.showAtLocation(setupLevelLayout, Gravity.CENTER, 0, 0);
                //popUpWindow.update(5, 10, 320, 90);
            } else {
                closePopUpWindow();
            }
        }
    }

    public void mainOnClick(View v){
        closePopUpWindow();
    }

    private void closePopUpWindow(){
        isClicked = true;
        popUpWindow.dismiss();
    }
}
