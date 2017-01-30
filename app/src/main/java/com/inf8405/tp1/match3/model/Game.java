package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.inf8405.tp1.match3.ui.AbstractBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game extends AbstractBaseActivity{
    private static Game singletonInstance = new Game();
    private boolean isStarted = false;
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
    }

    public void scanCells(Context context) {
        int nbMatch = 0;
        for(int i = 0; i < cellArrays.size(); ++i){
            Cell cell = cellArrays.get(i);
            if(cell.isSelected()){
                nbMatch+=foundSelected(cell);
            }
        }
        if(nbMatch >= 3){
            // TODO test
            for(Cell cell : cellArrays){
                if(cell.isSelected()){
                    cell.setSelected(false);
                    cell.getBackground().setAlpha(0);
                }
            }
        }
    }

    public void setTableColumns(int tableColumns) {
        nbColumns = tableColumns;
    }

    // TODO penser a un algo moins naif. Presentement on triple check le meme cell. perte de performance.
    // TODO l'utilisation recursive ou de quoi meilleure est souhaitable
    private int foundSelected(Cell cell){
        int nbAdjacentMatch = 0;
        int cellId = cell.getId();
        // pour cell position 1 (top left corner)
        if(cellId%nbColumns != 1 && cellArrays.get(cellId-2).isSelected()){
            ++nbAdjacentMatch;
            //cell.setSelected(false);
            //cellArrays.get(cellId-1).setSelected(false);
        }
        // pour cell dernier position
        if(cellId%nbColumns != 0 && cellArrays.get(cellId).isSelected()){
            ++nbAdjacentMatch;
            //cell.setSelected(false);
            //cellArrays.get(cellId-1).setSelected(false);
        }
        // pour cell netant pas a la premiere ligne
        if(cellId > nbColumns && cellArrays.get(cellId-nbColumns-1).isSelected()){
            ++nbAdjacentMatch;
            //cell.setSelected(false);
            //cellArrays.get(cellId-1).setSelected(false);
        }
        // pour cell netant pas a la derniere ligne
        if(cellId < cellArrays.size()-nbColumns && cellArrays.get(cellId+nbColumns-1).isSelected()){
            ++nbAdjacentMatch;
            //cell.setSelected(false);
            //cellArrays.get(cellId-1).setSelected(false);
        }
        return nbAdjacentMatch;
    }
}
