package com.nxchien.chpmusic.ui.tabs.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nxchien.chpmusic.R;
import com.nxchien.chpmusic.glide.ArtistGlideRequest;
import com.nxchien.chpmusic.glide.GlideApp;
import com.nxchien.chpmusic.loader.GenreLoader;
import com.nxchien.chpmusic.model.Artist;
import com.nxchien.chpmusic.model.Genre;
import com.nxchien.chpmusic.util.PhonographColorUtil;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder> implements FastScrollRecyclerView.SectionedAdapter {
    private static final String TAG = "ArtistAdapter";

    Context mContext;
    ArrayList<Artist> mData = new ArrayList<>();
    ArrayList<Genre>[] mGenres;

    ArtistAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<Artist> data) {
        mData.clear();
        if(data!=null) {
            mData.addAll(data);
            mGenres = new ArrayList[data.size()];
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_child,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        if(mGenres!=null&&mGenres.length>i-1)
        itemHolder.bind(mData.get(i), mGenres[i]);
        else itemHolder.bind(mData.get(i),null);
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }
    public static int lighter(int color, float factor, int alpha) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @NonNull
    @Override
    public String getSectionName(int i) {
        if(mData.get(i).getName().isEmpty())
            return "";
        return mData.get(i).getName().substring(0,1);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mImage.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, view.getWidth(), view.getHeight());
                    }
                });
                mImage.setClipToOutline(true);
            }
        }
        @BindView(R.id.artist)
        TextView mArtist;

        @BindView(R.id.image)
        ImageView mImage;

        @BindView(R.id.genre_one)
        TextView mGenreOne;

        @BindView(R.id.genre_two)
        TextView mGenreTwo;

        @BindView(R.id.panel)
        View mPanel;
        @BindView(R.id.panel_color)
        View mPanelColor;

        @OnClick(R.id.panel)
        void goToThisArtist() {

        }
        @BindView(R.id.root)
        View mRoot;

        @BindView(R.id.count)
        TextView mCount;

        public void bind(Artist artist, ArrayList<Genre> genres) {
            mArtist.setText(artist.getName());
            mCount.setText(String.format("%d %s", artist.songCount, mCount.getContext().getResources().getString(R.string.songs)));
            if(genres==null) {
                mGenreOne.setText("⋯");
                mGenreTwo.setVisibility(View.GONE);
                Log.d(TAG, "load genre item "+getAdapterPosition() );
                new GenreOfArtistTask(ArtistAdapter.this,artist,getAdapterPosition()).execute();
            } else bindGenre(genres);
                loadArtistImage(artist);
        }
        private void loadArtistImage(Artist artist) {
            ArtistGlideRequest.Builder.from(GlideApp.with(mContext), artist)
                    .generatePalette(mContext)
                    .build()
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            int color;
                            if(resource!=null)
                            color  = PhonographColorUtil.getColor(PhonographColorUtil.generatePalette(resource), mContext.getResources().getColor(R.color.FlatBlue));
                            else color =  mContext.getResources().getColor(R.color.FlatBlue);
                            int fixedColor = lighter(color,0.55f,0x90);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                mPanelColor.getBackground().setTint(fixedColor);
                            else mPanelColor.getBackground().setColorFilter(fixedColor, PorterDuff.Mode.SRC_ATOP);
                            return false;
                        }
                    })
                    .into(mImage);
        }

        public void bindGenre(ArrayList<Genre> genres) {
           if(genres.isEmpty()) {
                mGenreOne.setText("Unknown Genres");
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.GONE);
            } else if(genres.size()==1) {
                mGenreOne.setText(genres.get(0).name);
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.GONE);
            } else {
                mGenreOne.setText(genres.get(0).name);
                mGenreTwo.setText(genres.get(0).name);
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.VISIBLE);
            }
        }
    }
    private static class GenreOfArtistTask extends AsyncTask<Void,Void,ArrayList<Genre>> {
        private WeakReference<ArtistAdapter> mAAReference;
        private int mItemPos;
        private Artist mArtist;
        public GenreOfArtistTask(ArtistAdapter adapter, Artist artist, int itemPos) {
            super();
            mAAReference = new WeakReference<>(adapter);
            mArtist = artist;
            mItemPos = itemPos;
        }

        @Override
        protected ArrayList<Genre> doInBackground(Void... voids) {

            if(mAAReference.get()!=null&&mArtist!=null) {
                return GenreLoader.getGenreForArtist(mAAReference.get().mContext, mArtist.id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Genre> genres) {
            if(genres!=null&&mAAReference.get()!=null) {
             mAAReference.get().attachGenreByPosition(genres, mArtist, mItemPos);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull List<Object> payloads) {
    if(!payloads.isEmpty()) {
            if((payloads.get(0)).equals(GENRE_UPDATE)&&position<mGenres.length)
                holder.bindGenre(mGenres[position]);
    } else
        super.onBindViewHolder(holder, position, payloads);
    }
    private static final String GENRE_UPDATE = "genre_update";

    private void attachGenreByPosition(ArrayList<Genre> genres, Artist artist, int itemPos) {
        if(itemPos>=0 && itemPos<mData.size()) {
            if (artist.equals(mData.get(itemPos))&&mGenres[itemPos]==null)  {
                mGenres[itemPos] = genres;
               notifyItemChanged(itemPos,GENRE_UPDATE);
            }
        }
    }
}
