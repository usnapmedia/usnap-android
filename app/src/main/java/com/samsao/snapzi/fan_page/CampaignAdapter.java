package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.samsao.snapzi.api.entity.CampaignList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by vlegault on 15-04-28.
 */
public class CampaignAdapter extends FragmentStatePagerAdapter {

    /**
     * List of fragments
     */
    private ArrayList<WeakReference<CampaignFragment>> mFragments;

    /**
     * List of campaigns
     */
    private CampaignList mCampaigns;

    public CampaignAdapter(FragmentManager fragmentManager, CampaignList campaigns) {
        super(fragmentManager);
        mCampaigns = campaigns;
        mFragments = new ArrayList<>(mCampaigns.getResponse().size());
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
        try {
            mFragments.set(position, new WeakReference<>((CampaignFragment) fragment));
        } catch (IndexOutOfBoundsException e) {
            mFragments.add(position, new WeakReference<>((CampaignFragment) fragment));
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragments.get(position).clear();
        mFragments.set(position, null);
        super.destroyItem(container, position, object);
    }

    /**
     * Refreshes all
     */
    public void refreshAll() {
        for (WeakReference<CampaignFragment> campaignFragment : mFragments) {
            if (campaignFragment != null && campaignFragment.get() != null) {
                campaignFragment.get().refreshAll();
            }
        }
    }

    /**
     * Returns a reference of the fragment at a given position
     * @param position
     * @return
     */
    public CampaignFragment getFragmentAt(int position) {
        try {
            return mFragments.get(position).get();
        } catch (Exception e) {
            return null;
        }
    }
}
