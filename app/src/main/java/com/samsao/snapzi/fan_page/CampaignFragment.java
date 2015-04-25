package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.Campaign;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jingsilu
 * @since 2015-04-24
 */
public class CampaignFragment extends Fragment{

    @InjectView(R.id.fragment_fan_page_bannerImageView)
    public ImageView mImageView;

    private Campaign mCampaign;

    public static CampaignFragment newInstance(Campaign campaign) {
        CampaignFragment campaignFragment = new CampaignFragment();
        campaignFragment.setCampaign(campaign);
        return campaignFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fan_page, container, false);
        ButterKnife.inject(this, view);
        // TODO add placeHolder and errorHolder
        if (!TextUtils.isEmpty(mCampaign.getBannerImgUrl())) {
            Picasso.with(getActivity()).load(mCampaign.getBannerImgUrl()).into(mImageView);
        }
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void setCampaign(Campaign campaign) {
        mCampaign = campaign;
    }

    /**
     * Returns the campaign's name
     * @return
     */
    public String getName() {
        return mCampaign.getName();
    }
}
