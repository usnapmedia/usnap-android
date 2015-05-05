package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Campaign;
import com.samsao.snapzi.api.entity.FeedImage;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.TopCampaign;
import com.samsao.snapzi.api.entity.TopCampaignList;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.seeall.SeeAllActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
public class CampaignFragment extends Fragment {

    @InjectView(R.id.fragment_campaign_banner)
    public ImageView mBannerImage;
    @InjectView(R.id.view_fragment_campaign_top_10_card_1)
    public CardView mTop10CardView1;
    @InjectView(R.id.view_fragment_campaign_top_10_card_2)
    public CardView mTop10CardView2;
    @InjectView(R.id.view_fragment_campaign_top_10_card_3)
    public CardView mTop10CardView3;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_1)
    public CardView mLatestUploadsCardView1;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_2)
    public CardView mLatestUploadsCardView2;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_3)
    public CardView mLatestUploadsCardView3;

    @Icicle
    public Campaign mCampaign;

    // TODO inject me
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

        getTopSnaps();
        getLiveFeed();
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
     * Get the top 10 snaps from the backend
     */
    private void getTopSnaps() {
        mApiService.getTopCampaign(mCampaign.getId(), new Callback<TopCampaignList>() {
            @Override
            public void success(TopCampaignList topCampaignList, Response response) {
                List<TopCampaign> topCampaigns = topCampaignList.getResponse();
                TopCampaign campaign;
                try {
                    campaign = topCampaigns.get(0);
                    setTopSnapCard(campaign, mTop10CardView1);
                } catch (IndexOutOfBoundsException e) {
                    // TODO hide layout
                    mTop10CardView1.setVisibility(View.INVISIBLE);
                    mTop10CardView2.setVisibility(View.INVISIBLE);
                    mTop10CardView3.setVisibility(View.INVISIBLE);
                }
                try {
                    campaign = topCampaigns.get(1);
                    setTopSnapCard(campaign, mTop10CardView2);
                } catch (IndexOutOfBoundsException e) {
                    mTop10CardView2.setVisibility(View.INVISIBLE);
                    mTop10CardView3.setVisibility(View.INVISIBLE);
                }
                try {
                    campaign = topCampaigns.get(2);
                    setTopSnapCard(campaign, mTop10CardView3);
                } catch (IndexOutOfBoundsException e) {
                    mTop10CardView3.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Top Campaign Data: " + error.getMessage());
            }
        });
    }

    private Typeface getFont() {
        Typeface fontText = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf");
        return fontText;
    }

    /**
     * Set a top snap cardView according to a snap
     *
     * @param campaign
     * @param cardView
     */
    private void setTopSnapCard(final TopCampaign campaign, final CardView cardView) {
        final ImageView imageView = (ImageView) cardView.findViewById(R.id.view_top_campaign_img_view_id);
        final TextView nameTextView = (TextView) cardView.findViewById(R.id.view_top_campaign_name);
        final TextView likesCountTextView = (TextView) cardView.findViewById(R.id.view_top_campaign_likes_count);

        Picasso.with(getActivity()).load(campaign.getUrl()).into(imageView);
        if (!TextUtils.isEmpty(campaign.getEmail())) {
            nameTextView.setText(campaign.getEmail());
        }
        if (campaign.getUsnapScore() != null) {
            likesCountTextView.setText(getResources().getQuantityString(R.plurals.likes_plural, campaign.getUsnapScore(), campaign.getUsnapScore()));
        }
        nameTextView.setTypeface(getFont());
        likesCountTextView.setTypeface(getFont());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDetailsActivity.start(campaign, getActivity());
            }
        });
    }

    /**
     * Get the latest uploads pictures from the backend
     */
    private void getLiveFeed() {
        mApiService.getLiveFeed(mCampaign.getId(),new Callback<FeedImageList>() {
            @Override
            public void success(FeedImageList latestUploadsList, Response response) {
                List<FeedImage> feedImages = latestUploadsList.getResponse();
                FeedImage image;
                try {
                    image = feedImages.get(0);
                    setLatestUploadCard(image, mLatestUploadsCardView1);
                } catch (IndexOutOfBoundsException e) {
                    // TODO hide layout
                    mLatestUploadsCardView1.setVisibility(View.INVISIBLE);
                    mLatestUploadsCardView2.setVisibility(View.INVISIBLE);
                    mLatestUploadsCardView3.setVisibility(View.INVISIBLE);
                }
                try {
                    image = feedImages.get(1);
                    setLatestUploadCard(image, mLatestUploadsCardView2);
                } catch (IndexOutOfBoundsException e) {
                    mLatestUploadsCardView2.setVisibility(View.INVISIBLE);
                    mLatestUploadsCardView3.setVisibility(View.INVISIBLE);
                }
                try {
                    image = feedImages.get(2);
                    setLatestUploadCard(image, mLatestUploadsCardView3);
                } catch (IndexOutOfBoundsException e) {
                    mLatestUploadsCardView3.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Latest Uploads Data: " + error.getMessage());
            }
        });
    }

    /**
     * Set a latest upload cardView according to a live feed
     *
     * @param image
     * @param cardView
     */
    private void setLatestUploadCard(final FeedImage image, final CardView cardView) {
        final ImageView imageView = (ImageView) cardView.findViewById(R.id.view_top_campaign_img_view_id);
        final TextView nameTextView = (TextView) cardView.findViewById(R.id.view_top_campaign_name);
        final TextView likesCountTextView = (TextView) cardView.findViewById(R.id.view_top_campaign_likes_count);

        Picasso.with(getActivity()).load(image.getUrl()).into(imageView);
        if (!TextUtils.isEmpty(image.getEmail())) {
            nameTextView.setText(image.getEmail());
        }
        if (image.getFbLikes() != null) {
            likesCountTextView.setText(getResources().getQuantityString(R.plurals.likes_plural, image.getFbLikes(), image.getFbLikes()));
        }

        nameTextView.setTypeface(getFont());
        likesCountTextView.setTypeface(getFont());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDetailsActivity.start(image, getActivity());
            }
        });
    }

    public void setCampaign(Campaign campaign) {
        mCampaign = campaign;
    }

    @OnClick(R.id.fragment_campaign_see_all_top_10_btn)
    public void seeAllTop10() {
        SeeAllActivity.startTop10(getActivity(), mCampaign.getId());
    }

    @OnClick(R.id.fragment_campaign_see_all_latest_uploads_btn)
    public void seeAllLatestUploads() {
        SeeAllActivity.startLatestUploads(getActivity());
    }

    @OnClick(R.id.fragment_campaign_contest_btn)
    public void enterContest() {
        SelectMediaActivity.start(getActivity(), mCampaign.getId());
    }

    /**
     * Returns the campaign's name
     *
     * @return
     */
    public String getName() {
        return mCampaign.getName();
    }
}
