package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.util.Log;
import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game extends AbstractBaseActivity{
    private static Game singletonInstance = new Game();
    private boolean isStarted = false;
    private List<Cell> selectedCellArrays = new ArrayList<>();
    private List<Cell> cellArrays = new ArrayList<>();
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
            for(Cell cell : selectedCellArrays){
                nbMatch+=findSelectedManager(cell);
                cellTest += (" " + cell.getText());
            }
            Log.d("array", cellTest);
            if(nbMatch >= 3){
                // TODO test
                for(Cell cell : selectedCellArrays){
                    cell.setSelected(false);
                    cell.setCellIsVerified(false);
                    cell.getBackground().setAlpha(0);
                    clearSelectedArray();
                }
            }
            if(nbMatch < 1){
                clearSelectedArray();
            }
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
    private int findSelectedManager(Cell cell){
        int nbAdjacentMatch = 0;
        int cellPos = -1;
        int test = 0;
        try{
            if((cellPos = Integer.parseInt(String.valueOf(cell.getText()))) >= 0){
                // pour cell position 1 (top left corner)
                Log.d("real pos", cell.getText() + "=1=" + cellPos);
                nbAdjacentMatch += findSelected((cellPos+1)%(nbColumns) != 0, cellPos, cellPos+1);
                ++test;
                // pour cell dernier position
                Log.d("real pos", cell.getText() + "=2=" + cellPos);
                nbAdjacentMatch += findSelected((cellPos+1)%(nbColumns) != 1, cellPos, cellPos-1);
                ++test;
                // pour cell netant pas a la premiere ligne
                Log.d("real pos", cell.getText() + "=3=" + cellPos);
                nbAdjacentMatch += findSelected(cellPos > nbColumns-1, cellPos, cellPos-nbColumns);
                ++test;
                // pour cell netant pas a la derniere ligne
                Log.d("real pos", cell.getText() + "=4=" + cellPos);
                nbAdjacentMatch += findSelected(cellPos < cellArrays.size()-nbColumns, cellPos, cellPos+nbColumns);
                ++test;
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Log.e("cellpos", String.valueOf(cellPos));
            Log.e("testpos", String.valueOf(test));
            nbAdjacentMatch = 0;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            Log.d("nbadjacentMatch",String.valueOf(nbAdjacentMatch));
        }
        return nbAdjacentMatch;
    }

    private int findSelected(boolean passCondition,int cellPos, int nextCellPos){
        if(selectedCellArrays.size() > 0 && passCondition && cellArrays.get(nextCellPos).isSelected()){
            return checkColor(cellPos, nextCellPos);
        }
        if(passCondition)
        if(passCondition)
            Log.d("fail", "fail with " + cellPos + nextCellPos + passCondition + cellArrays.get(nextCellPos).isSelected());
        else
            Log.d("fail", "fail with " + cellPos + nextCellPos + passCondition);
        return 0;
    }

    private int checkColor(int cellPos, int nextCellPos){
        cellArrays.get(nextCellPos).setCellIsVerified(true);
        if(cellArrays.get(cellPos).getCurrentTextColor() == cellArrays.get(nextCellPos).getCurrentTextColor()){
            Log.d("checkColor", "success with " + cellPos + nextCellPos + " <===================");
            return 1;
        }
        Log.d("checkColor", "failed with " + cellPos + nextCellPos);
        clearSelectedArray();
        return 0;
    }


    private void clearSelectedArray(){
        Log.d("CD","Clearing");
        for(Cell cell : selectedCellArrays){
            if(cell.isSelected()){
                cell.setSelected(false);
            }
        }
        selectedCellArrays = new ArrayList<>();
    }

    private void clearArray(){
        for(Cell cell : cellArrays){
            if(cell.isSelected()){
                cell.setSelected(false);
            }
        }
    }
}
