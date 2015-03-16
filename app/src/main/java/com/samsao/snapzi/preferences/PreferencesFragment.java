package com.samsao.snapzi.preferences;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsao.snapzi.MainActivity;
import com.samsao.snapzi.R;
import com.samsao.snapzi.social.SocialNetworkFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class PreferencesFragment extends SocialNetworkFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static PreferencesFragment newInstance() {
        PreferencesFragment fragment = new PreferencesFragment();
        return fragment;
    }

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

//    @OnClick(R.id.fragment_login_fb_btn)
//    public void facebookLogin() {
//        loginWithFacebook(new OnLoginCompleteListener() {
//            @Override
//            public void onLoginSuccess(int socialNetworkId) {
//                AccessToken accessToken = getFacebookAccessToken();
//                if (accessToken != null) {
//                    PreferenceManager.setFacebookAccessToken(accessToken.token);
//                }
//                // TODO
//                Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(int socialNetworkId, String requestId, String errorMessage, Object data) {
//                // TODO
//                Toast.makeText(getActivity(), "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @OnClick(R.id.fragment_preferences_logout_btn)
    public void logout() {
        logoutFromFacebook();
        logoutFromTwitter();
        logoutFromGooglePlus();
        // TODO instagram

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
