package com.inf8405.tp1.match3.model;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.inf8405.tp1.match3.ui.GridActivity;

/**
 * Created by Lam on 1/27/2017.
 */

public class Cell extends Button{

    public Cell(Context context) {
        super(context);

    }

    public void overrideEventListener(final Cell cell, final GridActivity gridActivity, final GradientDrawable bgShape){
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(gridActivity.getApplicationContext(), "You clicked (" + cell.getId()+")", Toast.LENGTH_SHORT).show();
                //bgShape.setColor(ContextCompat.getColor(gridActivity, R.color.black));
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
                    v.invalidate();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getBackground().setAlpha(255);
                    v.invalidate();
                    //Toast.makeText(gridActivity.getApplicationContext(), "Reverse (" + cell.getId()+")", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}
