package com.example.autofitgridview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DataModel> dataModel;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView text_title;
        TextView text_desc;

        public MyViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
            this.text_title = (TextView) view.findViewById(R.id.text_title);
            this.text_desc = (TextView) view.findViewById(R.id.text_desc);
        }
    }

    public CustomAdapter(Context _context, ArrayList<DataModel> data) {
        this.context = _context;
        this.dataModel = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        // view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        TextView text_title = holder.text_title;
        TextView text_desc = holder.text_desc;

        Glide.with(context)
                .load(dataModel.get(position).getImage())
                .placeholder(R.drawable.legend_blogs)
                .into(holder.imageView);

        text_title.setText(dataModel.get(position).getTitle());
        text_desc.setText(dataModel.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }
}
