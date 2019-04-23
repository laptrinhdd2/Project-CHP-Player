package com.nxchien.chpmusic.ui.tabs.library;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nxchien.chpmusic.App;
import com.nxchien.chpmusic.R;
import com.nxchien.chpmusic.loader.ArtistLoader;
import com.nxchien.chpmusic.model.Artist;
import com.nxchien.chpmusic.model.Genre;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ArtistChildTab extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Nullable
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    ArtistAdapter mAdapter;
    Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this,view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArtistAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        if(mSwipeRefreshLayout!=null)
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        refresh();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mUnbinder!=null)
        mUnbinder.unbind();
    }
    private LoadArtistAsyncTask mLoadArtist;

    private void refresh() {

        if(mLoadArtist!=null) mLoadArtist.cancel(true);
        mLoadArtist= new LoadArtistAsyncTask(this);
        mLoadArtist.execute();

    }

    @Override
    public void onDestroy() {
        if(mLoadArtist!=null) mLoadArtist.cancel(true);
        super.onDestroy();
    }

    private static class AsyncResult {
        private List<Artist> mArtist;
    }
    private static class LoadArtistAsyncTask extends AsyncTask<Void, Void, AsyncResult> {
        private WeakReference<ArtistChildTab> mFragment;

        public LoadArtistAsyncTask(ArtistChildTab fragment) {
            super();
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected AsyncResult doInBackground(Void... voids) {
            AsyncResult result = new AsyncResult();
            Context context = null;

            if(App.getInstance()!=null)
            context = App.getInstance().getApplicationContext();

            if(context!=null)
            result.mArtist = ArtistLoader.getAllArtists(App.getInstance());
            else  return null;

            return result;
        }

        @Override
        protected void onPostExecute(AsyncResult asyncResult) {
            ArtistChildTab fragment = mFragment.get();
            if(fragment!=null&&!fragment.isDetached()) {
                if (fragment.mSwipeRefreshLayout != null)
                    fragment.mSwipeRefreshLayout.setRefreshing(false);
                if(!asyncResult.mArtist.isEmpty())
             //       fragment.mAdapter.setData(asyncResult.mArtist, asyncResult.mGenres);
                fragment.mAdapter.setData(asyncResult.mArtist);
                fragment.mLoadArtist = null;
            }
        }


    }
}
