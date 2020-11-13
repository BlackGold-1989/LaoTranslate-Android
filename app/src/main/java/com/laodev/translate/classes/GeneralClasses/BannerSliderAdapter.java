package com.laodev.translate.classes.GeneralClasses;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.laodev.translate.utils.Constants;

public class BannerSliderAdapter extends FragmentPagerAdapter {

    public BannerSliderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return Constants.getAdversCount();
    }

    @Override
    public Fragment getItem(int position) {
        return SwipeFragment.newInstance(position);
    }
}
