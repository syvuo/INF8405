package com.inf8405.tp1.match3.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.TextView;
import com.inf8405.tp1.match3.R;
import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import com.inf8405.tp1.match3.ui.GridActivity;
import com.inf8405.tp1.match3.ui.SetupActivity;
import java.util.ArrayList;
import java.util.List;

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
    private List<Cell> matchFoundArrays = new ArrayList<>();
    private int nbColumns = -1;
    private int nbRows = -1;
    private int sizeOfTable = -1;

    private float pressedDownX;
    private float pressedDownY;
    private GridLayout gameTable;
    private final int CELL_SPACING = 1;
    private int nbMoves = 100;
    private int currentMove = 0;
    private int scoreToWin = 100;
    private int currentScore = 0;
    private int gameLevel = 1;

    private final int LEVEL1_MOVE = 6;
    private final int LEVEL1_SCORE = 800;
    private final int LEVEL2_MOVE = 10;
    private final int LEVEL2_SCORE = 1200;
    private final int LEVEL3_MOVE = 10;
    private final int LEVEL3_SCORE = 1400;
    private final int LEVEL4_MOVE = 10;
    private final int LEVEL4_SCORE = 1800;

    private Game(){}

    public static Game getInstance() {
        return singletonInstance;
    }

    public void setIsStarted(boolean value, Activity activity, int level) {
        clearData();
        currentActivity = activity;
        gameLevel = level;
        setGameStatus(gameLevel);
        isStarted = value;
        if(isStarted){
            sizeOfTable= nbColumns*nbRows;
        }
    }

    public void clearData() {
        //cellArrays = new ArrayList<>();
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

    public void scanCells(Context context) {
        int nbMatch = 0;
        String cellTest = "";
        if(selectedCellArray.size() >= 2){
            //printAllTable();
            swapBtn(selectedCellArray.get(0), selectedCellArray.get(1));
            //printAllTable();
            int i = 0;
            boolean foundMatch3 = false;
            while(i < selectedCellArray.size()){
                //Log.d("selectedArraySize", selectedCellArrays.size()+ "");
                findSelectedManager(selectedCellArray.get(i),  selectedCellArray.get(i).getCurrentTextColor());
                if(matchFoundArrays.size()>= 3){
                    // for the swap only
                    foundMatch3 = true;
                    String matchFoundArrayString = ""; //
                    for(int x = 0; x < matchFoundArrays.size(); ++x){
                        Cell cell = matchFoundArrays.get(x);
                        matchFoundArrayString+= cell.getText() + " ";
                        cell.setSelected(false);
                        cell.getBackground().setAlpha(0);
                    }
                    updateScore();
                    Log.d("matchFoundArrayString", matchFoundArrayString);
                } else {
                    Log.d("non valid move", "non valid move");
                }
                clearMatchFoundArrays(!foundMatch3);
                ++i;
            }
            if(!foundMatch3){
                //
                swapBtn(selectedCellArray.get(1), selectedCellArray.get(0));
            } else {
                removeAndUpdateCells(cellToRemoveArray);
                clearCellToRemoveArrays();
                // TODO CHECK DOUBLE POINTAGE IF MATCH3 SEE BELOW :
                /*
                 Lorsque des combos sont réalisés (après la disparition d’un groupe, un nouveau groupe
                est formé et peut être supprimé sans action du joueur), le score du nouveau groupe qui disparait est
                multiplié par 2. Si le combo se poursuit (par exemple la disparition de ce groupe permet la
                formation d’un nouveau groupe), le score est multiplié par 3. Et ainsi de suite jusqu’à ce que le
                combo soit brisé, c’est-à-dire jusqu’à ce que le joueur ait à nouveau effectué une action
                 */
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
        }
        currentMove++;
        checkGameStatus();
    }

    public void setTableLayout(GridLayout tl){
        gameTable = tl;
    }

    public void setTableColumns(int tableColumns) {
        nbColumns = tableColumns;
    }

    public void setTableRows(int tableRows) {
        nbRows = tableRows;
    }

    public void addSelectedToArray(Cell cell){
        if(!selectedCellArray.contains(cell) && selectedCellArray.size()<=1){
            selectedCellArray.add(cell);
        }
    }

    public int getNbColumns() {
        return nbColumns;
    }

    private void addCellToRemoveArray(Cell cell){
        if(!cellToRemoveArray.contains(cell)){
            cellToRemoveArray.add(cell);
        }
    }

    private void addMatchFoundArrays(Cell cell){
        if(!matchFoundArrays.contains(cell)){
            matchFoundArrays.add(cell);
        }
    }

    private void updateScore(){
        int nbMatches = matchFoundArrays.size();
        if(nbMatches >= 5){
            currentScore+= 300;
        } else if(nbMatches == 4) {
            currentScore+= 200;
        } else {
            currentScore+= 100;
        }
    }

    private void checkGameStatus(){
        if(currentScore >= scoreToWin){
            // TODO VICTORY
            ++gameLevel;
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

    // TODO penser a un algo moins naif. Presentement on triple check le meme cell. perte de performance.
    // TODO l'utilisation recursive ou de quoi meilleure est souhaitable
    private void findSelectedManager(Cell cell1, int cellColorToCheck){
        if(!matchFoundArrays.contains(cell1)){
            addMatchFoundArrays(cell1);
        }
        // Check RIGHT
        checkColor(cell1, cell1.getRightCell(), cellColorToCheck);
        // Check LEFT
        checkColor(cell1, cell1.getLeftCell(), cellColorToCheck);
        // Check TOP
        checkColor(cell1, cell1.getTopCell(), cellColorToCheck);
        // Check BOTTOM
        checkColor(cell1, cell1.getBottomCell(), cellColorToCheck);
        cell1.setCellIsVerified(true);
    }

    private int checkColor(Cell cell1, Cell cell2, int cellColor){
        if(cell2 != null && !cell2.getCellIsVerified()){
            Log.d("checkColor", cell1.getText() + " && " +cell2.getText());
            //Log.d("checkColor2", cell1.getCurrentTextColor() + " && " + cell2.getCurrentTextColor() + " && " + cellColor);
            cell2.setCellIsVerified(true);

            if(cell1.getCurrentTextColor() == cell2.getCurrentTextColor() && cellColor == cell1.getCurrentTextColor() ){
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
        //Log.d("checkColor2", cell1.getCurrentTextColor() + " && " + cell2.getCurrentTextColor() + " && " + cellColor);
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
            for(Cell cell : matchFoundArrays){
                cell.getBackground().setAlpha(255);
            }
        }
        matchFoundArrays = new ArrayList<>();
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
        printAllTable();
    }

    private void addCellToParent(Cell cell, int idx){
        if(gameTable.indexOfChild(cell) == -1){
            gameTable.addView(cell, idx);
        }
    }

    private void removeCellFromParent(Cell cell){
        if(cell.getParent() != null){
            GridLayout glP = (GridLayout)cell.getParent();
            glP.removeView(cell);
            Log.d("removeE", "error while removing cell1. Had to attempt twice");
        }
    }

    private void updateSurroundingCells(Cell cell){
        final int idx = gameTable.indexOfChild(cell);
        Cell cell2 = (Cell)gameTable.getChildAt(idx-gameTable.getColumnCount());
        cell.setTopCell(cell2);
        cell2 = idx%nbColumns == nbColumns-1 ? null : (Cell)gameTable.getChildAt(idx+1);
        cell.setRightCell(cell2);
        cell2 = (Cell)gameTable.getChildAt(idx+gameTable.getColumnCount());
        cell.setBottomCell(cell2);
        cell2 = idx%nbColumns == 0 ? null : (Cell)gameTable.getChildAt(idx-1);
        cell.setLeftCell(cell2);
        printSurroundingCell(cell);
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

    private void removeAndUpdateCells(List<Cell> arr){
        String test = "";/*
        for(Cell cell: arr){
            int idx = gameTable.indexOfChild(cell);
            int id = Integer.parseInt(cell.getText().toString());

            // Nb of row affected
            int nbSwitches = (int)Math.ceil(idx/nbColumns);
            Log.d("Switch", ""+nbSwitches);
            for(int i = 0; i < nbSwitches+1; ++i){
                swapBtn(cell, cell.getTopCell(), true);
            }

            idx = gameTable.indexOfChild(cell);
            Random rand = new Random();
            //TODO use rand when done
            Cell btn = new Cell(gameTable.getContext(), null, id, gameTable);


            btn.setTopCell(cell.getTopCell());
            btn.setRightCell(cell.getRightCell());
            btn.setLeftCell(cell.getLeftCell());
            btn.setBottomCell(cell.getBottomCell());

            gameTable.removeView(cell);
            gameTable.addView(btn, idx);

            int gridLayoutWidth = gameTable.getWidth();
            int gridLayoutHeight = gameTable.getHeight();
            int cellWidth = gridLayoutWidth / gameTable.getColumnCount();
            int cellHeight = gridLayoutHeight / gameTable.getRowCount();
            GridLayout.LayoutParams params =
                    (GridLayout.LayoutParams) btn.getLayoutParams();
            params.width = cellWidth - 2 * CELL_SPACING;
            params.height = cellHeight - 2 * CELL_SPACING;
            params.setMargins(CELL_SPACING, CELL_SPACING, CELL_SPACING, CELL_SPACING);
            btn.setLayoutParams(params);
            gameMatch3 = Game.getInstance();
            btn.overrideEventListener(btn, gameMatch3);
            test += cell.getText() + "\t";

        }*/
        gameTable.invalidate();
        Log.d("ARR", "test : " + arr.size() + " with " + test);
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
}