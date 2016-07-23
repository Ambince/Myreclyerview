package com.example.amence_a.myreclyerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class StargeredActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecylerView;
    private List<String> mDatas;
    private Button mGridView;
    private Button mListView;
    private Button mHorizontal;
    private Button mStaggered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatas();
        initView();

        mRecylerView.setAdapter(new MyAdapter(this, mDatas));
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecylerView.setLayoutManager(manager);
        //设置item的分割线
        mRecylerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
    }

    private void initView() {
        mRecylerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mGridView = (Button) findViewById(R.id.gridView);
        mListView = (Button) findViewById(R.id.listView);
        mHorizontal = (Button) findViewById(R.id.horizontal);
        mStaggered = (Button) findViewById(R.id.staggered);

        mGridView.setOnClickListener(this);
        mListView.setOnClickListener(this);
        mHorizontal.setOnClickListener(this);
        mStaggered.setOnClickListener(this);
    }

    private void initDatas() {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add(0, "" + (char) i);

        }


    }

    @Override
    public void onClick(View v) {
        Log.v("Amence","gridView");

        switch (v.getId()) {
            case R.id.gridView:
                Log.v("Amence","gridView");
                mRecylerView.setLayoutManager(new GridLayoutManager(this, 3));

                break;
            case R.id.listView:
                mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                break;
            case R.id.horizontal:
                mRecylerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false));

                break;
            case R.id.staggered:
                break;

        }
    }
}
