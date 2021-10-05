package ru.skeletikana.fastersudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class BoardActivity extends AppCompatActivity {

    int commonScore;
    long addTimer;
    long timerRemain;
    static int holesInBoard;
    static int fullViewBoard = 9;
    int cellViewBoard = 3;
    boolean pauseFlag = false;

    CountDownTimer timer;

    private class NumButton {
        int value;
        Button bt;

        public NumButton(int initValue, Context THIS) {
            value = initValue;
            bt = new Button(THIS);
            bt.setTextColor(Color.WHITE);
            bt.setTextSize(24);
            bt.setBackgroundColor(Color.TRANSPARENT);
            bt.setTypeface(ResourcesCompat.getFont(BoardActivity.this, R.font.northern_gardarika));
            bt.setText(String.valueOf(value));
            bt.setOnClickListener(v -> {
                for (int i = 0; i < fullViewBoard; i++) {
                    for (int j = 0; j < fullViewBoard; j++) {
                        if (board[i][j].changeNum) {
                            board[i][j].checkEnd(value);
                        }
                    }
                }
            });
        }
    }

    private class Cell {
        int value;
        boolean fixed, changeNum;
        Button bt;

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable buttonDarker = getDrawable(R.drawable.board_button_darker);
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable buttonLighter = getDrawable(R.drawable.board_button_lighter);
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable buttonChosen = getDrawable(R.drawable.board_button_chosen);
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable buttonError = getDrawable(R.drawable.board_button_error);

        public void checkEnd(int newValue) {
            value = newValue;
            bt.setText(String.valueOf(newValue));
            if (completed()) {
                if (completeCorrect()) {
                    long newTimer = mTimeLeft;
                    timer.cancel();
                    Intent intent = new Intent(BoardActivity.this, BoardActivity.class);
                    intent.putExtra("cscore", ++commonScore);
                    intent.putExtra("rtimer", newTimer);
                    intent.putExtra("addtimer", (addTimer + 10000));
                    intent.putExtra("holes", ++holesInBoard);
                    startActivity(intent);
                    finish();
                }
                else {
                    new Thread(() -> {
                        try {
                            for (int i = 0; i < fullViewBoard; i++) {
                                for (int j = 0; j < fullViewBoard; j++) {
                                    board[i][j].bt.setBackground(buttonError);
                                }
                            }

                            Thread.sleep(700);

                            for (int i = 0; i < fullViewBoard; i++) {
                                for (int j = 0; j < fullViewBoard; j++) {
                                    if (((i > 2 && i < 6) || (j > 2 && j < 6)) && !((((i > 2 && i < 6) && (j > 2 && j < 6))))) {
                                        board[i][j].bt.setBackground(buttonLighter);
                                    } else {
                                        board[i][j].bt.setBackground(buttonDarker);
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }

        public Cell(int initValue, boolean darker, Context THIS) {
            value = initValue;
            fixed = value != 0;
            changeNum = false;
            bt = new Button(THIS);
            if (darker) {
                bt.setBackground(buttonDarker);
            } else {
                bt.setBackground(buttonLighter);
            }
            bt.setTypeface(ResourcesCompat.getFont(BoardActivity.this, R.font.northern_gardarika));
            bt.setTextSize(32);
            if (fixed) {
                bt.setText(String.valueOf(value));
                bt.setTextColor(Color.WHITE);
            } else {
                bt.setTextColor(Color.parseColor("#AEAEAE"));
            }
            bt.setOnClickListener(v -> {
                for (int i = 0; i < fullViewBoard; i++) {
                    for (int j = 0; j < fullViewBoard; j++) {
                        if (((i > 2 && i < 6) || (j > 2 && j < 6)) && !((((i > 2 && i < 6) && (j > 2 && j < 6))))) {
                            board[i][j].bt.setBackground(buttonLighter);
                        } else {
                            board[i][j].bt.setBackground(buttonDarker);
                        }
                        board[i][j].changeNum = false;
                    }
                }
                if (fixed) return;
                changeNum = true;
                bt.setBackground(buttonChosen);
            });
        }
    }

    boolean completed() {
        for (int i = 0; i < fullViewBoard; i++) {
            for (int j = 0; j < fullViewBoard; j++) {
                if (board[i][j].value == 0)
                    return false;
            }
        }
        return true;
    }

    boolean completeCorrect(int i1, int j1, int i2, int j2) {
        boolean[] seen = new boolean[fullViewBoard + 1];
        for (int i = 1; i <= fullViewBoard; i++) seen[i] = false;
        for (int i = i1; i < i2; i++) {
            for (int j = j1; j < j2; j++) {
                int thatValue = board[i][j].value;
                if (thatValue != 0) {
                    if (seen[thatValue]) return false;
                    seen[thatValue] = true;
                }
            }
        }
        return true;
    }

    boolean completeCorrect() {
        for (int i = 0; i < fullViewBoard; i++) {
            if (!completeCorrect(i, 0, i+1, fullViewBoard)) return false;
        }
        for (int j = 0; j < fullViewBoard; j++) {
            if (!completeCorrect(0, j, fullViewBoard, j+1)) return false;
        }
        for (int i = 0; i < cellViewBoard; i++) {
            for (int j = 0; j < cellViewBoard; j++) {
                if (!completeCorrect(
                        cellViewBoard * i,
                        cellViewBoard * j,
                        cellViewBoard * i + cellViewBoard,
                        cellViewBoard * j + cellViewBoard))
                    return false;
            }
        }
        return true;
    }

    public void pauseGame() {
        if (pauseFlag) {
            pauseFlag = false;
            timerRemain = mTimeLeft;
            timer = new CountDownTimer(timerRemain, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeft = millisUntilFinished;
                    updateCountDownText();
                }

                @Override
                public void onFinish() {
                    String finalScore = Integer.toString(commonScore);
                    Intent intent = new Intent(BoardActivity.this, FinishActivity.class);
                    intent.putExtra("fscore", finalScore);
                    startActivity(intent);
                }
            }.start();
        } else {
            pauseFlag = true;
            timer.cancel();
        }
    }

    long mTimeLeft;
    TextView txtTimer;

    private void startTimer() {
        timer = new CountDownTimer(timerRemain + addTimer, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                String finalScore = Integer.toString(commonScore);
                Intent intent = new Intent(BoardActivity.this, FinishActivity.class);
                intent.putExtra("fscore", finalScore);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeft / 1000) / 60;
        int seconds = (int) (mTimeLeft / 1000) % 60;

        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        txtTimer.setText(timeLeft);
    }

    Cell[][] board;
    NumButton[] numpad;
    String input;

    LinearLayout backLayout;
    TableLayout tl, numTl, upTl;

    @Override
    public void onBackPressed() {
        pauseGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Intent startIntent = getIntent();
        commonScore = startIntent.getIntExtra("cscore", 0);
        timerRemain = startIntent.getLongExtra("rtimer", 0);
        addTimer = startIntent.getLongExtra("addtimer", 30000);
        holesInBoard = startIntent.getIntExtra("holes", 9);

        backLayout = (LinearLayout) findViewById(R.id.backLayout);

        upTl = new TableLayout(this);
        TableRow upTr = new TableRow(this);
        TextView nowScore = new TextView(this);
        nowScore.setText(String.valueOf(commonScore));
        nowScore.setTextColor(Color.LTGRAY);
        nowScore.setTextSize(32);
        nowScore.setGravity(Gravity.CENTER);
        upTr.addView(nowScore);
        txtTimer = new TextView(this);
        txtTimer.setBackgroundColor(Color.LTGRAY);
        txtTimer.setGravity(Gravity.CENTER);
        txtTimer.setTextColor(Color.DKGRAY);
        txtTimer.setTextSize(36);
        upTr.addView(txtTimer);
        Button btnPause = new Button(this);
        btnPause.setText("Pause");
        btnPause.setOnClickListener(v -> pauseGame());
        upTr.addView(btnPause);
        upTl.addView(upTr);
        upTl.setShrinkAllColumns(true);
        upTl.setStretchAllColumns(true);
        LinearLayout.LayoutParams timerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        backLayout.addView(upTl, timerParams);

        int[][] beginBoard = GenerateBoard.generate(fullViewBoard, holesInBoard);
        board = new Cell[fullViewBoard][fullViewBoard];
        tl = new TableLayout(this);
        for (int i = 0; i < fullViewBoard; i++) {
            TableRow tr = new TableRow(this);
            for (int j = 0; j < fullViewBoard; j++) {
                if (((i > 2 && i < 6) || (j > 2 && j < 6)) && !((((i > 2 && i < 6) && (j > 2 && j < 6))))) {
                    board[i][j] = new Cell(beginBoard[i][j], false,this);
                } else {
                    board[i][j] = new Cell(beginBoard[i][j], true, this);
                }
                tr.addView(board[i][j].bt);
            }
            tl.addView(tr);
        }
        tl.setShrinkAllColumns(true);
        tl.setStretchAllColumns(true);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        backLayout.addView(tl, lParams);

        numpad = new NumButton[fullViewBoard];
        numTl = new TableLayout(this);
        TableRow numTr = new TableRow(this);
        for (int i = 0; i < fullViewBoard; i++) {
            numpad[i] = new NumButton(i + 1, this);
            numTr.addView(numpad[i].bt);
        }
        numTl.addView(numTr);
        numTl.setShrinkAllColumns(true);
        numTl.setStretchAllColumns(true);
        LinearLayout.LayoutParams lParamsNum = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        backLayout.addView(numTl, lParamsNum);
        startTimer();
    }
}