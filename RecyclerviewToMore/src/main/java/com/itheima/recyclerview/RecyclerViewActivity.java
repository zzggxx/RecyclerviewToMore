package com.itheima.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerViewActivity extends AppCompatActivity {
    private int[] arr = new int[11];
    private RecyclerView mRv;
    int startX;
    int dx;
    int viewWidth;
    int loadWidth = 0;
    View loadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        /*数据容器减1,也就是得到固定的数据后要进行加1*/
        mRv = (RecyclerView) findViewById(R.id.rv);
        for (int i = 0; i < arr.length - 1; i++) {
            arr[i] = R.mipmap.c;
        }
        /*布局管理器*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv.setLayoutManager(linearLayoutManager);
        /*适配器*/
        mRv.setAdapter(new MyAdapter());
        /*进行监听*/
        mRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getX();
                        dx = endX - startX;
                        if (islastitem(mRv) && dx < 0 && loadView != null) {
                            loadWidth = Math.abs(dx);
                            loadView.setPadding(0, 0, -viewWidth + loadWidth, 0);
                        }
                        if (dx > 0 && loadView != null) {
                            loadView.setPadding(0, 0, -viewWidth, 0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        /*状态重回*/
                        if (islastitem(mRv)) {
                            if (Math.abs(dx) > 200) {
                                Intent intent = new Intent(getApplicationContext(), LoadActivity.class);
                                startActivity(intent);
                            }
                            loadView.setPadding(0, 0, -viewWidth, 0);
                        }
                        break;
                }
                return false;
            }
        });
    }

    /*设置不同的适配器,注意是不同的布局*/
    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case 1:
                    /*不同的布局文件*/
                    view = View.inflate(getApplicationContext(), R.layout.item1, null);
                    holder = new LoadHolder(view);
                    break;
                case 0:
                    view = View.inflate(getApplicationContext(), R.layout.item0, null);
                    holder = new MyHolder(view);
                    break;
            }
            return holder;
        }

        //给ViewHolder绑定数据
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyHolder) {
                ((MyHolder) holder).iv.setImageResource(arr[position]);
            }
            /*其他不进行绑定数据*/
        }

        @Override
        public int getItemViewType(int position) {
            if (position == arr.length - 1) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public int getItemCount() {
            return arr.length;
        }

        /*定义两种ViewHolder*/
        class MyHolder extends RecyclerView.ViewHolder {
            ImageView iv;

            public MyHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.iv);
            }
        }

        class LoadHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public LoadHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv);
                /*重新的测量,并且设置padding值*/
                itemView.measure(0, 0);
                viewWidth = itemView.getMeasuredWidth();
                itemView.setPadding(0, 0, -viewWidth + loadWidth, 0);
                loadView = itemView;
            }
        }

    }

    /*不需要进行滑动监听,若是进行了滑动监听,有些坑,快滑或者是飞速的滑动是兼听不到的*/
    public boolean islastitem(RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //获取最后一个完全显示的ItemPosition
        int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
        int totalItemCount = manager.getItemCount();
        // 判断是否滚动到底部，并且是向右滚动
        if (lastVisibleItem == (totalItemCount - 2) || lastVisibleItem == (totalItemCount - 1)) {
            Log.i("onscrolled", "最后一条向左");
            return true;
        } else {
            return false;
        }
    }

}
