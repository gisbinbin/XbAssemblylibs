package com.xb.xblibrary.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by binbin1058 on 2018/4/27.
 */

public abstract class CommonAdapter <T> extends BaseAdapter {

    private Context context;
    private List<T> listData;
    private int layoutId;

    public CommonAdapter(Context context, List<T> listData, int layoutId) {
        super();
        this.context = context;
        this.listData = listData;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return this.listData == null ? 0 : this.listData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CommonViewHolder holder = CommonViewHolder.getViewHolder(this.context, convertView, parent, this.layoutId);
        this.fillData(holder, position);
        return holder.getMConvertView();
    }

    /**
     * 抽象方法，用于子类实现，填充数据
     *
     * @param holder
     * @param position
     */
    protected abstract void fillData(CommonViewHolder holder, int position);
}
