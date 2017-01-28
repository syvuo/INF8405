package com.example.sly.a8405_tp1.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import com.example.sly.a8405_tp1.MainActivity;
import com.example.sly.a8405_tp1.R;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.example.sly.a8405_tp1.model.*;

/**
 * Created by Lam on 1/26/2017.
 */

public class GridActivity extends AbstractBaseActivity {

    private static int level = 0;
    private static final int LEVEL = 0;
    private static TableLayout table = null;
    private static final int [] colorsArray = {R.color.blue, R.color.green, R.color.orange, R.color.purple, R.color.red};
    private static List<Cell> cellArrays = new ArrayList<>();

    // source: https://www.mkyong.com/android/android-gridview-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.grid_layout);
        if(table == null){
            Bundle bundleExtra = intent.getExtras();
            setupGrid(bundleExtra);
        }
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        clearAttributes();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        //Intent setIntent = new Intent(Intent.ACTION_MAIN);
        //setIntent.addCategory(Intent.CATEGORY_HOME);
        //setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Intent intent = new Intent(GridActivity.this, MainActivity.class);
        //moveTaskToBack(true);
        //startActivity(intent);
        closeAppDialog();
        clearAttributes();
    }
    private void setupGrid(Bundle bundleExtra){
        String extra = bundleExtra.get("level").toString();
        level = extra != null ? Integer.valueOf(extra.substring(extra.length() - 1)) : LEVEL;

        int tableRows = 8;
        int tableColumns = 8;
        switch(level){
            case 1:
                popToast(level);
                tableColumns = 5;
                break;
            case 2:
                popToast(level);
                tableColumns = 6;
                break;
            case 3:
                popToast(level);
                tableRows = 7;
                tableColumns = 7;
                break;
            case 4:
                popToast(level);
                tableRows = 7;
                tableColumns = 8;
                break;
        }
        table = (TableLayout) findViewById(R.id.view_root);

        Random rand = new Random();

        // Generate table grid
        for (int y = 0; y < tableRows; y++) {
            TableRow rows = new TableRow(this);
            table.addView(rows);
            for (int x = 0; x < tableColumns; x++) {
                rows.addView(createButton(this, rand));
            }
        }
        Game.setIsStarted(true);

        Button test = (Button)this.findViewById(cellArrays.get(4).getId());
        test.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
    }

    private void scanCells() {
        for(int i = 0; i < cellArrays.size(); ++i){
            Cell cell = cellArrays.get(i);
            if(cell.isSelected()){
                Toast.makeText(this, "(" + cell.getId() + ")", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Button createButton(final GridActivity gridActivity, final Random rand) {
        final Cell btn = new Cell(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        btn.setLayoutParams(params);
        btn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape));
        final GradientDrawable bgShape = (GradientDrawable) btn.getBackground();
        bgShape.setColor(ContextCompat.getColor(this, colorsArray[rand.nextInt(colorsArray.length)]));
        btn.setId(btn.generateViewId());
        btn.overrideEventListener(btn, gridActivity, bgShape);
        cellArrays.add(btn);
        return btn;
    }

    public void tableClick(View view){
        switch(view.getId()) {
            case R.id.view_root:
                // Load image from Drawable folder
                CharSequence test = " gg;";
                Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void clearAttributes(){
        table = null;
        cellArrays = new ArrayList<>();
    }
}
