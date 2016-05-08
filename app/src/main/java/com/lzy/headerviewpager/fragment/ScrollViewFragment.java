package com.lzy.headerviewpager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lzy.headerviewpager.Utils;
import com.lzy.headerviewpager.R;
import com.lzy.headerviewpager.fragment.base.HeaderViewPagerFragment;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/2/21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ScrollViewFragment extends HeaderViewPagerFragment {

    private View scrollView;

    public static ScrollViewFragment newInstance() {
        return new ScrollViewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scrollView = inflater.inflate(R.layout.fragment_scrollview, container, false);
        LinearLayout views = (LinearLayout) scrollView.findViewById(R.id.container);
        for (int i = 0; i < 10; i++) {
            View view = new View(getActivity());
            view.setBackgroundColor(Utils.generateBeautifulColor());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
            view.setLayoutParams(params);
            views.addView(view);
        }
        return scrollView;
    }

    @Override
    public View getScrollableView() {
        return scrollView;
    }
}
