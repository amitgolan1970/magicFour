package com.golan.amit.magicfour;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MagicFourActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnLongClickListener {

    MagicFourHelper mfh;
    Button[][] buttons;
    Button btnPlayAgain, btnShuffle, btnResetDbRecords;
    TextView tvTimerDisplay, tvMovesDisplay, tvTouchesDisplay, tvBestResult;

    /**
     * Timer
     * @param savedInstanceState
     */

    private static final long MINUTES = 10;  //  10 minutes
    private static final long TIMER = MINUTES * 60 * 1000;
    private long minutesRemain, secondsRemain;
    private int countDownInterval;
    private long timeToRemain;
    CountDownTimer cTimer;

    /**
     * Background Music
     * @param savedInstanceState
     */

    MediaPlayer mp;
    SeekBar sb;
    AudioManager am;

    Animation[] animRotate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_four);

        init();

        setListeners();

        getAndDisplayBestResults();

        play();
    }

    private void getAndDisplayBestResults() {
        btnResetDbRecords.setClickable(false);
        btnResetDbRecords.setVisibility(View.GONE);
        String bestResultsInfo = "";
        MagicFourDbHelper mfdh = new MagicFourDbHelper(this);
        mfdh.open();
        try {
            mfh.setBestSeconds(mfdh.minimumSecods());
            if (mfh.getBestSeconds() > 0) {
                bestResultsInfo += String.format("best time in seconds: %d", mfh.getBestSeconds());
            }
            mfh.setBestMoves(mfdh.minimumMoves());
            if (mfh.getBestMoves() > 0) {
                bestResultsInfo += "\n";
                bestResultsInfo += String.format("best (minimum) moves: %d", mfh.getBestMoves());
            }
            if(!bestResultsInfo.isEmpty()) {
                btnResetDbRecords.setClickable(true);
                btnResetDbRecords.setVisibility(View.VISIBLE);
                tvBestResult.setText(bestResultsInfo);
            }
        } catch (Exception eq) {
            Log.e(MainActivity.DEBUGTAG, "exception when querying db for best high score records " + eq);
        }
        mfdh.close();
    }

    private void init() {
        mfh = new MagicFourHelper();
        buttons = new Button[][] {
                {findViewById(R.id.btnr0c0), findViewById(R.id.btnr0c1), findViewById(R.id.btnr0c2), findViewById(R.id.btnr0c3) },
                {findViewById(R.id.btnr1c0), findViewById(R.id.btnr1c1), findViewById(R.id.btnr1c2), findViewById(R.id.btnr1c3) },
                {findViewById(R.id.btnr2c0), findViewById(R.id.btnr2c1), findViewById(R.id.btnr2c2), findViewById(R.id.btnr2c3) },
                {findViewById(R.id.btnr3c0), findViewById(R.id.btnr3c1), findViewById(R.id.btnr3c2), findViewById(R.id.btnr3c3) }
        };
        btnPlayAgain = findViewById(R.id.btnPlayAgainId);
        btnShuffle = findViewById(R.id.btnShuffleId);
        btnResetDbRecords = findViewById(R.id.btnResetHighScoreId);
        tvMovesDisplay = findViewById(R.id.tvMovesDisplayId);
        tvTimerDisplay = findViewById(R.id.tvTimerDisplayId);
        tvTouchesDisplay = findViewById(R.id.tvTouchesDisplayId);
        tvBestResult = findViewById(R.id.tvBestResultsId);

        cTimer = null;
        timeToRemain = TIMER;

        sb = findViewById(R.id.sbId);
        mp = MediaPlayer.create(this, R.raw.mission_impossible);
        mp.start();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sb.setMax(max);
        sb.setProgress(max / 4);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, max / 4, 0);
        sb.setOnSeekBarChangeListener(this);

        animRotate = new Animation[]{
                AnimationUtils.loadAnimation(this, R.anim.anim_rotate_right),
                AnimationUtils.loadAnimation(this, R.anim.anim_rotate_left)
        };
    }

    private void play() {
        btnShuffle.setVisibility(View.VISIBLE);
        mfh.shuffle();
        mfh.resetMoves();
        mfh.resetToches();

        int r = 0;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                String tmpNum = mfh.getElementByIndex(r);
                if (!tmpNum.equalsIgnoreCase("-1")) {
                    buttons[i][j].setText(tmpNum);
                } else {
                    buttons[i][j].setText("");
                }
                r++;
            }
        }
        updateTextDisplays();
        enableAll();
    }

    private void setListeners() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setOnClickListener(this);
            }
        }
        btnPlayAgain.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnResetDbRecords.setOnClickListener(this);
        if(MainActivity.DEBUG) {
            buttons[buttons.length - 1][buttons.length - 1].setOnLongClickListener(this);
        }
        if(MainActivity.DEBUG) {
            buttons[0][0].setOnLongClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnResetDbRecords) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MagicFourDbHelper mfdh = new MagicFourDbHelper(MagicFourActivity.this);
                    mfdh.open();
                    try {
                        mfdh.resetTableToScratch();
                        tvBestResult.setText("All records in database were cleared");
                        mfh.setBestSeconds(-1);
                        mfh.setBestMoves(-1);
                        btnResetDbRecords.setClickable(false);
                        btnResetDbRecords.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Log.e(MainActivity.DEBUGTAG, "exception when trying to reset database: " + e);
                    }
                    mfdh.close();
                }
            });
            builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setTitle("מחיקת הרשומות");
            builder.setMessage("האם למחוק את הרשומות?");
            AlertDialog dlg = builder.create();
            dlg.show();
        }
        else if(v == btnShuffle) {
            playAlone(1000);
            return;
        }
        else if(v == btnPlayAgain) {

            Intent i = new Intent(this, MagicFourActivity.class);
            startActivity(i);
            return;
        }
        analyzeClick(v);
        boolean ifWon = printVerificationProcess();
        if(ifWon) {
            btnPlayAgain.setVisibility(View.VISIBLE);
            btnPlayAgain.startAnimation(animRotate[(int) (Math.random() * animRotate.length)]);

            int currentTotalSecond = (int)(minutesRemain * 60 + secondsRemain);
            MagicFourDbHelper mfdh = new MagicFourDbHelper(this);
            mfdh.open();
            try {
                mfdh.insert(String.valueOf(currentTotalSecond),
                        String.valueOf(mfh.getMoves()), MagicFourHelper.DEFAULT_PLAYER);
                if(MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG, "inserted results into db");
                }
            } catch (Exception edbi) {
                Log.e(MainActivity.DEBUGTAG, "insert to db failed with exception: " + edbi);
            }
            mfdh.close();
            if(mfh.getBestSeconds() == 0 || currentTotalSecond < mfh.getBestSeconds()) {
                Toast.makeText(this, "WON - new best score record achieved", Toast.LENGTH_LONG).show();
                tvBestResult.setText("new record achived: " + currentTotalSecond);
                mfh.setBestSeconds(currentTotalSecond);
                btnResetDbRecords.setClickable(true);
                btnResetDbRecords.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "WON !!", Toast.LENGTH_SHORT).show();
            }
            cTimer.cancel();
//            tvTimerDisplay.setText("");
            disableAll();
            paintAllOnWin();
            btnShuffle.setVisibility(View.GONE);
        } else {
            updateTextDisplays();
        }
    }

    private void playAlone(int times) {
        int startX = -1, startY = -1;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (buttons[i][j].getText().toString().trim().isEmpty()) {
                    startY = i;
                    startX = j;
                    break;
                }
            }
            if (startX != -1 && startY != -1) {
                break;
            }
        }
        if (startX == -1 || startY == -1) {
            Log.e(MainActivity.DEBUGTAG, "could not locate empty square");
            return;
        }
        //  start moving alone
        if(MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "locate empty square in matrix x=" + startX + ", y=" + startY);
        }

        for (int cnt = 0; cnt < times; cnt++) {
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "cnt: " + cnt);
            }
            boolean hasMoved = false;
            mfh.generateRandomChoicesSeq();
            //  0: up, 1: left, 2:down, 3:right
//            mfh.displayChoices();
            for (int choose = 0; !hasMoved && choose < mfh.getChoicesLength(); choose++) {

                if (mfh.getChoiceByIndex(choose) == 0) {
                    if (startY > 0) {
                        buttons[startY][startX].setText(buttons[startY-1][startX].getText().toString().trim());
                        buttons[startY-1][startX].setText("");
                        startY--;
                        hasMoved = true;
                        break;
                    }
                } else if (mfh.getChoiceByIndex(choose) == 1) {
                    if (startX > 0) {
                        buttons[startY][startX].setText(buttons[startY][startX-1].getText().toString().trim());
                        buttons[startY][startX-1].setText("");
                        startX--;
                        hasMoved = true;
                        break;
                    }

                } else if (mfh.getChoiceByIndex(choose) == 2) {
                    if (startY < (buttons.length - 1)) {
                        buttons[startY][startX].setText(buttons[startY+1][startX].getText().toString().trim());
                        buttons[startY+1][startX].setText("");
                        startY++;
                        hasMoved = true;
                        break;
                    }
                } else if (mfh.getChoiceByIndex(choose) == 3) {
                    if (startX < (buttons[startX].length - 1)) {
                        buttons[startY][startX].setText(buttons[startY][startX+1].getText().toString().trim());
                        buttons[startY][startX+1].setText("");
                        startX++;
                        hasMoved = true;
                        break;
                    }
                }
            }
        }
    }

    private void disableAll() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setClickable(false);
                buttons[i][j].setBackgroundColor(Color.BLACK);
                buttons[i][j].setTextColor(Color.BLACK);

            }
        }
    }

    private void enableAll() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setClickable(true);
                buttons[i][j].setBackgroundColor(Color.GRAY);
                buttons[i][j].setTextColor(Color.YELLOW);
            }
        }
    }

    private void paintAllOnWin() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setBackgroundColor(Color.GREEN);
                buttons[i][j].setTextColor(Color.GREEN);
            }
        }
    }

    private void paintAllOnLoose() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setText("X");
                buttons[i][j].setTextColor(Color.RED);
            }
        }
    }

    private void updateTextDisplays() {
        tvTouchesDisplay.setText(String.valueOf(mfh.getTouches()));
        if(mfh.getTouches() > 20) {
            tvTouchesDisplay.setTextColor(Color.RED);
        } else {
            tvTouchesDisplay.setTextColor(Color.MAGENTA);
        }
        tvMovesDisplay.setText(String.valueOf(mfh.getMoves()));
        if(mfh.getMoves() > 100) {
            tvMovesDisplay.setTextColor(Color.RED);
        } else {
            tvMovesDisplay.setTextColor(Color.GREEN);
        }
    }

    private void analyzeClick(View v) {
        int x = -1;
        int y = -1;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (v == buttons[i][j]) {
                    y = i;
                    x = j;
                    break;
                }
            }
            if (x != -1 && y != -1)
                break;
        }
        if (x == -1 || y == -1) {
            Log.e(MainActivity.DEBUGTAG, "something wrong with click");
            return;
        }
        if (MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "button clicked. x=" + x + ", y=" + y + ", value: " + buttons[y][x].getText().toString());
//            Toast.makeText(this, "button clicked. x=" + x + ", y=" + y + ", value: " + buttons[y][x].getText().toString(), Toast.LENGTH_SHORT).show();
        }

        /**
         * Switch places
         */

        //  upper square:
        if (y > 0) {
            String tmpNum = buttons[y - 1][x].getText().toString();
            if (tmpNum.equalsIgnoreCase("")) {
                buttons[y - 1][x].setText(buttons[y][x].getText().toString());
                buttons[y][x].setText("");
                mfh.increaseMoves();
                return;
            }
        }
        //  lower square:
        if (y < (buttons.length - 1)) {
            String tmpNum = buttons[y + 1][x].getText().toString();
            if (tmpNum.equalsIgnoreCase("")) {
                buttons[y + 1][x].setText(buttons[y][x].getText().toString());
                buttons[y][x].setText("");
                mfh.increaseMoves();
                return;
            }
        }
        //  left square:
        if (x > 0) {
            String tmpNum = buttons[y][x - 1].getText().toString();
            if (tmpNum.equalsIgnoreCase("")) {
                buttons[y][x - 1].setText(buttons[y][x].getText().toString());
                buttons[y][x].setText("");
                mfh.increaseMoves();
                return;
            }
        }
        //  right square:
        if (x < (buttons[y].length - 1)) {
            String tmpNum = buttons[y][x + 1].getText().toString();
            if (tmpNum.equalsIgnoreCase("")) {
                buttons[y][x + 1].setText(buttons[y][x].getText().toString());
                buttons[y][x].setText("");
                mfh.increaseMoves();
                return;
            }
        }
        mfh.increaseTouches();
    }

    private boolean printVerificationProcess() {
        int r = 1;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG,
                            "[" + i + "," + j + "]" +
                                    "->" + buttons[i][j].getText().toString() +
                                    " -> r=" + r);
                }
                if (buttons[i][j].getText().toString().equalsIgnoreCase("") &&
                        r < MagicFourHelper.NUMARR) {
                    if (MainActivity.DEBUG) {
                        Log.d(MainActivity.DEBUGTAG, "returning due to empty square");
                    }
                    return false;
                }
                if (r < MagicFourHelper.NUMARR) {
                    int tmpInt = Integer.parseInt(buttons[i][j].getText().toString());
                    if (tmpInt != r) {
                        if (MainActivity.DEBUG) {
                            Log.d(MainActivity.DEBUGTAG, "returning due to not equal");
                        }
                        return false;
                    }
                }
                r++;
            }
        }
        return true;
    }

    private void timerDemo(final long millisInFuture) {
        countDownInterval = 1000;
        cTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeToRemain = millisUntilFinished;
                long Minutes = timeToRemain / (60 * 1000) % 60;
                long Seconds = timeToRemain / 1000 % 60;

                minutesRemain = MINUTES - (Minutes + 1);
                secondsRemain = 60 - Seconds;
                if(secondsRemain == 60) {
                    secondsRemain = 0;
                    minutesRemain++;
                }

                if(Minutes == 1 && Seconds == 0) {
                    Toast.makeText(MagicFourActivity.this, "הזמן אוזל...", Toast.LENGTH_LONG).show();
                }
                if(Minutes < 1) {
                    tvTimerDisplay.setTextColor(Color.RED);
                } else {
                    tvTimerDisplay.setTextColor(Color.BLUE);
                }
                String tmpTime = String.format("remain: %02d:%02d , passed: %01d:%02d", Minutes, Seconds, minutesRemain, secondsRemain);
                tvTimerDisplay.setText(tmpTime);
            }

            @Override
            public void onFinish() {
                cTimer.cancel();
                btnPlayAgain.setVisibility(View.VISIBLE);
                btnPlayAgain.startAnimation(animRotate[(int) (Math.random() * animRotate.length)]);
                Toast.makeText(MagicFourActivity.this, "נגמר הזמן", Toast.LENGTH_SHORT).show();
                tvTimerDisplay.setText("");
                disableAll();
                paintAllOnLoose();
                btnShuffle.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerDemo(timeToRemain);
        if (mp != null) {
            try {
                mp.start();
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "on resume, media player start exception");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cTimer.cancel();
        if (mp != null) {
            try {
                mp.pause();
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "on pause, media player pause exception");
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public boolean onLongClick(View v) {
        if(v == buttons[0][0]) {
            logCurrentState();
            return  true;
        }

        int r = 1;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setText(String.valueOf(r));
                r++;
            }
        }
        buttons[buttons.length-1][buttons.length-1].setText("");
        return true;
    }

    private void logCurrentState() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i = 0; i < buttons.length; i++) {
            for(int j = 0; j < buttons[i].length; j++) {
                if(buttons[i][j].getText().toString().trim().isEmpty()) {
                    sb.append("-1,");
                } else {
                    sb.append(buttons[i][j].getText().toString().trim() + ",");
                }
            }
        }
        sb.append("},");
        Log.i(MainActivity.DEBUGTAG, sb.toString());
    }
}
