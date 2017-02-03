package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
    private int x = 0, y = 0;
    public Cell(Context context) {
        super(context);
    }

    public void overrideEventListener(final Cell cell, final GridActivity gridActivity, final Game instance){
        this.setId(generateViewId());
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
                        if(cell2 != null){
                            instance.addSelectedToArray(cell2);
                            v.getBackground().setAlpha(255);
                            v.invalidate();
                            TableLayout tl = (TableLayout)v.getParent().getParent();
                            tl.performClick();
                        }
                        break;
                }
                instance.clearData();
                return true;
            }
        });
    }

    private Cell SwipeCheckDirection(int x, int y, int dx, int dy, Cell cell, final Game instance) {
        Cell cell2 = null;
        View row = (View)cell.getParent();
        boolean horizontal = Math.abs(y - dy) < getHeight();
        // RIGHT
        if(dx > x && horizontal){
            cell2 = (Cell)row.findViewById(cell.getId()+1);
        }
        // LEFT
        else if (dx < x && horizontal){
            cell2 = (Cell)row.findViewById(cell.getId()-1);
        }
        // DOWN
        else if (dy > y && !horizontal) {
            TableLayout tl = (TableLayout)row.getParent();
            //TableRow row2 = (TableRow)tl.findViewById(row.getId()+1);
            cell2 = (Cell)tl.findViewById(cell.getId()+instance.getNbColumns());
        }
        // UP
        else if (dy < y && !horizontal){
            TableLayout tl = (TableLayout)row.getParent();
            //TableRow row2 = (TableRow)tl.findViewById(row.getId()-1);
            cell2 = (Cell)tl.findViewById(cell.getId()+instance.getNbColumns());
        }
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
}
