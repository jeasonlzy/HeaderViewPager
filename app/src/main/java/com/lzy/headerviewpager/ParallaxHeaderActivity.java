package com.lzy.headerviewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

public class ParallaxHeaderActivity extends AppCompatActivity {

    public List<HeaderViewPagerFragment> fragments;
    private HeaderViewPagerLayout scrollableLayout;
    private View image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax_header);

        fragments = new ArrayList<>();
        fragments.add(ScrollViewFragment.newInstance());
        fragments.add(ListViewFragment.newInstance());
        fragments.add(GridViewFragment.newInstance());
        fragments.add(RecyclerViewFragment.newInstance());
        fragments.add(WebViewFragment.newInstance());

        scrollableLayout = (HeaderViewPagerLayout) findViewById(R.id.scrollableLayout);
        image = findViewById(R.id.image);
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
                image.setTranslationY(currentY / 2);
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
}
