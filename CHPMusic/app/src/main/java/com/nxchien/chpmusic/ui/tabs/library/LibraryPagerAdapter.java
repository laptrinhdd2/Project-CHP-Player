package com.nxchien.chpmusic.ui.tabs.library;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nxchien.chpmusic.R;

import java.util.ArrayList;

public class LibraryPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> mData = new ArrayList<>();
    private void initData() {
        mData.add(new SongChildTab());
        mData.add(new PlaylistChildTab());
        mData.add(new ArtistChildTab());
    }

    public LibraryPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        initData();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return mContext.getResources().getString(R.string.songs);
            case 1: return mContext.getResources().getString(R.string.playlists);
            case 2: return mContext.getResources().getString(R.string.artists);
            default: return null;
        }

    }
}
