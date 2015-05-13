package com.samsao.snapzi.contest_page;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.Campaign;
import com.samsao.snapzi.api.util.CustomJsonDateTimeDeserializer;
import com.samsao.snapzi.util.StringUtil;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jingsilu
 * @since 2015-05-12
 */
public class ContestFragment extends Fragment {
    public static final String CONTEST_PAGE_FRAGMENT_TAG = "com.samsao.snapzi.contest_page.CONTEST_PAGE_FRAGMENT_TAG";
    DateTimeFormatter mDateFormat;

    @InjectView(R.id.activity_contest_page_toolbar)
    public Toolbar mToolbar;
    @InjectView(R.id.activity_contest_page_imageView)
    public ImageView mImageView;
    @InjectView(R.id.activity_contest_page_title)
    public TextView mTitle;
    @InjectView(R.id.activity_contest_page_date)
    public TextView mDate;
    @InjectView(R.id.activity_contest_page_description)
    public TextView mDescription;
    @InjectView(R.id.activity_contest_page_prize)
    public TextView mPrize;
    @InjectView(R.id.activity_contest_page_rules)
    public TextView mRules;

    private Listener mListener;
    private Integer mCampaignId;
    private String mCampaignBannerUrl;
    private String mCampaignTitle;
    private String mCampaignDescription;
    private String mCampaignStartDate;
    private String mCampaignEndDate;
    private String mCampaignPrize;
    private String mContestRules;
    private String mDateString;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContestFragment.
     */
    public static ContestFragment newInstance() {
        ContestFragment contestFragment = new ContestFragment();
        return contestFragment;
    }

    public ContestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Campaign campaign = mListener.getCampaign();
        mCampaignId = campaign.getId();
        mCampaignBannerUrl = campaign.getBannerImgUrl();
        mCampaignTitle = campaign.getName();
        mCampaignDescription = campaign.getDescription();
        mCampaignStartDate = campaign.getStartDate();
        mCampaignEndDate = campaign.getEndDate();
        mCampaignPrize = campaign.getPrize();
        mContestRules = campaign.getRules();
        mDateFormat = DateTimeFormat.forPattern("dd MMMM");
        mDateString = composeDate(mCampaignStartDate, mCampaignEndDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_page, container, false);
        ButterKnife.inject(this, view);
        Picasso.with(getActivity()).load(mCampaignBannerUrl).into(mImageView);
        mTitle.setText(mCampaignTitle);
        mDescription.setText(mCampaignDescription);
        mDate.setText(mDateString);
        mPrize.setText(mCampaignPrize);

        setupToolbar();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PhotoDetailsFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private void setupToolbar() {
        if (mToolbar != null) {
            mListener.setSupportActionBar(mToolbar);
        }
        mListener.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListener.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mListener.getSupportActionBar().setTitle(StringUtil.getAppFontString(R.string.Contest));
    }

    /**
     * make up the Date from startDate and endDate
     * @param startDate
     * @param endDate
     * @return
     */
    private String composeDate(String startDate, String endDate) {
        DateTimeFormatter dateTimeFormatter = CustomJsonDateTimeDeserializer.getDateFormatter();
        DateTime mStartDate = dateTimeFormatter.parseDateTime(startDate);
        DateTime mEndDate = dateTimeFormatter.parseDateTime(endDate);
        return mDateFormat.print(mStartDate) + " - " + mDateFormat.print(mEndDate);
    }


    public interface Listener {
        Campaign getCampaign();
        void setSupportActionBar(Toolbar toolbar);
        ActionBar getSupportActionBar();
    }
}
