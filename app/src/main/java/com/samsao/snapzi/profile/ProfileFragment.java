package com.samsao.snapzi.profile;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.util.PreferenceManager;

import java.text.MessageFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


/**
 * @author vlegault
 * @since 15-04-30
 */
public class ProfileFragment extends Fragment {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private ProfileProvider mProfileProvider;

    @InjectView(R.id.fragment_profile_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.fragment_profile_letter_tile_container)
    FrameLayout mLetterTileContainer;

    @InjectView(R.id.fragment_profile_letter_tile_background)
    FrameLayout mLetterTileBackground;

    @InjectView(R.id.fragment_profile_letter_tile_letter)
    TextView mLetterTileLetter;

    @InjectView(R.id.fragment_profile_share_count)
    TextView mShareCount;

    @InjectView(R.id.fragment_profile_share_label)
    TextView mShareLabel;

    @InjectView(R.id.fragment_profile_score_count)
    TextView mScoreCount;

    @InjectView(R.id.fragment_profile_setting_button)
    TextView mSettingButton;

    @InjectView(R.id.fragment_profile_campaigns_button)
    Button mCampaignsButton;

    @InjectView(R.id.fragment_profile_my_feed_button)
    Button mMyFeedButton;

    @InjectView(R.id.fragment_profile_top_campaigns_container)
    RecyclerView mTopCampaignsContainer;
    private CampaignAdapter mCampaignAdapter;

    @InjectView(R.id.fragment_profile_my_feed_container)
    RecyclerView mMyFeedContainer;

    private ApiService mApiService = new ApiService();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoEditFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);

        // Setup toolbar
        mProfileProvider.setupToolbar(mToolbar);

        // Setup tile letter
        setupTileLetter();

        // Setup share count
        setupShareCount();

        // Setup score count
        setupScoreCount();

        // Setup setting button
        setupSettingButton();

        // Setup contests button
        setupContestsButton();

        // Setup my feed button
        setupMyFeedButton();

        mTopCampaignsContainer.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = (int) getResources().getDimension(R.dimen.elements_half_horizontal_margin);
                }
            }
        });
        showTopCampaigns();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mProfileProvider = (ProfileProvider) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ProfileProvider");
        }
    }

    /**
     * Setup tile letter
     */
    private void setupTileLetter() {
        mLetterTileContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Adapt tile background size to container
                int letterTileContainerShortestSideSize =
                        Math.min(mLetterTileContainer.getWidth(), mLetterTileContainer.getHeight());
                mLetterTileBackground.getLayoutParams().width = letterTileContainerShortestSideSize;
                mLetterTileBackground.getLayoutParams().height = letterTileContainerShortestSideSize;
                mLetterTileBackground.requestLayout();

                // Set tile letter
                PreferenceManager preferenceManager = new PreferenceManager();
                mLetterTileLetter.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) letterTileContainerShortestSideSize * 0.8f);
                // FIXME remove null case or find a better logic
                String username = preferenceManager.getUsername();
                if (username != null && !username.isEmpty()) {
                    mLetterTileLetter.setText(preferenceManager.getUsername());
                } else {
                    mLetterTileLetter.setText("pelvish");
                }
            }
        });
    }

    /**
     * Setup share count
     */
    private void setupShareCount() {
        //FIXME set real share count
        int shareCount = (int) (Math.random() * 1000.0f);
        mShareCount.setText(String.valueOf(shareCount));
        String label = getResources().getString(R.string.profile_share_plural);
        mShareLabel.setText(MessageFormat.format(label, shareCount));
    }

    /**
     * Setup score count
     */
    private void setupScoreCount() {
        //FIXME set real score
        int score = (int) (Math.random() * 1000.0f);
        mScoreCount.setText(String.valueOf(score));
    }

    private void setupSettingButton() {
        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SettingsActivity.start(getActivity());
            }
        });
    }

    /**
     * Setup contest button
     */
    private void setupContestsButton() {
        mCampaignsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCampaignsButton.isSelected()) {
                    showTopCampaigns();
                }
            }
        });
    }

    /**
     * Setup user feed button
     */
    private void setupMyFeedButton() {
        mMyFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMyFeedButton.isSelected()) {
                    showUserFeed();
                }
            }
        });
    }

    /**
     * Show top campaigns list view
     */
    private void showTopCampaigns() {
        mMyFeedButton.setSelected(false);
        mCampaignsButton.setSelected(true);
        mMyFeedContainer.setVisibility(View.GONE);
        mTopCampaignsContainer.setVisibility(View.VISIBLE);
        mTopCampaignsContainer.setHasFixedSize(true);

        // Set vertical scroll for top campaigns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTopCampaignsContainer.setLayoutManager(linearLayoutManager);

        mCampaignAdapter = new CampaignAdapter(getActivity());
        mTopCampaignsContainer.setAdapter(mCampaignAdapter);

        getCampaigns();
    }

    private void showUserFeed() {
        mCampaignsButton.setSelected(false);
        mMyFeedButton.setSelected(true);
        mTopCampaignsContainer.setVisibility(View.GONE);
        mMyFeedContainer.setVisibility(View.VISIBLE);
        mMyFeedContainer.setHasFixedSize(true);

        //TODO
        Toast.makeText(getActivity(), "TODO", Toast.LENGTH_LONG).show();
    }

    /**
     * Get the campaigns from the backend
     */
    private void getCampaigns() {
        mApiService.getCampaigns(new Callback<CampaignList>() {
            @Override
            public void success(CampaignList campaignList, Response response) {
                mCampaignAdapter.setCampaignList(campaignList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Top Campaign Data!");
            }
        });
    }
}