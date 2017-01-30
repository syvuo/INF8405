package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game {
    private static Game singletonInstance = new Game();
    private boolean isStarted = false;
    private List<Cell> cellArrays = new ArrayList<>();

    private Game(){}

    public static Game getInstance() {
        return singletonInstance;
    }

    public void setIsStarted(boolean value) {isStarted = value;}


    public void clearData() {
        cellArrays = new ArrayList<>();
    }

    public void scanCells(Context context) {
        for(int i = 0; i < cellArrays.size(); ++i){
            Cell cell = cellArrays.get(i);
            if(cell.isSelected()){
                Toast.makeText(context, "(" + cell.getId() + ")", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addCell(Cell cell){
        cellArrays.add(cell);
    }


}
