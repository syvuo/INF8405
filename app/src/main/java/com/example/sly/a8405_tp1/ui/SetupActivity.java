package com.example.sly.a8405_tp1.ui;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.sly.a8405_tp1.MainActivity;
import com.example.sly.a8405_tp1.R;
import com.example.sly.a8405_tp1.model.Cell;
import com.example.sly.a8405_tp1.model.Game;

/**
 * Created by Lam on 1/28/2017.
 */

public class SetupActivity extends AbstractBaseActivity {

    private final int numberLevel = 4;

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
        Game.setIsStarted(false);
        intent.putExtra("level", btn.getText());
        startActivity(intent);
    }
}
