package com.xb.xblibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xb.xblibrary.R;

/**
 * Created by Administrator on 2018/6/21.
 */

public class RowView extends LinearLayout {
    private RowView rowView;
    private OnRowViewClickListener mlistener;
    private LinearLayout row_layout;
    private ImageView iconview;
    private ImageView jumpmark;
    private ImageView emphasesmark;
    private TextView keytextview;
    private TextView valueview;
    private View splitters;

    public RowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_rowview, this, true);
        rowView=this;
        row_layout=(LinearLayout) findViewById(R.id.rowview_layout);
        row_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlistener!=null)
                    mlistener.onOnRowViewClickListener(rowView);
            }
        });
        iconview=(ImageView) findViewById(R.id.rowview_iconimage);
        jumpmark=(ImageView) findViewById(R.id.rowview_jumpmark);
        emphasesmark=(ImageView)findViewById(R.id.rowview_emphasesmarkview);
        keytextview = (TextView) findViewById(R.id.rowkey_view);
        valueview = (TextView) findViewById(R.id.rowvalue_view);
        splitters=(View)findViewById(R.id.rowview_splitters);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomRowView);
        if (attributes != null) {
            LinearLayout.LayoutParams lp = (LayoutParams) row_layout.getLayoutParams();
            float penalviewheight = attributes.getDimension(R.styleable.CustomRowView_rowheight, -1);
            if(penalviewheight>-1){
                lp.height=(int)penalviewheight;
                row_layout.setLayoutParams(lp);
            }
            String keystr = attributes.getString(R.styleable.CustomRowView_keyvaluekey);
            if (!TextUtils.isEmpty(keystr)) {
                keytextview.setText(keystr);
            }
            Boolean valuevisibility=attributes.getBoolean(R.styleable.CustomRowView_valuevisibility,false);
            if(valuevisibility) {
                valueview.setVisibility(View.VISIBLE);
                String valuehintstr = attributes.getString(R.styleable.CustomRowView_keyvaluevalue);
                if (!TextUtils.isEmpty(valuehintstr)) {
                    valueview.setText(valuehintstr);
                }
                Boolean emphasesmarkvisibility=attributes.getBoolean(R.styleable.CustomRowView_emphasesmarkvisibility,false);
                if(emphasesmarkvisibility)
                    emphasesmark.setVisibility(View.VISIBLE);
            }
            Boolean iconvisibility=attributes.getBoolean(R.styleable.CustomRowView_iconvisibility,false);
            if(iconvisibility) {
                iconview.setVisibility(View.VISIBLE);
                int resources = attributes.getResourceId(R.styleable.CustomRowView_iconresources,-1);
                if (resources>0) {
                    iconview.setImageResource(resources);
                }
            }
            Boolean jumpmarkvisibility=attributes.getBoolean(R.styleable.CustomRowView_jumpmarkvisibility,false);
            if(jumpmarkvisibility) {
                jumpmark.setVisibility(View.VISIBLE);
                int resources = attributes.getResourceId(R.styleable.CustomRowView_jumpmarresources,-1);
                if (resources>0) {
                    jumpmark.setImageResource(resources);
                }
            }
            Boolean splittersvisibility=attributes.getBoolean(R.styleable.CustomRowView_splittersvisibility,false);
            if(splittersvisibility) {
                splitters.setVisibility(View.VISIBLE);
            }
            attributes.recycle();
        }
    }

    public ImageView getIconView() {
        return iconview;
    }

    public ImageView getJumpmarkView() {
        return jumpmark;
    }

    public ImageView getEmphasesmarkView() {
        return emphasesmark;
    }

    public TextView getKeyTextView() {
        return keytextview;
    }

    public TextView getValueTextView() {
        return valueview;
    }

    public String getValue(){
        return valueview.getText().toString();
    }

    public void setValue(String value){
        valueview.setText(value);
    }

    public void setOnRowViewClickListener(OnRowViewClickListener listener) {
        mlistener=listener;
    }

    public interface OnRowViewClickListener{
        void onOnRowViewClickListener(RowView view);
    }
}
