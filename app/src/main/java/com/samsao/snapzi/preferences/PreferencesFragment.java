package com.samsao.snapzi.preferences;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.samsao.snapzi.MainActivity;
import com.samsao.snapzi.R;
import com.samsao.snapzi.social.OnGooglePlusLoginListener;
import com.samsao.snapzi.social.SocialNetworkFragment;
import com.samsao.snapzi.util.UserManager;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

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
     * Switches onChange listeners
     */
    private CompoundButton.OnCheckedChangeListener mFacebookSwitchOnCheckedChangeListener;
    private CompoundButton.OnCheckedChangeListener mTwitterSwitchOnCheckedChangeListener;
    private CompoundButton.OnCheckedChangeListener mGooglePlusSwitchOnCheckedChangeListener;

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

        mFacebookSwitchOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginWithFacebook(new OnLoginListener() {
                        @Override
                        public void onLogin() {
                            setFacebookAccessToken();
                            mFacebookSwitch.setChecked(true);
                            Toast.makeText(getActivity(), "Facebook login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNotAcceptingPermissions(Permission.Type type) {
                            removeFacebookAccessToken();
                            mFacebookSwitch.setOnCheckedChangeListener(null);
                            mFacebookSwitch.setChecked(false);
                            mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
                            Toast.makeText(getActivity(), "Facebook login failed: user did not accept permissions", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onThinking() {

                        }

                        @Override
                        public void onException(Throwable throwable) {
                            removeFacebookAccessToken();
                            mFacebookSwitch.setOnCheckedChangeListener(null);
                            mFacebookSwitch.setChecked(false);
                            mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
                            Toast.makeText(getActivity(), "Facebook login failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(String error) {
                            removeFacebookAccessToken();
                            mFacebookSwitch.setOnCheckedChangeListener(null);
                            mFacebookSwitch.setChecked(false);
                            mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
                            Toast.makeText(getActivity(), "Facebook login failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    logoutFromFacebook(new OnLogoutListener() {
                        @Override
                        public void onLogout() {

                        }

                        @Override
                        public void onThinking() {

                        }

                        @Override
                        public void onException(Throwable throwable) {

                        }

                        @Override
                        public void onFail(String s) {

                        }
                    });
                }
            }
        };

        mTwitterSwitchOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginWithTwitter(new Callback<TwitterSession>() {
                        @Override
                        public void success(Result<TwitterSession> twitterSessionResult) {
                            setTwitterAccessToken();
                            Toast.makeText(getActivity(), "Twitter login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(TwitterException e) {
                            removeTwitterAccessToken();
                            mTwitterSwitch.setOnCheckedChangeListener(null);
                            mTwitterSwitch.setChecked(false);
                            mTwitterSwitch.setOnCheckedChangeListener(mTwitterSwitchOnCheckedChangeListener);
                            Toast.makeText(getActivity(), "Twitter login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    logoutFromTwitter();
                }
            }
        };

        mGooglePlusSwitchOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginWithGooglePlus(new OnGooglePlusLoginListener() {
                        @Override
                        public void onSuccess() {
                            setGooglePlusAccessToken();
                            Toast.makeText(getActivity(), "Google+ login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail() {
                            removeGooglePlusAccessToken();
                            mGooglePlusSwitch.setOnCheckedChangeListener(null);
                            mGooglePlusSwitch.setChecked(false);
                            mGooglePlusSwitch.setOnCheckedChangeListener(mGooglePlusSwitchOnCheckedChangeListener);
                            Toast.makeText(getActivity(), "Google+ login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    disconnectFromGooglePlus();
                }
            }
        };

        initializeSwitches();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * Initializes the switches
     */
    protected void initializeSwitches() {
        initializeFacebookSwitch();
        initializeTwitterSwitch();
        initializeGooglePlusSwitch();
    }

    /**
     * Initializes the Facebook switch
     */
    protected void initializeFacebookSwitch() {
        mFacebookSwitch.setChecked(isFacebookConnected());
        mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
    }

    /**
     * Initializes the Twitter switch
     */
    protected void initializeTwitterSwitch() {
        mTwitterSwitch.setChecked(isTwitterConnected());
        mTwitterSwitch.setOnCheckedChangeListener(mTwitterSwitchOnCheckedChangeListener);
    }

    /**
     * Initializes the Google+ switch
     */
    protected void initializeGooglePlusSwitch() {
        mGooglePlusSwitch.setChecked(!TextUtils.isEmpty(UserManager.getGooglePlusAccessToken()));
        mGooglePlusSwitch.setOnCheckedChangeListener(mGooglePlusSwitchOnCheckedChangeListener);
    }

    @OnClick(R.id.fragment_preferences_logout_btn)
    public void logout() {
        logoutFromFacebook(new OnLogoutListener() {
            @Override
            public void onLogout() {

            }

            @Override
            public void onThinking() {

            }

            @Override
            public void onException(Throwable throwable) {

            }

            @Override
            public void onFail(String s) {

            }
        });
        logoutFromTwitter();
        logoutFromGooglePlus();
        // TODO instagram
        // TODO logout from backend

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
