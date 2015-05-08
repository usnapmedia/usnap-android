package com.samsao.snapzi.live_feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.FeedImage;
import com.samsao.snapzi.fan_page.PhotoDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * @author jingsilu
 * @since 2015-04-17
 */
public class LiveFeedAdapter extends RecyclerView.Adapter<LiveFeedAdapter.LiveFeedViewHolder> {

    private Context mContext;
    private List<FeedImage> mImageLiveFeedList;

    public LiveFeedAdapter(Context context) {
        mContext = context;
        mImageLiveFeedList = null;
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
        liveFeedViewHolder.setup(imgLiveFeed);
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


    public class LiveFeedViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgIcon;

        public LiveFeedViewHolder(View view) {
            super(view);
            mImgIcon = (ImageView) view;

        }

        public void setup(final FeedImage image) {
            // TODO add an error image and a placeholder
            Picasso.with(mContext).load(image.getUrl()).into(mImgIcon);
            mImgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoDetailsActivity.start(image, mContext);
                }
            });
        }
    }
}

