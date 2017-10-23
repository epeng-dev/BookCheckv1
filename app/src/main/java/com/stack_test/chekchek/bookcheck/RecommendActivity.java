package com.stack_test.chekchek.bookcheck;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.stack_test.chekchek.login.R;

import java.util.ArrayList;

public class RecommendActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ListViewItem> Itemsets;
    public RequestManager requestManager;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        final String libraries = getIntent().getStringExtra("Libraries");
        requestManager = Glide.with(this);
        recyclerView = (RecyclerView) findViewById(R.id.RecommendList);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Itemsets = new ArrayList<ListViewItem>();

        if(Itemsets.isEmpty()){
            Itemsets.add(new ListViewItem(null, "추천하는 책이", "없습니다.", null, null, false));
        }

        adapter = new ListViewAdapter(getApplicationContext(),Itemsets, requestManager, libraries);
        recyclerView.setAdapter(adapter);
    }

}


