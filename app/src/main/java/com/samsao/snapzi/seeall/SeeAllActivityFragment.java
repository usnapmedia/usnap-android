package com.samsao.snapzi.seeall;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.samsao.snapzi.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SeeAllActivityFragment extends Fragment {
    public static String FRAGMENT_TAG = "com.samsao.snapzi.seeall.SeeAllActivityFragment.FRAGMENT_TAG";

    @InjectView(R.id.fragment_see_all_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.fragment_see_all_recycler_view)
    public RecyclerView mRecyclerView;

    private Listener mListener;

    public SeeAllActivityFragment() {
    }

    public static SeeAllActivityFragment newInstance() {
        SeeAllActivityFragment fragment = new SeeAllActivityFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_see_all, container, false);
        ButterKnife.inject(this, view);
        setupToolbar(mToolbar);

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

    public interface Listener {
        ActionBar getSupportActionBar();
        void setSupportActionBar(Toolbar toolbar);
    }
}
