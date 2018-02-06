package com.example.android.snookertracker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String strUserName, strOpponentName;
    String strIncompleteAlert;
    String strURL;
    int nrOpponentCheckboxID, nrFrames;
    boolean isMistake;

    EditText et_username;
    RadioGroup rg_opponent_list;
    RadioButton rb_opponent;
    TextView tv_nr_frames;
    Button btn_rules;
    Toast toast;
    Long timestampLastToast, timestampClick, timeInterval;

    static final int TOAST_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        nrFrames = 5; // if not set by Switch change
        timestampLastToast = SystemClock.elapsedRealtime() - TOAST_TIMEOUT;

        strURL = getString(R.string.str_rules_url);
        strIncompleteAlert = getString(R.string.str_incomplete_full);
        isMistake = false;

        tv_nr_frames = findViewById(R.id.tv_nr_frames);
        et_username = findViewById(R.id.et_player_name);
        rg_opponent_list = findViewById(R.id.rg_opponent_list);
        btn_rules = findViewById(R.id.btn_rules);

        if (getIntent().getExtras() != null) {
            // Get variables from game over

            strUserName = getIntent().getStringExtra("strUserName");
            strOpponentName = getIntent().getStringExtra("strOpponentName");
            nrOpponentCheckboxID = getIntent().getExtras().getInt("nrOpponentCheckboxID");

            rb_opponent = findViewById(nrOpponentCheckboxID);
            // Set EditText to previous name
            et_username.setText(strUserName);

            // Set radio button to previous selection
            rb_opponent.setChecked(true);
        }

        btn_rules.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.MULTIPLY);
        
        SwitchCompat sw_nr_frames = findViewById(R.id.sw_nr_frames);
        sw_nr_frames.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    nrFrames = 7;
                    tv_nr_frames.setText("7");
                } else {
                    // The toggle is disabled
                    nrFrames = 5;
                    tv_nr_frames.setText("5");
                }
            }
        });
    }

    public void startGame(View view) {

        isMistake = false;
        timestampClick = SystemClock.elapsedRealtime();

        // Get player's name
        strUserName = et_username.getText().toString();

        // Get opponent's name
        nrOpponentCheckboxID = rg_opponent_list.getCheckedRadioButtonId();

        // Check if name is filled in and a radio button is selected
        if (strUserName.equals("")) {
            // No username set
            strIncompleteAlert = getString(R.string.str_incomplete_name);
            isMistake = true;
        }

        if (nrOpponentCheckboxID == -1) {
            // No opponent selected
            strIncompleteAlert = isMistake ? getString(R.string.str_incomplete_full) : getString(R.string.str_incomplete_opponent);
            isMistake = true;
        }

        timeInterval = timestampClick - timestampLastToast;
        if (timeInterval > TOAST_TIMEOUT) {
            // Avoid toast stacking; exec if 2 seconds have passed ( Toast.LENGTH_SHORT )
            displayToast(strIncompleteAlert);
            timestampLastToast = SystemClock.elapsedRealtime();
        }

        if (!isMistake) {
            // Start game
            rb_opponent = findViewById(nrOpponentCheckboxID);
            strOpponentName = rb_opponent.getText().toString();

            Intent i = new Intent(this, GameActivity.class);
            i.putExtra("username", strUserName);
            i.putExtra("opponent", strOpponentName);
            i.putExtra("nrOpponentCheckboxID", nrOpponentCheckboxID);
            i.putExtra("nrFrames", nrFrames);

            toast.cancel();
            startActivity(i);
            finish();
        }

//        timestamp2 = SystemClock.elapsedRealtime();
    }

    public void displayToast(String message) {
        toast = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public void showRules(View view) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(strURL));
        startActivity(i);
    }

}
