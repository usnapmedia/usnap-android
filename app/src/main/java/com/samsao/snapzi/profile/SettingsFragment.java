package com.samsao.snapzi.profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.util.CustomJsonDateTimeDeserializer;
import com.samsao.snapzi.social.OnGooglePlusLoginListener;
import com.samsao.snapzi.social.SocialNetworkFragment;
import com.samsao.snapzi.util.KeyboardUtil;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.StringUtil;
import com.samsao.snapzi.util.UserManager;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author jingsilu
 * @since 2015-05-05
 */
public class SettingsFragment extends SocialNetworkFragment implements DatePickerDialog.OnDateSetListener  {
    private final String DATE_PICKER_DIALOG_FRAGMENT_TAG = "com.samsao.snapzi.authentication.view.SettingsFragment.DATE_PICKER_DIALOG_FRAGMENT_TAG";

    // TODO inject me
    private PreferenceManager mPreferenceManager = new PreferenceManager();
    private UserManager mUserManager = new UserManager(mPreferenceManager);

    @InjectView(R.id.fragment_settings_name_editText)
    public MaterialEditText mName;

    @InjectView(R.id.fragment_settings_birthday_editText)
    public MaterialEditText mBirthday;

    @InjectView(R.id.fragment_settings_letter_tile_container)
    public FrameLayout mLetterTileContainer;
    @InjectView(R.id.fragment_settings_letter_tile_background)
    public FrameLayout mLetterTileBackground;
    @InjectView(R.id.fragment_settings_letter_tile_letter)
    public TextView mLetterTileLetter;

    @InjectView(R.id.fragment_settings_facebook)
    public LinearLayout mFacebookBtn;
    @InjectView(R.id.fragment_settings_twitter)
    public LinearLayout mTwitterBtn;
    @InjectView(R.id.fragment_settings_gplus)
    public LinearLayout mGooglePlusBtn;

    @InjectView(R.id.fragment_settings_toolbar)
    public Toolbar mToolbar;

    private Listener mListener;

    public static SettingsFragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this, view);

        mName.setTypeface(getFont());
        //TODO first name and last name
        String name = mUserManager.getUsername();
        mName.setText(name);

        setupDatePicker();
        setupToolbar();
        // Setup tile letter
        setupTitleLetter();
        initializeSocialNetworks();
        return view;
    }

    private void showBirthdayDatePicker(String date) {
        KeyboardUtil.hideKeyboard(getActivity());
        if (getFragmentManager().findFragmentByTag(DATE_PICKER_DIALOG_FRAGMENT_TAG) == null) {
            if (!TextUtils.isEmpty(date)) {
                DateTimeFormatter dateTimeFormatter = CustomJsonDateTimeDeserializer.getDateFormatter();
                try {
                    DateTime birthDayDate = dateTimeFormatter.parseDateTime(date);
                    DatePickerFragment.newInstance(SettingsFragment.this,
                            birthDayDate.getYear(),
                            birthDayDate.getMonthOfYear()-1,
                            birthDayDate.getDayOfMonth()).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);

                } catch (IllegalArgumentException e) {
                    // error in string format
                    DatePickerFragment.newInstance(SettingsFragment.this).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);
                }
            } else {
                DatePickerFragment.newInstance(SettingsFragment.this).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);
            }
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
                    + " must implement SettingsFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.fragment_settings_facebook)
    public void toggleFacebook() {
        if (!isFacebookConnected()) {
            loginWithFacebook(new OnLoginListener() {
                @Override
                public void onLogin() {
                    setFacebookAccessToken();
                    setFacebookBtn(true);
                }

                @Override
                public void onNotAcceptingPermissions(Permission.Type type) {
                    removeFacebookAccessToken();
                    setFacebookBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook login failed: user did not accept permissions", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onThinking() {

                }

                @Override
                public void onException(Throwable throwable) {
                    removeFacebookAccessToken();
                    setFacebookBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook login failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String error) {
                    removeFacebookAccessToken();
                    setFacebookBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook login failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            logoutFromFacebook(new OnLogoutListener() {
                @Override
                public void onLogout() {
                    setFacebookBtn(false);
                }

                @Override
                public void onThinking() {

                }

                @Override
                public void onException(Throwable throwable) {
                    setFacebookBtn(true);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook logout failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String s) {
                    setFacebookBtn(true);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook logout failed: " + s, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @OnClick(R.id.fragment_settings_twitter)
    public void toggleTwitter() {
        if (!isTwitterConnected()) {
            loginWithTwitter(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> twitterSessionResult) {
                    setTwitterAccessToken();
                    setTwitterBtn(true);
                }

                @Override
                public void failure(TwitterException e) {
                    removeTwitterAccessToken();
                    setTwitterBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Twitter login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            logoutFromTwitter();
            setTwitterBtn(false);
        }
    }

    @OnClick(R.id.fragment_settings_gplus)
    public void toggleGooglePlus() {
        if (!isGooglePlusConnected()) {
            loginWithGooglePlus(new OnGooglePlusLoginListener() {
                @Override
                public void onSuccess() {
                    setGooglePlusAccessToken();
                    setGooglePlusBtn(true);
                }

                @Override
                public void onFail() {
                    removeGooglePlusAccessToken();
                    setGooglePlusBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Google+ login failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            disconnectFromGooglePlus();
            setGooglePlusBtn(false);
        }
    }

    /**
     * Setup the date picker for birthday
     */
    public void setupDatePicker(){
        Long birthdayLong = mUserManager.getBirthday();
        if (birthdayLong != null) {
            String birthday = CustomJsonDateTimeDeserializer.getDateFormatter().print(birthdayLong);
            mBirthday.setText(birthday);
        }

        mBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showBirthdayDatePicker(mBirthday.getText().toString());
                }
            }
        });
        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdayDatePicker(mBirthday.getText().toString());
            }
        });
        mBirthday.setTypeface(getFont());
    }

    /**
     * Setup the toolbar
     */
    public void setupToolbar() {
        if (mToolbar != null) {
            mListener.setSupportActionBar(mToolbar);
        }
        mListener.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListener.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mListener.getSupportActionBar().setTitle(StringUtil.getAppFontString(R.string.settings));
    }

    @OnClick(R.id.fragment_settings_save_btn)
    public void saveSettings() {
        String name = mName.getText().toString();
        String birthday = mBirthday.getText().toString();
        mUserManager.setUsername(name);
        mUserManager.setBirthday(CustomJsonDateTimeDeserializer.getDateFormatter().parseMillis(birthday));
        //TODO update user info at backend

    }

    @OnClick(R.id.fragment_settings_help_center_btn)
    public void helpCenter() {
        //TODO help center
        Toast.makeText(getActivity(),"TODO help center",Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fragment_settings_report_a_problem_btn)
    public void reportAProblem() {
        //TODO report a problem
        Toast.makeText(getActivity(),"TODO report a problem",Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fragment_settings_log_out_btn)
    public void logout() {
        mUserManager.logout();
        //We must remove the text before set hint.
        mName.setText("");
        mBirthday.setText("");
        mName.setHint("Name");
        mBirthday.setHint("Birthday");
    }

    /**
     * Initializes the social network buttons
     */
    protected void initializeSocialNetworks() {
        setFacebookBtn(isFacebookConnected());
        setTwitterBtn(isTwitterConnected());
        setGooglePlusBtn(isGooglePlusConnected());
    }

    /**
     * Set the Facebook Button
     */
    protected void setFacebookBtn(boolean connected) {
        if (connected) {
            enableSocialNetworkBtn(mFacebookBtn);
        } else {
            disableSocialNetworkBtn(mFacebookBtn);
        }
    }

    /**
     * Set the Twitter Button
     */
    protected void setTwitterBtn(boolean connected) {
        if (connected) {
            enableSocialNetworkBtn(mTwitterBtn);
        } else {
            disableSocialNetworkBtn(mTwitterBtn);
        }
    }

    /**
     * Set the Google+ Button
     */
    protected void setGooglePlusBtn(boolean connected) {
        if (connected) {
            enableSocialNetworkBtn(mGooglePlusBtn);
        } else {
            disableSocialNetworkBtn(mGooglePlusBtn);
        }
    }

    /**
     * Enables a social network button
     *
     * @param linearLayout
     */
    public void enableSocialNetworkBtn(LinearLayout linearLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            linearLayout.setBackground(getActivity().getDrawable(R.drawable.sel_app_btn));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            linearLayout.setBackground(getResources().getDrawable(R.drawable.sel_app_btn));
        } else {
            //noinspection deprecation
            linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_app_btn));
        }
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ((TextView)linearLayout.getChildAt(i)).setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    /**
     * Disables a social network button
     *
     * @param linearLayout
     */
    public void disableSocialNetworkBtn(LinearLayout linearLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            linearLayout.setBackground(getActivity().getDrawable(R.drawable.sel_app_btn_disabled));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            linearLayout.setBackground(getResources().getDrawable(R.drawable.sel_app_btn_disabled));
        } else {
            //noinspection deprecation
            linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_app_btn_disabled));
        }
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ((TextView)linearLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.medium_gray));
        }
    }

    /**
     * Setup title letter
     */
    private void setupTitleLetter() {
        mLetterTileContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Adapt tile background size to container
                int letterTileContainerShortestSideSize =
                        Math.max(mLetterTileContainer.getWidth(), mLetterTileContainer.getHeight());
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
                    mLetterTileLetter.setText("pelvish");
                }
            }
        });
    }

    private Typeface getFont() {
        Typeface fontText = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf");
        return fontText;
    }

    public interface Listener {
        ActionBar getSupportActionBar();
        void setSupportActionBar(Toolbar toolbar);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
        mBirthday.setText(CustomJsonDateTimeDeserializer.getDateFormatter().print(dateTime));
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Integer mYear;
        private Integer mMonthOfYear;
        private Integer mDayOfMonth;
        private WeakReference<DatePickerDialog.OnDateSetListener> mListener;

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            DatePickerFragment fragment = DatePickerFragment.newInstance(listener);
            fragment.setYear(year);
            fragment.setMonthOfYear(monthOfYear);
            fragment.setDayOfMonth(dayOfMonth);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default values for the picker
            if (mYear == null || mMonthOfYear == null || mDayOfMonth == null) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonthOfYear = c.get(Calendar.MONTH);
                mDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            }

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, mYear, mMonthOfYear, mDayOfMonth);
            datePickerDialog.getDatePicker().setMaxDate(DateTime.now().getMillis());
            return datePickerDialog;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            if (mListener != null) {
                mListener.clear();
                mListener = null;
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            KeyboardUtil.hideKeyboard(getActivity());
        }

        public void setYear(Integer year) {
            mYear = year;
        }

        public void setMonthOfYear(Integer monthOfYear) {
            mMonthOfYear = monthOfYear;
        }

        public void setDayOfMonth(Integer dayOfMonth) {
            mDayOfMonth = dayOfMonth;
        }

        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            mListener = new WeakReference<>(listener);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (mListener != null && mListener.get() != null) {
                mListener.get().onDateSet(view, year, monthOfYear, dayOfMonth);
            }
        }
    }

}
