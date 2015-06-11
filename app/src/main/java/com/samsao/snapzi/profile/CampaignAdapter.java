package com.samsao.snapzi.profile;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.Campaign;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author vlegault
 * @since 2015-04-30
 */
public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder> {
    private Context mContext;
    private List<Campaign> mCampaignList;
    private int mCampaignId;


    public CampaignAdapter(Context context) {
        mContext = context;
        mCampaignList = null;
    }

    @Override
    public int getItemCount() {
        if (mCampaignList == null) {
            return 0;
        } else {
            return mCampaignList.size();
        }
    }

    @Override
    public void onBindViewHolder(CampaignViewHolder campaignViewHolder, int position) {
        Campaign campaign = mCampaignList.get(position);
        mCampaignId = campaign.getId();
        campaignViewHolder.setup(campaign);
    }

    @Override
    public CampaignViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_top_campaign_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMediaActivity.start(mContext, mCampaignId);
                ((Activity) mContext).finish();
            }
        });
        return new CampaignViewHolder(view);
    }

    public void setCampaignList(List<Campaign> list) {
        mCampaignList = list;
        notifyDataSetChanged();
    }

    public class CampaignViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mContainer;
        private ImageView mImageView;
        private TextView mName;
        private TextView mShareCount;
        private TextView mDescription;


        public CampaignViewHolder(View view) {
            super(view);
            mContainer = (LinearLayout) view.findViewById(R.id.fragment_profile_top_campaign_item);
            mImageView = (ImageView) view.findViewById(R.id.fragment_profile_top_campaign_item_image);
            mName = (TextView) view.findViewById(R.id.fragment_profile_top_campaign_item_name);
            mShareCount = (TextView) view.findViewById(R.id.fragment_profile_top_campaign_item_share_count);
            mDescription = (TextView) view.findViewById(R.id.fragment_profile_top_campaign_item_description);
        }

        public void setup(final Campaign campaign) {
            if (!TextUtils.isEmpty(campaign.getBannerImgUrl())) {
                Picasso.with(mContext).load(campaign.getBannerImgUrl()).into(mImageView);
            }
            setName(campaign.getName());
            // TODO get share count
            setShareCount(null);
            setDescription(campaign.getDescription());
        }

        public LinearLayout getContainer() {
            return mContainer;
        }

        public void setName(String name) {
            if (!TextUtils.isEmpty(name)) {
                mName.setText(name);
            } else {
                //FIXME to remove
                mName.setText("Pelvish Preshley");
            }
        }

        public void setShareCount(Integer count) {
            //FIXME to remove
            if (count == null) {
                count = (int) (Math.random() * 1000.0f);
            }
            String shareCount = mContext.getResources().getString(R.string.profile_share_plural);
            mShareCount.setText(count + " " + MessageFormat.format(shareCount, count));
        }

        public void setDescription(String description) {
            if (!TextUtils.isEmpty(description)) {
                mDescription.setText(description);
            } else {
                //FIXME to remove
                mDescription.setText("Pelvish Preshley lorem ipsum tatoum pitoum");
            }
        }
    }
}
