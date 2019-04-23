package com.nxchien.chpmusic.ui.tabs;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nxchien.chpmusic.ui.tabs.feature.FeatureTabFragment;
import com.nxchien.chpmusic.ui.tabs.library.LibraryTabFragment;
import com.nxchien.chpmusic.ui.widget.navigate.NavigateFragment;

import java.util.ArrayList;

public class BottomNavigationPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public BottomNavigationPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        initData();
    }

    public ArrayList<NavigateFragment> mData = new ArrayList<>();

    public boolean onBackPressed(int position) {
        if(position<mData.size())
        return mData.get(position).onBackPressed();
        return false;
    }


    private void initData() {
        mData.add(NavigateFragment.newInstance(new FeatureTabFragment()));
        mData.add(NavigateFragment.newInstance(new LibraryTabFragment()));
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Fragment getItem(int position) {
        if(position>=mData.size()) return null;
        return mData.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
