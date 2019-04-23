package com.nxchien.chpmusic.ui.tabs.library;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nxchien.chpmusic.R;
import com.nxchien.chpmusic.ui.tabs.pager.PlaylistPagerFragment;
import com.nxchien.chpmusic.loader.PlaylistLoader;
import com.nxchien.chpmusic.model.Playlist;
import com.nxchien.chpmusic.ui.tabs.feature.FeaturePlaylistAdapter;
import com.nxchien.chpmusic.ui.widget.fragmentnavigationcontroller.SupportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistChildTab extends Fragment implements FeaturePlaylistAdapter.PlaylistClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    PlaylistChildAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mAdapter = new PlaylistChildAdapter(getActivity(),true);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        refreshData();
;    }
    private void refreshData() {
      mAdapter.setData(PlaylistLoader.getPlaylists(getActivity(),true));
    }

    @Override
    public void onClickPlaylist(Playlist playlist, Bitmap bitmap) {
        SupportFragment sf = PlaylistPagerFragment.newInstance(getContext(),playlist,bitmap);
        Fragment parentFragment = getParentFragment();
        if(parentFragment instanceof SupportFragment)
            ((SupportFragment)parentFragment).getNavigationController().presentFragment(sf);
    }
}
