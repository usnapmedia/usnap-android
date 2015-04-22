package com.samsao.snapzi.live_feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.FeedImage;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;


/**
 * @author jingsilu
 * @since 2015-04-17
 */
public class LiveFeedAdapter extends RecyclerView.Adapter<LiveFeedAdapter.LiveFeedViewHolder> {

    private List<FeedImage> mImageLiveFeedList;
    private Listener mListener;

    public LiveFeedAdapter(Listener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mImageLiveFeedList == null) {
            return 0;
        } else {
            return mImageLiveFeedList.size();
        }
    }

    @Override
    public void onBindViewHolder(LiveFeedViewHolder liveFeedViewHolder, int position) {
        FeedImage imgLiveFeed = mImageLiveFeedList.get(position);


        if (position < (getItemCount() - 1)) {
            liveFeedViewHolder.itemView.setPadding(0,0,10,0);
        } else {
            liveFeedViewHolder.itemView.setPadding(0,0,0,0);
        }

        Context context = liveFeedViewHolder.mImgIcon.getContext();
        // TODO add an error image and a placeholder
        Picasso.with(context).load(imgLiveFeed.getUrl()).into(liveFeedViewHolder.mImgIcon);
    }

    @Override
    public LiveFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mLiveFeedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_livefeed, parent, false);
        return new LiveFeedViewHolder(mLiveFeedView);
    }

    public void setImageLiveFeed(List<FeedImage> list) {
        mImageLiveFeedList = list;
        notifyDataSetChanged();
    }


    public class LiveFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mImgIcon;

        public LiveFeedViewHolder(View v) {
            super(v);
            mImgIcon = (ImageView) v.findViewById(R.id.img_view_id);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(view, getAdapterPosition());
            } else {
                Timber.e("Fragment is destroyed!");
            }
        }
    }

    public interface Listener {
        public void onItemClick(View view, int position);
    }
}

