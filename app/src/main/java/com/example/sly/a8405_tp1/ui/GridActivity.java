package com.example.sly.a8405_tp1.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sly.a8405_tp1.R;
import com.example.sly.a8405_tp1.model.Game;

/**
 * Created by Lam on 1/26/2017.
 */

public class GridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);
        Object bundleExtra = getIntent().getExtras().get("gricColumns");
        int gridCols = bundleExtra != null ? (int)bundleExtra : Game.getDefaultGridColumns();
        Game.setGridColumns(gridCols);
        Game.setIsStarted(true);
    }

}
