package com.nxchien.chpmusic.ui.tabs.library;

import android.content.Context;
import android.content.res.ColorStateList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nxchien.chpmusic.glide.GlideApp;
import com.nxchien.chpmusic.glide.SongGlideRequest;
import com.nxchien.chpmusic.ui.AudioPreviewPlayer;
import com.nxchien.chpmusic.ui.MainActivity;
import com.nxchien.chpmusic.ui.popup.SortOrderBottomSheet;
import com.nxchien.chpmusic.ui.widget.CircularPlayPauseProgressBar;
import com.nxchien.chpmusic.util.Tool;
import com.nxchien.chpmusic.R;
import com.nxchien.chpmusic.ui.tabs.SongOptionBottomSheet;
import com.nxchien.chpmusic.model.Song;
import com.nxchien.chpmusic.service.MusicPlayer;
import com.nxchien.chpmusic.util.Utils;

import com.makeramen.roundedimageview.RoundedImageView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter,
        FastScrollRecyclerView.MeasurableAdapter, AudioPreviewPlayer.AudioPreviewerListener, SortOrderBottomSheet.SortOrderChangedListener {
    private static final String TAG = "SongAdapter";
    public ArrayList<Song> mData = new ArrayList<>();
    public int mCurrentHightLightPos = 0;
    private Context mContext;
    private long[] mSongIDs;
    private boolean isPlaylist;
    private long playlistId;


    public SongAdapter(Context context) {
        this.mContext = context;
    }

    public ArrayList<Song> getData() {
        return mData;
    }

    public void setData(List<Song> data) {
        initializeSong(data);
    }

    public int mSelected = 0;
    private Random mRandom = new Random();

    private void initializeSong(List<Song> data) {
        mData.clear();

        if (data != null) mData.addAll(data);
        this.mSongIDs = getSongIds();
        randommize();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return R.layout.item_sort_song_child;
        return R.layout.item_song_child;
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    private long[] getSongIds() {
        long[] ret = new long[mData.size()];
        for (int i = 0; i < mData.size(); i++) {
            ret[i] = mData.get(i).id;
        }

        return ret;
    }

    @Override
    public int getSavedOrder() {
        if (mSortOrderListener != null)
            return mSortOrderListener.getSavedOrder();
        return 0;
    }

    @Override
    public void onOrderChanged(int newType, String name) {
        if (mSortOrderListener != null) {
            mSortOrderListener.onOrderChanged(newType, name);
            notifyItemChanged(0);
        }
    }

    private SortOrderBottomSheet.SortOrderChangedListener mSortOrderListener;

    public void setSortOrderChangedListener(SortOrderBottomSheet.SortOrderChangedListener listener) {
        mSortOrderListener = listener;
    }

    public void removeOrderListener() {
        mSortOrderListener = null;
    }

    private void sortHolderClicked() {
        if (mContext instanceof AppCompatActivity) {
            SortOrderBottomSheet bs = SortOrderBottomSheet.newInstance(this);
            bs.show(((AppCompatActivity) mContext).getSupportFragmentManager(), TAG);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);

        if (viewType == R.layout.item_sort_song_child)
            return new SongAdapter.SortHolder(v);

        return new SongAdapter.ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder itemHolder, int position) {
        if (itemHolder instanceof ItemHolder)
            ((ItemHolder) itemHolder).bind(mData.get(position - 1));
        else ((SortHolder) itemHolder).bind();
    }

    private int getPositionInData(ItemHolder holder) {
        return holder.getAdapterPosition() - 1;
    }

    private void setOnPopupMenuListener(SongAdapter.ItemHolder itemHolder, final int position) {
        itemHolder.mMoreButton.setOnClickListener(v -> {
            SongOptionBottomSheet sheet = SongOptionBottomSheet.newInstance();
            sheet.show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                    "song_popup_menu");
            sheet.setListener(id -> {
                        switch (id) {
                            case R.id.popup_song_remove_playlist:
                                Utils.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                                removeSongAt(position);
                                notifyItemRemoved(position);
                                break;
                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext, mSongIDs, position, -1, Utils.IdType.NA, false);
                                break;
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = mData.get(position).id;
                                MusicPlayer.playNext(mContext, ids, -1, Utils.IdType.NA);
                                break;
                            case R.id.popup_song_goto_album:
                                break;
                            case R.id.popup_song_goto_artist:
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] _id = new long[1];
                                _id[0] = mData.get(position).id;
                                MusicPlayer.addToQueue(mContext, _id, -1, Utils.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:
                                break;
                        }
                        return true;
                    }
            );

            if (true) return;
            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popup_song_remove_playlist:
                            Utils.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                            removeSongAt(position);
                            notifyItemRemoved(position);
                            break;
                        case R.id.popup_song_play:
                            MusicPlayer.playAll(mContext, mSongIDs, position, -1, Utils.IdType.NA, false);
                            break;
                        case R.id.popup_song_play_next:
                            long[] ids = new long[1];
                            ids[0] = mData.get(position).id;
                            MusicPlayer.playNext(mContext, ids, -1, Utils.IdType.NA);
                            break;
                        case R.id.popup_song_goto_album:
                            break;
                        case R.id.popup_song_goto_artist:
                            break;
                        case R.id.popup_song_addto_queue:
                            long[] id = new long[1];
                            id[0] = mData.get(position).id;
                            MusicPlayer.addToQueue(mContext, id, -1, Utils.IdType.NA);
                            break;
                        case R.id.popup_song_addto_playlist:
                            break;
                    }
                    return false;
                }
            });
            menu.inflate(R.menu.popup_song);

            menu.show();
            if (isPlaylist)
                menu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
        });
    }

    public void randommize() {
        if (mData.isEmpty()) return;
        mSelected = mRandom.nextInt(mData.size());
        if (mCallBack != null) mCallBack.onFirstItemCreated(mData.get(mSelected));
    }

    public SongAdapter setCallBack(PreviewRandomPlayAdapter.FirstItemCallBack callBack) {
        mCallBack = callBack;
        return this;
    }

    public void removeCallBack() {
        mCallBack = null;
    }

    private PreviewRandomPlayAdapter.FirstItemCallBack mCallBack;

    public void shuffle() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            MusicPlayer.playAll(mContext, mSongIDs, mSelected, -1, Utils.IdType.NA, false);
            Handler handler1 = new Handler();
            handler1.postDelayed(() -> {
                notifyItemChanged(mCurrentHightLightPos);
                notifyItemChanged(mSelected);
                mCurrentHightLightPos = mSelected;
                randommize();
            }, 50);
        }, 100);
    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        if (position == 0) return "A";
        if (mData.get(position - 1).title.isEmpty())
            return "A";
        return mData.get(position - 1).title.substring(0, 1);
    }

    private int mPreviewItem = -1;

    private void playPreviewThisItem(ItemHolder itemHolder) {

        if (mPreviewItem != -1)
            notifyItemChanged(mPreviewItem, false);
        String path = mData.get(getPositionInData(itemHolder)).data;
        mPreviewItem = itemHolder.getAdapterPosition();
        ((MainActivity) mContext).getAudioPreviewPlayer().previewThisFile(SongAdapter.this, path);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (holder instanceof ItemHolder && !payloads.isEmpty() && payloads.get(0) instanceof Boolean) {
            ((ItemHolder) holder).resetProgress();
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    private long mNotifyDuration = 0;
    private long mNotifyTime = System.currentTimeMillis();

    @Override
    public void onPreviewStart(int totalTime) {
        if (mPreviewItem != -1) {
            mNotifyDuration = totalTime;
            mNotifyTime = System.currentTimeMillis();
            notifyItemChanged(mPreviewItem, totalTime);
        }
    }

    @Override
    public void onPreviewDestroy() {
        if (mPreviewItem != -1) {
            int temp = mPreviewItem;
            mPreviewItem = -1;
            notifyItemChanged(temp, false);
        }
    }

    public void forceStopPreview() {
        mPreviewItem = -1;
        ((MainActivity) mContext).getAudioPreviewPlayer().forceStop();
    }

    public void playAll() {
        if (!mData.isEmpty()) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayer.playAll(mContext, mSongIDs, 0, -1, Utils.IdType.NA, false);
                Handler handler1 = new Handler();
                handler1.postDelayed(() -> {
                    notifyItemChanged(mCurrentHightLightPos);
                    notifyItemChanged(0);
                    mCurrentHightLightPos = 0;
                }, 50);
            }, 100);
        }
    }

    public int getViewTypeHeight(RecyclerView recyclerView, @Nullable RecyclerView.ViewHolder viewHolder, int viewType) {
        if (viewType == R.layout.item_sort_song_child) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_sort_song_child_height);
        } else if (viewType == R.layout.item_song_child) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_song_child_height);
        }
        return 0;
    }

    public void notifyMetaChanged() {
        long newPlayingID = MusicPlayer.getCurrentAudioId();
        boolean isStillOldPos = false;
        if (-1 < mCurrentHightLightPos && mCurrentHightLightPos < mSongIDs.length) {
            isStillOldPos = mSongIDs[mCurrentHightLightPos] == newPlayingID;
            notifyItemChanged(mCurrentHightLightPos);
        }
        // find new pos
        if (!isStillOldPos) {
            int newPos = mData.indexOf(newPlayingID);
            mCurrentHightLightPos = newPos;
            if (newPos != -1) notifyItemChanged(newPos);
        }
    }


    public class SortHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sort_text)
        TextView mSortText;

        @OnClick(R.id.sort_parent)
        void sortClicked() {
            sortHolderClicked();
        }

        public SortHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind() {
            if (mSortOrderListener != null) {
                String str = mContext.getResources().getString(
                        SortOrderBottomSheet.mSortStringRes[mSortOrderListener.getSavedOrder()]);
                mSortText.setText(str);
            }
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnAttachStateChangeListener {

        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.artist)
        TextView mArtist;
        @BindView(R.id.image)
        ImageView mImage;
        @BindView(R.id.more)
        View mMoreButton;
        @BindView(R.id.quick_play_pause)
        ImageView mQuickPlayPause;
        @BindView(R.id.number)
        TextView mNumber;

        @BindView(R.id.loader)
        View mLoader;
        @BindView(R.id.present)
        View mPresentButton;

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            mLoader.addOnAttachStateChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayer.playAll(mContext, mSongIDs, getPositionInData(this), -1, Utils.IdType.NA, false);
                Handler handler1 = new Handler();
                handler1.postDelayed(() -> {
                    notifyItemChanged(mCurrentHightLightPos);
                    notifyItemChanged(getAdapterPosition());
                    mCurrentHightLightPos = getAdapterPosition();
                }, 50);
            }, 100);
        }

        public void resetProgress() {
            if (mPresentButton instanceof CircularPlayPauseProgressBar)
                ((CircularPlayPauseProgressBar) mPresentButton).resetProgress();

        }

        public void bind(Song song) {
            mNumber.setText("" + (getPositionInData(this) + 1));
            mTitle.setText(song.title);
            mArtist.setText(song.artistName);

            SongGlideRequest.Builder.from(GlideApp.with(mContext), song)
                    .ignoreMediaStore(false)
                    .generatePalette(mContext).build()
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            if (mImage instanceof RoundedImageView)
                                ((RoundedImageView) mImage).setBorderWidth(R.dimen.oneDP);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (mImage instanceof RoundedImageView)
                                ((RoundedImageView) mImage).setBorderWidth(0f);
                            return false;
                        }
                    })
                    .into(mImage);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((RippleDrawable) itemView.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
                ((RippleDrawable) mMoreButton.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
                ((RippleDrawable) mPresentButton.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
            }
            setOnPopupMenuListener(this, getPositionInData(this));
            highLight();
            bindPresent(song);
        }

        public void bindPresent(Song song) {
            if (mPresentButton instanceof CircularPlayPauseProgressBar) {
                if (getAdapterPosition() != mPreviewItem && ((CircularPlayPauseProgressBar) mPresentButton).getMode() == CircularPlayPauseProgressBar.PLAYING)
                    ((CircularPlayPauseProgressBar) mPresentButton).resetProgress();
                else if (getAdapterPosition() == mPreviewItem) {
                    long timePlayed = System.currentTimeMillis() - mNotifyTime;
                    if (timePlayed <= mNotifyDuration)
                        ((CircularPlayPauseProgressBar) mPresentButton).syncProgress((int) mNotifyDuration, (int) timePlayed);
                }
            }
        }

        private void highLight() {
            if (MusicPlayer.getCurrentAudioId() == mData.get(getPositionInData(this)).id) {
                mCurrentHightLightPos = getAdapterPosition();
                int baseColor = ArtistAdapter.lighter(Tool.getBaseColor(), 0.6f);
                mTitle.setTextColor(ArtistAdapter.lighter(Tool.getBaseColor(), 0.25f));
                mArtist.setTextColor(Color.argb(0xAA, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor)));
                mQuickPlayPause.setColorFilter(baseColor);

                if (MusicPlayer.isPlaying()) {
                    mQuickPlayPause.setImageResource(R.drawable.ic_volume_up_black_24dp);
                } else {
                    mQuickPlayPause.setImageResource(R.drawable.ic_volume_mute_black_24dp);
                }
            } else {
                mQuickPlayPause.setImageDrawable(null);
                int flatWhite = mContext.getResources().getColor(R.color.FlatWhite);
                mTitle.setTextColor(mContext.getResources().getColor(R.color.FlatWhite));
                mArtist.setTextColor(Color.argb(0xAA, Color.red(flatWhite), Color.green(flatWhite), Color.blue(flatWhite)));
            }
        }

        @OnClick(R.id.present)
        void clickPresent() {

            // set previewItem = -1 if the preview is end
            if (mPresentButton instanceof CircularPlayPauseProgressBar) {
                CircularPlayPauseProgressBar mProgressBar = (CircularPlayPauseProgressBar) mPresentButton;
                if (mPreviewItem == getAdapterPosition() && mProgressBar.getMode() == CircularPlayPauseProgressBar.RESET)
                    mPreviewItem = -1;
            }


            if (mPreviewItem != getAdapterPosition())
                playPreviewThisItem(this);
            else {
                resetProgress();
                forceStopPreview();
            }
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            mLoader.clearAnimation();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            mLoader.clearAnimation();
        }
    }

    public void removeSongAt(int i) {
        mData.remove(i);
    }
}
