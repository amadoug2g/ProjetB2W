package com.born2win.mycardviews.Descriptions;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.born2win.mycardviews.MainActivity;
import com.born2win.mycardviews.PageAccueil;
import com.born2win.mycardviews.R;

/**
 * Créé par Amadougaye Cissé le 30/07/2018
 */

public class Page_desc1 extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionPageAdapter sectionPageAdapter;

    private ViewPager viewPager;

    android.support.v7.widget.Toolbar bar_daction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_description);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        bar_daction = findViewById(R.id.description_toolbar);
        setSupportActionBar(bar_daction);

        //afficher le bouton retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setElevation(25);
        }

        bar_daction.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }

    public void setupViewPager (ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Desc1_bio_fragment(),"Biographie");
        adapter.addFragment(new Desc1_itw_fragment(),"Interviews");
        adapter.addFragment(new Desc1_news_fragment(),"News");
        viewPager.setAdapter(adapter);
    }
}
