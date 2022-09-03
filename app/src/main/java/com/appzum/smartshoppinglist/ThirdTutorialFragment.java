package com.appzum.smartshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;


public class ThirdTutorialFragment extends Fragment {

    ViewPager2 tutorialViewPager;
    Context cxt;

    public ThirdTutorialFragment(ViewPager2 tutorialViewPager, Context cxt) {
        this.tutorialViewPager = tutorialViewPager;
        this.cxt = cxt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third_tutorial, container, false);

        Button tutorialStartButton = view.findViewById(R.id.tutorialStartButton);
        tutorialStartButton.setOnClickListener((view1 -> {
            Intent intent = new Intent(cxt, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }));

        Button tutorialBackButton2 = view.findViewById(R.id.tutorialBackButton2);
        tutorialBackButton2.setOnClickListener((view1 -> {
            tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem()-1);
        }));

        return view;
    }
}