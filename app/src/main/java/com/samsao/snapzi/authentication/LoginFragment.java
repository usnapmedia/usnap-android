package com.samsao.snapzi.authentication;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.api.entity.User;
import com.samsao.snapzi.api.entity.UserList;
import com.samsao.snapzi.api.util.CustomJsonDateTimeDeserializer;
import com.samsao.snapzi.util.KeyboardUtil;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * @author jingsilu
 * @since 2015-04-28
 */
public class LoginFragment extends Fragment implements Validator.ValidationListener {
    @Required(order = 1)
    @InjectView(R.id.fragment_login_user_name)
    public MaterialEditText mMaterialEditTextUserName;
    @Required(order = 2)
    @InjectView(R.id.fragment_login_password)
    public MaterialEditText mMaterialEditTextPassword;

    private Validator mValidator;
    private ApiService mApiService = new ApiService();
    // TODO inject me
    private UserManager mUserManager = new UserManager(new PreferenceManager());

    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mBirthday;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, v);

        mMaterialEditTextUserName.setTypeface(getFont());
        mMaterialEditTextPassword.setTypeface(getFont());
        return v;
    }

    private Typeface getFont() {
        Typeface fontText = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf");
        return fontText;
    }

    @OnClick(R.id.fragment_login_login_button)
    public void validate() {
        mValidator.validate();
    }

    public static CharSequence getName() {
        return SnapziApplication.getContext().getString(R.string.action_login);
    }

    public String getUserName() {
        return mMaterialEditTextUserName.getText().toString();
    }

    public String getPassword() {
        return mMaterialEditTextPassword.getText().toString();
    }

    @Override
    public void onValidationSucceeded() {
        mApiService.login(getUserName(), getPassword(), new retrofit.Callback<com.samsao.snapzi.api.entity.Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                KeyboardUtil.hideKeyboard(getActivity());
                Toast.makeText(getActivity(), SnapziApplication.getContext().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                mUserManager.login(getUserName(), getPassword());
                getUserInformation();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        ((MaterialEditText) failedView).setError(message);
        failedView.requestFocus();
    }

    private void getUserInformation() {
        mApiService.getUserInfo(new Callback<UserList>() {
            @Override
            public void success(UserList userList, retrofit.client.Response response) {
                List<User> userInfo = userList.getResponse();
                User user = userInfo.get(0);
                mFirstName = user.getFirstName();
                mLastName = user.getLastName();
                mEmail = user.getEmail();
                mBirthday = user.getDob();
                saveUserInPreferences(mFirstName, mLastName, mEmail, mBirthday);
            }
            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), SnapziApplication.getContext().getString(R.string.fail_to_retrieve_user_info), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * save user info into user preferences after logged in
     * @param firstName
     * @param lastName
     * @param email
     * @param birthday
     */
    private void saveUserInPreferences(String firstName, String lastName, String email, String birthday) {
        mUserManager.setFirstName(firstName);
        mUserManager.setLastName(lastName);
        mUserManager.setEmail(email);
        mUserManager.setBirthday(CustomJsonDateTimeDeserializer.getDateFormatter().parseMillis(birthday));
    }
}
