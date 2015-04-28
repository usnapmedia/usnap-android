package com.samsao.snapzi.fan_page;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.TopCampaign;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author jingsilu
 * @since 2015-04-27
 */
public class TopCampaignAdapter extends RecyclerView.Adapter<TopCampaignAdapter.TopCampaignViewHolder> {

    private Context mContext;
    private List<TopCampaign> mTopCampaignList;

    public TopCampaignAdapter(Context context) {
        mContext = context;
        mTopCampaignList = null;
    }

    @Override
    public int getItemCount() {
        if (mTopCampaignList == null) {
            return 0;
        } else {
            return mTopCampaignList.size();
        }
    }

    @Override
    public void onBindViewHolder(TopCampaignViewHolder topCampaignViewHolder, int position) {
        TopCampaign topCampaign = mTopCampaignList.get(position);
        // TODO setup the view holder with the TopCampaign object
        Picasso.with(mContext).load(topCampaign.getUrl()).into(topCampaignViewHolder.mImageView);
        topCampaignViewHolder.setLikesCount(topCampaign.getFbLikes());
    }

    @Override
    public TopCampaignViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mTopCampaignView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_top_campaign, null);
        return new TopCampaignViewHolder(mTopCampaignView);
    }

    public void setTopCampaignList(List<TopCampaign> list) {
        mTopCampaignList = list;
        notifyDataSetChanged();
    }

    public class TopCampaignViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mLikesCount;

        public TopCampaignViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.view_top_campaign_img_view_id);
            mLikesCount = (TextView) v.findViewById(R.id.view_top_campaign_likes_count);
        }

        public void setLikesCount(Integer count) {
            // FIXME to remove
            if (count == null) {
                count = 10;
            }
            mLikesCount.setText(mContext.getResources().getQuantityString(R.plurals.likes_plural, count, count));
        }
    }
}
