package com.appzum.smartshoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        ViewPager2 tutorialViewPager = findViewById(R.id.tutorialViewPager);

        ArrayList<Fragment> tutorialsFragments = new ArrayList<>();
        tutorialsFragments.add(new FirstTutorialFragment(tutorialViewPager));
        tutorialsFragments.add(new SecondTutorialFragment(tutorialViewPager));
        tutorialsFragments.add(new ThirdTutorialFragment(tutorialViewPager, this));
        TutorialAdapter tutorialAdapter = new TutorialAdapter(this, tutorialsFragments);

        tutorialViewPager.setAdapter(tutorialAdapter);
    }
}