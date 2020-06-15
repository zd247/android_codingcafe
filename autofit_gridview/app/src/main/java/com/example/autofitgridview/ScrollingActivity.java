package com.example.autofitgridview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {

    private static Context context;
    private static CustomAdapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        context = this;

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();
        for (int i = 0; i < DataSet.title_Array.length; i++) {
            data.add(new DataModel(
                    DataSet.image_Array[i],
                    DataSet.title_Array[i],
                    DataSet.desc_Array[i]
            ));
        }

        adapter = new CustomAdapter(context, data);
        recyclerView.setAdapter(adapter);
    }
}