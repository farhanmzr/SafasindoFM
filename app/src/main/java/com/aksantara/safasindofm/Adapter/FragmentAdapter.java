package com.aksantara.safasindofm.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aksantara.safasindofm.Fragment.BeritaFragment;
import com.aksantara.safasindofm.Fragment.FacebookFragment;
import com.aksantara.safasindofm.Fragment.InstagramFragment;
import com.aksantara.safasindofm.Fragment.YoutubeFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    private final int numOfTabs;

    public FragmentAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                fragment = new BeritaFragment();
                return fragment;

            case 1:
                fragment = new InstagramFragment();
                return fragment;

            case 2:
                fragment = new FacebookFragment();
                return fragment;

            case 3:
                fragment = new YoutubeFragment();
                return fragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}

