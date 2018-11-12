package com.born2win.mycardviews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class PageAccueil extends AppCompatActivity {

    private static final String TAG = "PageAccueil";

    private ImageView fond_decran;
    int temps_1 = 500;
    //int temps_2 = 15000;
    RelativeLayout linearLayout;
    TextView registerText;
    Button btnCo, btnIns;
    private FirebaseAuth authentification;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil);

        authentification = FirebaseAuth.getInstance();

        //Vérification de l'état de connexion de l'utilisateur
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.d(TAG,"non connecte");
                }
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG,"connecte");
                    Intent etatIntent = new Intent(PageAccueil.this, MainActivity.class);
                    startActivity(etatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        };

        //Récupération des vues de la page d'accueil
        fond_decran = findViewById(R.id.accueil_fond);

        linearLayout = findViewById(R.id.accueil_layout);

        registerText = findViewById(R.id.accueil_registerText);

        btnCo = findViewById(R.id.accueil_btnConnexion);
        btnIns = findViewById(R.id.accueil_btnInscription);

        //transition vers la page principale
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listeIntent = new Intent(PageAccueil.this, MainActivity.class);
                startActivity(listeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                finish();
            }
        });

        //transition vers la page de connexion
        btnCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent connexionIntent = new Intent(PageAccueil.this, PageConnexion.class);
                startActivity(connexionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                finish();
            }
        });

        //transition vers la page d'inscription
        btnIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inscriptionIntent = new Intent(PageAccueil.this, PageInscription.class);
                startActivity(inscriptionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                finish();
            }
        });

        //Changement de la taille du fond d'écran après un 500ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                agrandirFond();
            }
        },temps_1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        authentification.addAuthStateListener(listener);

    }

    //Fonction permettant de faire un zoom sur l'image en fond
    public void agrandirFond () {
        //Changement de la taille

        TransitionManager.beginDelayedTransition(linearLayout);

        fond_decran.animate()
                //position du zoom final
                .scaleX((float)1.3)
                .scaleY((float)1.3)
                //durée de l'effet (ms)
                .setDuration(25000);
    }


}
