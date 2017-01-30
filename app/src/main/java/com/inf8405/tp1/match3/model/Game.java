package com.inf8405.tp1.match3.model;

import android.content.Context;
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
    private List<Cell> cellSelectedArrays = new ArrayList<>();
    private int nbColumns = -1;
    private boolean nextMoveInvalid = false;

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
        cellSelectedArrays = new ArrayList<>();
        isStarted = false;
        nbColumns = -1;
        nextMoveInvalid = false;
    }

    public void scanCells(Context context) {
        int nbMatch = 0;
        for(int i = 0; i < cellArrays.size(); ++i){
            Cell cell = cellArrays.get(i);
            if(cell.isSelected()){
                nbMatch+=foundSelected(cell);
            }
        }
        if((nbMatch >= 3 && !checkAllAdjSelectedColor()) /*|| nbMatch >= 3 && nextMoveInvalid*/){
            // TODO test
            for(Cell cell : cellArrays){
                if(cell.isSelected()){
                    cell.setSelected(false);
                    cell.getBackground().setAlpha(0);
                }
            }
            nextMoveInvalid = false;
            clearSelectedArray();
            nbMatch = 0;
        }
    }

    public void setTableColumns(int tableColumns) {
        nbColumns = tableColumns;
    }


    public void addSelectedCell(Cell cell) {
        cellSelectedArrays.add(cell);
    }

    // TODO penser a un algo moins naif. Presentement on triple check le meme cell. perte de performance.
    // TODO l'utilisation recursive ou de quoi meilleure est souhaitable
    private int foundSelected(Cell cell){
        int nbAdjacentMatch = 0;
        int cellId = cell.getId();

        if(!checkAllAdjColor(cell)){
            nextMoveInvalid = true;
            clearSelectedArray();
            return 0;
        }

        // pour cell position 1 (top left corner)
        try{

            if(cellId%nbColumns != 1 && cellArrays.get(cellId-2).isSelected()){
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId-2);
            }
            // pour cell dernier position
            if(cellId%nbColumns != 0 && cellArrays.get(cellId).isSelected()){
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId);
            }
            // pour cell netant pas a la premiere ligne
            if(cellId > nbColumns && cellArrays.get(cellId-nbColumns-1).isSelected()){
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId-nbColumns-1);
            }
            // pour cell netant pas a la derniere ligne
            if(cellId < cellArrays.size()-nbColumns && cellArrays.get(cellId+nbColumns-1).isSelected()){
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId+nbColumns-1);
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            nbAdjacentMatch = 0;
        }
        return nbAdjacentMatch;
    }

    private void clearSelectedArray(){
        for(Cell cell : cellArrays){
            if(cell.isSelected()){
                cell.setSelected(false);
            }
        }
        cellSelectedArrays = new ArrayList<>();
    }

    private boolean checkAllAdjSelectedColor(){
        boolean possibleMove = false;
        for(Cell cell : cellSelectedArrays){
            possibleMove |= (checkAllAdjColorUnselected(cell));
            if(possibleMove){
                return possibleMove;
            }
        }
        return possibleMove;
    }

    // TODO method is duplicated. need refactoring
    private boolean checkAllAdjColor(Cell cell) {
        int nbAdjacentMatch = 0;
        try {
            int cellId = cell.getId();
            if (cellId % nbColumns != 1) {
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId - 2);
            }
            // pour cell dernier position
            if (cellId % nbColumns != 0) {
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId);
            }
            // pour cell netant pas a la premiere ligne
            if (cellId > nbColumns) {
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId-nbColumns-1);
            }
            // pour cell netant pas a la derniere ligne
            if (cellId < cellArrays.size() - nbColumns) {
                nbAdjacentMatch = nbAdjacentMatch + checkColor(cell, cellId+nbColumns-1);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return nbAdjacentMatch > 0 ? true: false;
    }

    // TODO method is duplicated. need refactoring -> maybe use command pattern or function pointer
    private boolean checkAllAdjColorUnselected(Cell cell) {
        int nbAdjacentMatch = 0;
        try {
            int cellId = cell.getId();
            if (cellId % nbColumns != 1) {
                nbAdjacentMatch = nbAdjacentMatch + checkColorUnselected(cell, cellId - 2);
            }
            // pour cell dernier position
            if (cellId % nbColumns != 0) {
                nbAdjacentMatch = nbAdjacentMatch + checkColorUnselected(cell, cellId);
            }
            // pour cell netant pas a la premiere ligne
            if (cellId > nbColumns) {
                nbAdjacentMatch = nbAdjacentMatch + checkColorUnselected(cell, cellId-nbColumns-1);
            }
            // pour cell netant pas a la derniere ligne
            if (cellId < cellArrays.size() - nbColumns) {
                nbAdjacentMatch = nbAdjacentMatch + checkColorUnselected(cell, cellId+nbColumns-1);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return nbAdjacentMatch > 0 ? true: false;
    }

    private int checkColorUnselected(Cell centerCell, int nextCell) {
        if(centerCell.getCurrentTextColor() == cellArrays.get(nextCell).getCurrentTextColor()
                && !cellArrays.get(nextCell).isSelected()){
            return 1;
        }
        //clearSelectedArray();
        return 0;
    }

    private int checkColor(Cell centerCell, int nextCell){
        if(centerCell.getCurrentTextColor() == cellArrays.get(nextCell).getCurrentTextColor()){
            return 1;
        }
        //clearSelectedArray();
        return 0;
    }
}
