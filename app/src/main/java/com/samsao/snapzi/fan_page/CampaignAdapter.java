package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.samsao.snapzi.api.entity.CampaignList;

import java.lang.ref.WeakReference;

/**
 * Created by vlegault on 15-04-28.
 */
public class CampaignAdapter extends FragmentStatePagerAdapter {

    private WeakReference<CampaignFragment> mCampaignFragment;
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mCampaignFragment = new WeakReference<>((CampaignFragment) fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mCampaignFragment = null;
        super.destroyItem(container, position, object);
    }

    /**
     * Refreshes all
     */
    public void refreshAll() {
        if (mCampaignFragment != null) {
            CampaignFragment campaignFragment = mCampaignFragment.get();
            if (campaignFragment != null) {
                campaignFragment.refreshAll();
            }
        }
    }
}
