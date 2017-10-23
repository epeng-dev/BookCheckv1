package com.stack_test.chekchek.bookcheck;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stack_test.chekchek.login.R;

/**
 * Created by 10201Kangminsub on 2016-09-25.
 */
public class LightActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        final String Title = getIntent().getStringExtra("Title");
        final String Author = getIntent().getStringExtra("Author");
        final String ISBN = getIntent().getStringExtra("ISBN");
        final String Where = getIntent().getStringExtra("BookcaseNumber");
        final LinearLayout llColor = (LinearLayout) findViewById(R.id.LLColor);
        final TextView tvWhat = (TextView) findViewById(R.id.tWhat);
        final TextView tvWhere = (TextView) findViewById(R.id.tWhere);
        String LightColor = getIntent().getStringExtra("lightColor");


       llColor.setBackgroundColor(Color.parseColor(LightColor));
        tvWhat.setText("작가: "+ Author + "\n" + "책: " + Title + "\n" + "ISBN: " + ISBN);
        tvWhere.setText("책은 "+Where +"번 책장에 있습니다." + "\n" + "불은 10초간 유지됩니다." + "\n" + "불의 색깔은 위 그림과 같습니다.");

    }
}
