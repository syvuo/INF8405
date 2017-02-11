package com.inf8405.tp1.match3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.Toast;

import com.inf8405.tp1.match3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.inf8405.tp1.match3.model.*;

/**
 * Created by Lam on 1/26/2017.
 */

public class GridActivity extends AbstractBaseActivity {

    private static int level = 0;
    private static final int DEFAULT_LEVEL = 0;
    private static GridLayout table = null;
    private static List<Cell> cells = new ArrayList<Cell>();
    private int cellSpacing = 5;
    private int tableRows = 8;
    private int tableColumns = 8;

    private final int LEVEL1_COL = 5;
    private final int LEVEL2_COL = 6;
    private final int LEVEL3_ROW = 7;
    private final int LEVEL3_COL = 7;
    private final int LEVEL4_ROW = 7;
    private final int LEVEL4_COL = 8;

    // source: https://www.mkyong.com/android/android-gridview-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startNewGame();
    }

    protected void startNewGame(){
        Intent intent = getIntent();
        cells = new ArrayList<>();
        setContentView(R.layout.grid_layout);

        Bundle bundleExtra = intent.getExtras();
        setupGrid(bundleExtra);
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
                //table.removeAllViews();
                //table = null;
                //clearAttributes();
                closeAppDialog(SetupActivity.class);
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            // Refresh button
            case R.id.action_refresh:
                table.removeAllViews();
                table = null;
                cells = new ArrayList<>();
                clearAttributes();
                gameMatch3.clearData();
                setupGrid(level);

                table.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        switch(v.getId()) {
                            case R.id.view_root:
                                try{
                                    // TODO delete try catch
                                    gameMatch3.scanCells(null);
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }

                });
                //onCreate(new Bundle());
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        table.removeAllViews();
        closeAppDialog();
        clearAttributes();
    }

    private void setupGrid(Bundle bundleExtra){
        cellSpacing = gameMatch3.getCellSpacing();
        String extra = bundleExtra.get("level").toString();
        level = extra != null ? Integer.valueOf(extra.substring(extra.length() - 1)) : DEFAULT_LEVEL;
        setupGrid(level);
    }

    private void setupGrid(int level){

        switch(level){
            case 1:
                tableColumns = LEVEL1_COL;
                break;
            case 2:
                tableColumns = LEVEL2_COL;
                break;
            case 3:
                tableRows = LEVEL3_ROW;
                tableColumns = LEVEL3_COL;
                break;
            case 4:
                tableRows = LEVEL4_ROW;
                tableColumns = LEVEL4_COL;
                break;
            default: // for future usage
        }
        table = new GridLayout(this);
        table = (GridLayout) findViewById(R.id.view_root);
        table.setColumnCount(tableColumns);
        table.setRowCount(tableRows);

        Random rand = new Random();

        // Generate table grid
        int btnPos = 0;
        for (int y = 0; y < tableRows; ++y) {
            for (int x = 0; x < tableColumns; ++x) {
                //Cell btn = new Cell(this, x, y, table);
                Cell btn = new Cell(this, rand, btnPos, table);

                btn.overrideEventListener(btn, gameMatch3);
                //btn = createButton(this, rand, btnPos, btn);
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

                        Log.d("width", cellWidth + "" + " cell spacing " + cellSpacing);

                        for (int yPos = 0; yPos < tableRows; yPos++) {
                            for (int xPos = 0; xPos < tableColumns; xPos++) {
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams) cells.get(yPos * tableColumns + xPos).getLayoutParams();
                                params.width = cellWidth - 2 * cellSpacing;
                                params.height = cellHeight - 2 * cellSpacing;

                                params.setMargins(cellSpacing, cellSpacing, cellSpacing, cellSpacing);
                                cells.get(yPos * tableColumns + xPos).setLayoutParams(params);
                                //cells.get(yPos * tableColumns + xPos).setCellId(yPos * tableColumns + xPos);
                            }
                        }
                        // Update neighbours (left, right, top and bottom)
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

        gameMatch3.setTableColumns(tableColumns);
        gameMatch3.setTableLayout(table);
        gameMatch3.setIsStarted(getBaseContext(), true, this, level);

        table.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()) {
                    case R.id.view_root:
                        try{
                            // TODO delete try catch when appropriate
                            gameMatch3.scanCells(null);
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        finally{
                            //clearAlphaAllCells();
                        }
                        break;
                }
            }
        });
    }

    private void clearAttributes(){
        table = null;
        gameMatch3.clearData();
        gameMatch3 = Game.getInstance();
    }
}
