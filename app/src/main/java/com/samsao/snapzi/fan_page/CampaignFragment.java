package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samsao.snapzi.BuildConfig;
import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Campaign;
import com.samsao.snapzi.api.entity.Snap;
import com.samsao.snapzi.api.entity.SnapList;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.contest_page.ContestActivity;
import com.samsao.snapzi.seeall.SeeAllActivity;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
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
    @InjectView(R.id.fragment_campaign_top_10_layout)
    public LinearLayout mTop10Layout;
    @InjectView(R.id.view_fragment_campaign_top_10_card_1)
    public CardView mTop10CardView1;
    @InjectView(R.id.view_fragment_campaign_top_10_card_2)
    public CardView mTop10CardView2;
    @InjectView(R.id.view_fragment_campaign_top_10_card_3)
    public CardView mTop10CardView3;
    @InjectView(R.id.fragment_campaign_latest_layout)
    public LinearLayout mLatestLayout;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_1)
    public CardView mLatestUploadsCardView1;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_2)
    public CardView mLatestUploadsCardView2;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_3)
    public CardView mLatestUploadsCardView3;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_4)
    public CardView mLatestUploadsCardView4;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_5)
    public CardView mLatestUploadsCardView5;
    @InjectView(R.id.view_fragment_campaign_latest_uploads_card_6)
    public CardView mLatestUploadsCardView6;

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
        getLatestSnaps();
        return view;
    }

    @OnClick(R.id.fragment_campaign_banner)
    public void gotoContestPage() {
        ContestActivity.start(mCampaign, getActivity());
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
        mApiService.getTopSnaps(mCampaign.getId(), new Callback<SnapList>() {
            @Override
            public void success(SnapList snapList, Response response) {
                List<Snap> snaps = snapList.getResponse();
                Snap snap;
                try {
                    if (mTop10CardView1 != null) {
                        snap = snaps.get(0);
                        setSnapCard(snap, mTop10CardView1);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mTop10Layout.setVisibility(View.GONE);
                }
                try {
                    if (mTop10CardView2 != null) {
                        snap = snaps.get(1);
                        setSnapCard(snap, mTop10CardView2);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mTop10CardView2.setVisibility(View.INVISIBLE);
                    mTop10CardView3.setVisibility(View.INVISIBLE);
                }
                try {
                    if (mTop10CardView3 != null) {
                        snap = snaps.get(2);
                        setSnapCard(snap, mTop10CardView3);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mTop10CardView3.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (getActivity() != null) {
//                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    Timber.e("Error Fetching Top 10 snaps Data: " + error.getMessage());
                }
            }
        });
    }

    /**
     * Get the latest snaps from the backend
     */
    private void getLatestSnaps() {
        mApiService.getLiveFeed(mCampaign.getId(), new Callback<SnapList>() {
            @Override
            public void success(SnapList latestUploadsList, Response response) {
                List<Snap> snaps = latestUploadsList.getResponse();
                Snap image;
                try {
                    if (mLatestUploadsCardView1 != null) {
                        image = snaps.get(0);
                        setSnapCard(image, mLatestUploadsCardView1, true);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mLatestLayout.setVisibility(View.GONE);
                }
                try {
                    image = snaps.get(1);
                    if (mLatestUploadsCardView2 != null) {
                        setSnapCard(image, mLatestUploadsCardView2, true);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mLatestUploadsCardView2.setVisibility(View.INVISIBLE);
                    mLatestUploadsCardView3.setVisibility(View.INVISIBLE);
                    mLatestUploadsCardView4.setVisibility(View.GONE);
                    mLatestUploadsCardView5.setVisibility(View.GONE);
                    mLatestUploadsCardView6.setVisibility(View.GONE);
                }
                try {
                    image = snaps.get(2);
                    if (mLatestUploadsCardView3 != null) {
                        setSnapCard(image, mLatestUploadsCardView3, true);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mLatestUploadsCardView3.setVisibility(View.INVISIBLE);
                    mLatestUploadsCardView4.setVisibility(View.GONE);
                    mLatestUploadsCardView5.setVisibility(View.GONE);
                    mLatestUploadsCardView6.setVisibility(View.GONE);
                }
                try {
                    if (mLatestUploadsCardView4 != null) {
                        image = snaps.get(3);
                        setSnapCard(image, mLatestUploadsCardView4, true);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mLatestUploadsCardView4.setVisibility(View.GONE);
                    mLatestUploadsCardView5.setVisibility(View.GONE);
                    mLatestUploadsCardView6.setVisibility(View.GONE);
                }
                try {
                    if (mLatestUploadsCardView5 != null) {
                        image = snaps.get(4);
                        setSnapCard(image, mLatestUploadsCardView5, true);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mLatestUploadsCardView5.setVisibility(View.INVISIBLE);
                    mLatestUploadsCardView6.setVisibility(View.INVISIBLE);
                }
                try {
                    if (mLatestUploadsCardView6 != null) {
                        image = snaps.get(5);
                        setSnapCard(image, mLatestUploadsCardView6, true);
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    mLatestUploadsCardView6.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (getActivity() != null) {
//                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    Timber.e("Error Fetching Latest snaps data: " + error.getMessage());
                }
            }
        });
    }

    /**
     * Set a snap cardView according to a snap
     *
     * @param snap
     * @param cardView
     */
    private void setSnapCard(final Snap snap, final CardView cardView, boolean hideText) {
        final ImageView imageView = (ImageView) cardView.findViewById(R.id.view_top_campaign_img_view_id);
        final TextView nameTextView = (TextView) cardView.findViewById(R.id.view_top_campaign_name);
        final TextView likesCountTextView = (TextView) cardView.findViewById(R.id.view_top_campaign_likes_count);

        if (BuildConfig.DEBUG) {
            Picasso.with(getActivity()).setIndicatorsEnabled(true);
        }
        Picasso.with(getActivity()).load(snap.getThumbnail(imageView.getMeasuredWidth(),imageView.getMeasuredHeight())).into(imageView);
        if (!TextUtils.isEmpty(snap.getUsername())) {
            nameTextView.setText(snap.getUsername());
        }

        int fbLikes = snap.getFbLikes() == null ? 0 : snap.getFbLikes();
        String fbCount = getActivity().getResources().getString(R.string.top10_snaps_plural);
        likesCountTextView.setText(Integer.toString(fbLikes) + " " + MessageFormat.format(fbCount, fbLikes));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoDetailsActivity.start(snap, getActivity());
            }
        });

        if (hideText) {
            cardView.findViewById(R.id.view_top_campaign_text_layout).setVisibility(View.GONE);
        }
    }

    /**
     * Shortcut
     * @param snap
     * @param cardView
     */
    private void setSnapCard(final Snap snap, final CardView cardView) {
        setSnapCard(snap, cardView, false);
    }

    /**
     * Refresh all
     */
    public void refreshAll() {
        getTopSnaps();
        getLatestSnaps();
    }

    /**
     * Set mCampaign
     * @param campaign
     */

    public void setCampaign(Campaign campaign) {
        mCampaign = campaign;
    }

    @OnClick(R.id.fragment_campaign_see_all_top_10_btn)
    public void seeAllTop10() {
        SeeAllActivity.startTop10(getActivity(), mCampaign.getId());
    }

    @OnClick(R.id.fragment_campaign_see_all_latest_uploads_btn)
    public void seeAllLatestUploads() {
        SeeAllActivity.startLatestUploads(getActivity(), mCampaign.getId());
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
