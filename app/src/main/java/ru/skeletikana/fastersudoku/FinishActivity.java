package ru.skeletikana.fastersudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;

public class FinishActivity extends AppCompatActivity {

    TextView txtScore, txtBest;
    Button btnExitToMenu;

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
        btnExitToMenu = (Button) findViewById(R.id.btnExitToMenu);

        btnExitToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(FinishActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}