package com.inf8405.tp1.match3.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.inf8405.tp1.match3.MainActivity;
import com.inf8405.tp1.match3.R;
import com.inf8405.tp1.match3.model.Game;

/**
 * Created by Lam on 1/28/2017.
 */

public class AbstractBaseActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_refresh);
        if (AbstractBaseActivity.this instanceof MainActivity) {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Close button
            case R.id.action_close:
                closeAppDialog();
                return true;
            // Back button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            // Refresh button
            case R.id.action_refresh:
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void closeAppDialog(){
        closeAppDialog(MainActivity.class);
    }

    // source: https://www.developpez.net/forums/d1214833/java/general-java/java-mobiles/android/evenement-clic-bouton-item-d-listview/
    protected void closeAppDialog(final Class tClass) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.close_message).setPositiveButton("Quitter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (AbstractBaseActivity.this instanceof MainActivity) {
                            Toast.makeText(getApplicationContext(), "Aurevoir", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), tClass);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            Game.clearData();
                            startActivity(intent);
                        }
                    }
                })
                .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setIcon(android.R.drawable.ic_dialog_info).show();
    }

    public void popToast(int text){
        popToast(String.valueOf(text));
    }

    public void popToast(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
