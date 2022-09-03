package com.appzum.smartshoppinglist;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class TutorialAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> tutorialsFragments;
    public TutorialAdapter(FragmentActivity fragmentActivity, ArrayList<Fragment> tutorialsFragments) {
        super(fragmentActivity);
        this.tutorialsFragments = tutorialsFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return(tutorialsFragments.get(position));
    }

    @Override
    public int getItemCount() {
        return tutorialsFragments.size();
    }
}
