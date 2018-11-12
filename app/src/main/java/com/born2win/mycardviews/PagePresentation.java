package com.born2win.mycardviews;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PagePresentation extends AppCompatActivity {

    private static final String TAG = "PagePresentation";

    ViewPager viewPager;
    private LinearLayout layout;

    SlideAdapter adapter;

    TextView[] points;

    private Button btnS, btnC;

    private int pageAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_presentation);

        viewPager = findViewById(R.id.intro_viewpager);
        layout = findViewById(R.id.intro_bp);

        adapter = new SlideAdapter(this);
        viewPager.setAdapter(adapter);

        btnC = findViewById(R.id.intro_credit);
        btnS = findViewById(R.id.intro_suivant);

        afficherPoints(0);

        viewPager.addOnPageChangeListener(listener);

        final String credit_freepik, credit_flaticon, credit_cc;

        credit_freepik = getString(R.string.credit_freepik);

        credit_flaticon = getString(R.string.credit_flaticon);

        credit_cc = getString(R.string.credit_cc);
        //action des boutons
        //btnP.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        viewPager.setCurrentItem(pageAct - 1);
        //    }
        //});

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageAct == points.length-1) {
                    Intent lancementIntent = new Intent(PagePresentation.this, PageAccueil.class);
                    startActivity(lancementIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(PagePresentation.this)
                        .setTitle("Sources des images")
                        .setMessage("Icônes faîtes par : " + credit_freepik + "\nsur " + credit_flaticon + " licencié par " + credit_cc)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "Sources des images.");
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

        /*
        *
        * new android.app.AlertDialog.Builder(this)
                    .setTitle("Vignette vide")
                    //.setMessage("Si vous ne choissisez pas d'image, une vignette par défaut sera assignée à votre article.")
                    .setMessage("Veuillez choisir une image.")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "Changement de la vignette");
                        }
                    })
                    .show();
        *
        *
        *
        * */
    }

    public void afficherPoints(int position) {

        points = new TextView[4];
        layout.removeAllViews();

        for (int i = 0; i < points.length ; i++) {
            points[i] = new TextView(this);
            points[i].setText(Html.fromHtml("&#8226"));
            points[i].setTextSize(45);
            points[i].setTextColor(getResources().getColor(R.color.pointsBlanc));

            layout.addView(points[i]);
        }

        if(points.length > 0) {

            points[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            points[position].offsetTopAndBottom(30);

        }

    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            afficherPoints(position);
            pageAct = position;

            /*
            if (position == 0) {
                //btnS.setEnabled(true);
                //btnP.setEnabled(false);
                //btnP.setVisibility(View.INVISIBLE);
                //btnS.setText(getString(R.string.btnSuiv));
                //btnP.setText("");
            }
            */
            if (position == 0 || position == 1 || position == points.length-1) {
                btnC.setEnabled(true);
                //btnP.setEnabled(false);
                btnC.setVisibility(View.VISIBLE);
                //btnS.setText(getString(R.string.btnSuiv));
                //btnP.setText("");
            }

            if (position == points.length - 1) {
                btnS.setEnabled(true);
                btnS.setVisibility(View.VISIBLE);
                //btnP.setEnabled(true);
                //btnP.setVisibility(View.VISIBLE);
                //btnS.setText(getString(R.string.btnFin));
                //btnP.setText(getString(R.string.btnPrec));
            }

            else {
                btnS.setVisibility(View.INVISIBLE);
                btnC.setVisibility(View.INVISIBLE);
                //btnS.setEnabled(true);
                //btnP.setEnabled(true);
                //btnP.setVisibility(View.VISIBLE);
                //btnS.setText(getString(R.string.btnSuiv));
                //btnP.setText(getString(R.string.btnPrec));
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
