package ru.skeletikana.fastersudoku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    Button btnInfo;
    private long backPressedTime;

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish();
            System.exit(0);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnInfo = findViewById(R.id.btnInfo);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BoardActivity.class);
            startActivity(intent);
        });

        AlertDialog.Builder builderInfo = new AlertDialog.Builder(MainActivity.this);
        builderInfo.setTitle("Faster Sudoku")
                .setMessage("version: 1.0\nmade by skeletikana, 2021")
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.cancel();
                });

        btnInfo.setOnClickListener(v -> {
            AlertDialog alertInfo = builderInfo.create();
            builderInfo.show();
        });
    }
}