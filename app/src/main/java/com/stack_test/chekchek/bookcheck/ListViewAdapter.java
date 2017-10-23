package com.stack_test.chekchek.bookcheck;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.stack_test.chekchek.login.R;

import java.util.ArrayList;

class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {
    final ArrayList<ListViewItem> Itemset;
    private RequestManager glide;
    private Context Listcontext;
    private String libraries;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView BookImage;
        public TextView TitleTxt;
        public TextView AuthorTxt;
        public TextView AvailableTxt;
        public CardView ItemView;
        public ViewHolder(View itemView){
            super(itemView);
            BookImage = (ImageView)itemView.findViewById(R.id.BookImage);
            TitleTxt = (TextView)itemView.findViewById(R.id.BookTitle);
            AuthorTxt = (TextView)itemView.findViewById(R.id.BookAuthor);
            AvailableTxt = (TextView)itemView.findViewById(R.id.BookAvailble);
            ItemView = (CardView)itemView.findViewById(R.id.ItemCardView);
        }
    }

    public ListViewAdapter(Context context, ArrayList<ListViewItem> itemset, RequestManager requestManager, String library){
        this.Listcontext = context;
        this.Itemset = itemset;
        this.glide = requestManager;
        this.libraries = library;
    }

    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        holder.AuthorTxt.setText(Itemset.get(position).Author);
        holder.TitleTxt.setText(Itemset.get(position).Title);
        if(Itemset.get(position).Available){
            holder.AvailableTxt.setText("이용가능");
            holder.AvailableTxt.setTextColor(ContextCompat.getColor(Listcontext,R.color.Ocecn));
        }else{
            holder.AvailableTxt.setText("이용불가");
            holder.AvailableTxt.setTextColor(ContextCompat.getColor(Listcontext, R.color.Scarlet));
        }
        glide.load(Itemset.get(position).BookimageURL).placeholder(R.drawable.loading).error(R.drawable.warning).into(holder.BookImage);

        holder.ItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Itemset.get(position).Available) {
                    Intent intent = new Intent(Listcontext, BookinfoActivity.class);
                    intent.putExtra("item", Itemset.get(position));
                    intent.putExtra("Libraries", libraries);
                    Listcontext.startActivity(intent);
                }
                else{
                    Toast.makeText(Listcontext, "이용하실 수 없습니다. \n불편을 끼쳐 죄송합니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Itemset.size();
    }
}

