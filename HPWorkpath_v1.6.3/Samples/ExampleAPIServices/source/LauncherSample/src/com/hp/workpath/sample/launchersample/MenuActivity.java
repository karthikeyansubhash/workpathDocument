// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.launchersample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Button mCopyButton;
    Button mPrintButton;
    Button mScanButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewElements();
    }

    private void findViewElements() {
        mCopyButton = findViewById(R.id.copyButton);
        mPrintButton = findViewById(R.id.printButton);
        mScanButton = findViewById(R.id.scanButton);

        mCopyButton.setOnClickListener(buttonClickListener);
        mPrintButton.setOnClickListener(buttonClickListener);
        mScanButton.setOnClickListener(buttonClickListener);
    }

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view instanceof Button) {
                String text = ((Button) view).getText().toString();
                Toast.makeText(MenuActivity.this, text + " Button", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
