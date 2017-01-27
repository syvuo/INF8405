package com.example.sly.a8405_tp1.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

    private static int gridColumns = 0;
    private static int gridRows = 0;
    private final int defaultGridColumns = 8;
    private final int defaultGridRows = 8;

    GridView gridView;

    // source: https://www.mkyong.com/android/android-gridview-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);
        Bundle bundleExtra = getIntent().getExtras();
        setupGrid(bundleExtra);
    }

    private void setupGrid(Bundle bundleExtra){
        gridColumns = bundleExtra.get("gridColumns") != null ? (int)bundleExtra.get("gridColumns") : defaultGridColumns;
        gridRows = bundleExtra.get("gridRows") != null ? (int)bundleExtra.get("gridRows") : defaultGridRows;

        gridView = (GridView) findViewById(R.id.gridView1);

        gridView.setNumColumns(gridColumns);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        ArrayList<Integer> objects = new ArrayList<Integer>(gridColumns*gridRows);
        for(int i = 0; i < gridColumns*gridRows; ++i){
            objects.add(i);
        }

        WeightListAdapter adapter = new WeightListAdapter(this, objects);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        Game.setIsStarted(true);
    }

    public static class WeightListAdapter extends ArrayAdapter<Integer> {

        public WeightListAdapter(Context context, List<Integer> objects) {
            super(context, R.layout.simple_list_item_black, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            if(position%10 ==0){
                v.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.blue));
            }
            else {
                v.setBackgroundColor(Color.parseColor("#FF00FF"));
            }

            Random rand = new Random();


            return v;
        }

    }

}
