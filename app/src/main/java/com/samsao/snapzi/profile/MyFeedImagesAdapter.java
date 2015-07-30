package com.samsao.snapzi.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.BuildConfig;
import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.Snap;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author jingsilu
 * @since 2015-05-08
 */
public class MyFeedImagesAdapter extends RecyclerView.Adapter<MyFeedImagesAdapter.MyFeedImagesViewHolder> {
    private Context mContext;
    private List<Snap> mFeedImagesList;

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
        Snap snap = mFeedImagesList.get(position);
        myFeedImagesViewHolder.setup(snap);
    }

    @Override
    public MyFeedImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_my_feed_item, parent, false);
        return new MyFeedImagesViewHolder(view);
    }

    public void setMyFeedImages(List<Snap> feedImagesList) {
        mFeedImagesList = feedImagesList;
        notifyDataSetChanged();
    }

    public class MyFeedImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public MyFeedImagesViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.fragment_profile_my_feed_img_view_id);
        }

        public void setup(final Snap snap) {
            if (BuildConfig.DEBUG) {
                Picasso.with(mContext).setIndicatorsEnabled(true);
            }
            Picasso.with(mContext).load(snap.getThumbnail(mImageView.getMeasuredWidth(), mImageView.getMeasuredHeight())).into(mImageView);
        }
    }
}
