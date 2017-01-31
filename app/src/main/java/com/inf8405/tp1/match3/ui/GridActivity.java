package com.inf8405.tp1.match3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.inf8405.tp1.match3.R;
import java.util.Random;
import com.inf8405.tp1.match3.model.*;

/**
 * Created by Lam on 1/26/2017.
 */

public class GridActivity extends AbstractBaseActivity {

    private static int level = 0;
    private static final int DEFAULT_LEVEL = 0;
    private static TableLayout table = null;
    private static final int [] colorsArray = {R.color.blue, R.color.green, R.color.orange, R.color.purple, R.color.red, R.color.yellow};
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Close or back button
            case R.id.action_close:
            case android.R.id.home:
                clearAttributes();
                closeAppDialog(SetupActivity.class);
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            // Refresh button
            case R.id.action_refresh:
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    //Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show ();
                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
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
        level = extra != null ? Integer.valueOf(extra.substring(extra.length() - 1)) : DEFAULT_LEVEL;

        int tableRows = 8;
        int tableColumns = 8;
        switch(level){
            case 1:
                tableColumns = 5;
                break;
            case 2:
                tableColumns = 6;
                break;
            case 3:
                tableRows = 7;
                tableColumns = 7;
                break;
            case 4:
                tableRows = 7;
                tableColumns = 8;
                break;
            default: // for future usage
        }
        //popToast(level);
        table = (TableLayout) findViewById(R.id.view_root);

        Random rand = new Random();

        // Generate table grid
        int btnPos = 0;
        for (int y = 0; y < tableRows; y++) {
            final TableRow rows = new TableRow(this);
            table.addView(rows);
            for (int x = 0; x < tableColumns; x++) {
                rows.addView(createButton(this, rand, btnPos));
                ++btnPos;
            }
        }
        gameMatch3.setTableColumns(tableColumns);
        gameMatch3.setTableLayout(table);
        gameMatch3.setIsStarted(true);
    }

    private Button createButton(final GridActivity gridActivity, final Random rand, int text) {
        final Cell btn = new Cell(this);
        int colorTemp = colorsArray[rand.nextInt(colorsArray.length)];
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        btn.setLayoutParams(params);


        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(this,colorTemp));
        btn.setBackground(cd);
        /*btn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape));
        final GradientDrawable bgShape = (GradientDrawable) btn.getBackground();
        bgShape.setColor(ContextCompat.getColor(this, colorTemp));*/


        btn.setText(String.valueOf(text));
        btn.setTextColor(ContextCompat.getColor(this, colorTemp));

        btn.overrideEventListener(btn, gridActivity, gameMatch3);
        gameMatch3.addCell(btn);
        return btn;
    }

    public void tableClick(View view){
        switch(view.getId()) {
            case R.id.view_root:
                gameMatch3.scanCells(getApplicationContext());
                break;
        }
    }

    private void clearAttributes(){
        table = null;
        gameMatch3.clearData();
        gameMatch3 = Game.getInstance();
    }
}
