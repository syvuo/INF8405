package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.inf8405.tp1.match3.ui.*;
/**
 * Created by Lam on 1/27/2017.
 */

public class Cell extends Button{

    //private enum DIR {CENTER, LEFT, RIGHT, UP, DOWN};
    //private DIR dir;
    private boolean cellIsVerified = false;
    private boolean isMatched = false;
    private int x = 0, y = 0;
    private Cell topNeighbour;
    private Cell rightNeighbour;
    private Cell bottomNeighbour;
    private Cell leftNeighbour;
    private TableRow parent;
    public Cell(Context context) {
        super(context);
    }


    public void setTopCell(Cell cell) {
        topNeighbour = cell;
    }

    public void setRightCell(Cell cell) {
        rightNeighbour = cell;
    }

    public void setBottomCell(Cell cell) {
        bottomNeighbour = cell;
    }

    public void setLeftCell(Cell cell) {
        leftNeighbour = cell;
    }

    public void setParentLayout(TableRow parent) {
        this.parent = parent;
    }

    public Cell getTopCell() {
        return topNeighbour;
    }

    public Cell getRightCell() {
        return rightNeighbour;
    }

    public Cell getBottomCell() {
        return bottomNeighbour;
    }

    public Cell getLeftCell() {
        return leftNeighbour;
    }

    public TableRow getParentLayout() {
        return this.parent;
    }

    public void setCellSurrounding(int nbCol, int nbRow){

    }


/*
    public void overrideEventListener(final Cell cell, final GridActivity gridActivity, final Game instance){

        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cell.setFocusable(true);
                cell.setFocusableInTouchMode(true);///add this line
                cell.requestFocus();
            }

        });
        cell.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int dx, dy;
                try{

                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            v.getBackground().setAlpha(128);
                            v.setSelected(true);
                            instance.addSelectedToArray(cell);
                            v.invalidate();
                            x = (int)event.getX();
                            y = (int)event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            dx = (int)(event.getX());
                            dy = (int)(event.getY());
                            Cell cell2 = SwipeCheckDirection(x, y, dx, dy, cell, instance);
                            if(cell2 != null && cell != cell2){
                                instance.addSelectedToArray(cell2);
                                GridLayout gl = (GridLayout)v.getParent().getParent();
                                gl.performClick();
                            }
                            v.invalidate();
                            //v.getBackground().setAlpha(255);
                            break;
                    }
                }
                catch (Exception e){
                    v.getBackground().setAlpha(255);
                    e.printStackTrace();
                }
                //instance.clearData();
                return true;
            }
        });
    }*/

    public void overrideEventListener(final Cell cell, final GridActivity gridActivity, final Game instance){
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cell.setFocusable(true);
                cell.setFocusableInTouchMode(true);///add this line
                cell.requestFocus();
            }

        });
        cell.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setAlpha(128);
                    v.setSelected(true);
                    instance.addSelectedToArray(cell);
                    v.invalidate();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getBackground().setAlpha(255);
                    v.invalidate();
                    TableLayout tl = (TableLayout)v.getParent().getParent();
                    tl.performClick();
                }
                return true;
            }
        });
    }

    private Cell SwipeCheckDirection(int x, int y, int dx, int dy, Cell cell, final Game instance) {
        Cell cell2 = null;
        View row = (View)cell.getParent();
        String dir = "";
        boolean horizontal = Math.abs(y - dy) < getHeight();
        // RIGHT
        if(dx > x && horizontal){
            cell2 = (Cell)row.findViewById(cell.getId()+1);
            dir = "RIGHT";
        }
        // LEFT
        else if (dx < x && horizontal){
            cell2 = (Cell)row.findViewById(cell.getId()-1);
            dir = "LEFT";
        }
        // DOWN
        else if (dy > y && !horizontal) {
            TableLayout tl = (TableLayout)row.getParent();
            //TableRow row2 = (TableRow)tl.findViewById(row.getId()+1);
            cell2 = (Cell)tl.findViewById(cell.getId()+instance.getNbColumns());
            dir = "DOWN";
        }
        // UP
        else if (dy < y && !horizontal){
            TableLayout tl = (TableLayout)row.getParent();
            //TableRow row2 = (TableRow)tl.findViewById(row.getId()-1);
            cell2 = (Cell)tl.findViewById(cell.getId()-instance.getNbColumns());
            dir = "UP";
        }
        Log.d("DIR", dir);
        Log.d("cell1", " " + cell.getText());
        Log.d("cell2", " " + cell2.getText());
        return cell2;
    }

    public void setCellIsVerified(boolean checked){
        cellIsVerified = checked;
    }

    public boolean getCellIsVerified(){
        return cellIsVerified;
    }

    public void setCellIsMatched(boolean checked){
        isMatched = checked;
    }

    public boolean getCellIsMatched(){
        return isMatched;
    }


}
