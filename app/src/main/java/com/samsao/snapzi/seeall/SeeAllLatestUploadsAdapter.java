package com.samsao.snapzi.seeall;

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
 * @author jfcartier
 * @since 2015-05-05
 */
public class SeeAllLatestUploadsAdapter extends RecyclerView.Adapter<SeeAllLatestUploadsAdapter.SeeAllLatestUploadsViewHolder> {

    private Context mContext;
    private List<FeedImage> mFeedImages;

    public SeeAllLatestUploadsAdapter(Context context) {
        mContext = context;
        mFeedImages = null;
    }

    @Override
    public int getItemCount() {
        if (mFeedImages == null) {
            return 0;
        } else {
            return mFeedImages.size();
        }
    }

    @Override
    public void onBindViewHolder(SeeAllLatestUploadsViewHolder seeAllLatestUploadsViewHolder, int position) {
        FeedImage image = mFeedImages.get(position);
        seeAllLatestUploadsViewHolder.setup(image);
    }

    @Override
    public SeeAllLatestUploadsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_see_all_item, parent, false);
        return new SeeAllLatestUploadsViewHolder(view);
    }

    public void setFeedImages(List<FeedImage> feedImages) {
        mFeedImages = feedImages;
        notifyDataSetChanged();
    }

    public class SeeAllLatestUploadsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public SeeAllLatestUploadsViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view;

        }
        public void setup(final FeedImage image) {
            // TODO add an error image and a placeholder
            // TODO replace with thumbUrl
            Picasso.with(mContext).load(image.getUrl()).into(mImageView);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoDetailsActivity.start(image, mContext);
                }
            });
        }
    }
}

