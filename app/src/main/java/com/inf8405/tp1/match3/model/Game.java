package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TableRow;
import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import com.inf8405.tp1.match3.ui.GridActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game extends AbstractBaseActivity {
    private static Game singletonInstance = new Game();
    private boolean isStarted = false;
    private List<Cell> selectedCellArrays = new ArrayList<>();
    private List<Cell> colorVerifiedCellArrays = new ArrayList<>();
    private List<Cell> cellToRemoveArrays = new ArrayList<>();
    private List<Cell> matchFoundArrays = new ArrayList<>();
    private int nbColumns = -1;
    private int nbRows = -1;
    private int sizeOfTable = -1;
    private float pressedDownX;
    private float pressedDownY;
    private GridLayout gameTable;
    private final int CELL_SPACING = 1;

    private Game(){}

    public static Game getInstance() {
        return singletonInstance;
    }

    public void setIsStarted(boolean value) {
        isStarted = value;
        if(isStarted){
            sizeOfTable= nbColumns*nbRows;
        }
    }

    /*public void addCell(Cell cell){
        cellArrays.add(cell);
    }*/

    public void clearData() {
        //cellArrays = new ArrayList<>();
        selectedCellArrays =  new ArrayList<>();;
    }

    public int getCellSpacing(){
        return CELL_SPACING;
    }

    public void scanCells(Context context) {
        int nbMatch = 0;
        String cellTest = "";
        if(selectedCellArrays.size() >= 2){
            //printAllTable();
            swapBtn(selectedCellArrays.get(0), selectedCellArrays.get(1));
            //printAllTable();
            int i = 0;
            int j = 1;
            boolean foundMatch3 = false;
            while(i < selectedCellArrays.size() && j >= 0){
                //Log.d("selectedArraySize", selectedCellArrays.size()+ "");
                findSelectedManager(selectedCellArrays.get(i),  selectedCellArrays.get(i).getCurrentTextColor());
                if(matchFoundArrays.size()>= 3){
                    // for the swap only
                    foundMatch3 = true;
                    String matchFoundArrayString = ""; //
                    for(int x = 0; x < matchFoundArrays.size(); ++x){
                        Cell cell = matchFoundArrays.get(x);
                        matchFoundArrayString+= cell.getText() + " ";
                        cell.setSelected(false);
                        cell.setCellIsVerified(false);
                        cell.getBackground().setAlpha(0);
                    }
                    //Log.d("matchFoundArrayString", matchFoundArrayString);
                } else {
                    //Log.d("non valid move", "non valid move");
                }
                clearMatchFoundArrays();
                ++i;
                --j;
            }
            if(!foundMatch3){
                swapBtn(selectedCellArrays.get(1), selectedCellArrays.get(0));
            } else {
                removeAndUpdateCells(cellToRemoveArrays);
                clearCellToRemoveArrays();
            }

            for(int x = 0; x < colorVerifiedCellArrays.size(); ++x){
                Cell cell = colorVerifiedCellArrays.get(x);
                if(cell == null){
                    continue;
                }
                cell.setSelected(false);
                cell.setCellIsVerified(false);
                if(!cell.getCellIsMatched()){
                    cell.getBackground().setAlpha(255);
                    Log.d("255", cell.getText() + "");
                }
            }
            clearMatchFoundArrays();
            clearColorVerifiedArray();
            clearSelectedArray();
        }
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
        if(!selectedCellArrays.contains(cell) && selectedCellArrays.size()<=1){
            selectedCellArrays.add(cell);
        }
    }

    public void addCellToRemoveArray(Cell cell){
        if(!cellToRemoveArrays.contains(cell)){
            cellToRemoveArrays.add(cell);
        }
    }

    public int getNbColumns() {
        return nbColumns;
    }

    // TODO penser a un algo moins naif. Presentement on triple check le meme cell. perte de performance.
    // TODO l'utilisation recursive ou de quoi meilleure est souhaitable
    private void findSelectedManager(Cell cell1, int cellColorToCheck){
        if(!matchFoundArrays.contains(cell1)){
            matchFoundArrays.add(cell1);
        }
        int cellColor = cellColorToCheck;
        int test = 0;
        // pour cell position 1 (top left corner)
        //Log.d("real pos", cell1.getText() + "=1=" + cellPos);
        // Check RIGHT
        checkColor(cell1, cell1.getRightCell(), cellColor);
        ++test; ///
        // pour cell dernier position
        //Log.d("real pos", cell1.getText() + "=2=" + cellPos);
        // Check LEFT
        checkColor(cell1, cell1.getLeftCell(), cellColor);
        ++test;
        // pour cell netant pas a la premiere ligne
        //Log.d("real pos", cell1.getText() + "=3=" + cellPos);
        // Check TOP
        checkColor(cell1, cell1.getTopCell(), cellColor);
        ++test;
        // pour cell netant pas a la derniere ligne
        //Log.d("real pos", cell1.getText() + "=4=" + cellPos);
        // Check BOTTOM
        checkColor(cell1, cell1.getBottomCell(), cellColor);
        cell1.setCellIsVerified(true);
    }

    private int checkColor(Cell cell1, Cell cell2, int cellColor){
        if(cell2 != null && !cell2.getCellIsVerified()){
            //Log.d("checkColor", cell1.getText() + " && " +cell2.getText());
            //Log.d("checkColor2", cell1.getCurrentTextColor() + " && " + cell2.getCurrentTextColor() + " && " + cellColor);
            cell2.setCellIsVerified(true);

            if(cell1.getCurrentTextColor() == cell2.getCurrentTextColor() && cellColor == cell1.getCurrentTextColor() ){
                cell1.setCellIsMatched(true);
                cell2.setCellIsMatched(true);
                addCellToRemoveArray(cell1);
                addCellToRemoveArray(cell2);
                findSelectedManager(cell2, cellColor);
                //Log.d("matchFoundArray", " is " + matchFoundArrays.size() + " <=====================================");
                return 1;
            }
        }
        //Log.d("checkColor1", "failed with " + cell1.getText() + " " + cell2.getText());
        //Log.d("checkColor2", cell1.getCurrentTextColor() + " && " + cell2.getCurrentTextColor() + " && " + cellColor);
        colorVerifiedCellArrays.add(cell2);
        return 0;
    }

    private void clearSelectedArray(){
        for(Cell cell : selectedCellArrays){
            if(cell != null && cell.isSelected()){
                cell.setSelected(false);
            }
        }
        selectedCellArrays = new ArrayList<>();
    }

    private void clearColorVerifiedArray(){
        for(Cell cell : colorVerifiedCellArrays){
            if(cell != null && cell.getCellIsVerified()){
                cell.setCellIsVerified(false);
            }
        }
        colorVerifiedCellArrays.clear();
        colorVerifiedCellArrays = new ArrayList<>();
    }

    private void clearMatchFoundArrays(){
        matchFoundArrays.clear();
        matchFoundArrays = new ArrayList<>();
    }

    private void clearCellToRemoveArrays(){
        cellToRemoveArrays.clear();
        cellToRemoveArrays = new ArrayList<>();
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

        gameTable.removeView(cell1);
        gameTable.addView(cell1, idx2);
        gameTable.removeView(cell2);
        gameTable.addView(cell2, idx1);
        updateSurroundingCells(cell1);
        if(!onRemoveState){
            updateSurroundingCells(cell2);
        }
        printAllTable();
    }

    private void updateSurroundingCells(Cell cell){
        final int idx = gameTable.indexOfChild(cell);
        Cell cell2 = (Cell)gameTable.getChildAt(idx-gameTable.getColumnCount());
        cell.setTopCell(cell2);
        cell2 = (Cell)gameTable.getChildAt(idx+1);
        cell.setRightCell(cell2);
        cell2 = (Cell)gameTable.getChildAt(idx+gameTable.getColumnCount());
        cell.setBottomCell(cell2);
        cell2 = (Cell)gameTable.getChildAt(idx-1);
        cell.setLeftCell(cell2);
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

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pressedDownX = event.getX();
                pressedDownY = event.getY();

                for (int i=0; i < gameTable.getChildCount(); ++i){
                    TableRow rows = (TableRow)gameTable.getChildAt(i);
                    if(pressedDownX > rows.getLeft() && pressedDownX < rows.getRight() && pressedDownY > rows.getTop() && pressedDownY < rows.getBottom()){

                        for(int j=0; j< rows.getChildCount(); ++j){
                            Cell cell = (Cell)gameTable.getChildAt(i);
                            if(pressedDownX > cell.getLeft() && pressedDownX < cell.getRight() && pressedDownY > cell.getTop() && pressedDownY < cell.getBottom()){
                                selectedCellArrays.add(cell);
                            }
                        }
                        //touch is within this child
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            //touch has ended
                            pressedDownX = event.getX();
                            pressedDownY = event.getY();

                            for (int x=0; i < gameTable.getChildCount(); ++x) {
                                TableRow rowsEnd = (TableRow) gameTable.getChildAt(x);
                                if (pressedDownX > rowsEnd.getLeft() && pressedDownX < rowsEnd.getRight() && pressedDownY > rowsEnd.getTop() && pressedDownY < rowsEnd.getBottom()) {

                                    for (int y = 0; y < rowsEnd.getChildCount(); ++y) {
                                        Cell cell = (Cell) gameTable.getChildAt(i);
                                        if (pressedDownX > cell.getLeft() && pressedDownX < cell.getRight() && pressedDownY > cell.getTop() && pressedDownY < cell.getBottom()) {
                                            selectedCellArrays.add(cell);
                                            //CheckIfAdjacent(selectedCellArrays);
                                            selectedCellArrays.clear();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    };


    private void removeAndUpdateCells(List<Cell> arr){
        String test = "";
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
            btn.overrideEventListener(btn, gameMatch3);
            test += cell.getText() + "\t";
        }
        gameTable.invalidate();
        Log.d("ARR", "test : " + arr.size() + " with " + test);
    }
}