package ru.skeletikana.fastersudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FinishActivity extends AppCompatActivity {

    TextView txtScore, txtBest;
    Button btnExitToMenu;
    SharedPreferences preferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FinishActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent boardIntent = getIntent();

        String fScore = boardIntent.getStringExtra("fscore");

        txtScore = (TextView) findViewById(R.id.txtScore);
        txtScore.setText(fScore);

        txtBest = (TextView) findViewById(R.id.txtBest);
        String toBestScore = checkBest(fScore);
        txtBest.setText(toBestScore);

        btnExitToMenu = (Button) findViewById(R.id.btnExitToMenu);

        btnExitToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(FinishActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    public String checkBest(String fScore) {
        int nowScore = Integer.parseInt(fScore);
        int prevScore = preferences.getInt("bestScore", 0);

        if (nowScore > prevScore) {
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("bestScore", nowScore);
            Toast.makeText(FinishActivity.this, "New best score!", Toast.LENGTH_SHORT).show();
            prevScore = nowScore;
        }

        String setScore = String.valueOf(prevScore);

        return ("Best score: " + setScore);
    }
}