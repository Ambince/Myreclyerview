package com.example.amence_a.myreclyerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Amence_A on 2016/7/21.
 */
public class MyAdapter extends RecyclerView.Adapter<MyHolder> {


    private Context mContext;
    private List<String> mDatas;
    private LayoutInflater mLayoutInflater;

    public MyAdapter(Context context, List<String> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    //将获取得到的xml文件放入ViewHoler中实例化控件
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.my_item, parent, false);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    //在这里设置ViewHolder中控件的值
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        //给相应的控件设置值
        holder.mTextView.setText(mDatas.get(position));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}


class MyHolder extends RecyclerView.ViewHolder {
    TextView mTextView;

    public MyHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.my_textView);

    }
}
