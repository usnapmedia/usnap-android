package com.samsao.snapzi.authentication;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Response;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * @author jingsilu
 * @since 2015-04-28
 */
public class LoginFragment extends Fragment{
    @InjectView(R.id.fragment_login_user_name)
    public MaterialEditText mMaterialEditTextUserName;
    @InjectView(R.id.fragment_login_password)
    public MaterialEditText mMaterialEditTextPassword;
    @InjectView(R.id.fragment_login_login_button)
    public Button mButton;

    private ApiService mApiService = new ApiService();

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, v);
        mButton.setBackgroundColor(getResources().getColor(R.color.fan_page_tab_blue));
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApiService.login(getUserName(), getPassword(), new retrofit.Callback<com.samsao.snapzi.api.entity.Response>() {
                    @Override
                    public void success(Response response, retrofit.client.Response response2) {
                        Toast.makeText(getActivity(), "Login Success!", Toast.LENGTH_SHORT).show();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity(), "Login Failure!", Toast.LENGTH_SHORT).show();
                        AuthenticationActivity.start(getActivity());
                        getActivity().finish();
                    }
                });
            }
        });
        return v;
    }

    public static CharSequence getName() {
        return "LOG IN";
    }

    public String getUserName() {
        return mMaterialEditTextUserName.getText().toString();
    }

    public String getPassword() {
        return mMaterialEditTextPassword.getText().toString();
    }

}
