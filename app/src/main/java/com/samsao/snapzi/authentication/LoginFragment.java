package com.samsao.snapzi.authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.samsao.snapzi.R;
import com.samsao.snapzi.social.SocialNetworkFragment;
import com.samsao.snapzi.util.PreferenceManager;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginFragment extends SocialNetworkFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.fragment_login_fb_btn)
    public void facebookLogin() {
        loginWithFacebook(new OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(int socialNetworkId) {
                AccessToken accessToken = getFacebookAccessToken();
                if (accessToken != null) {
                    PreferenceManager.setFacebookAccessToken(accessToken.token);
                }
                // TODO
                Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int socialNetworkId, String requestId, String errorMessage, Object data) {
                // TODO
                Toast.makeText(getActivity(), "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.fragment_login_twitter_btn)
    public void twitterLogin() {
        Toast.makeText(getActivity(), "TBD", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fragment_login_gplus_btn)
    public void gplusLogin() {
        Toast.makeText(getActivity(), "TBD", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fragment_login_instagram_btn)
    public void instagramLogin() {
        Toast.makeText(getActivity(), "TBD", Toast.LENGTH_SHORT).show();
    }
}
