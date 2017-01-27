package com.example.sly.a8405_tp1.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sly.a8405_tp1.R;
import com.example.sly.a8405_tp1.model.Game;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lam on 1/26/2017.
 */

public class GridActivity extends AppCompatActivity {

    private static int tableColumns = 0;
    private static int tableRows = 0;
    private static final int DEFAULT_TABLE_WIDTH = 8;
    private static final int DEFAULT_TABLE_HEIGHT = 8;
    TableLayout table;
    private static final int [] colorsArray = {R.color.blue, R.color.green, R.color.orange, R.color.purple, R.color.red};

    // source: https://www.mkyong.com/android/android-gridview-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);
        Bundle bundleExtra = getIntent().getExtras();
        setupGrid(bundleExtra);
    }

    private void setupGrid(Bundle bundleExtra){
        tableColumns = bundleExtra.get("gridColumns") != null ? (int)bundleExtra.get("gridColumns") : DEFAULT_TABLE_WIDTH;
        tableRows = bundleExtra.get("gridRows") != null ? (int)bundleExtra.get("gridRows") : DEFAULT_TABLE_HEIGHT;

        table = (TableLayout) findViewById(R.id.view_root);
        Random rand = new Random();

        // Generate table grid
        for (int y = 0; y < tableRows; y++) {
            final int row = y;
            TableRow cell = new TableRow(this);
            table.addView(cell);
            for (int x = 0; x < tableColumns; x++) {
                final int col = x;
                Button btn = new Button(this);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                btn.setLayoutParams(params);
                btn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape));

                GradientDrawable bgShape = (GradientDrawable) btn.getBackground();
                bgShape.setColor(ContextCompat.getColor(this, colorsArray[rand.nextInt(colorsArray.length)]));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "You clicked (" + row + "," + col + ")", Toast.LENGTH_SHORT).show();
                    }
                });
                cell.addView(btn);
            }
        }
        Game.setIsStarted(true);
    }
}
