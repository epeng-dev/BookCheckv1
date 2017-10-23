package com.stack_test.chekchek.bookcheck;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.stack_test.chekchek.login.R;

import java.io.Serializable;
import java.util.ArrayList;

public class BooklistActivity extends Activity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ListViewItem> Itemsets;
    public RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booklist);
        final String libraries = getIntent().getStringExtra("Libraries");
        requestManager = Glide.with(this);
        recyclerView = (RecyclerView) findViewById(R.id.BookList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Itemsets = new ArrayList<ListViewItem>();

        ArrayList<String> url = getIntent().getStringArrayListExtra("Url");
        ArrayList<String> Title = getIntent().getStringArrayListExtra("Title");
        ArrayList<String> Author = getIntent().getStringArrayListExtra("Author");
        ArrayList<String> Description = getIntent().getStringArrayListExtra("Description");
        final ArrayList<String> ISBN = getIntent().getStringArrayListExtra("ISBN");
        final ArrayList<String> Available = getIntent().getStringArrayListExtra("Available");
        for (int i = 0; i < ISBN.size(); i++) {
            Itemsets.add(new ListViewItem(url.get(i), Title.get(i), Author.get(i), Description.get(i), ISBN.get(i), Boolean.parseBoolean(Available.get(i))));
        }

        adapter = new ListViewAdapter(getApplicationContext(),Itemsets, requestManager, libraries);
        recyclerView.setAdapter(adapter);
    }
}


