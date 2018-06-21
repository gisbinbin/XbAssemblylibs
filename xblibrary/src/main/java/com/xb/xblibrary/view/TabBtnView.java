package com.xb.xblibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xb.xblibrary.R;

/**
 * Created by Administrator on 2018/6/21.
 */

public class TabBtnView  extends LinearLayout {
    private Context mcontext;
    private TabBtnView tabBtnView;
    private OnTabBtnViewClickListener mlistener;
    private Boolean selected;
    private LinearLayout tabbtnview_layout;
    private FrameLayout tab_icon_layout;
    private ImageView iconview;
    private TextView tab_texttip;
    private TextView tab_pagetxt;
    private View tab_bottomline;
    private int iconresources;
    private int selectediconresources;
    private int textcolor;
    private int selectedtextcolor;

    public TabBtnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mcontext=context;
        LayoutInflater.from(context).inflate(R.layout.widget_tabbtnview, this, true);
        tabBtnView=this;
        tabbtnview_layout=(LinearLayout) findViewById(R.id.tabbtnview_layout);
        tabbtnview_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlistener!=null)
                    mlistener.onTabBtnViewClickListener(tabBtnView);
            }
        });
        tab_icon_layout=(FrameLayout) findViewById(R.id.tab_icon_layout);
        iconview=(ImageView) findViewById(R.id.tabbtnpageimg);
        tab_texttip=(TextView) findViewById(R.id.tab_texttip);
        tab_pagetxt=(TextView) findViewById(R.id.tab_pagetxt);
        tab_bottomline=(View)findViewById(R.id.tab_bottomline);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomTabBtnView);
        if (attributes != null) {
            selected=attributes.getBoolean(R.styleable.CustomTabBtnView_selected,false);
            Boolean iconvisibility=attributes.getBoolean(R.styleable.CustomTabBtnView_Tabiconvisibility,false);
            if(iconvisibility){
                tab_icon_layout.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tab_icon_layout.getLayoutParams();
                float penalviewheight = attributes.getDimension(R.styleable.CustomTabBtnView_Tabiconheight, -1);
                if(penalviewheight>-1){
                    lp.height=(int)penalviewheight;
                }
                float penalviewwidth = attributes.getDimension(R.styleable.CustomTabBtnView_Tabiconwidth, -1);
                if(penalviewwidth>-1){
                    lp.width=(int)penalviewwidth;
                }
                tab_icon_layout.setLayoutParams(lp);
                selectediconresources=attributes.getResourceId(R.styleable.CustomTabBtnView_selectedTabiconresources,-1);
                iconresources = attributes.getResourceId(R.styleable.CustomTabBtnView_Tabiconresources,-1);
            }
            else
                tab_icon_layout.setVisibility(View.GONE);

            String text = attributes.getString(R.styleable.CustomTabBtnView_text);
            if (!TextUtils.isEmpty(text)) {
                tab_pagetxt.setText(text);
            }
            selectedtextcolor=attributes.getResourceId(R.styleable.CustomTabBtnView_selectedtextcolor,-1);
            textcolor = attributes.getResourceId(R.styleable.CustomTabBtnView_textcolor,-1);
            float textsize = attributes.getDimension(R.styleable.CustomTabBtnView_textsize, -1);
            if(textsize>-1){
                tab_pagetxt.setTextSize(textsize);
            }
            Boolean bottomvisibility=attributes.getBoolean(R.styleable.CustomTabBtnView_bottomvisibility,false);
            if(bottomvisibility){
                tab_bottomline.setVisibility(View.VISIBLE);
                int resources = attributes.getResourceId(R.styleable.CustomTabBtnView_bottombackground,-1);
                if (resources>0) {
                    tab_bottomline.setBackgroundResource(resources);
                }
            }
            else
                tab_bottomline.setVisibility(View.GONE);
            setTabSelected(selected);
            attributes.recycle();
        }
    }

    public void setTabSelected(Boolean selected) {
        if(selected) {
            if(selectediconresources>0)
                iconview.setImageResource(selectediconresources);
            if (selectedtextcolor>0) {
                tab_pagetxt.setTextColor(ContextCompat.getColor(mcontext,selectedtextcolor));
            }
            if(tab_bottomline.getVisibility()!=GONE)
                tab_bottomline.setVisibility(VISIBLE);
        }
        else {
            if(iconresources>0)
                iconview.setImageResource(iconresources);
            if (textcolor>0) {
                tab_pagetxt.setTextColor(ContextCompat.getColor(mcontext,textcolor));
            }
            if(tab_bottomline.getVisibility()!=GONE)
                tab_bottomline.setVisibility(INVISIBLE);
        }
    }

    public void setTip(String txt){
        tab_texttip.setVisibility(VISIBLE);
        if(TextUtils.isEmpty(txt))
            tab_texttip.setText(txt);
    }

    public void setTipVisibility(Boolean isShow){
        if(isShow)
            tab_texttip.setVisibility(VISIBLE);
        else
            tab_texttip.setVisibility(GONE);
    }

    public FrameLayout getTab_icon_layout() {
        return tab_icon_layout;
    }

    public ImageView getIconview() {
        return iconview;
    }

    public TextView getTab_texttip() {
        return tab_texttip;
    }

    public TextView getTab_pagetxt() {
        return tab_pagetxt;
    }

    public View getTab_bottomline() {
        return tab_bottomline;
    }

    public void setOnTabBtnViewClickListener(OnTabBtnViewClickListener listener) {
        mlistener=listener;
    }

    public interface OnTabBtnViewClickListener{
        void onTabBtnViewClickListener(TabBtnView view);
    }
}
