package com.samsao.snapzi.authentication.view;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.api.exception.ApiException;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO inject me
    public ApiService mApiService = new ApiService();
    public UserManager mUserManager = new UserManager(new PreferenceManager());

    @InjectView(R.id.fragment_login_login_view)
    public LoginView mLoginView;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);
        mLoginView.setLoginCallback(new LoginView.LoginCallback() {
            @Override
            public void onLoginValidated() {
                final String username = mLoginView.getLoginUsername();
                final String password = mLoginView.getLoginPassword();
                mApiService.login(username,
                        password,
                        new Callback<Response>() {
                            @Override
                            public void success(Response response, retrofit.client.Response response2) {
                                // TODO
                                try {
                                    mUserManager.login(username, password);
                                } catch (IllegalArgumentException e) {
                                    showError("Login error");
                                }
                                showError("Login successful");
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                if (retrofitError.getCause() instanceof ApiException) {
                                    showError(((ApiException)retrofitError.getCause()).getMessage());
                                }
                                // TODO
                            }
                        });
            }

            @Override
            public void onResetPasswordClick() {
                // TODO no route in backend yet
                showError("TODO: waiting for route in backend");
            }
        });
        mLoginView.setSignupCallback(new LoginView.SignupCallback() {
            @Override
            public void onSignupValidated() {
                // TODO route needs to be fixed in backend
                showError("TODO: waiting for route in backend");
            }

            @Override
            public void showBirthdayDatePicker() {
                // TODO
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * Show an error in a Toast
     * @param error
     */
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }
}
