package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.samsao.snapzi.api.entity.CampaignList;

/**
 * Created by vlegault on 15-04-28.
 */
public class CampaignAdapter extends FragmentStatePagerAdapter {
    /**
     * List of campaigns
     */
    private CampaignList mCampaigns;

    public CampaignAdapter(FragmentManager fragmentManager, CampaignList campaigns) {
        super(fragmentManager);
        mCampaigns = campaigns;
    }

    @Override
    public int getCount() {
        return mCampaigns.getResponse().size();
    }

    @Override
    public Fragment getItem(int position) {
        return CampaignFragment.newInstance(mCampaigns.getResponse().get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mCampaigns.getResponse().get(position).getName();
    }
}
