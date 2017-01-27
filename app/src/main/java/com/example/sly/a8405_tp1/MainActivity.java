package com.example.sly.a8405_tp1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sly.a8405_tp1.R;
import com.example.sly.a8405_tp1.model.Game;
import com.example.sly.a8405_tp1.ui.GridActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void startButtonClicked(View v){
        popToast("Starting new game");
        Intent intent = new Intent(getApplicationContext(), GridActivity.class);
        Game.setIsStarted(false);
        intent.putExtra("gridColumns", 8);
        startActivity(intent);

    }

    public void rulesButtonClicked(View v){
        popToast("Rules Clicked");

    }

    public void exitButtonClicked(View v){
        popToast("Application Closed");
        finish();
    }

    private void popToast(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }












    // TODO DELETE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
