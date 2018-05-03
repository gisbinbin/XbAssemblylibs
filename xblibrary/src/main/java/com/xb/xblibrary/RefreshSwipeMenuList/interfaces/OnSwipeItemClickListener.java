package com.xb.xblibrary.RefreshSwipeMenuList.interfaces;

import com.xb.xblibrary.RefreshSwipeMenuList.bean.SwipeMenu;
import com.xb.xblibrary.RefreshSwipeMenuList.view.SwipeMenuView;

public interface OnSwipeItemClickListener {
    void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
}