package com.inf8405.tp1.match3.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inf8405.tp1.match3.R;
import com.inf8405.tp1.match3.ui.AbstractBaseActivity;
import com.inf8405.tp1.match3.ui.GridActivity;
import com.inf8405.tp1.match3.ui.SetupActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Lam on 1/26/2017.
 */

public final class Game extends AbstractBaseActivity {
    private static Game singletonInstance = new Game();
    private Activity currentActivity;
    private boolean isStarted = false;

    private List<Cell> selectedCellArray = new ArrayList<>();
    private List<Cell> colorVerifiedCellArray = new ArrayList<>();
    private List<Cell> cellToRemoveArray = new ArrayList<>();
    private List<Cell> matchFoundArray = new ArrayList<>();
    private List<Cell> comboArray = new ArrayList<>();
    private List<Toast> toastArray = new ArrayList<>();
    private List<Animation> animationArray = new ArrayList<>();
    private int nbColumns = -1;
    private GridLayout gameTable;
    private int nbMoves = 100;
    private int currentMove = 0;
    private int scoreToWin = 100;
    private int currentScore = 0;
    private int gameLevel = 1;
    private int comboCount = 1;
    private Context context;
    private Handler handler = new Handler();
    private int gain;
    private boolean gameWon = false;
    private int currentLevel = 0;
    private TextView gainTV;
    private final int CELL_SPACING = 1;
    private final int LEVEL1_MOVE = 6;
    private final int LEVEL1_SCORE = 800;
    private final int LEVEL2_MOVE = 10;
    private final int LEVEL2_SCORE = 1200;
    private final int LEVEL3_MOVE = 10;
    private final int LEVEL3_SCORE = 1400;
    private final int LEVEL4_MOVE = 10;
    private final int LEVEL4_SCORE = 1800;
    private final int LEVEL_MAX = 4;
    private final int TIME_FADEOUT = 750;

    private Game(){}

    public static Game getInstance() {
        return singletonInstance;
    }

    public void setIsStarted(Context context, boolean value, Activity activity, int level) {
        clearData();
        currentActivity = activity;
        currentLevel = level;
        setGameStatus(level);
        isStarted = value;
        this.context = context.getApplicationContext();
        gainTV = (TextView)currentActivity.findViewById(R.id.text_score_gain);
        gainTV.setText("");
        gameWon = false;
    }

    public void clearData() {
        comboArray = new ArrayList<>();
        selectedCellArray =  new ArrayList<>();
        nbMoves = 100;
        currentMove = 0;
        scoreToWin = 100;
        currentScore = 0;
        comboCount = 1;
        clearToastQueue();
        stopAnimations();
    }

    public void clearArrays(){
        selectedCellArray = new ArrayList<>();
        colorVerifiedCellArray = new ArrayList<>();
        cellToRemoveArray = new ArrayList<>();
        matchFoundArray = new ArrayList<>();
        comboArray = new ArrayList<>();
        toastArray = new ArrayList<>();
        stopAnimations();
        animationArray = new ArrayList<>();
    }

    public int getGameLevel(){
        return gameLevel;
    }

    public int getCellSpacing(){
        return CELL_SPACING;
    }

    // Fonction appele par gridActivity lors dun click (via Cell class)
    public void scanCells(List<Cell> arr){
        scanCells(arr, false);
    }

    public void scanCells(List<Cell> arr, boolean comboCheck) {
        clearCellToRemoveArrays();
        List<Cell> selectedArr = arr;
        boolean switchMode = false;

        // Dans le cas ou on fait on swipe
        if(selectedArr == null && !comboCheck){
            selectedArr = selectedCellArray;
            if(selectedArr.size() >= 2){
                switchMode = true;
                swapBtn(selectedArr.get(0), selectedArr.get(1));
            }
        }
        lazyUpdateAllSurroundingAllCells();
        boolean foundMatch3 = false;
        int doubleMatch3 = 0;
        // On parcourt chaque cellule
        if(selectedArr.size() > 0){
            int i = 0;
            while(i < selectedArr.size()){
                findSelectedManager(selectedArr.get(i),  selectedArr.get(i).getCurrentTextColor());
                if(matchFoundArray.size()>= 3){
                    // for the swap only
                    foundMatch3 = true;
                    ++doubleMatch3;
                    // Update score
                    updateScore(comboCheck);
                }
                clearMatchFoundArrays(!foundMatch3);
                ++i;
            }
            if(!foundMatch3){
                // Si aucun match et on est en mode swipe. On replace les cellules au bon endroit
                if(switchMode && !comboCheck){
                    swapBtn(selectedArr.get(1), selectedArr.get(0));
                }
            } else {
                // A chaque match3 (on verifie sil y a un doublematch - valide seulement si on est en mode swipe et non combo)
                if(doubleMatch3 == 2 && !comboCheck){
                    popToast(this.context.getString(R.string.double_match), Gravity.BOTTOM|Gravity.RIGHT);
                }
                // Mise a jour des nouvelles cellules
                removeAndUpdateCells(cellToRemoveArray);
                clearCellToRemoveArrays();
            }
            // Clear les arrays
            for(int x = 0; x < colorVerifiedCellArray.size(); ++x){
                Cell cell = colorVerifiedCellArray.get(x);
                if(cell == null){
                    continue;
                }
                cell.setSelected(false);
                cell.setCellIsVerified(false);
            }
            clearColorVerifiedArray();
            clearMatchFoundArrays(true);
            clearColorVerifiedArray();
            clearSelectedArray();
        }
        // Si on est en mode swipe, on incremente le nombre de move
        if(!comboCheck){
            ++currentMove;
        } else {
            comboCount = 1;
            setComboArray(null);
        }
        gameTable.invalidate();
        // Mettre a jour des stats
        checkGameStatus();
    }

    public void setTableLayout(GridLayout tl){
        gameTable = tl;
    }

    public void setTableColumns(int tableColumns) {
        nbColumns = tableColumns;
    }

    // TODO optimize this.
    // Mise a jour de toutes les cellules avec leur entourage. Evidement, ce nest pas performant.
    public void lazyUpdateAllSurroundingAllCells() {
        for (int i = 0; i < gameTable.getChildCount(); ++i){
            Cell cell = (Cell)gameTable.getChildAt(i);
            updateSurroundingCells(cell);
            cell.setVisibility(View.VISIBLE);
            cell.setAlpha(1);
            cell.getBackground().setAlpha(255);
        }
    }

    // Utilise pour le swipe et scanCell
    public void addSelectedToArray(Cell cell){
        if(!selectedCellArray.contains(cell) && selectedCellArray.size()<=1){
            selectedCellArray.add(cell);
        }
    }

    private void addCellToRemoveArray(Cell cell){
        if(!cellToRemoveArray.contains(cell)){
            cellToRemoveArray.add(cell);
        }
    }

    private void addMatchFoundArrays(Cell cell){
        if(!matchFoundArray.contains(cell)){
            matchFoundArray.add(cell);
        }
    }

    private void addComboArray(Cell cell){
        if(cell!= null && !comboArray.contains(cell)){
            comboArray.add(cell);
        }
    }
    // Mettre a jour du score
    private void updateScore(boolean comboCheck){
        int nbMatches = matchFoundArray.size();
        if(comboCheck){
            ++comboCount;
        }
        if(nbMatches >= 5){
            gain = 300*comboCount;
        } else if(nbMatches == 4) {
            gain = 200*comboCount;
        } else {
            gain = 100*comboCount;
        }
        currentScore+= gain;
        // Animation fade out du gain
        startDisplayGain();
        stopDisplayGain();
        fadeOutGainTV();
        if(comboCount > 1){
            popToast(this.context.getString(R.string.combo_x)+comboCount, Gravity.BOTTOM);
        }
    }
    // Mettre a juor les statistiques. Pop un alert dialogue si fin de jeu
    private void checkGameStatus(){
        if(currentScore >= scoreToWin){
            // VICTORY
            // Verification si nous avons battu le bon niveau
            if(!gameWon && currentLevel == gameLevel){
                gameLevel = gameLevel <  LEVEL_MAX ? gameLevel + 1 : gameLevel;
                gameWon = true;
            }
            endGameAppDialog(currentActivity.getString(R.string.victory), currentActivity.getString(R.string.victory_msg));
        }
        else if(currentMove >= nbMoves){
            // DEFEAT
            endGameAppDialog(currentActivity.getString(R.string.defeat), currentActivity.getString(R.string.retry_msg));
        }
        printGameStatus();
    }
    // Mettre a jour les conditions de victoire
    private void setGameStatus(int level) {
        switch (level) {
            case 1:
                nbMoves = LEVEL1_MOVE;
                scoreToWin = LEVEL1_SCORE;
                break;
            case 2:
                nbMoves = LEVEL2_MOVE;
                scoreToWin = LEVEL2_SCORE;
                break;
            case 3:
                nbMoves = LEVEL3_MOVE;
                scoreToWin = LEVEL3_SCORE;
                break;
            case 4:
                nbMoves = LEVEL4_MOVE;
                scoreToWin = LEVEL4_SCORE;
                break;
            default:
                nbMoves = LEVEL1_MOVE;
                scoreToWin = LEVEL1_SCORE;
                break;
        }
        printGameStatus();
    }
    // Mettre a jour les textView pour les stats
    private void printGameStatus(){
        TextView txM = (TextView) currentActivity.findViewById(R.id.text_move_player);
        TextView txS = (TextView) currentActivity.findViewById(R.id.text_score_player);
        txS.setText(" " + String.valueOf(scoreToWin) + " " + currentActivity.getResources().getString(R.string.current_score) + currentScore);
        txM.setText(currentActivity.getResources().getString(R.string.move)+" " + String.valueOf(nbMoves-currentMove) + " ");
    }
    // Utilise par scan, pour verifier le voisinage et les couleurs pour trouver un match
    private void findSelectedManager(Cell cell1, int cellColorToCheck){
        if(!matchFoundArray.contains(cell1)){
            addMatchFoundArrays(cell1);
        }
        // Check RIGHT
        checkColor(cell1, cell1.getRightCell(), cellColorToCheck, "RIGHT");
        // Check LEFT
        checkColor(cell1, cell1.getLeftCell(), cellColorToCheck, "LEFT");
        // Check TOP
        checkColor(cell1, cell1.getTopCell(), cellColorToCheck, "TOP");
        // Check BOTTOM
        checkColor(cell1, cell1.getBottomCell(), cellColorToCheck, "BOTTOM");
        cell1.setCellIsVerified(true);
    }
    // Verification et comparaison entre 2 cellules selon leur couleur (appele 4 fois : up down left right)
    private int checkColor(Cell cell1, Cell cell2, int cellColor, String additionnalMsg){
        try {
            if (cell2 != null && !cell2.getCellIsVerified()) {
                //Log.d("checkColor", cell1.getText() + " && " + cell2.getText());
                //Log.d("checkColor2", cell1.getCurrentTextColor() + " && " + cell2.getCurrentTextColor() + " && " + cellColor);
                if (cell1.getCurrentTextColor() == cell2.getCurrentTextColor() && cellColor == cell1.getCurrentTextColor()) {
                    // Si match de couleur, on marque les deux cellules comme MATCHED
                    cell1.setCellIsMatched(true);
                    cell2.setCellIsMatched(true);
                    cell1.setCellIsVerified(true);
                    cell2.setCellIsVerified(true);
                    addMatchFoundArrays(cell1);
                    addMatchFoundArrays(cell2);
                    addCellToRemoveArray(cell1);
                    addCellToRemoveArray(cell2);
                    // Recursivement, on verifie le 2e cellule a nouveau pour voir sil y a un match avec ses voisinages de la 2e cellule
                    findSelectedManager(cell2, cellColor);
                    //Log.d("matchFoundArray", " is " + matchFoundArrays.size() + " <=====================================");
                    return 1;
                }
                //Log.d("checkColor1", "failed with " + cell1.getText() + " " + cell2.getText());
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        // Pour deboggage
        if(cell2 != null) {
            //Log.d("ChckClrVerFail" + additionnalMsg, cell1.getText() + " && " + cell2.getText());
        }
        // Pour memorise toutes les cellules vues. On va remettre a letat neuf a la fin
        colorVerifiedCellArray.add(cell2);
        return 0;
    }

    public void clearSelectedArray(){
        selectedCellArray = new ArrayList<>();
    }

    private void clearColorVerifiedArray(){
        colorVerifiedCellArray = new ArrayList<>();
    }

    private void clearMatchFoundArrays(boolean clear){
        if(clear){
            for(Cell cell : matchFoundArray){
                cell.setAlpha(1);
            }
        }
        matchFoundArray = new ArrayList<>();
    }

    private void clearCellToRemoveArrays(){
        cellToRemoveArray = new ArrayList<>();
    }

    private void addToatsArray(Toast toast) {
        if(!this.toastArray.contains(toast)){
            this.toastArray.add(toast);
        }
    }

    // Cancel all toast
    private void clearToastQueue(){
        for(Toast toast : this.toastArray){
            toast.cancel();
        }
    }
    // Interchange la position de deux cellules si en mode gesture ou en mode de generationNouvelleCellule
    private void swapBtn(Cell cell1, Cell cell2){
        swapBtn(cell1, cell2, false);
    }
    // Si onRemoveState = true, on ne met pas a jour les cellules du top (cell2) lors de la generationNouvelleCellule. La mise a jour se fait a la fin
    private void swapBtn(Cell cell1, Cell cell2, boolean onRemoveState){

        if(cell2 == null){
            return;
        }
        lazyUpdateAllSurroundingAllCells();
        final int idx1 = gameTable.indexOfChild(cell1) == -1 ? findChildByText(cell1.getText().toString()) : gameTable.indexOfChild(cell1);
        final int idx2 = gameTable.indexOfChild(cell2) == -1 ? findChildByText(cell2.getText().toString()) : gameTable.indexOfChild(cell2);

        removeCellFromParent(cell1);
        addCellToParent(cell1, idx2);
        removeCellFromParent(cell2);
        addCellToParent(cell2, idx1);
        updateSurroundingCells(cell1);

        if(!onRemoveState){
            updateSurroundingCells(cell2);
        }
        printAllTable();
    }

    private void addCellToParent(Cell cell, int idx){
        if(gameTable.indexOfChild(cell) == -1 && idx != -1){
            //Log.d("indexInAdd",  "\tId "+cell.getText() + "\tidx "+idx);
            gameTable.addView(cell, idx);
        } else {
            Log.d("indexInAddFAILLLL",  "\tId "+cell.getText() + "\tidx "+idx);
        }
    }

    private void  removeCellFromParent(Cell cell){
        if(cell.getParent() != null){
            GridLayout glP = (GridLayout)cell.getParent();
            {
                glP.removeView(cell);
                gameTable.invalidate();
                //Log.d("removeE", "error while removing cell1. Had to attempt twice : " + cell.getText());
            }
        }
        // sometime removeView wont work because of cell modification and its parent wont be able to find him
        CharSequence id = cell.getText();
        for(int i = 0; i < gameTable.getChildCount(); ++i){
            if(id == ((Cell)gameTable.getChildAt(i)).getText()){
                gameTable.removeViewAt(i);
                Log.d("RemovalFail222222", "Got yaaaaaaaaaaaaaaaaaaaa " + i + " id " + id);
                return;
            }
        }

    }

    private void updateSurroundingCells(Cell cell){
        if(cell != null){
            final int idx = gameTable.indexOfChild(cell);
            Cell cell2 = (Cell)gameTable.getChildAt(idx-gameTable.getColumnCount());
            cell.setTopCell(cell2);
            cell2 = idx%nbColumns == nbColumns-1 ? null : (Cell)gameTable.getChildAt(idx+1);
            cell.setRightCell(cell2);
            cell2 = (Cell)gameTable.getChildAt(idx+gameTable.getColumnCount());
            cell.setBottomCell(cell2);
            cell2 = idx%nbColumns == 0 ? null : (Cell)gameTable.getChildAt(idx-1);
            cell.setLeftCell(cell2);
            //printSurroundingCell(cell);
        }
    }

    private void printSurroundingCell(Cell cell){
        String textL = cell.getLeftCell()==null?"N":cell.getLeftCell().getText().toString();
        String textR = cell.getRightCell()==null?"N":cell.getRightCell().getText().toString();
        String textT = cell.getTopCell()==null?"N":cell.getTopCell().getText().toString();
        String textB = cell.getBottomCell()==null?"N":cell.getBottomCell().getText().toString();
        Log.d("updateSurrounding", "\t\t"+textT);
        Log.d("updateSurrounding", "\t"+ textL +"\t"+cell.getText()+ "\t"+textR);
        Log.d("updateSurrounding", "\t\t"+textB);
    }

    private void printAllTable(){

        String test = "";
        for (int i=0; i < gameTable.getChildCount(); ++i){

            if(i%gameTable.getColumnCount() == 0){
                test+="\n";
            }
            test += ((Cell)gameTable.getChildAt(i)).getText() + "\t";
        }
        Log.d("allTable",test);
    }

    private void printAllTableWithColor(){

        String test = "";
        for (int i=0; i < gameTable.getChildCount(); ++i){

            if(i%gameTable.getColumnCount() == 0){
                test+="\n";
            }
            test += "("+((Cell)gameTable.getChildAt(i)).getText()+")"+((Cell)gameTable.getChildAt(i)).getCurrentTextColor() + "\t\t";
        }
        Log.d("allTable",test);
    }

    // TODO verify combo if its really working.. the score increase very fast with combo activated.
    private void removeAndUpdateCells(List<Cell> arr){
        String test = "";
        for(Cell cell: arr){
            fadeOutCell(cell);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lazyUpdateAllSurroundingAllCells();
                gameTable.invalidate();
                scanCells(comboArray, true);
            }
        }, 2*TIME_FADEOUT);
    }

    private int findChildByText(String id) {
        for(int i = 0; i < gameTable.getChildCount(); ++i){
            if(id == ((Cell)gameTable.getChildAt(i)).getText()){
                return i;
            }
        }
        return -1;
    }



    private void endGameAppDialog(String title, String msg) {
        if((currentLevel == LEVEL_MAX) && msg.toString().contains("prochain")){
            msg = "Bravo, vous avez termine tous les niveaux. Recommencez une partie du dernier niveau?";
        }
        new AlertDialog.Builder(currentActivity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, GridActivity.class);
                        intent.putExtra("level", gameLevel);
                        clearData();
                        currentActivity.startActivity(intent);// continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, SetupActivity.class);
                        intent.putExtra("level", gameLevel);
                        clearData();
                        currentActivity.startActivity(intent);// continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

    }

    private void fadeInCell(final Cell cell){

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(TIME_FADEOUT);
        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                cell.setVisibility(View.VISIBLE);
                cell.setAlpha(1);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        fadeIn.setFillAfter(true);
        animationArray.add(fadeIn);
        cell.startAnimation(fadeIn);
    }

    private void fadeOutCell(final Cell cell)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(TIME_FADEOUT);
        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                int idx;
                final int id = Integer.parseInt(cell.getText().toString());
                // Swap the cell to be deleted with the ones above him until his new top neighbour is a null
                // which means that hes at the first row
                Cell tempCellTop;
                int i = 0;
                do{
                    swapBtn(cell, cell.getTopCell(), true);
                    tempCellTop = cell.getTopCell();
                    addComboArray(tempCellTop);
                    ++i;
                }
                while(tempCellTop != null && i < gameTable.getRowCount());

                Random rand = new Random();
                Cell btn = new Cell(context, rand, id, gameTable);

                // Get neighbour before removal
                final Cell cellL2 = cell.getLeftCell();
                final Cell cellR2 = cell.getRightCell();
                final Cell cellB2 = cell.getBottomCell();
                final Cell cellT2 = cell.getTopCell();
                // Update new cell with the proper neighbour
                btn.setTopCell(cellT2);
                btn.setRightCell(cellR2);
                btn.setLeftCell(cellL2);
                btn.setBottomCell(cellB2);
                btn.setText(String.valueOf(cell.getText()));
                // Remove cell from the table

                idx = gameTable.indexOfChild(cell) == -1 ? findChildByText(cell.getText().toString()) : gameTable.indexOfChild(cell);
                if(idx == -1){
                    printAllTable();
                    idx = findChildByText(String.valueOf(id));
                }
                removeCellFromParent(cell);
                // Add new cell to the table at the right index with the right params
                btn.setVisibility(View.INVISIBLE);
                addCellToParent(btn, idx);
                int gridLayoutWidth = gameTable.getWidth();
                int gridLayoutHeight = gameTable.getHeight();
                int cellWidth = gridLayoutWidth / gameTable.getColumnCount();
                int cellHeight = gridLayoutHeight / gameTable.getRowCount();
                GridLayout.LayoutParams params =
                        (GridLayout.LayoutParams) btn.getLayoutParams();
                params.width = cellWidth - 2 * CELL_SPACING;
                params.height = cellHeight - 2 * CELL_SPACING;
                params.setMargins(CELL_SPACING, CELL_SPACING, CELL_SPACING, CELL_SPACING);
                btn.setLayoutParams(params);
                gameMatch3 = Game.getInstance();
                btn.overrideEventListener(btn, gameMatch3);
                // Add new cell to the comboArray for new match-3 verification at the end (see scanCells() call below)
                addComboArray(btn);
                lazyUpdateAllSurroundingAllCells();
                fadeInCell(btn);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        fadeOut.setFillAfter(true);
        animationArray.add(fadeOut);
        cell.startAnimation(fadeOut);
    }

    private void fadeOutGainTV(){
        Animation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(TIME_FADEOUT);
        fadeOut.setFillAfter(true);
        gainTV.startAnimation(fadeOut);
    }

    private void stopAnimations(){
        for(Cell cell : comboArray){
            cell.clearAnimation();
        }
        for(Cell cell : cellToRemoveArray){
            cell.clearAnimation();
        }
    }

    public Runnable displayGain = new Runnable() {
        @Override
        public void run() {
            gainTV.setText("\t\t+++"+gain);
            handler.post(displayGain);
        }
    };

    void startDisplayGain()
    {
        displayGain.run();
    }

    void stopDisplayGain()
    {
        handler.removeCallbacks(displayGain);
    }

    private List<Cell> getComboArray() {
        return comboArray;
    }

    private void setComboArray(List<Cell> comboArray) {
        if(comboArray != null) this.comboArray = comboArray;
        else this.comboArray = new ArrayList<>();
    }

    private void popToast(String msg, int toastPos){
        Toast toast = Toast.makeText(this.context, msg, Toast.LENGTH_SHORT);
        toast.getView().setBackgroundColor(Color.TRANSPARENT);
        toast.setGravity(toastPos, 0, 0);
        toast.show();
        addToatsArray(toast);
    }
}