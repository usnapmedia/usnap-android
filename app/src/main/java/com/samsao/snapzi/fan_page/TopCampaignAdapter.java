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
    // TODO inject me

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

        if (position < (getItemCount() -1)) {
            topCampaignViewHolder.itemView.setPadding(0,0,10,0);
        } else {
            topCampaignViewHolder.itemView.setPadding(0,0,0,0);
        }

        Context context = topCampaignViewHolder.mImageView.getContext();
        Picasso.with(context).load(topCampaign.getUrl()).into(topCampaignViewHolder.mImageView);
        topCampaignViewHolder.mTextView.setText(topCampaign.getEmail());
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
        private TextView mTextView;

        public TopCampaignViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.view_top_campaign_img_view_id);
            mTextView = (TextView) v.findViewById(R.id.view_top_campaign_text_id);
        }
    }
}
