package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game extends AbstractBaseActivity {
    private static Game singletonInstance = new Game();
    private boolean isStarted = false;
    private List<Cell> selectedCellArrays = new ArrayList<>();
    private List<Cell> colorVerifiedCellArrays = new ArrayList<>();
    //private List<Cell> cellArrays = new ArrayList<>();
    private List<Cell> matchFoundArrays = new ArrayList<>();
    private int nbColumns = -1;
    private int nbRows = -1;
    private int sizeOfTable = -1;
    private float pressedDownX;
    private float pressedDownY;
    private TableLayout gameTable;

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
        selectedCellArrays = new ArrayList<>();
    }

    public void scanCells(Context context) {
        int nbMatch = 0;
        String cellTest = "";
        if(selectedCellArrays.size() >= 2){
            printAllTable();
            swapBtn(context, selectedCellArrays);
            printAllTable();
            int i = 0;
            int j = 1;
            boolean foundMatch3 = false;
            while(i < selectedCellArrays.size()){
                findSelectedManager(selectedCellArrays.get(i),  selectedCellArrays.get(j), selectedCellArrays.get(j).getCurrentTextColor());
                if(matchFoundArrays.size()>= 3){
                    // for the swap only
                    foundMatch3 = true;
                    int idx = matchFoundArrays.indexOf(selectedCellArrays.get(0));
                    Log.d("idx", ""+idx);
                    String matchFoundArrayString = "";
                    for(int x = 0; x < matchFoundArrays.size(); ++x){
                        Cell cell = matchFoundArrays.get(x);
                        matchFoundArrayString+= cell.getText() + " ";
                        cell.setSelected(false);
                        cell.setCellIsVerified(false);
                        cell.getBackground().setAlpha(0);
                    }
                    Log.d("matchFoundArrayString", matchFoundArrayString);
                } else {
                    Log.d("non valid move", "non valid move");
                    //swapBtn(selectedCellArrays);
                }
                clearMatchFoundArrays();
                ++i;
                --j;
            }
            if(foundMatch3){
                Log.d("swap", "swap");
                //swapBtn(selectedCellArrays);
            } else {
                printAllTable();
                swapBtn(context, selectedCellArrays);
                printAllTable();
            }
            clearMatchFoundArrays();
            clearColorVerifiedArray();
            clearSelectedArray();
        }

    }

    public void setTableLayout(TableLayout tl){
        gameTable = tl;
    }

    public void setTableColumns(int tableColumns) {
        nbColumns = tableColumns;
    }

    public void setTableRows(int tableRows) {
        nbRows = tableRows;
    }

    public void addSelectedToArray(Cell cell){
        if(!selectedCellArrays.contains(cell)){
            selectedCellArrays.add(cell);
        }
    }

    // TODO penser a un algo moins naif. Presentement on triple check le meme cell. perte de performance.
    // TODO l'utilisation recursive ou de quoi meilleure est souhaitable
    private void findSelectedManager(Cell cell1, Cell cell2, int cellColorToCheck){
        if(!matchFoundArrays.contains(cell1)){
            matchFoundArrays.add(cell1);
        }
        int cellColor = cellColorToCheck;
        int cellPos = -1;
        int test = 0;
        try{
            if((cellPos = Integer.parseInt(String.valueOf(cell1.getText()))) >= 0){
                // pour cell position 1 (top left corner)
                Log.d("real pos", cell1.getText() + "=1=" + cellPos);
                findSelected((cellPos)%(nbColumns) != 0, cell1, cell2, cellColor, VisiteurTableLayout(cell1));
                ++test;
                // pour cell dernier position
                Log.d("real pos", cell1.getText() + "=2=" + cellPos);
                findSelected((cellPos+1)%(nbColumns) != 0, cell1, cell2, cellColor, VisiteurTableLayout(cell1));
                ++test;
                // pour cell netant pas a la premiere ligne
                Log.d("real pos", cell1.getText() + "=3=" + cellPos);
                findSelected(cellPos > nbColumns-1, cell1, cell2, cellColor, VisiteurTableLayout(cell2));
                ++test;
                // pour cell netant pas a la derniere ligne
                Log.d("real pos", cell1.getText() + "=4=" + cellPos);
                findSelected(cellPos < sizeOfTable-nbColumns, cell1, cell2, cellColor, VisiteurTableLayout(cell2));
                ++test;
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Log.e("cellpos", String.valueOf(cellPos));
            Log.e("testpos", String.valueOf(test));
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            Log.e("cellpos", String.valueOf(cellPos));
            Log.e("testpos", String.valueOf(test));
        }
        cell1.setCellIsVerified(true);
    }

    private TableRow VisiteurTableLayout(Cell cell){
        for (int i=0; i < gameTable.getChildCount(); ++i){
            TableRow rows = (TableRow) gameTable.getChildAt(i);
            if((rows.indexOfChild(cell)) >= 0){
                return rows;
            }
        }
        return null;
    }

    private int findSelected(boolean passCondition, Cell cell1, Cell cell2, int cellColor, TableRow row){
        if(passCondition && row != null){
            if(row.indexOfChild(cell2) != -1){
                if(!((Cell)row.getChildAt(row.indexOfChild(cell2))).getCellIsVerified()){
                    return checkColor(cell1, cell2, cellColor);
                }
            }
            Log.d("passConditionButFailRow", cell1.getText() +" " + cell2.getText());
        }
        Log.d("failCondition1", cell1.getText() +" " + cell2.getText());

        return 0;
    }

    private int checkColor(Cell cell1, Cell cell2, int cellColor){

        cell2.setCellIsVerified(true);

        colorVerifiedCellArrays.add(cell2);
        if(cell1.getCurrentTextColor() == cell2.getCurrentTextColor() && cellColor == cell1.getCurrentTextColor() ){
            Log.d("checkColor", "success with " + cell1.getText() + cell2.getText() + " <===================");
            findSelectedManager(cell2, cell1, cellColor);
            Log.d("matchFoundArray", " is " + matchFoundArrays.size() + " <=====================================");
            return 1;
        }
        Log.d("checkColor1", "failed with " + cell1.getText() + cell2.getText());
        Log.d("checkColor2", cell1.getCurrentTextColor() + " && " + cell2.getCurrentTextColor() + " && " + cellColor);
        //clearSelectedArray();
        return 0;
    }


    private void clearSelectedArray(){
        Log.d("sa","Clearing");
        for(Cell cell : selectedCellArrays){
            if(cell.isSelected()){
                cell.setSelected(false);
            }
        }
        selectedCellArrays.clear();
        selectedCellArrays = new ArrayList<>();
    }

    private void clearColorVerifiedArray(){
        Log.d("cva","Clearing");
        for(Cell cell : colorVerifiedCellArrays){
            if(cell.getCellIsVerified()){
                cell.setCellIsVerified(false);
            }
        }
        colorVerifiedCellArrays.clear();
        colorVerifiedCellArrays = new ArrayList<>();
    }

    private void clearMatchFoundArrays(){
        Log.d("mfa","Clearing");
        matchFoundArrays.clear();
        matchFoundArrays = new ArrayList<>();
    }

    private void swapBtn(Context context, List<Cell> arr){
        // TODO DELETE TRY CATCH
        try{
            swapBtn(context, arr.get(0),arr.get(1));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private void swapBtn(Context context, Cell cell1, Cell cell2){
        //exchangeButtons(cell1, cell2);
        int idx1 = -1;
        int idx2 = -1;
        int rowIdx1 = -1;
        int rowIdx2 = -1;
        TableRow tr1 = new TableRow(context);
        TableRow tr2 = new TableRow(context);
        TableRow trTemp = new TableRow(context);
        for (int i=0; i < gameTable.getChildCount(); ++i){
            TableRow rows = (TableRow) gameTable.getChildAt(i);
            if((rows.indexOfChild(cell1)) >= 0){
                idx1 = rows.indexOfChild(cell1);
                tr1 = rows;
                rowIdx1 = i;
            }
            if((rows.indexOfChild(cell2)) >= 0){
                idx2 = rows.indexOfChild(cell2);
                tr2 = rows;
                rowIdx2 = i;
            }
            if(idx1 != -1 && idx2 != -1){
                break;
            }
        }
        if(rowIdx1 == rowIdx2 && rowIdx1 >= 0){
            for (int i=0; i < tr1.getChildCount(); ++i){
                if(i != idx1 && i != idx2){
                    trTemp.addView(tr1.getChildAt(i));
                } else if (i == idx1 ){
                    // Swap 1 a 2
                    trTemp.addView(tr1.getChildAt(idx2));
                } else if (i == idx2) {
                    // Swap 2 a 1
                    trTemp.addView(tr1.getChildAt(idx1));
                }
            }
            tr1.removeAllViews();
            tr1 = trTemp;
            Log.d("trTemp", printTable(tr1));
        } else {
            Cell cellTemp1 = (Cell)tr1.getChildAt(idx1);
            Cell cellTemp2 = (Cell)tr2.getChildAt(idx2);
            tr1.removeView(cell1);
            tr2.removeView(cell2);
            tr1.addView(cellTemp2,idx1);
            tr2.addView(cellTemp1,idx2);

            Log.d("tr1", printTable(tr1));
            Log.d("tr2", printTable(tr2));
        }
    }

    private void printAllTable(){
        String test = "";
        for (int i=0; i < gameTable.getChildCount(); ++i){
            TableRow rows = (TableRow) gameTable.getChildAt(i);
            test += printTable(rows);
            Log.d("allTable",test);
        }
    }

    private String printTable(TableRow row){
        String test = "";
        for(int i = 0; i < row.getChildCount(); ++i){
            test += " " + ((Cell)row.getChildAt(i)).getText();
        }
        return test;
    }

    private void exchangeButtons(Cell btn1, Cell btn2) {
        // Create the animation set
        AnimationSet exchangeAnimation = new AnimationSet(true);
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, btn2.getLeft(),
                Animation.RELATIVE_TO_SELF, btn1.getLeft(),
                Animation.RELATIVE_TO_SELF, btn2.getRight(),
                Animation.RELATIVE_TO_SELF, btn1.getRight());
        translate.setDuration(1500);
        exchangeAnimation.addAnimation(translate);
        //int fromX = btn1.getLeft();
        //int fromY = btn1.getRight();
        //int toX = btn2.getLeft();
        //int toY = btn2.getRight();

        AnimationSet exchangeAnimation1 = new AnimationSet(true);
        TranslateAnimation translate1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, btn1.getLeft(),
                Animation.RELATIVE_TO_SELF, btn2.getLeft(),
                Animation.RELATIVE_TO_SELF, btn1.getRight(),
                Animation.RELATIVE_TO_SELF, btn2.getRight());
        translate1.setDuration(500);
        exchangeAnimation1.addAnimation(translate1);
        // EXECUTE btn1.startAnimation(exchangeAnimation);
        btn2.startAnimation(exchangeAnimation1);
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

    private void CheckIfAdjacent(Context context, List<Cell> selectedCellArrays) {
        //Cell cell1 = selectedCellArrays.get(0);
        //Cell cell2 = selectedCellArrays.get(1);
        swapBtn(context, selectedCellArrays);
    }
}
/*
    final float x0 = selectedCellArrays.get(0).getX();
    final float x1 = selectedCellArrays.get(1).getX();
    final float y0 = selectedCellArrays.get(0).getY();
    final float y1 = selectedCellArrays.get(1).getY();
    final CharSequence t0 = selectedCellArrays.get(0).getText();
    final CharSequence t1 = selectedCellArrays.get(1).getText();
    final int c0 = selectedCellArrays.get(0).getCurrentTextColor();
    final int c1 = selectedCellArrays.get(1).getCurrentTextColor();
            selectedCellArrays.get(0).setX(x1);
            selectedCellArrays.get(0).setY(y1);
            selectedCellArrays.get(1).setX(x0);
            selectedCellArrays.get(1).setY(y0);
            selectedCellArrays.get(0).setText(t1);
            selectedCellArrays.get(1).setText(t0);
            selectedCellArrays.get(0).setTextColor(c1);
            selectedCellArrays.get(1).setTextColor(c0);*/

            /*cellArrays.get(Integer.parseInt(String.valueOf(t0))).setX(x1);
            cellArrays.get(Integer.parseInt(String.valueOf(t0))).setY(y1);
            cellArrays.get(Integer.parseInt(String.valueOf(t1))).setX(x0);
            cellArrays.get(Integer.parseInt(String.valueOf(t1))).setY(y0);
            cellArrays.get(Integer.parseInt(String.valueOf(t0))).setText(t1);
            cellArrays.get(Integer.parseInt(String.valueOf(t1))).setText(t0);
            cellArrays.get(Integer.parseInt(String.valueOf(t0))).setTextColor(c1);
            cellArrays.get(Integer.parseInt(String.valueOf(t1))).setTextColor(c0);*/

/*
                selectedCellArrays.get(0).setX(x0);
                selectedCellArrays.get(0).setY(y0);
                selectedCellArrays.get(1).setX(x1);
                selectedCellArrays.get(1).setY(y1);
                selectedCellArrays.get(0).setText(t0);
                selectedCellArrays.get(1).setText(t1);
                selectedCellArrays.get(0).setTextColor(c0);
                selectedCellArrays.get(1).setTextColor(c1);*/
                /*cellArrays.get(Integer.parseInt(String.valueOf(t0))).setX(x0);
                cellArrays.get(Integer.parseInt(String.valueOf(t0))).setY(y0);
                cellArrays.get(Integer.parseInt(String.valueOf(t1))).setX(x1);
                cellArrays.get(Integer.parseInt(String.valueOf(t1))).setY(y1);
                cellArrays.get(Integer.parseInt(String.valueOf(t0))).setText(t0);
                cellArrays.get(Integer.parseInt(String.valueOf(t1))).setText(t1);
                cellArrays.get(Integer.parseInt(String.valueOf(t0))).setTextColor(c0);
                cellArrays.get(Integer.parseInt(String.valueOf(t1))).setTextColor(c1);*/