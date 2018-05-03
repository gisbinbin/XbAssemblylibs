package com.xb.xbassemblylibs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.xb.xbassemblylibs.Util.Utils;
import com.xb.xblibrary.Adapter.CommonAdapter;
import com.xb.xblibrary.Adapter.CommonViewHolder;
import com.xb.xblibrary.RefreshSwipeMenuList.RefreshTime;
import com.xb.xblibrary.RefreshSwipeMenuList.bean.SwipeMenu;
import com.xb.xblibrary.RefreshSwipeMenuList.bean.SwipeMenuItem;
import com.xb.xblibrary.RefreshSwipeMenuList.interfaces.IXListViewListener;
import com.xb.xblibrary.RefreshSwipeMenuList.interfaces.OnMenuItemClickListener;
import com.xb.xblibrary.RefreshSwipeMenuList.interfaces.SwipeMenuCreator;
import com.xb.xblibrary.view.RefreshSwipeMenuListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by binbin1058 on 2018/4/27.
 */

public class RefreshSwipeMenuListActivity extends Activity implements IXListViewListener {
    private RefreshSwipeMenuListAdpter adapter;
    private RefreshSwipeMenuListView mListView;
    private List<String> datas;
    private Context mContext;
    private int pageIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refereshswipemenulist);
        mContext=RefreshSwipeMenuListActivity.this;
        datas=new ArrayList<String>();
        mListView=findViewById(R.id.RefreshSwipeMenuList_Dataview);
        pageIndex=0;
        datas.clear();
        for (int i=0;i<15;i++){
            datas.add("测试"+(pageIndex*15+i+1));
        }
        pageIndex=pageIndex+1;
        adapter = new RefreshSwipeMenuListAdpter(mContext, datas, R.layout.item_layout);
        mListView.setAdapter(adapter);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true,true);
        mListView.setXListViewListener(this);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                openItem.setWidth(Utils.dip2px(mContext,90));
                openItem.setTitle("删除");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
                SwipeMenuItem closeItem = new SwipeMenuItem(getApplicationContext());
                closeItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0xC9, 0xCE)));
                closeItem.setWidth(Utils.dip2px(mContext,90));
                closeItem.setTitle("关闭");
                closeItem.setTitleSize(18);
                closeItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(closeItem);
            }
        };
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                datas.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        }, 500);
                        break;
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
        });
    }

    @Override
    public void onRefresh() {
        String t=RefreshTime.getRefreshTime(mContext);
        mListView.setRefreshTime(t);
        pageIndex=0;
        datas.clear();
        for (int i=0;i<15;i++){
            datas.add("测试"+(pageIndex*15+i+1));
        }
        pageIndex=pageIndex+1;
        adapter.notifyDataSetChanged();
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true,true);
        mListView.setXListViewListener(this);
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        RefreshTime.setRefreshTime(getApplicationContext(), df.format(new Date()));
        mListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        if(pageIndex==0)
            datas.clear();
        if(pageIndex<5) {
            for (int i = 0; i < 15; i++) {
                datas.add("测试" + (pageIndex * 15 + i + 1));
            }
            pageIndex = pageIndex + 1;
            adapter.notifyDataSetChanged();
            mListView.setPullLoadEnable(true, true);
            mListView.setXListViewListener(this);
            mListView.stopRefresh();
            mListView.stopLoadMore();
        }
        else {
            mListView.setPullLoadEnable(true, false);
            mListView.stopRefresh();
            mListView.stopLoadMore();
        }

    }

    public class RefreshSwipeMenuListAdpter extends CommonAdapter<String> {
        List<String> mlistData;
        public RefreshSwipeMenuListAdpter(Context context, List<String> listData, int layoutId) {
            super(context, listData, layoutId);
            mlistData=listData;
        }

        @Override
        protected void fillData(CommonViewHolder holder, int position) {
            if(mlistData!=null) {
                ((TextView) holder.getView(R.id.item_text1)).setText(mlistData.get(position));
            }
        }
    }
}
