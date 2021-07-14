package com.example.amusic.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.amusic.CurrentPlaylist;
import com.example.amusic.FavouriteActivity;
import com.example.amusic.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    private final List<String> mfragmentTitleList = new ArrayList<>();
    private final Context mContext;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    public SectionsPagerAdapter(Context context, FragmentManager fm,int NumOfTabs) {
        super(fm);
        mNumOfTabs = NumOfTabs;
        mContext = context;
    }



    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return mFragmentList.get(position);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mfragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment,String title){
        mFragmentList.add(fragment);
        mfragmentTitleList.add(title);

    }
}