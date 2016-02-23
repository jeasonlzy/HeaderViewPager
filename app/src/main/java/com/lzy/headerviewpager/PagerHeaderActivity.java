package com.lzy.headerviewpager;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.lzy.headerviewpager.fragment.GridViewFragment;
import com.lzy.headerviewpager.fragment.ListViewFragment;
import com.lzy.headerviewpager.fragment.RecyclerViewFragment;
import com.lzy.headerviewpager.fragment.ScrollViewFragment;
import com.lzy.headerviewpager.fragment.WebViewFragment;
import com.lzy.headerviewpager.fragment.base.HeaderViewPagerFragment;
import com.lzy.headerviewpager.ui.PagerSlidingTabStrip;
import com.lzy.ui.HeaderViewPagerLayout;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class PagerHeaderActivity extends AppCompatActivity {

    public List<HeaderViewPagerFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_header);

        fragments = new ArrayList<>();
        fragments.add(ScrollViewFragment.newInstance());
        fragments.add(ListViewFragment.newInstance());
        fragments.add(GridViewFragment.newInstance());
        fragments.add(RecyclerViewFragment.newInstance());
        fragments.add(WebViewFragment.newInstance());

        final HeaderViewPagerLayout scrollableLayout = (HeaderViewPagerLayout) findViewById(R.id.scrollableLayout);
        final PtrFrameLayout ptr = (PtrFrameLayout) findViewById(R.id.ptr);
        ptr.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return scrollableLayout.canPtr();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptr.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptr.refreshComplete();
                    }
                }, 2000);
            }
        });

        final ViewPager pagerHeader = (ViewPager) findViewById(R.id.pagerHeader);
        pagerHeader.setAdapter(new HeaderAdapter());
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabs.setViewPager(viewPager);
        scrollableLayout.setCurrentScrollableContainer(fragments.get(0));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                scrollableLayout.setCurrentScrollableContainer(fragments.get(position));
            }
        });
        scrollableLayout.setOnScrollListener(new HeaderViewPagerLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                pagerHeader.setTranslationY(currentY / 2);
            }
        });
        viewPager.setCurrentItem(0);
    }

    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ScrollView";
                case 1:
                    return "ListView";
                case 2:
                    return "GridView";
                case 3:
                    return "RecyclerView";
                case 4:
                    return "WebView";
                default:
                    return "";
            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private class HeaderAdapter extends PagerAdapter {

        public List<Integer> colors;

        public HeaderAdapter() {
            colors = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                colors.add(ColorUtil.generateBeautifulColor());
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView textView = (TextView) View.inflate(getApplicationContext(), android.R.layout.simple_list_item_1, null);
            textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setBackgroundColor(colors.get(position));
            textView.setText("第" + position + "页");
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            container.addView(textView);
            return textView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return colors.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
