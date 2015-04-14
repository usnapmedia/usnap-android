package com.samsao.snapzi.authentication.view;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.api.exception.ApiException;
import com.samsao.snapzi.util.LocaleUtil;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    // TODO inject me
    public ApiService mApiService = new ApiService();
    public UserManager mUserManager = new UserManager(new PreferenceManager());

    private final String DATE_PICKER_DIALOG_FRAGMENT_TAG = "com.samsao.snapzi.authentication.view.LoginFragment.DATE_PICKER_DIALOG_FRAGMENT_TAG";
    private final String DATE_FORMAT = "yyyy-MM-dd";

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
                                    showError((retrofitError.getCause()).getMessage());
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
            public void showBirthdayDatePicker(String date) {
                if (!TextUtils.isEmpty(date)) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, LocaleUtil.getLocale());
                    try {
                        cal.setTime(simpleDateFormat.parse(date));
                        DatePickerFragment.newInstance(LoginFragment.this,
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);
                    } catch (ParseException e) {
                        DatePickerFragment.newInstance(LoginFragment.this).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);
                    }
                } else {
                    DatePickerFragment.newInstance(LoginFragment.this).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);
                }
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
     *
     * @param error
     */
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

    /**
     * DatePicker DialogFragment
     */
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

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, mYear, mMonthOfYear, mDayOfMonth);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener.clear();
            mListener = null;
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
