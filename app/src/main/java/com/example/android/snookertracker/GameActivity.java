package com.example.android.snookertracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannedString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


// todo: extend it to actual game: add randomized chance system for each action (color), which might result in a foul (despite hitting a target ball)
// todo cont.: add sound fx for ball, add 'Snooker' button/action to attempt to 'snooker' your opponent (defense move), add worse chance for the next player after a successful snooker

public class GameActivity extends Activity {

    View currentView;
    TextView tv_referee, tv_player_stats, tv_scroller, tv_player_a, tv_player_b, tv_player_a_score, tv_player_b_score, tv_frame_compare;
    Button btn_red, btn_yellow, btn_green, btn_brown, btn_blue, btn_pink, btn_black, btn_miss, btn_foul;
    Toast toast;

    boolean isPlayerA;
    boolean isP2;
    boolean isValidMove;
    boolean isPopupActive;

    // Frame & Game over vars
    boolean isFrameStarterPlayerA, isGameOver, isFrameWinnerPlayerA, isGameWinnerPlayerA, isFrameOver, isBlackBallGame;
    int nrFrame, nrFrames, nrFramesWonA, nrFramesWonB;
    int nrBreak, nrBreakMax, nrBreakMaxFrameA, nrBreakMaxFrameB, nrBreakMaxGameA, nrBreakMaxGameB, nrPtsMaxGameA, nrPtsMaxGameB, nrMistakesA, nrMistakesB, nrHitsA, nrHitsB;
    String strDisplayText, strFrameWinner, strFrameLoser, strGameResult, strGameWinner;

    // User preferences (Intent)
    String strUserName, strOpponentName;
    int nrOpponentCheckboxID;

    // Essentials
    String strPlayer, strPlayerA, strPlayerB, strPlayerIdle, strPotted, strReferee;
    int nrPotted, nrTarget; // Ball potted and current target ball (ball "on"). 1=red, 2-7=specific color, 8=any color
    int nrRedsAvailable, nrPtsLeft, nrPtsDifference;
    int nrFoulPenalty;

    // Game stats
    int nrPtsA, nrPtsB, nrPtsCurrent, nrPtsIdle;

    int nrDeactivatedBall = 0; // 1-7 red-black, saves which ball color is no longer active (potted permanently).
    int nrPtsLeftP2[] = {27, 25, 22, 18, 13, 7, 27}; // For Phase 2, counts remaining colored ball values.

    @Override
    protected void onSaveInstanceState(Bundle bundle) {

        bundle.putBoolean("isFrameOver", isFrameOver);
        bundle.putBoolean("isFrameStarterPlayerA", isFrameStarterPlayerA);
        bundle.putBoolean("isFrameWinnerPlayerA", isFrameWinnerPlayerA);
        bundle.putBoolean("isGameWinnerPlayerA", isGameWinnerPlayerA);
        bundle.putBoolean("isGameOver", isGameOver);
        bundle.putBoolean("isBlackBallGame", isBlackBallGame);
        bundle.putBoolean("isPopupActive", isPopupActive);
        bundle.putBoolean("isPlayerA", isPlayerA);
        bundle.putBoolean("isP2", isP2);

        bundle.putString("strDisplayText", strDisplayText);
        bundle.putString("strGameResult", strGameResult);
        bundle.putString("strUserName", strUserName);
        bundle.putString("strOpponentName", strOpponentName);
        bundle.putString("strPlayerA", strPlayerA);
        bundle.putString("strPlayerB", strPlayerB);
        bundle.putString("strPlayer", strPlayer);
        bundle.putString("strPlayerIdle", strPlayerIdle);
        bundle.putString("strPotted", strPotted);
        bundle.putString("strReferee", strReferee);

        bundle.putInt("nrFrame", nrFrame);
        bundle.putInt("nrFrames", nrFrames);
        bundle.putInt("nrFramesWonA", nrFramesWonA);
        bundle.putInt("nrFramesWonB", nrFramesWonB);
        bundle.putInt("nrPtsA", nrPtsA);
        bundle.putInt("nrPtsB", nrPtsB);
        bundle.putInt("nrPtsCurrent", nrPtsCurrent);
        bundle.putInt("nrPtsIdle", nrPtsIdle);
        bundle.putInt("nrBreak", nrBreak);
        bundle.putInt("nrBreakMax", nrBreakMax);
        bundle.putInt("nrBreakMaxFrameA", nrBreakMaxFrameA);
        bundle.putInt("nrBreakMaxFrameB", nrBreakMaxFrameB);
        bundle.putInt("nrBreakMaxGameA", nrBreakMaxGameA);
        bundle.putInt("nrBreakMaxGameB", nrBreakMaxGameB);
        bundle.putInt("getNrPtsMaxA", nrPtsMaxGameA);
        bundle.putInt("getNrPtsMaxB", nrPtsMaxGameB);
        bundle.putInt("nrMistakesA", nrMistakesA);
        bundle.putInt("nrMistakesB", nrMistakesB);
        bundle.putInt("nrHitsA", nrHitsA);
        bundle.putInt("nrHitsB", nrHitsB);
        bundle.putInt("nrPotted", nrPotted);
        bundle.putInt("nrTarget", nrTarget);
        bundle.putInt("nrOpponentCheckboxID", nrOpponentCheckboxID);
        bundle.putInt("nrFoulPenalty", nrFoulPenalty);
        bundle.putInt("nrDeactivatedBall", nrDeactivatedBall);
        bundle.putInt("nrRedsAvailable", nrRedsAvailable);
        bundle.putInt("nrPtsLeft", nrPtsLeft);
        bundle.putInt("nrPtsDifference", nrPtsDifference);

        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        // Generic config
        tv_referee = findViewById(R.id.view_referee);
        tv_player_stats = findViewById(R.id.view_player_stats);
        tv_scroller = findViewById(R.id.str_scroller);
        tv_scroller.setSelected(true);
        tv_player_a = findViewById(R.id.str_player_a);
        tv_player_a.setText(strPlayerA);
        tv_player_b = findViewById(R.id.str_player_b);
        tv_player_b.setText(strPlayerB);
        tv_player_a_score = findViewById(R.id.str_player_a_score);
        tv_player_b_score = findViewById(R.id.str_player_b_score);

        btn_red = findViewById(R.id.btn_red);
        btn_yellow = findViewById(R.id.btn_yellow);
        btn_green = findViewById(R.id.btn_green);
        btn_brown = findViewById(R.id.btn_brown);
        btn_blue = findViewById(R.id.btn_blue);
        btn_pink = findViewById(R.id.btn_pink);
        btn_black = findViewById(R.id.btn_black);
        btn_miss = findViewById(R.id.btn_miss);
        btn_foul = findViewById(R.id.btn_foul);

        // Load values from MainActivity
        if (getIntent().getExtras() != null){
            strUserName = getIntent().getStringExtra("username");
            strOpponentName = getIntent().getStringExtra("opponent");
            nrOpponentCheckboxID = getIntent().getExtras().getInt("nrOpponentCheckboxID");
            nrFrames = getIntent().getExtras().getInt("nrFrames");
        }

        if (bundle != null) {
            // EXISTING LAUNCH, load data from previous activity

            isGameOver = bundle.getBoolean("isGameOver");
            isFrameOver = bundle.getBoolean("isFrameOver");
            isFrameStarterPlayerA = bundle.getBoolean("isFrameStarterPlayerA");
            isGameWinnerPlayerA = bundle.getBoolean("isGameWinnerPlayerA");
            strDisplayText = bundle.getString("strDisplayText");
            strGameResult = bundle.getString("strGameResult");
            isBlackBallGame = bundle.getBoolean("isBlackBallGame");
            isPopupActive = bundle.getBoolean("isPopupActive");
            isPlayerA = bundle.getBoolean("isPlayerA");
            isFrameWinnerPlayerA = bundle.getBoolean("isFrameWinnerPlayerA");
            isP2 = bundle.getBoolean("isP2");

            strUserName = bundle.getString("strUserName");
            strOpponentName = bundle.getString("strOpponentName");
            strPlayerA = bundle.getString("strPlayerA");
            strPlayerB = bundle.getString("strPlayerB");
            strPlayer = bundle.getString("strPlayer");
            strPlayerIdle = bundle.getString("strPlayerIdle");
            strPotted = bundle.getString("strPotted");
            strReferee = bundle.getString("strReferee");

            nrFramesWonA = bundle.getInt("nrFramesWonA");
            nrFramesWonB = bundle.getInt("nrFramesWonB");
            nrFrame = bundle.getInt("nrFrame");
            nrFrames = bundle.getInt("nrFrames");
            nrPtsA = bundle.getInt("nrPtsA");
            nrPtsB = bundle.getInt("nrPtsB");
            nrPtsCurrent = bundle.getInt("nrPtsCurrent");
            nrPtsIdle = bundle.getInt("nrPtsIdle");
            nrBreak = bundle.getInt("nrBreak");
            nrBreakMax = bundle.getInt("nrBreakMax");
            nrBreakMaxFrameA = bundle.getInt("nrBreakMaxFrameA");
            nrBreakMaxFrameB = bundle.getInt("nrBreakMaxFrameB");
            nrBreakMaxGameA = bundle.getInt("nrBreakMaxGameA");
            nrBreakMaxGameB = bundle.getInt("nrBreakMaxGameB");
            nrPtsMaxGameA = bundle.getInt("getNrPtsMaxA");
            nrPtsMaxGameB = bundle.getInt("getNrPtsMaxB");
            nrMistakesA = bundle.getInt("nrMistakesA");
            nrMistakesB = bundle.getInt("nrMistakesB");
            nrHitsA = bundle.getInt("nrHitsA");
            nrHitsB = bundle.getInt("nrHitsB");
            nrFoulPenalty = bundle.getInt("nrFoulPenalty");
            nrPotted = bundle.getInt("nrPotted");
            nrTarget = bundle.getInt("nrTarget");
            nrDeactivatedBall = bundle.getInt("nrDeactivatedBall");
            nrPotted = bundle.getInt("nrPotted");
            nrRedsAvailable = bundle.getInt("nrRedsAvailable");
            nrPtsLeft = bundle.getInt("nrPtsLeft");
            nrPtsDifference = bundle.getInt("nrPtsDifference");

            // Restore active popup before rotation
            if (isPopupActive) {
                frameOver();
            }

        } else {
            // FRESH LAUNCH

            isFrameStarterPlayerA = true;
            nrFrame = 1;
            nrFramesWonA = 0;
            nrFramesWonB = 0;
            nrFoulPenalty = 4;
            nrPtsMaxGameA = 0;
            nrPtsMaxGameB = 0;

            resetFrame(currentView);

            // Welcome message
            displayToast(getString(R.string.str_good_luck));
        }

        // RENDER
        updateUIPlayers();
        updateUIButtons();
        updateUIStats();
    }

    public void playerAction(View v) {

        //  Which ball was potted
        currentView = v; // for generic View parameters elsewhere
        strPotted = v.getTag().toString();

        if (strPotted.equals("miss")) {
            // Miss, handle miss and return
            handleMiss();
            updateUI();
            return;
        }
        if (strPotted.equals("foul")) {
            // Foul, handle foul and return
            handleFoul(true);
            calcStats();
            updateUI();
            return;
        }

        // **Ball was potted if control reaches here**

        // Get potted ball value
        nrPotted = Integer.parseInt(strPotted);

        // Current player name
        strPlayer = isPlayerA ? strPlayerA : strPlayerB;
        strPlayerIdle = isPlayerA ? strPlayerB : strPlayerA;

        validateAction();
        calcStats();
        updateUI();
    }

    public void validateAction() { // Based on game rules, validates user's action

        isValidMove = false;

        // *** Rule out the only two possible valid moves first ***

        if (nrPotted == nrTarget) {  // Direct match
            isValidMove = true;
        }
        if (!isP2 && nrPotted != 1 && nrTarget != 1) { // In Phase 1 && target and potted balls were colored (non-red). This is because there can be more than 1 target ball ("8" means any colored ball)
            isValidMove = true;
        }

        // *** Check for foul move ***

        if (!isValidMove) {
            handleFoul(false);
            return;
        }

        // *** At this stage, a valid ball was potted ***

        strReferee = Integer.toString(getBallValue(strPotted));
        handleReward();

        // *** At this stage, the valid move has been awarded ***

        // Check for frame over (last black target was potted)
        if (nrTarget == 7 && nrPotted == 7) {
            if (nrPtsB == nrPtsA) {
                // Tied frame, decide with coin toss (real rule)
                double toss;
                toss = Math.random();
                isPlayerA = (toss <= 0.5 ? true : false);
                strPlayer = isPlayerA ? strPlayerA : strPlayerB;
                strReferee = getString(R.string.str_referee_tied, strPlayer);
                isBlackBallGame = true; // Needed so if Foul in next turn, game ends with this last move (real rule)
                return; // Game continues one more round with nothing else changing (black is still target). So, skips findNextTarget() below.
            } else {
                // True frame over with a clear winner
                nrDeactivatedBall++; // To grey out last ball for visuals
                frameOver();
                return; // no need to find next target, frame will be reset.
            }
        }

        // *** At this stage, it's a standard ball pot. Handle removing reds (not replacing reds) and greyed out button count ***

        if (nrPotted == 1) {
            nrRedsAvailable--;
            if (nrRedsAvailable == 0) nrDeactivatedBall = 1;
        } else {
            if (nrTarget != 8) nrDeactivatedBall++;
        }

        // Find next target
        findNextTarget();
    }

    public void findNextTarget() {

        if (nrTarget == 8) {
            // Colored ball was potted
            if (nrRedsAvailable == 0) {
                // Last colored ball was potted, transition to Phase2
                isP2 = true;
                nrTarget = 2;
                return;
            } else {
                // Normal colored ball pot, next target is red
                nrTarget = 1;
                return;
            }
        }

        if (nrTarget == 1) {
            // Red ball was potted, next target is always "any colored ball"
            nrTarget = 8;
            return;
        }

        // *** At this stage, all P1 events are exhausted, must be a P2 event with direct target (no 8 or 1)

        nrTarget++; // In P2, the next target is the next colored value

    }

    public void handleMiss() {
        strReferee = nrBreak != 0 ? (strPlayer + ": " + Integer.toString(nrBreak)) : "";
        if (isPlayerA) nrMistakesA++;
        else nrMistakesB++;

        calcFrameBreaks();
        switchPlayers();
        calcStats();
    }

    public void handleFoul(boolean isGenericFoul) {  // to be called with 'true' if Foul button was pressed, 'false' if did not pot the right ball.
        nrFoulPenalty = isGenericFoul ? 4 : Math.max(nrPotted, 4);  // Foul is 4 pts, or the value of the ball if higher.

        if (isPlayerA) nrMistakesA++;
        else nrMistakesB++;

        calcFrameBreaks();
        if (isPlayerA) nrPtsB += nrFoulPenalty;
        else nrPtsA += nrFoulPenalty;
        strReferee = getString(R.string.str_referee_foul, strPlayer, nrBreak);

        // Check for tied frame situation (was same score after potting last black. One more turn was allowed. Now having failed on the black actually ends the frame instead.
        if (isBlackBallGame) {
            frameOver();
            return;
        }

        switchPlayers();
        calcStats();
    }

    public void handleReward() {
        // Adds score to current player and their  'break', 'max break' stats
        nrBreak += nrPotted;
        strReferee = Integer.toString(nrBreak);

        // Add pts and breaks
        calcFrameBreaks();
        if (isPlayerA) { // Player A
            nrPtsA += nrPotted;
            nrHitsA++;
        } else { // Player B
            nrPtsB += nrPotted;
            nrHitsB++;
        }
    }

    public void calcFrameBreaks() { // Separate function as it is used in 3 places.
        if (isPlayerA)
            nrBreakMaxFrameA = Math.max(nrBreakMaxFrameA, nrBreak);
        else
            nrBreakMaxFrameB = Math.max(nrBreakMaxFrameB, nrBreak);
    }

    public void switchPlayers() {
        // Switches active player and resets target
        isPlayerA = !isPlayerA;
        strPlayer = isPlayerA ? strPlayerA : strPlayerB;
        strPlayerIdle = strPlayer;

        // Resets target
        if (nrRedsAvailable > 0) {
            nrTarget = 1;
        } else {
            nrTarget = nrDeactivatedBall + 1;
        }
        nrBreak = 0;
    }

    public void calcStats() {
        // Calc available points left for current player to possibly score
        if (isP2) {
            nrPtsLeft = nrPtsLeftP2[nrTarget - 2]; // Read from array the preset value, e.g. target is yellow (2), then array[0] is read (=27) (=2+3+4+5+6+7).
        } else {
            nrPtsLeft = nrRedsAvailable * 8 + 27 + nrTarget == 8 ? 7 : 0; // at most x red and black pairs can be made(1+7pts). If the current target is any color (8), you can also score a black (7) on top.
        }

        // This turn's player scores
        nrPtsCurrent = isPlayerA ? nrPtsA : nrPtsB;
        nrPtsIdle = isPlayerA ? nrPtsB : nrPtsA;

        // Calc "Behind" stat
        nrPtsDifference = nrPtsIdle - nrPtsCurrent;
    }

    public int getBallValue(String color) { // Returns point value of ball's name
        switch (color) {
            case "red":
                return (1);
            case "yellow":
                return (2);
            case "green":
                return (3);
            case "brown":
                return (4);
            case "blue":
                return (5);
            case "pink":
                return (6);
            case "black":
                return (7);
            case "colored":
                return (8);
            default:
                return (0);
        }
    }

    public void updateUI() {
        updateUIStats();
        updateUIPlayers();
        updateUIButtons();
    }

    public void updateUIPlayers() { // Updates active player highlight and player scores.
        if (isPlayerA) {
            tv_player_a.setBackgroundResource(R.drawable.gradient_player_left);
            tv_player_b.setBackgroundResource(R.color.transparent);
        } else {
            tv_player_b.setBackgroundResource(R.drawable.gradient_player_right);
            tv_player_a.setBackgroundResource(R.color.transparent);
        }

        tv_player_a.setText(strPlayerA);
        tv_player_b.setText(strPlayerB);
        tv_player_a_score.setText(String.valueOf(nrPtsA));
        tv_player_b_score.setText(String.valueOf(nrPtsB));

        // Frame count
        tv_frame_compare = findViewById(R.id.tv_frame_compare);
        tv_frame_compare.setText(getString(R.string.str_frame_compare, nrFramesWonA, nrFrames, nrFramesWonB));
    }

    public void updateUIButtons() { // Updates all buttons (text, colors) as per target ball

        // RED
        btn_red.setText(getString(R.string.btn_red, nrRedsAvailable));

        if (nrDeactivatedBall >= 1) {
            // No more reds
            deactivateButton(1);
        } else {
            // There are reds
            if (nrTarget == 1) {
                // Red target
                btn_red.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btn_valid), PorterDuff.Mode.MULTIPLY);
                btn_red.setTextColor(ContextCompat.getColor(this, R.color.black));
            } else {
                // Colored target
                btn_red.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btn_invalid), PorterDuff.Mode.MULTIPLY);
                btn_red.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        }
        // MISS and FOUL
        if (isFrameOver) {
            btn_miss.setEnabled(false);
            btn_foul.setEnabled(false);
        } else {
            btn_miss.setTextColor(ContextCompat.getColor(this, R.color.black));
            btn_miss.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btn_special), PorterDuff.Mode.MULTIPLY);
            btn_foul.setTextColor(ContextCompat.getColor(this, R.color.black));
            btn_foul.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btn_special), PorterDuff.Mode.MULTIPLY);
        }

        // COLORS
        Button btn_temp;
        for (int i = 2; i <= 7; i++) {
            // Go through all colors
            btn_temp = getBallViewID(i);

            if (nrDeactivatedBall >= i) {
                // Deactivate if necessary
                deactivateButton(i);
            } else {
                if (nrTarget == i || (nrTarget == 8)) {
                    // Color button to suggest 'valid move' (green)
                    btn_temp.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btn_valid), PorterDuff.Mode.MULTIPLY);
                    btn_temp.setTextColor(ContextCompat.getColor(this, R.color.black));
                } else {
                    // Color button to suggest 'invalid move' (red)
                    btn_temp.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btn_invalid), PorterDuff.Mode.MULTIPLY);
                    btn_temp.setTextColor(ContextCompat.getColor(this, R.color.black));
                }
            }
        }
    }

    public void updateUIStats() { // Updates TextViews

        // Updates referee text
        tv_referee.setText(strReferee);

        // 
        if (nrPtsDifference >= 0) {
            tv_player_stats.setText(getString(R.string.str_realtime_stats_behind, nrFrame, Math.abs(nrPtsDifference), nrPtsLeft));
        } else if (nrPtsDifference < 0) {
            tv_player_stats.setText(getString(R.string.str_realtime_stats_ahead, nrFrame, Math.abs(nrPtsDifference), nrPtsLeft));
        }

        // Adds break to player stats if not zero
        if (nrBreak != 0) {
            tv_player_stats.setText(tv_player_stats.getText() + getString(R.string.str_break, nrBreak));
        }
    }

    public Button getBallViewID(int value) {
        switch (value) {
            case 1:
                return (btn_red);
            case 2:
                return (btn_yellow);
            case 3:
                return (btn_green);
            case 4:
                return (btn_brown);
            case 5:
                return (btn_blue);
            case 6:
                return (btn_pink);
            case 7:
                return (btn_black);
            default:
                return (btn_miss);  // testing
        }
    }

    public void deactivateButton(int value) { // Deactivates the given ball. (n/a for Miss and Foul buttons)
        Button btn = getBallViewID(value);
        btn.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btn_inactive), PorterDuff.Mode.MULTIPLY);
        btn.setTextColor(ContextCompat.getColor(this, R.color.grey));
        btn.setEnabled(false);
    }

    public void displayResults(String title, String msg) { // Popup window for Frame end and Game end situations.
        SpannedString message = new SpannedString(Html.fromHtml(msg));
        isPopupActive = true; // Required to rebuild it in case of rotation
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        dialogBuild.setMessage(message);
        dialogBuild.setTitle(title);
        if (isGameOver) { // Game over
            if (isGameWinnerPlayerA) dialogBuild.setIcon(R.drawable.ic_trophy);
            else dialogBuild.setIcon(R.drawable.ic_loser);
        } else { // Frame over
            if (isFrameWinnerPlayerA) dialogBuild.setIcon(R.drawable.ic_winner);
            else dialogBuild.setIcon(R.drawable.ic_loser);
        }

        if (!isGameOver) {
            // Add Next frame button
            dialogBuild.setNeutralButton(getString(R.string.str_next_frame), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    // Resets frame, updates frame vars
                    nrFrame++;
                    isFrameStarterPlayerA = !isFrameStarterPlayerA; // Switch frame starter
                    resetFrame(currentView);
                }
            });
        }

        // Add "New Game" button
        dialogBuild.setPositiveButton(getString(R.string.str_new_game), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                // action on button click
                openMenuActivity(currentView);
            }
        });

        // Add "Quit" button
        dialogBuild.setNegativeButton(getString(R.string.str_quit_app), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        });
        AlertDialog dialog = dialogBuild.create();
        dialog.show();
    }

    public void displayToast(String message) { // Toast on top
        toast = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
        toast.show();
    }

    public void frameOver() { // Frame end

        if (!isPopupActive) { // This is for rotation change etc
            if (isFrameWinnerPlayerA) nrFramesWonA++;
            else nrFramesWonB++;
        }

        // Show all buttons gray for style
        btn_miss.setEnabled(false);
        btn_foul.setEnabled(false);

        // Calc max break
        nrBreakMax = isFrameWinnerPlayerA ? nrBreakMaxFrameA : nrBreakMaxFrameB; // only for Referee below
        nrBreakMaxGameA = Math.max(nrBreakMaxFrameA, nrBreakMaxGameA);
        nrBreakMaxGameB = Math.max(nrBreakMaxFrameB, nrBreakMaxGameB);

        // Calc max frame points
        nrPtsMaxGameA = Math.max(nrPtsA, nrPtsMaxGameA);
        nrPtsMaxGameB = Math.max(nrPtsB, nrPtsMaxGameB);

        // Who won the frame
        isFrameWinnerPlayerA = false;
        if (nrPtsA > nrPtsB) { // A won the frame
            strFrameWinner = strPlayerA;
            strFrameLoser = strPlayerB;
            isFrameWinnerPlayerA = true;
        } else { // B won the frame
            strFrameWinner = strPlayerB;
            strFrameLoser = strPlayerA;
        }

        // Referee
        strReferee = strPlayer.equals(strFrameWinner) ? getString(R.string.str_referee_frame_won, strFrameWinner, nrBreak) : getString(R.string.str_referee_frame_lost, strPlayer, nrBreak, strPlayerIdle);

        // Check for game over
        int i = Math.max(nrFramesWonA, nrFramesWonB);
        isGameOver = (i > Math.ceil(nrFrames / 2));

        if (isGameOver) {
            // Prepare for "Game over" data

            strGameWinner = isGameWinnerPlayerA ? strPlayerA : strPlayerB;

            // Set display text
            strDisplayText = getString(R.string.str_game_stats, strGameWinner, nrFramesWonA, nrFramesWonB, nrFrames, strReferee, nrPtsMaxGameA, nrPtsMaxGameB, nrBreakMaxGameA, nrBreakMaxGameB);

            if (nrBreakMaxGameA == 147)
                strDisplayText += getString(R.string.str_game_stats_max_break, strPlayerA);
            if (nrBreakMaxGameB == 147)
                strDisplayText += getString(R.string.str_game_stats_max_break, strPlayerB);

        } else {
            // Prepare for "Frame over" data
            strDisplayText = isFrameWinnerPlayerA ? getString(R.string.str_frame_won, nrPtsA, nrPtsB) : getString(R.string.str_frame_lost, nrPtsA, nrPtsB);
            strDisplayText += getString(R.string.str_frame_stats, strReferee, nrBreakMaxFrameA, nrBreakMaxFrameB, nrMistakesA, nrMistakesB, nrHitsA, nrHitsB);
        }

        // Show frame over or game over
        String title = isGameOver ? "Game over" : "Frame over";
        displayResults(title, strDisplayText);

    }

    public void openMenuActivity(View v) { // Opens MainActivity - the new game screen

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("nrBreakMax", nrBreakMax);
        intent.putExtra("nrPtsLeft", nrPtsLeft);
        intent.putExtra("strUserName", strUserName);
        intent.putExtra("strOpponentName", strOpponentName);
        intent.putExtra("nrOpponentCheckboxID", nrOpponentCheckboxID);
        intent.putExtra("nrPtsDifference", nrPtsDifference);
        intent.putExtra("nrHitsA", nrHitsA);
        intent.putExtra("nrMistakesA", nrMistakesA);
        intent.putExtra("nrPtsA", nrPtsA);

        intent.putExtra("strReferee", strReferee);
        intent.putExtra("isFrameWinnerPlayerA", isFrameWinnerPlayerA);
        intent.putExtra("isFrameOver", isFrameOver);

        startActivity(intent);
        finish();
    }

    public void setDefaultVariables() { // Called by onCreate, at frame end. This is to avoid room for error with duplicating

        isFrameWinnerPlayerA = true;
        isGameWinnerPlayerA = true;
        isP2 = false;
        isFrameOver = false;
        isPlayerA = isFrameStarterPlayerA;
        isGameOver = false;
        isPopupActive = false;
        isBlackBallGame = false;

        strPlayerA = strUserName;
        strPlayerB = strOpponentName;
        strPlayer = strPlayerA;
        strPotted = "";
        nrPotted = 0;
        nrTarget = 1;
        
        strReferee = getString(R.string.str_referee_starter, strPlayer);

        nrPtsA = 0;
        nrPtsB = 30;
        nrPtsCurrent = 0;
        nrPtsIdle = 0;
        nrBreak = 0;
        nrBreakMaxFrameA = 0;
        nrBreakMaxFrameB = 0;
        nrMistakesA = 0;
        nrMistakesB = 0;
        nrHitsA = 0;
        nrHitsB = 0;

        nrDeactivatedBall = 0;
        nrRedsAvailable = 1;
        nrPtsLeft = 0;
        nrPtsDifference = 0;

        // Set player names once
        tv_player_a.setText(strPlayerA);
        tv_player_b.setText(strPlayerB);

        // Activate buttons after being disabled during a frame
        btn_red.setEnabled(true);
        btn_yellow.setEnabled(true);
        btn_green.setEnabled(true);
        btn_brown.setEnabled(true);
        btn_blue.setEnabled(true);
        btn_pink.setEnabled(true);
        btn_black.setEnabled(true);
        btn_miss.setEnabled(true);
        btn_foul.setEnabled(true);
    }

    public void writeDebugLog() {
        Log.d("isPlayerA", Boolean.toString(isPlayerA));
        Log.d("strPlayer", strPlayer);
        Log.d("strPlayerA", strPlayerA);
        Log.d("strPlayerB", strPlayerB);
        Log.d("nrTarget", Integer.toString(nrTarget));
        Log.d("nrRedsAvailable", Integer.toString(nrRedsAvailable));
        Log.d("nrDeactivatedBall", Integer.toString(nrDeactivatedBall));
        Log.d("nrFrame", Integer.toString(nrFrame));
        Log.d("nrFrames", Integer.toString(nrFrames));
        Log.d("isP2", Boolean.toString(isP2));
        Log.d("isGameOver", Boolean.toString(isGameOver));
        Log.d("------", "------");
    }

    public void resetFrame(View v) {  // Seems useless being this short, but this gets called by all buttons
        setDefaultVariables();
        updateUI();
    }
}