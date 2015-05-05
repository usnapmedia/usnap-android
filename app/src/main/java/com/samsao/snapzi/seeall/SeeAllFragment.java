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

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.FeedImage;
import com.samsao.snapzi.api.entity.TopCampaign;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SeeAllFragment extends Fragment {
    public static String FRAGMENT_TAG = "com.samsao.snapzi.seeall.SeeAllFragment.FRAGMENT_TAG";

    @InjectView(R.id.fragment_see_all_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.fragment_see_all_recycler_view)
    public RecyclerView mRecyclerView;

    private SeeAllLatestUploadsAdapter mSeeAllLatestUploadsAdapter;
    private SeeAllTop10Adapter mSeeAllTop10Adapter;
    private GridLayoutManager mGridLayoutManager;
    private Listener mListener;

    public SeeAllFragment() {
    }

    public static SeeAllFragment newInstance() {
        SeeAllFragment fragment = new SeeAllFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_see_all, container, false);
        ButterKnife.inject(this, view);
        setupToolbar(mToolbar);

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

    public void setupToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            mToolbar = toolbar;
            mListener.setSupportActionBar(mToolbar);
            mListener.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mListener.getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    public interface Listener {
        ActionBar getSupportActionBar();
        void setSupportActionBar(Toolbar toolbar);
    }
}
