package com.samsao.snapzi.preferences;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.samsao.snapzi.MainActivity;
import com.samsao.snapzi.R;
import com.samsao.snapzi.social.SocialNetworkFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class PreferencesFragment extends SocialNetworkFragment {

    @InjectView(R.id.fragment_preferences_facebook)
    public Switch mFacebookSwitch;
    @InjectView(R.id.fragment_preferences_twitter)
    public Switch mTwitterSwitch;
    @InjectView(R.id.fragment_preferences_gplus)
    public Switch mGooglePlusSwitch;
    @InjectView(R.id.fragment_preferences_instagram)
    public Switch mInstagramSwitch;
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

        setSocialNetworkManagerOnInitializationCompleteListener(new SocialNetworkManager.OnInitializationCompleteListener() {
            @Override
            public void onSocialNetworkManagerInitialized() {
                initializeSwitches();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    protected void initializeSwitches() {
        initializeFacebookSwitch();
    }

    protected void initializeFacebookSwitch() {
        mFacebookSwitch.setChecked(isFacebookConnected());
        mFacebookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginWithFacebook(new OnLoginCompleteListener() {
                        @Override
                        public void onLoginSuccess(int socialNetworkId) {
                            setFacebookAccessToken();
                            Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int socialNetworkId, String requestId, String errorMessage, Object data) {
                            removeFacebookAccessToken();
                            Toast.makeText(getActivity(), "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    logoutFromFacebook();
                }
            }
        });
    }

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
