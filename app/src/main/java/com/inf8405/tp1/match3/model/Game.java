package com.inf8405.tp1.match3.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inf8405.tp1.match3.R;
import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import com.inf8405.tp1.match3.ui.GridActivity;
import com.inf8405.tp1.match3.ui.SetupActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game extends AbstractBaseActivity {
    private static Game singletonInstance = new Game();
    private Activity currentActivity;
    private boolean isStarted = false;

    private List<Cell> selectedCellArray = new ArrayList<>();
    private List<Cell> colorVerifiedCellArray = new ArrayList<>();
    private List<Cell> cellToRemoveArray = new ArrayList<>();
    private List<Cell> matchFoundArray = new ArrayList<>();
    private List<Cell> comboArray = new ArrayList<>();
    private int nbColumns = -1;
    private GridLayout gameTable;
    private int nbMoves = 100;
    private int currentMove = 0;
    private int scoreToWin = 100;
    private int currentScore = 0;
    private int gameLevel = 1;
    private int comboCount = 1;
    private final int CELL_SPACING = 1;
    private final int LEVEL1_MOVE = 6;
    private final int LEVEL1_SCORE = 800;
    private final int LEVEL2_MOVE = 10;
    private final int LEVEL2_SCORE = 1200;
    private final int LEVEL3_MOVE = 10;
    private final int LEVEL3_SCORE = 1400;
    private final int LEVEL4_MOVE = 10;
    private final int LEVEL4_SCORE = 1800;
    private final int LEVEL_MAX = 4;
    private final int TIME_FADEOUT = 1500;

    private Game(){}

    public static Game getInstance() {
        return singletonInstance;
    }

    public void setIsStarted(boolean value, Activity activity, int level) {
        clearData();
        currentActivity = activity;
        setGameStatus(level);
        isStarted = value;
    }

    public void clearData() {
        //cellArrays = new ArrayList<>();
        comboArray = new ArrayList<>();
        selectedCellArray =  new ArrayList<>();
        nbMoves = 100;
        currentMove = 0;
        scoreToWin = 100;
        currentScore = 0;
    }

    public int getGameLevel(){
        return gameLevel;
    }

    public int getCellSpacing(){
        return CELL_SPACING;
    }

    public void scanCells(Context context, List<Cell> arr){
        scanCells(context, arr, false);
    }

    public void scanCells(Context context, List<Cell> arr, boolean comboCheck) {
        List<Cell> selectedArr = arr;
        boolean switchMode = false;
        if(selectedArr == null){
            selectedArr = selectedCellArray;
            if(selectedArr.size() >= 2){
                switchMode = true;
                swapBtn(selectedArr.get(0), selectedArr.get(1));
            }
        }
        lazyUpdateAllSurroundingAllCells();
        int nbMatch = 0;
        String cellTest = "";
        boolean foundMatch3 = false;
        int doubleMatch3 = 0;
        if(selectedArr.size() > 0){
            // TODO REMOVE DEBUG USAGE
            if(comboCheck){
                Log.d("below", "here" );
                printAllTable();
                printAllTableWithColor();
                Log.d("above", "here" );
            }
            //END TODO

            int i = 0;

            while(i < selectedArr.size()){
                //Log.d("selectedArraySize", selectedCellArrays.size()+ "");
                findSelectedManager(selectedArr.get(i),  selectedArr.get(i).getCurrentTextColor());
                if(matchFoundArray.size()>= 3){
                    // for the swap only
                    foundMatch3 = true;
                    ++doubleMatch3;
                    String matchFoundArrayString = ""; //
                    for(int x = 0; x < matchFoundArray.size(); ++x){
                        Cell cell = matchFoundArray.get(x);
                        matchFoundArrayString+= cell.getText() + " ";
                        cell.setSelected(false);
                        cell.getBackground().setAlpha(0);
                    }
                    // If true, toast combo after updating score
                    if(updateScore(comboCheck)){
                        Toast toast = Toast.makeText(context, getString(R.string.combo_x)+comboCount, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0,0);
                        toast.getView().setBackgroundColor(Color.RED);
                        toast.show();
                    }
                    Log.d("matchFoundArrayString", matchFoundArrayString);
                } else {
                    Log.d("non valid move", "non valid move");
                }
                clearMatchFoundArrays(!foundMatch3);
                ++i;
            }
            if(!foundMatch3){
                //
                if(switchMode){
                    swapBtn(selectedArr.get(1), selectedArr.get(0));
                }
            } else {
                // TODO CHECK DOUBLE POINTAGE IF MATCH3 SEE BELOW :
                /*
                 Lorsque des combos sont réalisés (après la disparition d’un groupe, un nouveau groupe
                est formé et peut être supprimé sans action du joueur), le score du nouveau groupe qui disparait est
                multiplié par 2. Si le combo se poursuit (par exemple la disparition de ce groupe permet la
                formation d’un nouveau groupe), le score est multiplié par 3. Et ainsi de suite jusqu’à ce que le
                combo soit brisé, c’est-à-dire jusqu’à ce que le joueur ait à nouveau effectué une action
                 */
                if(doubleMatch3 == 2){
                    Toast toast = Toast.makeText(context, R.string.double_match, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0,0);
                    toast.show();
                }
                // TODO CHECK REMOVE AND UPDATE METHOD
                removeAndUpdateCells(cellToRemoveArray, context);
                clearCellToRemoveArrays();
            }

            for(int x = 0; x < colorVerifiedCellArray.size(); ++x){
                Cell cell = colorVerifiedCellArray.get(x);
                if(cell == null){
                    continue;
                }
                cell.setSelected(false);
                cell.setCellIsVerified(false);
            }
            clearColorVerifiedArray();
            clearMatchFoundArrays(true);
            clearColorVerifiedArray();
            clearSelectedArray();
            doubleMatch3 = 0;
        }
        if(!comboCheck){
            ++currentMove;
        } else {
            setComboArray(null);
        }
        checkGameStatus();
        delayThread(TIME_FADEOUT);
    }

    public void setTableLayout(GridLayout tl){
        gameTable = tl;
    }

    public void setTableColumns(int tableColumns) {
        nbColumns = tableColumns;
    }

    public void addSelectedToArray(Cell cell){
        if(!selectedCellArray.contains(cell) && selectedCellArray.size()<=1){
            selectedCellArray.add(cell);
        }
    }

    private void addCellToRemoveArray(Cell cell){
        if(!cellToRemoveArray.contains(cell)){
            cellToRemoveArray.add(cell);
        }
    }

    private void addMatchFoundArrays(Cell cell){
        if(!matchFoundArray.contains(cell)){
            matchFoundArray.add(cell);
        }
    }

    private void addComboArray(Cell cell){
        if(!comboArray.contains(cell)){
            comboArray.add(cell);
        }
    }

    private boolean updateScore(boolean comboCheck){
        int nbMatches = matchFoundArray.size();
        if(nbMatches >= 5){
            currentScore+= 300;
        } else if(nbMatches == 4) {
            currentScore+= 200;
        } else {
            currentScore+= 100;
        }
        // Combo appears only when there was previously a match3. Combo will be verified in the second scanCell after the first match3 (after all removal and
        // all adding view processes
        if(comboCheck){
            ++comboCount;
            currentScore*=comboCount;
            return true;
        }
        return false;
    }

    private void checkGameStatus(){
        if(currentScore >= scoreToWin){
            // TODO VICTORY
            gameLevel = gameLevel <  LEVEL_MAX ? gameLevel + 1 : gameLevel;
            endGameAppDialog(currentActivity.getString(R.string.victory), currentActivity.getString(R.string.victory_msg));
        }
        else if(currentMove >= nbMoves){
            // TODO DEFEAT
            endGameAppDialog(currentActivity.getString(R.string.defeat), currentActivity.getString(R.string.retry_msg));
        }
        printGameStatus();
    }

    private void setGameStatus(int level) {
        switch (level) {
            case 1:
                nbMoves = LEVEL1_MOVE;
                scoreToWin = LEVEL1_SCORE;
                break;
            case 2:
                nbMoves = LEVEL2_MOVE;
                scoreToWin = LEVEL2_SCORE;
                break;
            case 3:
                nbMoves = LEVEL3_MOVE;
                scoreToWin = LEVEL3_SCORE;
                break;
            case 4:
                nbMoves = LEVEL4_MOVE;
                scoreToWin = LEVEL4_SCORE;
                break;
            default:
                nbMoves = LEVEL1_MOVE;
                scoreToWin = LEVEL1_SCORE;
                break;
        }
        printGameStatus();
    }

    private void printGameStatus(){
        TextView txM = (TextView) currentActivity.findViewById(R.id.text_move_player);
        TextView txS = (TextView) currentActivity.findViewById(R.id.text_score_player);
        txS.setText(currentActivity.getResources().getString(R.string.score)+" " + String.valueOf(scoreToWin) + " " + currentActivity.getResources().getString(R.string.current_score) + currentScore);
        txM.setText(currentActivity.getResources().getString(R.string.move)+" " + String.valueOf(nbMoves-currentMove) + " ");
    }

    private void findSelectedManager(Cell cell1, int cellColorToCheck){
        if(!matchFoundArray.contains(cell1)){
            addMatchFoundArrays(cell1);
        }
        // Check RIGHT
        checkColor(cell1, cell1.getRightCell(), cellColorToCheck, "RIGHT");
        // Check LEFT
        checkColor(cell1, cell1.getLeftCell(), cellColorToCheck, "LEFT");
        // Check TOP
        checkColor(cell1, cell1.getTopCell(), cellColorToCheck, "TOP");
        // Check BOTTOM
        checkColor(cell1, cell1.getBottomCell(), cellColorToCheck, "BOTTOM");
        cell1.setCellIsVerified(true);
    }

    private int checkColor(Cell cell1, Cell cell2, int cellColor, String additionnalMsg){
        try {
            if (cell2 != null && !cell2.getCellIsVerified()) {
                Log.d("checkColor", cell1.getText() + " && " + cell2.getText());
                //Log.d("checkColor2", cell1.getCurrentTextColor() + " && " + cell2.getCurrentTextColor() + " && " + cellColor);
                cell2.setCellIsVerified(true);

                if (cell1.getCurrentTextColor() == cell2.getCurrentTextColor() && cellColor == cell1.getCurrentTextColor()) {
                    cell1.setCellIsMatched(true);
                    cell2.setCellIsMatched(true);
                    addMatchFoundArrays(cell1);
                    addMatchFoundArrays(cell2);
                    addCellToRemoveArray(cell1);
                    addCellToRemoveArray(cell2);
                    findSelectedManager(cell2, cellColor);
                    //Log.d("matchFoundArray", " is " + matchFoundArrays.size() + " <=====================================");
                    return 1;
                }
                Log.d("checkColor1", "failed with " + cell1.getText() + " " + cell2.getText());
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        if(cell2 != null)
            Log.d("ChckClrVerFail"+additionnalMsg, cell1.getText() + " && " + cell2.getText());
        colorVerifiedCellArray.add(cell2);
        return 0;
    }

    public void clearSelectedArray(){
        selectedCellArray = new ArrayList<>();
    }

    private void clearColorVerifiedArray(){
        colorVerifiedCellArray = new ArrayList<>();
    }

    private void clearMatchFoundArrays(boolean clear){
        if(clear){
            for(Cell cell : matchFoundArray){
                cell.getBackground().setAlpha(255);
            }
        }
        matchFoundArray = new ArrayList<>();
    }

    private void clearCellToRemoveArrays(){
        cellToRemoveArray = new ArrayList<>();
    }

    private void swapBtn(Cell cell1, Cell cell2){
        swapBtn(cell1, cell2, false);
    }

    private void swapBtn(Cell cell1, Cell cell2, boolean onRemoveState){

        if(cell2 == null){
            return;
        }
        final int idx1 = gameTable.indexOfChild(cell1);
        final int idx2 = gameTable.indexOfChild(cell2);

        Log.d("swap: cell 1 ", cell1.getText()+"");
        Log.d("swap: cell 2 ", cell2.getText()+"");

        removeCellFromParent(cell1);
        addCellToParent(cell1, idx2);
        removeCellFromParent(cell2);
        addCellToParent(cell2, idx1);

        updateSurroundingCells(cell1);
        if(!onRemoveState){
            updateSurroundingCells(cell2);
        }
        lazyUpdateAllSurroundingAllCells();
        printAllTable();
    }

    private void addCellToParent(Cell cell, int idx){
        if(gameTable.indexOfChild(cell) == -1){
            Log.d("indexInAdd", "\t"+idx);
            gameTable.addView(cell, idx);
        }
    }

    private void removeCellFromParent(Cell cell){
        if(cell.getParent() != null){
            GridLayout glP = (GridLayout)cell.getParent();
            glP.removeView(cell);
            Log.d("removeE", "error while removing cell1. Had to attempt twice : " + cell.getText());
        }
    }

    private void updateSurroundingCells(Cell cell){
        if(cell != null){
            final int idx = gameTable.indexOfChild(cell);
            Cell cell2 = (Cell)gameTable.getChildAt(idx-gameTable.getColumnCount());
            cell.setTopCell(cell2);
            cell2 = idx%nbColumns == nbColumns-1 ? null : (Cell)gameTable.getChildAt(idx+1);
            cell.setRightCell(cell2);
            cell2 = (Cell)gameTable.getChildAt(idx+gameTable.getColumnCount());
            cell.setBottomCell(cell2);
            cell2 = idx%nbColumns == 0 ? null : (Cell)gameTable.getChildAt(idx-1);
            cell.setLeftCell(cell2);
            //printSurroundingCell(cell);
        }
    }

    private void printSurroundingCell(Cell cell){
        String textL = cell.getLeftCell()==null?"N":cell.getLeftCell().getText().toString();
        String textR = cell.getRightCell()==null?"N":cell.getRightCell().getText().toString();
        String textT = cell.getTopCell()==null?"N":cell.getTopCell().getText().toString();
        String textB = cell.getBottomCell()==null?"N":cell.getBottomCell().getText().toString();
        Log.d("updateSurrounding", "\t\t"+textT);
        Log.d("updateSurrounding", "\t"+ textL +"\t"+cell.getText()+ "\t"+textR);
        Log.d("updateSurrounding", "\t\t"+textB);
    }

    private void printAllTable(){

        String test = "";
        for (int i=0; i < gameTable.getChildCount(); ++i){

            if(i%gameTable.getColumnCount() == 0){
                test+="\n";
            }
            test += ((Cell)gameTable.getChildAt(i)).getText() + "\t";
        }
        Log.d("allTable",test);
    }

    private void printAllTableWithColor(){

        String test = "";
        for (int i=0; i < gameTable.getChildCount(); ++i){

            if(i%gameTable.getColumnCount() == 0){
                test+="\n";
            }
            test += "("+((Cell)gameTable.getChildAt(i)).getText()+")"+((Cell)gameTable.getChildAt(i)).getCurrentTextColor() + "\t\t";
        }
        Log.d("allTable",test);
    }

    private void removeAndUpdateCells(List<Cell> arr, Context context){
        String test = "";
        for(Cell cell: arr){
            int idx;
            int id = Integer.parseInt(cell.getText().toString());
            Cell tempCellTop;
            do{
                swapBtn(cell, cell.getTopCell(), true);
                tempCellTop = cell.getTopCell();
            }
            while(tempCellTop != null);

            idx = gameTable.indexOfChild(cell);
            Log.d("afterSwapCell", "idx is " + idx + " for id " + id);
            Random rand = new Random();
            //TODO use rand when done
            Cell btn = new Cell(gameTable.getContext(), rand, id, gameTable);

            // Get neighbour before removal
            final Cell cellL2 = cell.getLeftCell();
            final Cell cellR2 = cell.getRightCell();
            final Cell cellB2 = cell.getBottomCell();
            final Cell cellT2 = cell.getTopCell();

            btn.setTopCell(cellT2);
            btn.setRightCell(cellR2);
            btn.setLeftCell(cellL2);
            btn.setBottomCell(cellB2);
            btn.setText(String.valueOf(cell.getText()));

            animateFade(cell, btn);
        }
        gameTable.invalidate();
        Log.d("ARR", "test : " + arr.size() + " with " + test);

        delayThread(TIME_FADEOUT);
        scanCells(context, comboArray,true);
    }

    // TODO optimize this.
    private void lazyUpdateAllSurroundingAllCells() {
        for (int i = 0; i < gameTable.getChildCount(); ++i){
            Cell cell = (Cell)gameTable.getChildAt(i);
            updateSurroundingCells(cell);
        }
    }

    private void endGameAppDialog(String title, String msg) {
        new AlertDialog.Builder(currentActivity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, GridActivity.class);
                        intent.putExtra("level", gameLevel);
                        clearData();
                        currentActivity.startActivity(intent);// continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, SetupActivity.class);
                        intent.putExtra("level", gameLevel);
                        clearData();
                        currentActivity.startActivity(intent);// continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    // Source: http://stackoverflow.com/questions/14156837/animation-fade-in-and-out
    private void animateFade(final Cell cell1, final Cell cell2){

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(TIME_FADEOUT);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                cell1.setVisibility(View.GONE);
                final int idx = gameTable.indexOfChild(cell1);
                gameTable.removeView(cell1);
                gameTable.addView(cell2, idx);
                int gridLayoutWidth = gameTable.getWidth();
                int gridLayoutHeight = gameTable.getHeight();
                int cellWidth = gridLayoutWidth / gameTable.getColumnCount();
                int cellHeight = gridLayoutHeight / gameTable.getRowCount();
                GridLayout.LayoutParams params =
                        (GridLayout.LayoutParams) cell2.getLayoutParams();
                params.width = cellWidth - 2 * CELL_SPACING;
                params.height = cellHeight - 2 * CELL_SPACING;
                params.setMargins(CELL_SPACING, CELL_SPACING, CELL_SPACING, CELL_SPACING);
                cell2.setLayoutParams(params);
                gameMatch3 = Game.getInstance();
                cell2.overrideEventListener(cell2, gameMatch3);
                addComboArray(cell2);
                lazyUpdateAllSurroundingAllCells();
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        cell1.startAnimation(fadeOut);

    }

    private void delayThread(int time){
        delayThread(time, new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private void delayThread(int time, Runnable func){
        final Handler handler = new Handler();
        handler.postDelayed(func, time);
    }

    private List<Cell> getComboArray() {
        return comboArray;
    }

    private void setComboArray(List<Cell> comboArray) {
        if(comboArray != null) this.comboArray = comboArray;
        else this.comboArray = new ArrayList<>();
    }
}