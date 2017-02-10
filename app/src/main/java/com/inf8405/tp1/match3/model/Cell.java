package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import com.inf8405.tp1.match3.R;
import java.util.Random;

/**
 * Created by Lam on 1/27/2017.
 */

public class Cell extends Button{

    private enum DIR {CENTER, LEFT, RIGHT, UP, DOWN};
    private String dir;
    private boolean cellIsVerified = false;
    private boolean isMatched = false;
    private int xPosI = 0, yPosI = 0;

    private Cell topNeighbour;
    private Cell rightNeighbour;
    private Cell bottomNeighbour;
    private Cell leftNeighbour;
    private GridLayout parent;
    private static final int [] COLORS_ARRAY = {R.color.blue, R.color.green, R.color.orange, R.color.purple, R.color.red, R.color.yellow};

    public Cell(Context context) {
        super(context);
    }

    public Cell(Context context, final Random rand, int text, GridLayout layout){
        super(context);
        parent = layout;
        //TODO remove blue
        int colorTemp;
        if(rand == null)
            colorTemp = COLORS_ARRAY[0];
        else
            colorTemp = COLORS_ARRAY[rand.nextInt(COLORS_ARRAY.length)];
        //END OF TODO REMOVE BLUE
        setBackground(ContextCompat.getDrawable(context, R.drawable.shape));

        final GradientDrawable bgShape = (GradientDrawable) getBackground();
        bgShape.setColor(ContextCompat.getColor(context, colorTemp));


        setText(String.valueOf(text));
        setTextColor(ContextCompat.getColor(context, colorTemp));
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

    public void setParentLayout(GridLayout parent) {
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

    public GridLayout getParentLayout() {
        return this.parent;
    }

    public void overrideEventListener(final Cell cell, final Game instance){
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
                    xPosI = (int)event.getX();
                    yPosI = (int)event.getY();
                    instance.addSelectedToArray(cell);
                    v.invalidate();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //v.getBackground().setAlpha(255);
                    //v.invalidate();
                    v.setSelected(true);
                    GridLayout tl = (GridLayout)v.getParent();
                    Cell cell2 = swipeCheckDirection(xPosI, yPosI, (int)event.getX(), (int)event.getY(), cell, instance);
                    if(cell2 != null){
                        instance.addSelectedToArray(cell2);
                        tl.performClick();
                    }
                }
                return true;
            }
        });
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

    private Cell swipeCheckDirection(int x, int y, int dx, int dy, Cell cell, final Game instance) {
        Cell cell2 = null;
        dir = "UNKNOWN";
        boolean horizontal = Math.abs(y - dy) < getHeight();
        // RIGHT
        if(dx > x && horizontal){
            cell2 = cell.getRightCell();
            dir = "RIGHT";
        }
        // LEFT
        else if (dx < x && horizontal){
            cell2 = cell.getLeftCell();
            dir = "LEFT";
        }
        // DOWN
        else if (dy > y && !horizontal) {
            cell2 = cell.getBottomCell();
            dir = "DOWN";
        }
        // UP
        else if (dy < y && !horizontal){
            cell2 = cell.getTopCell();
            dir = "UP";
        }
        Toast.makeText(getContext(), dir, Toast.LENGTH_SHORT).show();
        Log.d("DIR", dir);
        if(cell != null) Log.d("cell1", " " + cell.getText());
        if(cell2!= null) Log.d("cell2", " " + cell2.getText());
        return cell2;
    }
}
