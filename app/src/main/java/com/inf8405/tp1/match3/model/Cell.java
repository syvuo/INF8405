package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import com.inf8405.tp1.match3.ui.*;

/**
 * Created by Lam on 1/27/2017.
 */

public class Cell extends Button{

    private boolean cellIsVerified = false;

    public Cell(Context context) {
        super(context);

    }

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

    public void setCellIsVerified(boolean checked){
        cellIsVerified = checked;
    }

    public boolean getCellIsVerified(){
        return cellIsVerified;
    }
}
