package com.samsao.snapzi.fan_page;

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
 * @since 2015-04-27
 */
public class LatestUploadsAdapter extends  RecyclerView.Adapter<LatestUploadsAdapter.LatestUploadsViewHolder>{
    private Context mContext;
    private List<FeedImage> mLatestUploadsList;
    // TODO inject me

    public LatestUploadsAdapter(Context context) {
        mContext = context;
        mLatestUploadsList = null;
    }

    @Override
    public int getItemCount() {
        if (mLatestUploadsList == null) {
            return 0;
        } else {
            return mLatestUploadsList.size();
        }
    }

    @Override
    public void onBindViewHolder(LatestUploadsViewHolder latestUploadsViewHolderViewHolder, int position) {
        FeedImage latestUploads = mLatestUploadsList.get(position);

        if (position < (getItemCount() -1)) {
            latestUploadsViewHolderViewHolder.itemView.setPadding(0,0,10,0);
        } else {
            latestUploadsViewHolderViewHolder.itemView.setPadding(0,0,0,0);
        }

        Context context = latestUploadsViewHolderViewHolder.mImageView.getContext();
        Picasso.with(context).load(latestUploads.getUrl()).into(latestUploadsViewHolderViewHolder.mImageView);
    }

    @Override
    public LatestUploadsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mTopCampaignView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_latest_uploads, null);
        return new LatestUploadsViewHolder(mTopCampaignView);
    }

    public void setLatestUploads(List<FeedImage> list) {
        mLatestUploadsList = list;
        notifyDataSetChanged();
    }

    public class LatestUploadsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public LatestUploadsViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.view_latest_uploads_image);
        }
    }
}
