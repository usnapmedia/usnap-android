package com.samsao.snapzi.seeall;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.FeedImage;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.TopCampaign;
import com.samsao.snapzi.api.entity.TopCampaignList;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icicle;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SeeAllFragment extends Fragment {
    public static String FRAGMENT_TAG = "com.samsao.snapzi.seeall.SeeAllFragment.FRAGMENT_TAG";
    private final static int SEE_ALL_PHOTOS = 0;
    private final static int SEE_ALL_VIDEOS = 1;
    private final static int SEE_ALL_ALL = 2;

    //TODO the two lists should be fetched from feed/top/photos and feed/top/videos
    private final static int SEE_ALL_TOP_10 = 0;
    private final static int SEE_ALL_LATEST = 1;

    // TODO inject me
    private ApiService mApiService = new ApiService();

    @Icicle
    public int mSeeAllMode;

    @InjectView(R.id.fragment_see_all_recycler_view)
    public RecyclerView mRecyclerView;

    private SeeAllLatestUploadsAdapter mSeeAllLatestUploadsAdapter;
    private SeeAllTop10Adapter mSeeAllTop10Adapter;
    private GridLayoutManager mGridLayoutManager;
    private Listener mListener;

    public SeeAllFragment() {
    }

    public static SeeAllFragment newInstance(int seeAllMode) {
        SeeAllFragment fragment = new SeeAllFragment();
        fragment.mSeeAllMode = seeAllMode;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_see_all, container, false);
        ButterKnife.inject(this, view);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = (int) getResources().getDimension(R.dimen.elements_quarter_vertical_margin);
                outRect.right = (int) getResources().getDimension(R.dimen.elements_quarter_vertical_margin);
                outRect.bottom = (int) getResources().getDimension(R.dimen.elements_quarter_vertical_margin);
                outRect.left = (int) getResources().getDimension(R.dimen.elements_quarter_vertical_margin);
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SeeAllActivityFragment.Listener");
        }
    }

    /**
     * Set adapter data for latest uploads
     * @param list
     */
    public void setLatestUploadsAdapterData(List<FeedImage> list) {
        mSeeAllLatestUploadsAdapter = new SeeAllLatestUploadsAdapter(getActivity());
        mRecyclerView.setAdapter(mSeeAllLatestUploadsAdapter);
        mSeeAllLatestUploadsAdapter.setFeedImages(list);
    }

    /**
     * Set adapter data for top 10
     * @param list
     */
    public void setTop10AdapterData(List<TopCampaign> list) {
        mSeeAllTop10Adapter = new SeeAllTop10Adapter(getActivity());
        mRecyclerView.setAdapter(mSeeAllTop10Adapter);
        mSeeAllTop10Adapter.setTopCampaigns(list);
    }

    public void setupData(){
        //setup data for fragments according to the mode
        if (mListener.getMode() == SEE_ALL_TOP_10) {
            switch (mSeeAllMode) {
                case SEE_ALL_PHOTOS:
                    getTop10PhotosData();
                    break;
                case SEE_ALL_VIDEOS:
                    getTop10VideosData();
                    break;
                default:
                    getTop10AllData();
                    break;
            }

        } else {
            switch (mSeeAllMode) {
                case SEE_ALL_PHOTOS:
                    getLastestPhotosData();
                    break;
                case SEE_ALL_VIDEOS:
                    getLastestVideosData();
                    break;
                default:
                    getLastestAllData();
                    break;
            }

        }
    }

    /**
     * Refreshes the photos
     */
    public void refreshPhotos() {
        if (mListener.getMode() == SEE_ALL_TOP_10) {
            getTop10PhotosData();
        }else{
            getLastestPhotosData();
        }
    }

    /**
     * Refreshes the videos
     */
    public void refreshVideos() {
        if (mListener.getMode() == SEE_ALL_TOP_10) {
            getTop10VideosData();
        }else{
            getLastestVideosData();
        }
    }

    /**
     * Refreshes all
     */
    public void refreshAll() {
        if (mListener.getMode() == SEE_ALL_TOP_10) {
            getTop10AllData();
        }else{
            getLastestAllData();
        }
    }

    public void getTop10PhotosData() {
        mApiService.getTopCampaign(new Callback<TopCampaignList>() {
            @Override
            public void success(TopCampaignList topCampaignList, Response response) {
                SeeAllFragment.this.setTop10AdapterData(topCampaignList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO string resource
                Toast.makeText(getActivity(), "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    public void getLastestPhotosData() {
        mApiService.getLiveFeed(new Callback<FeedImageList>() {
            @Override
            public void success(FeedImageList feedImageList, Response response) {
                SeeAllFragment.this.setLatestUploadsAdapterData(feedImageList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO string resource
                Toast.makeText(getActivity(), "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    public void getTop10VideosData() {
        mApiService.getTopCampaign(new Callback<TopCampaignList>() {
            @Override
            public void success(TopCampaignList topCampaignList, Response response) {
                SeeAllFragment.this.setTop10AdapterData(topCampaignList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO string resource
                Toast.makeText(getActivity(), "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    public void getLastestVideosData() {
        mApiService.getLiveFeed(new Callback<FeedImageList>() {
            @Override
            public void success(FeedImageList feedImageList, Response response) {
                SeeAllFragment.this.setLatestUploadsAdapterData(feedImageList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO string resource
                Toast.makeText(getActivity(), "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    public void getTop10AllData() {
        mApiService.getTopCampaign(new Callback<TopCampaignList>() {
            @Override
            public void success(TopCampaignList topCampaignList, Response response) {
                SeeAllFragment.this.setTop10AdapterData(topCampaignList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO string resource
                Toast.makeText(getActivity(), "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    public void getLastestAllData() {
        mApiService.getLiveFeed(new Callback<FeedImageList>() {
            @Override
            public void success(FeedImageList feedImageList, Response response) {
                SeeAllFragment.this.setLatestUploadsAdapterData(feedImageList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO string resource
                Toast.makeText(getActivity(), "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    public interface Listener {
        ActionBar getSupportActionBar();
        void setSupportActionBar(Toolbar toolbar);
        int getMode();
    }
}
