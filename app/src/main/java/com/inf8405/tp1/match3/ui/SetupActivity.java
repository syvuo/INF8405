package com.inf8405.tp1.match3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.inf8405.tp1.match3.R;

/**
 * Created by Lam on 1/28/2017.
 */

public class SetupActivity extends AbstractBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_menu);
    }

    public void onClick(View v){
        Button btn = (Button)v;
        Intent intent = new Intent(SetupActivity.this, GridActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        gameMatch3.setIsStarted(false);
        intent.putExtra("level", btn.getText());
        startActivity(intent);
    }
}
