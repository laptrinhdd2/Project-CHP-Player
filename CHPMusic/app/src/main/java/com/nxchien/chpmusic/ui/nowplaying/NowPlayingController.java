package com.nxchien.chpmusic.ui.nowplaying;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.motion.MotionLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nxchien.chpmusic.R;
import com.nxchien.chpmusic.ui.tabs.BaseLayerFragment;
import com.nxchien.chpmusic.loader.QueueLoader;
import com.nxchien.chpmusic.model.Song;
import com.nxchien.chpmusic.service.MusicPlayer;
import com.nxchien.chpmusic.ui.BaseActivity;
import com.nxchien.chpmusic.ui.LayerController;
import com.nxchien.chpmusic.service.MusicStateListener;
import com.nxchien.chpmusic.ui.MainActivity;
import com.nxchien.chpmusic.ui.tabs.SongOptionBottomSheet;
import com.nxchien.chpmusic.ui.widget.AudioVisualSeekBar;
import com.nxchien.chpmusic.util.BitmapEditor;
import com.nxchien.chpmusic.util.Tool;
import com.nxchien.chpmusic.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nxchien.chpmusic.util.BitmapEditor.updateSat;

public class NowPlayingController extends BaseLayerFragment implements MusicStateListener, AudioVisualSeekBar.OnSeekBarChangeListener, ColorPickerAdapter.OnColorChangedListener {
    private static final String TAG ="NowPlayingController";
    @BindView(R.id.root) CardView mRoot;
    @BindView(R.id.dim_view) View mDimView;
    private float mMaxRadius= 18;

    @BindView(R.id.minimize_bar) View mMinimizeBar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.visual_seek_bar)
    AudioVisualSeekBar mVisualSeekBar;
    @BindView(R.id.time_text_view) TextView mTimeTextView;
    @BindView(R.id.big_title) TextView mBigTitle;
    @BindView(R.id.big_artist) TextView mBigArtist;
    private NowPlayingAdapter mAdapter;
    @BindView(R.id.color_picker_recycler_view) RecyclerView mColorPickerRecyclerView;
    private ColorPickerAdapter mColorPickerAdapter;

    @OnClick(R.id.more)
    void more() {
        SongOptionBottomSheet sheet =  SongOptionBottomSheet.newInstance();
        sheet.show((getActivity()).getSupportFragmentManager(),
                "song_popup_menu");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.now_playing_controller,container,false);
    }

    SnapHelper snapHelper = new PagerSnapHelper();
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mMaxRadius = getResources().getDimension(R.dimen.max_radius_layer);
        mTitle.setSelected(true);

        mAdapter = new NowPlayingAdapter(getActivity());
      //  mRecyclerView.setPageTransformer(false, new SliderTransformer());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        snapHelper.attachToRecyclerView(mRecyclerView);

        mColorPickerAdapter = new ColorPickerAdapter(this);
        mColorPickerRecyclerView.setAdapter(mColorPickerAdapter);
        mColorPickerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        mRecyclerView.setOnTouchListener((v, event) -> mLayerController.streamOnTouchEvent(mRoot,event));
        mVisualSeekBar.setOnTouchListener((v, event) -> {
            return mLayerController.streamOnTouchEvent(mRoot, event) &&  event.getAction()!=MotionEvent.ACTION_DOWN;
        });

        mVisualSeekBar.setOnSeekBarChangeListener(this);
       if(getActivity() instanceof BaseActivity) ((MainActivity)getActivity()).addMusicStateListener(this,true);
       onMetaChanged();
    }

    @Override
    public void onDestroyView() {
        if(mLoadQueueTask!=null) mLoadQueueTask.cancel();
        if(getActivity() instanceof BaseActivity) ((MainActivity)getActivity()).removeMusicStateListener(this);

        super.onDestroyView();
    }

    private void setRadius(float value) {
        if(mRoot!=null) {
            float valueTemp;
            if(value>1) valueTemp=1;
            else if(value<=0.1f) valueTemp = 0;
            else valueTemp = value;
            mRoot.setRadius(mMaxRadius * valueTemp);
        }
    }

    @Override
    public void onUpdateLayer( ArrayList<LayerController.Attr> attrs, ArrayList<Integer> actives, int me) {

        if(mRoot==null) return;
        if(me ==1) {
            mDimView.setAlpha(0.3f*(attrs.get(actives.get(0)).getRuntimePercent()));
            setRadius( attrs.get(actives.get(0)).getRuntimePercent());
        } else
        {
            float min = 0.3f, max =0.65f;
            float hieu = max - min;
            float heSo_sau = (me-1.0f)/(me-0.75f); // 1/2, 2/3,3/4, 4/5, 5/6 ...
            float heSo_truoc =  (me-2.0f)/(me-0.75f); // 0/1, 1/2, 2/3, ...
            float darken = min + hieu*heSo_truoc + hieu*(heSo_sau - heSo_truoc)*attrs.get(actives.get(0)).getRuntimePercent();
            mDimView.setAlpha(darken);
            setRadius(1);
        }
    }

    @Override
    public void onTranslateChanged(LayerController.Attr attr) {
        if(getMaxPositionType())
        setRadius(0);
        else setRadius(attr.getRuntimePercent());

        mConstraintRoot.setProgress(attr.getRuntimePercent());

        if(mConstraintRoot.getProgress()!=0&&!mTimeTextIsSync) mTimeTextView.setText(timeTextViewTemp);
        if(mConstraintRoot.getProgress()==0||mConstraintRoot.getProgress()==1)
            try {
                mRecyclerView.scrollToPosition(MusicPlayer.getQueuePosition());
            } catch (Exception ignore) {}

    }

    @Override
    public int minPosition(Context context,int h) {
        return (int) (context.getResources().getDimension(R.dimen.bottom_navigation_height)+ context.getResources().getDimension(R.dimen.now_laying_height_in_minimize_mode));
    }

    @Override
    public String tag() {
        return TAG;
    }

    @Override
    public void restartLoader() {

    }
    @OnClick({R.id.play_pause_button,R.id.button_right})
    void playOrPause() {
        Handler handler = new Handler();
        handler.postDelayed(MusicPlayer::playOrPause,100);
    }
    @OnClick(R.id.prev_button)
    void goToPrevSong() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {MusicPlayer.previous(getActivity(),true);},100);
    }
    @OnClick(R.id.next_button)
    void goToNextSong() {
        Handler handler = new Handler();
        handler.postDelayed(MusicPlayer::next,100);
    }
    void updatePlayPauseState(){
        if(MusicPlayer.isPlaying()) {
            mButtonRight.setImageResource(R.drawable.ic_pause_black_24dp);
            mPlayPauseButton.setImageResource(R.drawable.pause_round);
        } else {
            mButtonRight.setImageResource(R.drawable.ic_play_white_36dp);
            mPlayPauseButton.setImageResource(R.drawable.play_round);
        }
    }

    @Override
    public void onPlaylistChanged() {

    }
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.playlist_title) TextView mPlaylistTitle;
    @BindView(R.id.button_right)
    ImageView mButtonRight;
    @BindView(R.id.prev_button) ImageView mPrevButton;
    @BindView(R.id.next_button) ImageView mNextButton;

    @BindView(R.id.play_pause_button)
    ImageView mPlayPauseButton;
    LoadQueueTask mLoadQueueTask;

    @OnClick(R.id.playlist_title)
    void popUpPlayingList() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            ((MainActivity)getActivity()).popUpPlaylistTab();
        }
    }
    @Override
    public void onMetaChanged() {
        mTitle.setText(String.format("%s %s %s", MusicPlayer.getTrackName(), getString(R.string.middle_dot), MusicPlayer.getArtistName()));
        mBigTitle.setText(MusicPlayer.getTrackName());
        mBigArtist.setText(MusicPlayer.getArtistName());

      if(mLoadQueueTask!=null) mLoadQueueTask.cancel();
      mLoadQueueTask = new LoadQueueTask(this);
      mLoadQueueTask.execute();
    }
    private void onQueueReady(List<Song> songs2) {
        long start = System.currentTimeMillis();

        if(!songs2.isEmpty()) {
            mAdapter.setData(songs2);
            mRecyclerView.smoothScrollToPosition(MusicPlayer.getQueuePosition());

            if(getActivity() instanceof MainActivity)
                ((MainActivity)getActivity()).setDataForPlayingQueue(songs2);
        }
        String path = MusicPlayer.getPath();
        long duration = MusicPlayer.duration();
        if(duration>0&&path!=null&&!path.isEmpty()&&!mVisualSeekBar.getCurrentFileName().equals(path))
            mVisualSeekBar.visualize(path,duration, (int) MusicPlayer.position());
        mVisualSeekBar.postDelayed(mUpdateProgress,10);
        updatePlayPauseState();
    }
    private void onColorPaletteReady(int color1, int color2, float alpha1, float alpha2) {
        mPlayPauseButton.setColorFilter(Tool.getBaseColor());
        mPrevButton.setColorFilter(color2);
        mNextButton.setColorFilter(color2);

        mBigTitle.setTextColor(Tool.lighter(color1,0.5f));
        mBigArtist.setTextColor(color2);
        mVisualSeekBar.updateProperties();
        if(getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).setPlaylistColorPalette(color1,color2,alpha1,alpha2);
        }
    }

    @BindView(R.id.constraint_root)
    MotionLayout mConstraintRoot;

    @Override
    public boolean onGestureDetected(int gesture) {
        if(gesture==LayerController.SINGLE_TAP_UP) {
            LayerController.Attr a = mLayerController.getMyAttr(this);
            if(a!=null) {
                if(a.getState()== LayerController.Attr.MINIMIZED)
                    a.animateToMax();
                else
                    a.animateToMin();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getMaxPositionType() {
        return true;
    }

    @Override
    public void onColorChanged(int position, int newColor) {
        mBigTitle.setTextColor(newColor);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        fragmentPaused = false;
        if(mVisualSeekBar!=null) {
            mVisualSeekBar.postDelayed(mUpdateProgress,10);
        }
    }

    private boolean isTouchedVisualSeekbar = false;
    private int overflowcounter=0;
    boolean fragmentPaused = false;

    public Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
            long position = MusicPlayer.position();

            if(!isTouchedVisualSeekbar)
                setTextTime(position,MusicPlayer.duration());

            if(mVisualSeekBar!=null) {
                mVisualSeekBar.setProgress((int) position);
                //TODO: Set elapsedTime
            }
            overflowcounter--;
            if(MusicPlayer.isPlaying()) {
                //TODO: ???
                int delay = (int) (150 -(position)%100);
                if(overflowcounter<0 && !fragmentPaused) {
                    overflowcounter ++;
                    mVisualSeekBar.postDelayed(mUpdateProgress,delay);
                }
            }
        }
    };
    @Override
    public void onSeekBarSeekTo(AudioVisualSeekBar seekBar, int i, boolean b) {
        if(b) MusicPlayer.seek(i);
    }

    @Override
    public void onSeekBarTouchDown(AudioVisualSeekBar seekBar) {
        isTouchedVisualSeekbar = true;
    }

    @Override
    public void onSeekBarTouchUp(AudioVisualSeekBar seekBar) {
        isTouchedVisualSeekbar = false;
    }

    @Override
    public void onSeekBarSeeking(int seekingValue) {
        setTextTime(seekingValue,MusicPlayer.duration());
    }
    private void setTextTime(long pos, long duration) {
        int minute = (int) (pos/1000/60);
        int second = (int) (pos/1000-  minute*60);
        int dur_minute = (int) (duration/1000/60);
        int dur_second = (int) (duration/1000 - dur_minute*60);

        String text ="";
        if(minute<10) text+="0";
        text+=minute+":";
        if(second<10) text+="0";
        text+= second+" | ";
        if(dur_minute<10) text+="0";
        text+= dur_minute+":";
        if(dur_second<10) text+="0";
        text+=dur_second;
        if(mConstraintRoot.getProgress()!=0) {
            mTimeTextView.setText(text);
            mTimeTextIsSync = true;
        }
        else {
            mTimeTextIsSync = false;
            timeTextViewTemp = text;
        }
    }

    private boolean mTimeTextIsSync = false;

    private String timeTextViewTemp = "00:00";

    private static class LoadQueueTask extends AsyncTask<Void,Void,Boolean> {
        private NowPlayingController mFragment;
        LoadQueueTask(NowPlayingController fragment) {
            mFragment = fragment;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean c;

            try {
                c = getQueue();
            } catch (Exception ignore) {
                c = false;
            }

            return c;
        }
        private boolean getQueue() {
            List<Song> songs2;
            if(mFragment!=null)
                songs2 =  QueueLoader.getQueueSongs(mFragment.getContext());
            else return false;
            if(songs2==null||songs2.isEmpty()) return false;
            if(mFragment!=null&&mFragment.getActivity()!=null)
                mFragment.getActivity().runOnUiThread(() -> {
                    if(mFragment!=null)
                        mFragment.onQueueReady(songs2);
                });
            else return false;
            return true;
        }

        public void cancel() {
            cancel(true);
            if(mFragment!=null) {
                mFragment.mLoadQueueTask = null;
                mFragment = null;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }

    }
}
