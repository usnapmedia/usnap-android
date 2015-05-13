package com.samsao.snapzi.seeall;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;

import java.lang.ref.WeakReference;


/**
 * @author jingsilu
 * @since 2015-05-07
 */
public class SeeAllAdapter extends FragmentStatePagerAdapter{

    private final static int NUM_SEE_ALL_MODE = 3;

    public final static int FRAGMENT_SEE_ALL_PHOTOS = 0;
    public final static int FRAGMENT_SEE_ALL_VIDEOS = 1;
    public final static int FRAGMENT_SEE_ALL_ALL = 2;

    private WeakReference<SeeAllFragment> mSeeAllPhotosFragment;
    private WeakReference<SeeAllFragment> mSeeAllVideosFragment;
    private WeakReference<SeeAllFragment> mSeeAllAllFragment;

    public SeeAllAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return SeeAllFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return NUM_SEE_ALL_MODE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case FRAGMENT_SEE_ALL_PHOTOS:
                return SnapziApplication.getContext().getString(R.string.see_all_photos);
            case FRAGMENT_SEE_ALL_VIDEOS:
                return SnapziApplication.getContext().getString(R.string.see_all_videos);
            case FRAGMENT_SEE_ALL_ALL:
                return SnapziApplication.getContext().getString(R.string.see_all_all);
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        switch(position){
            case FRAGMENT_SEE_ALL_PHOTOS:
                mSeeAllPhotosFragment = new WeakReference<>((SeeAllFragment) fragment);
                break;
            case FRAGMENT_SEE_ALL_VIDEOS:
                mSeeAllVideosFragment = new WeakReference<>((SeeAllFragment) fragment);
                break;
            case FRAGMENT_SEE_ALL_ALL:
                mSeeAllAllFragment = new WeakReference<>((SeeAllFragment) fragment);
                break;
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        switch(position){
            case FRAGMENT_SEE_ALL_PHOTOS:
                mSeeAllPhotosFragment = null;
            case FRAGMENT_SEE_ALL_VIDEOS:
                mSeeAllVideosFragment = null;
            case FRAGMENT_SEE_ALL_ALL:
                mSeeAllAllFragment = null;
        }
        super.destroyItem(container, position, object);
    }

    /**
     * Refreshes the photos
     */
    public void refreshPhotos() {
        if (mSeeAllPhotosFragment != null) {
            SeeAllFragment seeAllPhotosFragment = mSeeAllPhotosFragment.get();
            if (seeAllPhotosFragment != null) {
                seeAllPhotosFragment.refreshPhotos();
            }
        }
    }

    /**
     * Refreshes the videos
     */
    public void refreshVideos() {
        if (mSeeAllVideosFragment != null) {
            SeeAllFragment seeAllVideosFragment = mSeeAllVideosFragment.get();
            if (seeAllVideosFragment != null) {
                seeAllVideosFragment.refreshVideos();
            }
        }
    }

    /**
     * Refreshes all
     */
    public void refreshAll() {
        if (mSeeAllAllFragment != null) {
            SeeAllFragment seeAllAllFragment = mSeeAllAllFragment.get();
            if (seeAllAllFragment != null) {
                seeAllAllFragment.refreshAll();
            }
        }
    }
}
