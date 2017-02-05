package com.inf8405.tp1.match3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.inf8405.tp1.match3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.inf8405.tp1.match3.model.*;

/**
 * Created by Lam on 1/26/2017.
 */

public class GridActivity extends AbstractBaseActivity {

    private static int level = 0;
    private static final int DEFAULT_LEVEL = 0;
    private static GridLayout table = null;
    private static final int [] colorsArray = {R.color.blue, R.color.green, R.color.orange, R.color.purple, R.color.red, R.color.yellow};
    private static List<Cell> cells = new ArrayList<Cell>();
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    final int CELL_SPACING = 1;
    private int tableRows = 8;
    private int tableColumns = 8;

    // source: https://www.mkyong.com/android/android-gridview-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.grid_layout);
        if(table == null){
            Bundle bundleExtra = intent.getExtras();
            setupGrid(bundleExtra);

            table.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    switch(v.getId()) {
                        case R.id.view_root:
                            try{
                                // TODO delete try catch
                                gameMatch3.scanCells(getApplicationContext());
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                    }
                }

            });
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
        table = (GridLayout) findViewById(R.id.view_root);
        table.setColumnCount(tableColumns);
        table.setRowCount(tableRows);

        Random rand = new Random();

        // Generate table grid
        int btnPos = 0;
        for (int y = 0; y < tableRows; ++y) {
            for (int x = 0; x < tableColumns; ++x) {
                Cell btn = new Cell(this, x, y, table);
                btn = createButton(this, rand, btnPos, btn);
                cells.add(btn);
                //btn.setGravity(Gravity.FILL);
                table.addView(btn);
                ++btnPos;
            }
        }
        table.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int gridLayoutWidth = table.getWidth();
                        int gridLayoutHeight = table.getHeight();
                        int cellWidth = gridLayoutWidth / tableColumns;
                        int cellHeight = gridLayoutHeight / tableRows;

                        for (int yPos = 0; yPos < tableRows; yPos++) {
                            for (int xPos = 0; xPos < tableColumns; xPos++) {
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams) cells.get(yPos * tableColumns + xPos).getLayoutParams();
                                params.width = cellWidth - 2 * CELL_SPACING;
                                params.height = cellHeight - 2 * CELL_SPACING;
                                params.setMargins(CELL_SPACING, CELL_SPACING, CELL_SPACING, CELL_SPACING);
                                cells.get(yPos * tableColumns + xPos).setLayoutParams(params);
                                //cells.get(yPos * tableColumns + xPos).setCellId(yPos * tableColumns + xPos);
                            }
                        }
                        // Register neighbours

                        for (int y = 0; y < tableRows; ++y) {
                            for (int x = 0; x < tableColumns; ++x) {
                                Cell cell = cells.get(y * tableColumns + x);
                                // Bottom neighbour
                                if (y < tableRows - 1) {
                                    cell.setBottomCell(cells.get((y + 1) * tableColumns + x));
                                }
                                // Right neighbour
                                if (x < tableColumns - 1) {
                                    cell.setRightCell(cells.get(y * tableColumns + x + 1));
                                }
                                // Top neighbour
                                if (y > 0) {
                                    cell.setTopCell(cells.get((y - 1) * tableColumns + x));
                                }
                                // Left neighbour
                                if (x > 0) {
                                    cell.setLeftCell(cells.get(y * tableColumns + x - 1));
                                }
                            }
                        }
                        table.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
        );
        /*
        for (int y = 0; y < tableRows; ++y) {
            for (int x = 0; x < tableColumns; ++x) {
                Cell cell = cells.get(y * tableColumns + x);
                // Bottom neighbour
                if (y < tableRows - 1) {
                    cell.setBottomCell(cells.get((y + 1) * tableColumns + x));
                }
                // Right neighbour
                if (x < tableColumns - 1) {
                    cell.setRightCell(cells.get(y * tableColumns + x + 1));
                }
                // Top neighbour
                if (y > 0) {
                    cell.setTopCell(cells.get((y - 1) * tableColumns + x));
                }
                // Left neighbour
                if (x > 0) {
                    cell.setLeftCell(cells.get(y * tableColumns + x - 1));
                }
            }
        }*/
        gameMatch3.setTableColumns(tableColumns);
        gameMatch3.setTableRows(tableRows);
        gameMatch3.setTableLayout(table);
        gameMatch3.setIsStarted(true);
    }

    private Cell createButton(final GridActivity gridActivity, final Random rand, int text, Cell btn) {

        int colorTemp = colorsArray[rand.nextInt(colorsArray.length)];
        //params.weight = 1;
        //btn.setLayoutParams(params);


        //ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(this,colorTemp));
        //btn.setBackground(cd);
        btn.setBackground(ContextCompat.getDrawable(this, R.drawable.shape));

        final GradientDrawable bgShape = (GradientDrawable) btn.getBackground();
        bgShape.setColor(ContextCompat.getColor(this, colorTemp));


        btn.setText(String.valueOf(text));
        btn.setTextColor(ContextCompat.getColor(this, colorTemp));

        btn.overrideEventListener(btn, gridActivity, gameMatch3);
        //gameMatch3.addCell(btn);
        return btn;
    }


    private void clearAttributes(){
        table = null;
        gameMatch3.clearData();
        gameMatch3 = Game.getInstance();
    }
}
