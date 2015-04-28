package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Campaign;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.TopCampaignList;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * @author jingsilu
 * @since 2015-04-24
 */
public class CampaignFragment extends Fragment{

    @InjectView(R.id.fragment_campaign_banner)
    public ImageView mBannerImage;

    @InjectView(R.id.fragment_campaign_top_campaign_container)
    public RecyclerView mTopCampaignContainer;
    private TopCampaignAdapter mTopCampaignAdapter;

    @InjectView(R.id.fragment_campaign_latest_uploads_recyclerView)
    public RecyclerView mLatestUploadsRecyclerView;
    private GridLayoutManager mLatestUploadsLayoutManager;
    private LatestUploadsAdapter mLatestUploadsAdapter;

    @Icicle
    public Campaign mCampaign;

    private ApiService mApiService = new ApiService();


    public static CampaignFragment newInstance(Campaign campaign) {
        CampaignFragment campaignFragment = new CampaignFragment();
        campaignFragment.setCampaign(campaign);
        return campaignFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campaign, container, false);
        ButterKnife.inject(this, view);

        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        // TODO add placeHolder and errorHolder
        if (!TextUtils.isEmpty(mCampaign.getBannerImgUrl())) {
            Picasso.with(getActivity()).load(mCampaign.getBannerImgUrl()).into(mBannerImage);
        }

        initTopCampaign();
        initLatestUploads();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Initialize the top 10 uploads
     */
    private void initTopCampaign() {
        mTopCampaignContainer.setHasFixedSize(true);
        // Set horizontal scroll for top campaigns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTopCampaignContainer.setLayoutManager(linearLayoutManager);

        mTopCampaignAdapter = new TopCampaignAdapter(getActivity());
        mTopCampaignContainer.setAdapter(mTopCampaignAdapter);
        mTopCampaignContainer.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = (int) getResources().getDimension(R.dimen.elements_half_horizontal_margin);
                }
                outRect.right = (int) getResources().getDimension(R.dimen.elements_half_horizontal_margin);
            }
        });
        getTopCampaign();
    }

    /**
     * Get the top 10 shares from the backend
     */
    private void getTopCampaign() {
        mApiService.getTopCampaign(new Callback<TopCampaignList>() {
            @Override
            public void success(TopCampaignList topCampaignList, Response response) {
                mTopCampaignAdapter.setTopCampaignList(topCampaignList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Top Campaign Data!");
            }
        });
    }

    /**
     * Initialize the latest uploads grid
     */
    private void initLatestUploads() {
//        mLatestUploadsRecyclerView.setHasFixedSize(true);
        mLatestUploadsLayoutManager = new GridLayoutManager(getActivity(), 4);
        mLatestUploadsRecyclerView.setLayoutManager(mLatestUploadsLayoutManager);
        mLatestUploadsAdapter = new LatestUploadsAdapter(getActivity());
        mLatestUploadsRecyclerView.setAdapter(mLatestUploadsAdapter);
        mLatestUploadsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = (int) getResources().getDimension(R.dimen.elements_quarter_horizontal_margin);
                outRect.right = (int) getResources().getDimension(R.dimen.elements_quarter_horizontal_margin);
                outRect.bottom = (int) getResources().getDimension(R.dimen.elements_quarter_horizontal_margin);
                outRect.left = (int) getResources().getDimension(R.dimen.elements_quarter_horizontal_margin);
            }
        });
        getLiveFeed();
    }

    /**
     * Get the latest uploads pictures from the backend
     */
    private void getLiveFeed() {
        mApiService.getLiveFeed(new Callback<FeedImageList>() {
            @Override
            public void success(FeedImageList latestUploadsList, Response response) {
                mLatestUploadsAdapter.setLatestUploads(latestUploadsList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Latest Uploads Data!");
            }
        });
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
