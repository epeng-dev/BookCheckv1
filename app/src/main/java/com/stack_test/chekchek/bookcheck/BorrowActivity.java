package com.stack_test.chekchek.bookcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.stack_test.chekchek.login.R;

public class BorrowActivity extends AppCompatActivity {
    private ImageButton Barcode;
    private ImageButton RFID;
    String library;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        library = getIntent().getStringExtra("Libraries");
        Barcode = (ImageButton) findViewById(R.id.bBarcode);
        RFID = (ImageButton) findViewById(R.id.bRFID);

        Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BarcodeIntent = new Intent(BorrowActivity.this, BarcodeReadActivity.class);
                BarcodeIntent.putExtra("Libraries", library);
                startActivity(BarcodeIntent);
            }
        });

        RFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RFIDIntent = new Intent(BorrowActivity.this, RFIDBorrow.class);
                RFIDIntent.putExtra("Libraries", library);
                startActivity(RFIDIntent);
            }
        });
    }
}
