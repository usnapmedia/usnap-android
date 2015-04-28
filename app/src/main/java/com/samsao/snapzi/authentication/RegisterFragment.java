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
import com.samsao.snapzi.camera.SelectMediaActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

/**
 * @author jingsilu
 * @since 2015-04-28
 */
public class RegisterFragment extends Fragment{
    @InjectView(R.id.fragment_register_first_name)
    public MaterialEditText mMaterialEditTextFirstName;
    @InjectView(R.id.fragment_register_last_name)
    public MaterialEditText mMaterialEditTextLastName;
    @InjectView(R.id.fragment_register_email)
    public MaterialEditText mMaterialEditTextEmail;
    @InjectView(R.id.fragment_register_user_name)
    public MaterialEditText mMaterialEditTextUserName;
    @InjectView(R.id.fragment_register_password)
    public MaterialEditText mMaterialEditTextPassword;
    @InjectView(R.id.fragment_register_birthday)
    public MaterialEditText mMaterialEditTextBirthday;
    @InjectView(R.id.fragment_register_sign_up_button)
    public Button mButton;

    private ApiService mApiService = new ApiService();

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register,container,false);
        ButterKnife.inject(this, v);
        //mButton = (Button) v.findViewById(R.id.fragment_register_sign_up_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApiService.register(getUserName(),getPassword(),getEmail(),getFirstName(),getLastName(),getBirthday(),new retrofit.Callback<com.samsao.snapzi.api.entity.Response>() {
                    @Override
                    public void success(Response response, retrofit.client.Response response2) {
                        Toast.makeText(getActivity(), "Registration Success!", Toast.LENGTH_SHORT).show();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity(), "Registration Failure!", Toast.LENGTH_SHORT).show();
                        SelectMediaActivity.start(getActivity());
                        getActivity().finish();
                    }
                });
            }
        });
        return v;
    }

    public static CharSequence getName() {
        return "REGISTER";
    }

    public String getFirstName() {
        return mMaterialEditTextFirstName.getText().toString();
    }

    public String getLastName() {
        return mMaterialEditTextLastName.getText().toString();
    }

    public String getEmail() {
        return mMaterialEditTextEmail.getText().toString();
    }

    public String getUserName() {
        return mMaterialEditTextUserName.getText().toString();
    }

    public String getPassword() {
        return mMaterialEditTextPassword.getText().toString();
    }

    public String getBirthday() {
        return mMaterialEditTextBirthday.getText().toString();
    }




}
