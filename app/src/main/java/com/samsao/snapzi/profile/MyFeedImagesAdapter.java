package com.samsao.snapzi.profile;

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

/**
 * @author jingsilu
 * @since 2015-05-08
 */
public class MyFeedImagesAdapter extends RecyclerView.Adapter<MyFeedImagesAdapter.MyFeedImagesViewHolder> {
    private Context mContext;
    private List<FeedImage> mFeedImagesList;

    public MyFeedImagesAdapter(Context context) {
        mContext = context;
        mFeedImagesList = null;
    }

    @Override
    public int getItemCount() {
        if (mFeedImagesList == null) {
            return 0;
        } else {
            return mFeedImagesList.size();
        }
    }

    @Override
    public void onBindViewHolder(MyFeedImagesViewHolder myFeedImagesViewHolder, int position) {
        FeedImage feedImage = mFeedImagesList.get(position);
        myFeedImagesViewHolder.setup(feedImage);
    }

    @Override
    public MyFeedImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_my_feed_item, parent, false);
        return new MyFeedImagesViewHolder(view);
    }

    public void setMyFeedImages(List<FeedImage> feedImagesList) {
        mFeedImagesList = feedImagesList;
        notifyDataSetChanged();
    }

    public class MyFeedImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public MyFeedImagesViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.fragment_profile_my_feed_img_view_id);
        }

        public void setup(final FeedImage feedImage) {
            // TODO add an error image and a placeholder
            Picasso.with(mContext).load(feedImage.getUrl()).into(mImageView);
        }
    }
}
