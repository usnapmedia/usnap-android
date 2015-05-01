package com.samsao.snapzi.profile;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.util.PreferenceManager;

import java.text.MessageFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;


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

        mProfileProvider.setupToolbar(mToolbar);

        // Setup tile letter
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
                    mLetterTileLetter.setText("SNAPZI");
                }

            }
        });

        // Setup share count
        //FIXME set real share count
        int shareCount = (int) (Math.random() * 1000.0f);
        mShareCount.setText(String.valueOf(shareCount));
        String fmt = getResources().getString(R.string.profile_share_plural);
        mShareLabel.setText(MessageFormat.format(fmt, shareCount));

        // Setup score count
        //FIXME set real score
        int score = (int) (Math.random() * 1000.0f);
        mScoreCount.setText(String.valueOf(score));

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
}