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
import com.samsao.snapzi.util.KeyboardUtil;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
                // TODO string resource
                KeyboardUtil.hideKeyboard(getActivity());
                Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                // TODO retrieve account info and add them to preferences
                mUserManager.login(getUserName(), getPassword());
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
}
