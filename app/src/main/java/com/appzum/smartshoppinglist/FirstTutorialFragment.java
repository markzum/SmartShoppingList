package com.appzum.smartshoppinglist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FirstTutorialFragment extends Fragment {

    ViewPager2 tutorialViewPager;

    public FirstTutorialFragment(ViewPager2 tutorialViewPager) {
        this.tutorialViewPager = tutorialViewPager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_tutorial, container, false);

        Button tutorialNextButton1 = view.findViewById(R.id.tutorialStartButton);

        tutorialNextButton1.setOnClickListener((view1 -> {
            tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem()+1);
        }));

        return view;
    }

}