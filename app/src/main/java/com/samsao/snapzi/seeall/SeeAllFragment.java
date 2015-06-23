package com.samsao.snapzi.seeall;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.SnapList;
import com.samsao.snapzi.seeall.state.State;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class SeeAllFragment extends Fragment {

    // TODO inject me
    private ApiService mApiService = new ApiService();

    @InjectView(R.id.fragment_see_all_recycler_view)
    public RecyclerView mRecyclerView;

    private SeeAllSnapsAdapter mSeeAllSnapsAdapter;
    private GridLayoutManager mGridLayoutManager;
    private Listener mListener;

    @Icicle
    public State mState;

    public SeeAllFragment() {
    }

    public static SeeAllFragment newInstance(State state) {
        SeeAllFragment fragment = new SeeAllFragment();
        fragment.setState(state);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_see_all, container, false);
        ButterKnife.inject(this, view);

        mSeeAllSnapsAdapter = new SeeAllSnapsAdapter(getActivity());
        mRecyclerView.setAdapter(mSeeAllSnapsAdapter);
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
        fetchData();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Fetch data
     */
    public void fetchData() {
        mState.fetchData(mApiService, mListener.getCampaignId(), new Callback<SnapList>() {
            @Override
            public void success(SnapList snapList, Response response) {
                mSeeAllSnapsAdapter.setSnaps(snapList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                mSeeAllSnapsAdapter.clear();
                Timber.e("Error fetching snaps: " + error.getClass().getName() + ": " + error.getMessage());
                getActivity().finish();
            }
        });
    }

    public void setState(State state) {
        mState = state;
    }

    public interface Listener {
        Integer getCampaignId();
    }
}
