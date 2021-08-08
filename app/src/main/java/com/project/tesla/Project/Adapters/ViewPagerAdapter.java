package com.project.tesla.Project.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.project.tesla.Project.Fragments.Followersfragment;
import com.project.tesla.Project.Fragments.Followingfragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    Context context;
    int totalTabs;
    public ViewPagerAdapter(Context context, FragmentManager fm, Lifecycle lifecycle, int totalTabs) {
        super(fm,lifecycle);
        this.totalTabs=totalTabs;
        this.context=context;
    }




    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Followersfragment();
            case 1:
                return new Followingfragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
