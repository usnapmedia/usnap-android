package com.samsao.snapzi.authentication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.util.KeyboardUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author jingsilu
 * @since 2015-04-28
 */
public class RegisterFragment extends Fragment implements Validator.ValidationListener, DatePickerDialog.OnDateSetListener{
    @Required(order=1)
    @InjectView(R.id.fragment_register_first_name)
    public MaterialEditText mMaterialEditTextFirstName;

    @Required(order=2)
    @InjectView(R.id.fragment_register_last_name)
    public MaterialEditText mMaterialEditTextLastName;

    @Required(order=3)
    @Email(order=4)
    @InjectView(R.id.fragment_register_email)
    public MaterialEditText mMaterialEditTextEmail;

    @Required(order=5)
    @InjectView(R.id.fragment_register_user_name)
    public MaterialEditText mMaterialEditTextUserName;

    @Required(order=6)
    @Password(order=7)
    @InjectView(R.id.fragment_register_password)
    public MaterialEditText mMaterialEditTextPassword;

    @Required(order=8)
    @InjectView(R.id.fragment_register_birthday)
    public MaterialEditText mMaterialEditTextBirthday;

    @InjectView(R.id.fragment_register_sign_up_button)
    public Button mButton;

    private ApiService mApiService = new ApiService();

    private Validator mValidator;

    private DateTime mBirthDayDate;

    private final String DATE_PICKER_DIALOG_FRAGMENT_TAG = "com.samsao.snapzi.authentication.view.LoginFragment.DATE_PICKER_DIALOG_FRAGMENT_TAG";
    private final String DATE_FORMAT = "yyyy-MM-dd";

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register,container,false);
        ButterKnife.inject(this, v);
        mButton.setBackgroundColor(getResources().getColor(R.color.fan_page_tab_blue));
        mMaterialEditTextBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showBirthdayDatePicker(mMaterialEditTextBirthday.getText().toString());
                }
            }
        });
        mMaterialEditTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdayDatePicker(mMaterialEditTextBirthday.getText().toString());
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateSignupInformation();
            }
        });

        return v;
    }

    private void showBirthdayDatePicker(String date) {
        KeyboardUtil.hideKeyboard(getActivity());
        if (getFragmentManager().findFragmentByTag(DATE_PICKER_DIALOG_FRAGMENT_TAG) == null) {
            if (!TextUtils.isEmpty(date)) {
                DateTimeFormatter dateTimeFormatter = getDateFormatter();
                try {
                    mBirthDayDate = dateTimeFormatter.parseDateTime(date);
                    DatePickerFragment.newInstance(RegisterFragment.this,
                            mBirthDayDate.getYear(),
                            mBirthDayDate.getMonthOfYear(),
                            mBirthDayDate.getDayOfMonth()).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);

                } catch (IllegalArgumentException e) {
                    // error in string format
                    DatePickerFragment.newInstance(RegisterFragment.this).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);
                }
            } else {
                DatePickerFragment.newInstance(RegisterFragment.this).show(getFragmentManager(), DATE_PICKER_DIALOG_FRAGMENT_TAG);
            }
        }
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


    @Override
    public void onValidationSucceeded() {
        Toast.makeText(getActivity(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        mApiService.register(getUserName(),getPassword(),getEmail(),getFirstName(),getLastName(),getBirthday(),new retrofit.Callback<com.samsao.snapzi.api.entity.Response>() {
            @Override
            public void success(com.samsao.snapzi.api.entity.Response response, Response response2) {
                Toast.makeText(getActivity(), "Registration Success!", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Registration Failure!", Toast.LENGTH_SHORT).show();
                AuthenticationActivity.start(getActivity());
                getActivity().finish();
            }
        });
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        Toast.makeText(getActivity(), "Registration invalid! Please try it again!", Toast.LENGTH_LONG).show();
        String message = failedRule.getFailureMessage();
        ((MaterialEditText)failedView).setError(message);
        failedView.requestFocus();
    }

    /**
     * validate the sign up information when the sign up button is clicked.
     */
    public void validateSignupInformation() {
        mValidator.validate();
    }


    /**
     * Returns the date formatter for birthday
     * @return
     */
    private DateTimeFormatter getDateFormatter() {
        return DateTimeFormat.forPattern(DATE_FORMAT);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear += 1;
        mMaterialEditTextBirthday.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

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
            if (mListener != null) {
                mListener.clear();
                mListener = null;
            }
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
