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
import com.squareup.picasso.Picasso;

/**
 * @author jingsilu
 * @since 2015-04-24
 */
public class CampaignFragment extends Fragment{
    private String mName;
    private String mBannerImageUrl;
    private ImageView mImageView;

    public static CampaignFragment newInstance(String name, String url) {
        CampaignFragment campaignFragment = new CampaignFragment();
        campaignFragment.setName(name);
        campaignFragment.setBannerImageUrl(url);
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
        mImageView = (ImageView) view.findViewById(R.id.fragment_fan_page_bannerImageView);
        // TODO add placeHolder and errorHolder
        if (!TextUtils.isEmpty(mBannerImageUrl)) {
            Picasso.with(getActivity()).load(mBannerImageUrl).into(mImageView);
        }
        return view;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getBannerImageUrl() {
        return mBannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        mBannerImageUrl = bannerImageUrl;
    }
}
