package com.born2win.mycardviews.Descriptions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.born2win.mycardviews.R;

/**
 * Créé par Amadougaye Cissé le 30/07/2018
 */

public class Desc1_news_fragment extends android.support.v4.app.Fragment {
    private static final String TAG = "Onglet3";

    private Button bouton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.desc1_news, container, false);
        bouton = view.findViewById(R.id.bouton_3);

        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "PAGE 3 : NEWS", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
