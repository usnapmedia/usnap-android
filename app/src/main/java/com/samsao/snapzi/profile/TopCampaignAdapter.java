package com.samsao.snapzi.profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.TopCampaign;
import com.samsao.snapzi.fan_page.PhotoDetailsActivity;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author vlegault
 * @since 2015-04-30
 */
public class TopCampaignAdapter extends RecyclerView.Adapter<TopCampaignAdapter.TopCampaignViewHolder> {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

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
        topCampaignViewHolder.setup(topCampaign);
    }

    @Override
    public TopCampaignViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mTopCampaignView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_top_campaign_item, parent, false);
        return new TopCampaignViewHolder(mTopCampaignView);
    }

    public void setTopCampaignList(List<TopCampaign> list) {
        mTopCampaignList = list;
        notifyDataSetChanged();
    }

    public class TopCampaignViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mContainer;
        private ImageView mImageView;
        private TextView mName;
        private TextView mShareCount;
        private TextView mDescription;


        public TopCampaignViewHolder(View view) {
            super(view);

            mContainer = (LinearLayout) view.findViewById(R.id.fragment_profile_top_campaign_item);
            mImageView = (ImageView) view.findViewById(R.id.fragment_profile_top_campaign_item_image);
            mName = (TextView) view.findViewById(R.id.fragment_profile_top_campaign_item_name);
            mShareCount = (TextView) view.findViewById(R.id.fragment_profile_top_campaign_item_share_count);
            mDescription = (TextView) view.findViewById(R.id.fragment_profile_top_campaign_item_description);
        }

        public void setup(final TopCampaign topCampaign) {
            Picasso.with(mContext).load(topCampaign.getUrl()).into(mImageView);

            setName(topCampaign.getUsername());
            setShareCount(Integer.valueOf(topCampaign.getFbLikes()));
            //setDescription(topCampaign.getText().toString());
            if (topCampaign.getText() != null && !topCampaign.getText().toString().isEmpty()) {
                mDescription.setText(topCampaign.getText().toString());
            } else {
                //FIXME to remove
                mDescription.setText("Pelvish Preshley lorem ipsum tatoum pitoum");
            }

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PhotoDetailsActivity.class);
                    intent.putExtra(PhotoDetailsActivity.EXTRA_PHOTO_PATH, topCampaign.getUrl());
                    if (topCampaign.getText() != null) {
                        intent.putExtra(PhotoDetailsActivity.EXTRA_PHOTO_TEXT, topCampaign.getText().toString());
                    }
                    if (topCampaign.getUsername() != null) {
                        intent.putExtra(PhotoDetailsActivity.EXTRA_PHOTO_USERNAME, topCampaign.getUsername());
                    }
                    mContext.startActivity(intent);
                }
            });
        }

        public LinearLayout getContainer() {
            return mContainer;
        }

        public void setName(String name) {
            if (name != null && !name.isEmpty()) {
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
            if (description != null && !description.isEmpty()) {
                mDescription.setText(description);
            } else {
                //FIXME to remove
                mDescription.setText("Pelvish Preshley lorem ipsum tatoum pitoum");
            }
        }
    }
}
