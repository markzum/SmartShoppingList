package com.appzum.smartshoppinglist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SecondTutorialFragment extends Fragment {

    ViewPager2 tutorialViewPager;

    public SecondTutorialFragment(ViewPager2 tutorialViewPager) {
        this.tutorialViewPager = tutorialViewPager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_tutorial, container, false);

        Button tutorialNextButton2 = view.findViewById(R.id.tutorialStartButton);
        tutorialNextButton2.setOnClickListener((view1 -> {
            tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem()+1);
        }));

        Button tutorialBackButton1 = view.findViewById(R.id.tutorialBackButton2);
        tutorialBackButton1.setOnClickListener((view1 -> {
            tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem()-1);
        }));

        return view;
    }
}