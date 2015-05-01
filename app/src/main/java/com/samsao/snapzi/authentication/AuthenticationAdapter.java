package com.samsao.snapzi.authentication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * @author jingsilu
 * @since 2015-04-28
 */
public class AuthenticationAdapter extends FragmentStatePagerAdapter{
    public AuthenticationAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RegisterFragment.newInstance();
            case 1:
                return LoginFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return RegisterFragment.getName();
            case 1:
                return LoginFragment.getName();
            default:
                return "";
        }
    }
}
