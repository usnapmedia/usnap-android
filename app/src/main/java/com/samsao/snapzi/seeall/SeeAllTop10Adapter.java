package com.samsao.snapzi.seeall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.TopCampaign;
import com.samsao.snapzi.fan_page.PhotoDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * @author jfcartier
 * @since 2015-05-05
 */
public class SeeAllTop10Adapter extends RecyclerView.Adapter<SeeAllTop10Adapter.SeeAllTop10ViewHolder> {

    private Context mContext;
    private List<TopCampaign> mTopCampaigns;

    public SeeAllTop10Adapter(Context context) {
        mContext = context;
        mTopCampaigns = null;
    }

    @Override
    public int getItemCount() {
        if (mTopCampaigns == null) {
            return 0;
        } else {
            return mTopCampaigns.size();
        }
    }

    @Override
    public void onBindViewHolder(SeeAllTop10ViewHolder seeAllTop10ViewHolder, int position) {
        TopCampaign topCampaign = mTopCampaigns.get(position);
        seeAllTop10ViewHolder.setup(topCampaign);
    }

    @Override
    public SeeAllTop10ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_see_all_item, parent, false);
        return new SeeAllTop10ViewHolder(view);
    }

    public void setTopCampaigns(List<TopCampaign> topCampaigns) {
        mTopCampaigns = topCampaigns;
        notifyDataSetChanged();
    }

    public class SeeAllTop10ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public SeeAllTop10ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view;

        }
        public void setup(final TopCampaign topCampaign) {
            // TODO add an error image and a placeholder
            // TODO replace with thumbUrl
            Picasso.with(mContext).load(topCampaign.getUrl()).into(mImageView);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoDetailsActivity.start(topCampaign, mContext);
                }
            });
        }
    }
}

