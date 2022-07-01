package com.matrix_maeny.textdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Objects;

public class ResultTextActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_text);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Result");
        TextView textView = findViewById(R.id.resultAcTxt);
        String text = getIntent().getStringExtra("result");

        if (!text.equals("")) {
            textView.setText(text);
        } else {
            textView.setText("No text available...!");

        }
    }
}