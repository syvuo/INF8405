package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game extends AbstractBaseActivity{
    private static Game singletonInstance = new Game();
    private boolean isStarted = false;
    private List<Cell> selectedCellArrays = new ArrayList<>();
    private List<Cell> colorVerifiedCellArrays = new ArrayList<>();
    private List<Cell> cellArrays = new ArrayList<>();
    private List<Cell> matchFoundArrays = new ArrayList<>();
    private int nbColumns = -1;

    private Game(){}

    public static Game getInstance() {
        return singletonInstance;
    }

    public void setIsStarted(boolean value) {
        isStarted = value;
    }

    public void addCell(Cell cell){
        cellArrays.add(cell);
    }

    public void clearData() {
        cellArrays = new ArrayList<>();
        selectedCellArrays = new ArrayList<>();
    }

    public void scanCells(Context context) {
        int nbMatch = 0;
        String cellTest = "";
        if(selectedCellArrays.size() >= 2){
            final CharSequence t0 = selectedCellArrays.get(0).getText();
            final CharSequence t1 = selectedCellArrays.get(1).getText();
            selectedCellArrays.get(0).setText(t1);
            selectedCellArrays.get(1).setText(t0);
            cellArrays.get(Integer.parseInt(String.valueOf(t0))).setText(t1);
            cellArrays.get(Integer.parseInt(String.valueOf(t1))).setText(t0);
            Collections.swap(selectedCellArrays, 0, 1);
            Collections.swap(cellArrays, Integer.parseInt(String.valueOf(t1)), Integer.parseInt(String.valueOf(t0)));


            int i = 0;
            boolean foundMatch3 = false;
            while(i < selectedCellArrays.size()){
                findSelectedManager(selectedCellArrays.get(i), selectedCellArrays.get(i).getCurrentTextColor());
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
                }
                clearMatchFoundArrays();
                i++;
            }
            if(foundMatch3){
                Log.d("swap", "swap");
                swapBtn();
            } else {
                selectedCellArrays.get(0).setText(t1);
                selectedCellArrays.get(1).setText(t0);
                cellArrays.get(Integer.parseInt(String.valueOf(t0))).setText(t1);
                cellArrays.get(Integer.parseInt(String.valueOf(t1))).setText(t0);
                Collections.swap(cellArrays, Integer.parseInt(String.valueOf(t0)), Integer.parseInt(String.valueOf(t1)));
                Collections.swap(selectedCellArrays, 1, 0);
            }
            clearMatchFoundArrays();
            clearColorVerifiedArray();
            clearSelectedArray();
            clearArray();
        }

    }

    public void setTableColumns(int tableColumns) {
        nbColumns = tableColumns;
    }

    public void addSelectedToArray(Cell cell){
        if(!selectedCellArrays.contains(cell)){
            selectedCellArrays.add(cell);
        }
    }

    // TODO penser a un algo moins naif. Presentement on triple check le meme cell. perte de performance.
    // TODO l'utilisation recursive ou de quoi meilleure est souhaitable
    private void findSelectedManager(Cell cell, int cellColorToCheck){
        matchFoundArrays.add(cell);
        int cellColor = cellColorToCheck;
        int cellPos = -1;
        int test = 0;
        try{
            if((cellPos = Integer.parseInt(String.valueOf(cell.getText()))) >= 0){
                // pour cell position 1 (top left corner)
                Log.d("real pos", cell.getText() + "=1=" + cellPos);
                findSelected((cellPos+1)%(nbColumns) != 0, cellPos, cellPos+1, cellColor);
                ++test;
                // pour cell dernier position
                Log.d("real pos", cell.getText() + "=2=" + cellPos);
                findSelected((cellPos+1)%(nbColumns) != 1, cellPos, cellPos-1, cellColor);
                ++test;
                // pour cell netant pas a la premiere ligne
                Log.d("real pos", cell.getText() + "=3=" + cellPos);
                findSelected(cellPos > nbColumns-1, cellPos, cellPos-nbColumns, cellColor);
                ++test;
                // pour cell netant pas a la derniere ligne
                Log.d("real pos", cell.getText() + "=4=" + cellPos);
                findSelected(cellPos < cellArrays.size()-nbColumns, cellPos, cellPos+nbColumns, cellColor);
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
    }

    private int findSelected(boolean passCondition,int cellPos, int nextCellPos, int cellColor){
        if(passCondition && !cellArrays.get(nextCellPos).getCellIsVerified()){
            return checkColor(cellPos, nextCellPos, cellColor);
        }
        Log.d("findSelected", passCondition + " " + !cellArrays.get(nextCellPos).getCellIsVerified());
        return 0;
    }

    private int checkColor(int cellPos, int nextCellPos, int cellColor){
        cellArrays.get(nextCellPos).setCellIsVerified(true);
        colorVerifiedCellArrays.add(cellArrays.get(nextCellPos));
        if(cellArrays.get(cellPos).getCurrentTextColor() == cellArrays.get(nextCellPos).getCurrentTextColor() && cellColor ==cellArrays.get(cellPos).getCurrentTextColor() ){
            Log.d("checkColor", "success with " + cellPos + nextCellPos + " <===================");
            findSelectedManager(cellArrays.get(nextCellPos), cellColor);
            Log.d("matchFoundArray", " is " + matchFoundArrays.size() + " <=====================================");
            return 1;
        }
        Log.d("checkColor1", "failed with " + cellPos + nextCellPos);
        Log.d("checkColor2", cellArrays.get(cellPos).getCurrentTextColor() + " && " + cellArrays.get(nextCellPos).getCurrentTextColor() + " && " + cellColor);
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
        selectedCellArrays = new ArrayList<>();
    }

    private void clearColorVerifiedArray(){
        Log.d("cva","Clearing");
        for(Cell cell : colorVerifiedCellArrays){
            if(cell.getCellIsVerified()){
                cell.setCellIsVerified(false);
            }
        }
        colorVerifiedCellArrays = new ArrayList<>();
    }

    private void clearMatchFoundArrays(){
        Log.d("mfa","Clearing");
        matchFoundArrays = new ArrayList<>();
    }
    private void clearArray(){
        for(Cell cell : cellArrays){
            if(cell.isSelected()){
                cell.setSelected(false);
            }
            if(cell.getCellIsVerified()){
                cell.setCellIsVerified(false);
            }
        }
    }

    private void swapBtn(){
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
        selectedCellArrays.get(1).setTextColor(c0);
        //selectedCellArrays.get(0).setBackgroundColor(bg1);
        //selectedCellArrays.get(1).setBackgroundColor(bg0);
        final ColorDrawable bg0 = (ColorDrawable)selectedCellArrays.get(0).getBackground();
        final ColorDrawable bg1 = (ColorDrawable)selectedCellArrays.get(1).getBackground();
        //final GradientDrawable bg0 = (GradientDrawable)selectedCellArrays.get(0).getBackground();
        //final GradientDrawable bg1 = (GradientDrawable)selectedCellArrays.get(1).getBackground();

        selectedCellArrays.get(1).setBackground(bg0);
        selectedCellArrays.get(0).setBackground(bg1);
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