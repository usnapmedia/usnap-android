package com.samsao.snapzi.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samsao.snapzi.MainActivity;
import com.samsao.snapzi.R;
import com.samsao.snapzi.social.SocialNetworkFragment;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.listeners.OnLoginListener;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginFragmentOld extends SocialNetworkFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragmentOld newInstance() {
        LoginFragmentOld fragment = new LoginFragmentOld();
        return fragment;
    }

    public LoginFragmentOld() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_old, container, false);
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
        if (!isFacebookConnected()) {
            loginWithFacebook(new OnLoginListener() {
                @Override
                public void onLogin() {
                    setFacebookAccessToken();
                    login();
                    Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNotAcceptingPermissions(Permission.Type type) {
                    removeFacebookAccessToken();
                    Toast.makeText(getActivity(), "Login failed: permissions not accepted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onThinking() {

                }

                @Override
                public void onException(Throwable throwable) {
                    removeFacebookAccessToken();
                    Toast.makeText(getActivity(), "Login failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String error) {
                    removeFacebookAccessToken();
                    Toast.makeText(getActivity(), "Login failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setFacebookAccessToken();
            login();
        }
    }

    @OnClick(R.id.fragment_login_twitter_btn)
    public void twitterLogin() {
        Toast.makeText(getActivity(), "Twitter TBD", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fragment_login_gplus_btn)
    public void gplusLogin() {
        Toast.makeText(getActivity(), "Google+ TBD", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fragment_login_instagram_btn)
    public void instagramLogin() {
        Toast.makeText(getActivity(), "Instagram TBD", Toast.LENGTH_SHORT).show();
    }

    /**
     * Login
     */
    protected void login() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
