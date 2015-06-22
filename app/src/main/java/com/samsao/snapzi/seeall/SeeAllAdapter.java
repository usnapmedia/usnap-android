package com.samsao.snapzi.seeall;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.seeall.state.StateLatestAll;
import com.samsao.snapzi.seeall.state.StateLatestPhotos;
import com.samsao.snapzi.seeall.state.StateLatestVideos;
import com.samsao.snapzi.seeall.state.StateTop10All;
import com.samsao.snapzi.seeall.state.StateTop10Photos;
import com.samsao.snapzi.seeall.state.StateTop10Videos;

import java.lang.ref.WeakReference;


/**
 * @author jingsilu
 * @since 2015-05-07
 */
public class SeeAllAdapter extends FragmentStatePagerAdapter {

    public final static int FRAGMENT_SEE_ALL_PHOTOS = 0;
    public final static int FRAGMENT_SEE_ALL_VIDEOS = 1;
    public final static int FRAGMENT_SEE_ALL_ALL = 2;

    private WeakReference<SeeAllFragment> mSeeAllPhotosFragment;
    private WeakReference<SeeAllFragment> mSeeAllVideosFragment;
    private WeakReference<SeeAllFragment> mSeeAllAllFragment;

    private int mMode;

    public SeeAllAdapter(FragmentManager fragmentManager, int mode) {
        super(fragmentManager);
        mMode = mode;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRAGMENT_SEE_ALL_PHOTOS:
                if (mMode == SeeAllActivity.SEE_ALL_TOP_10) {
                    return SeeAllFragment.newInstance(new StateTop10Photos());
                } else {
                    return SeeAllFragment.newInstance(new StateLatestPhotos());
                }
            case FRAGMENT_SEE_ALL_VIDEOS:
                if (mMode == SeeAllActivity.SEE_ALL_TOP_10) {
                    return SeeAllFragment.newInstance(new StateTop10Videos());
                } else {
                    return SeeAllFragment.newInstance(new StateLatestVideos());
                }
            case FRAGMENT_SEE_ALL_ALL:
                if (mMode == SeeAllActivity.SEE_ALL_TOP_10) {
                    return SeeAllFragment.newInstance(new StateTop10All());
                } else {
                    return SeeAllFragment.newInstance(new StateLatestAll());
                }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
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
        switch (position) {
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
        switch (position) {
            case FRAGMENT_SEE_ALL_PHOTOS:
                mSeeAllPhotosFragment.clear();
                mSeeAllPhotosFragment = null;
                break;
            case FRAGMENT_SEE_ALL_VIDEOS:
                mSeeAllVideosFragment.clear();
                mSeeAllVideosFragment = null;
                break;
            case FRAGMENT_SEE_ALL_ALL:
                mSeeAllAllFragment.clear();
                mSeeAllAllFragment = null;
                break;
            default:
                break;
        }
        super.destroyItem(container, position, object);
    }

    /**
     * Refresh
     *
     * @param position
     */
    public void refresh(int position) {
        switch (position) {
            case FRAGMENT_SEE_ALL_PHOTOS:
                if (mSeeAllPhotosFragment != null) {
                    SeeAllFragment seeAllFragment = mSeeAllPhotosFragment.get();
                    if (seeAllFragment != null) {
                        seeAllFragment.fetchData();
                    }
                }
                break;
            case FRAGMENT_SEE_ALL_VIDEOS:
                if (mSeeAllVideosFragment != null) {
                    SeeAllFragment seeAllFragment = mSeeAllVideosFragment.get();
                    if (seeAllFragment != null) {
                        seeAllFragment.fetchData();
                    }
                }
                break;
            case FRAGMENT_SEE_ALL_ALL:
                if (mSeeAllAllFragment != null) {
                    SeeAllFragment seeAllFragment = mSeeAllAllFragment.get();
                    if (seeAllFragment != null) {
                        seeAllFragment.fetchData();
                    }
                }
                break;
            default:
                break;
        }
    }
}
