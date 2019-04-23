package com.nxchien.chpmusic.ui.tabs.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nxchien.chpmusic.App;
import com.nxchien.chpmusic.R;
import com.nxchien.chpmusic.loader.SongLoader;
import com.nxchien.chpmusic.model.Song;
import com.nxchien.chpmusic.service.MusicStateListener;
import com.nxchien.chpmusic.ui.BaseActivity;
import com.nxchien.chpmusic.ui.popup.SortOrderBottomSheet;
import com.nxchien.chpmusic.util.Tool;
import com.nxchien.chpmusic.util.Utils;
import com.nxchien.chpmusic.util.Animation;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongChildTab extends Fragment implements SortOrderBottomSheet.SortOrderChangedListener, PreviewRandomPlayAdapter.FirstItemCallBack, MusicStateListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.refresh)
    ImageView mRefresh;

    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.artist)
    TextView mArtist;

    private int mCurrentSortOrder = 0;
    private void initSortOrder() {
         mCurrentSortOrder = App.getInstance().getPreferencesUtility().getSongChildSortOrder();
    }

    @OnClick({R.id.preview_random_panel})
     void shuffle() {
        mAdapter.shuffle();
    }

    SongAdapter mAdapter;

    @OnClick(R.id.refresh)
    void refresh() {
        mRefresh.animate().rotationBy(360).setInterpolator(Animation.getInterpolator(6)).setDuration(650);
        mRefresh.postDelayed(mAdapter::randommize,300);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.song_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initSortOrder();

        mAdapter = new SongAdapter(getActivity());
        mAdapter.setCallBack(this);
        mAdapter.setSortOrderChangedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);

        refreshData();
        if(getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).addMusicStateListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        if(getActivity() instanceof BaseActivity)
            ((BaseActivity)getActivity()).removeMusicStateListener(this);
        mAdapter.removeCallBack();
        mAdapter.removeOrderListener();
        super.onDestroyView();
    }

    private void refreshData() {
        ArrayList<Song> songs = SongLoader.getAllSongs(getActivity(),SortOrderBottomSheet.mSortOrderCodes[mCurrentSortOrder]);
        mAdapter.setData(songs);
        showOrHidePreview(!songs.isEmpty());

    }
    private void showOrHidePreview(boolean show) {
        int v = show ? View.VISIBLE : View.GONE;

            mImage.setVisibility(v);
            mRefresh.setVisibility(v);
            mTitle.setVisibility(v);
            mArtist.setVisibility(v);
    }

    @Override
    public void onFirstItemCreated(Song song) {
        mTitle.setText(song.title);
        mArtist.setText(song.artistName);

        Picasso.get()
                .load(Utils.getAlbumArtUri(song.albumId))
                .placeholder(R.drawable.music_empty)
                .error(R.drawable.music_empty)
                .into(mImage);

    }

    @Override
    public void onResume() {
        super.onResume();
       Activity a = getActivity();
       if(a instanceof BaseActivity)
           ((BaseActivity)a).addMusicStateListener(this);
    }

    @Override
    public void onPause() {
        Activity a = getActivity();
        if(a instanceof BaseActivity)
            ((BaseActivity)a).removeMusicStateListener(this);
        super.onPause();
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        if(mRecyclerView instanceof FastScrollRecyclerView) {
            FastScrollRecyclerView recyclerView = ((FastScrollRecyclerView)mRecyclerView);
            recyclerView.setPopupBgColor(Tool.getHeavyColor());
            recyclerView.setThumbColor(Tool.getHeavyColor());
        }

        if(mAdapter!=null)mAdapter.notifyMetaChanged();
    }

    @Override
    public int getSavedOrder() {
        return mCurrentSortOrder;
    }

    @Override
    public void onOrderChanged(int newType, String name) {
        if(mCurrentSortOrder!=newType) {
            mCurrentSortOrder = newType;
            App.getInstance().getPreferencesUtility().setSongChildSortOrder(mCurrentSortOrder);
            refreshData();
        }
    }
}
