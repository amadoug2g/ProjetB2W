package com.born2win.mycardviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PageLogo extends AppCompatActivity {
    //Déclaration de variables
    //Temps passé sur l'écran d'introduction (en millisecondes)
    int temps =  1500;

    @Override
    //Lancement de cette fonction au début de l'application
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ecran);

        //Fonction permettant de changer d'écran après un certain temps
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                //si l'application a déjà été lancée au moins une fois
                if(pref.getBoolean("activity_executed", false)){
                    //On passe à l'écran d'accueil
                    Intent intent = new Intent(PageLogo.this, PageAccueil.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    //suppression de l'animation
                    overridePendingTransition(0,0);

                    //Fermeture de cette activité
                    finish();
                    //si l'application n'a jamais été lancée
                } else {
                    //On passe à l'écran de présentation des fonctionnalités
                    SharedPreferences.Editor ed = pref.edit();
                    ed.putBoolean("activity_executed", true);
                    ed.apply();

                    //Passage de l'écran d'introduction à celui des présentations
                    Intent intent = new Intent(PageLogo.this, PagePresentation.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    //suppression de l'animation
                    overridePendingTransition(0,0);

                    //Fermeture de cette activité
                    finish();
                }
            }
        },temps);
    }
}
