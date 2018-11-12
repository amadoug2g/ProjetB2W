package com.born2win.mycardviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PageInscription extends AppCompatActivity {

    private static final String TAG = "PageInscription";

    Button valider;

    private TextInputLayout mailLayout, mdpLayout, nomLayout;
    private ConstraintLayout inscription;
    private LinearLayout chargement;
    private EditText mail, mdp, nom;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    android.support.v7.widget.Toolbar bar_daction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Utilisateurs");

        valider = findViewById(R.id.inscription_validation);

        mail = findViewById(R.id.inscription_mail);
        mailLayout = findViewById(R.id.inscription_mail_layout);

        mdp = findViewById(R.id.inscription_mdp);
        mdpLayout = findViewById(R.id.inscription_mdp_layout);

        /*
        conf = findViewById(R.id.inscription_conf);
        confLayout = findViewById(R.id.inscription_conf_layout);
        */
        nom = findViewById(R.id.inscription_nom);
        nomLayout = findViewById(R.id.inscription_nom_layout);

        bar_daction = findViewById(R.id.inscription_toolbar);
        setSupportActionBar(bar_daction);

        inscription = findViewById(R.id.inscription_layout);
        chargement = findViewById(R.id.inscription_chargement);

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
                startActivity(new Intent(PageInscription.this,PageAccueil.class));
                overridePendingTransition(R.anim.slide_transition_gauche_e, R.anim.slide_transition_droite_s);
                finish();
            }
        });

        nom.addTextChangedListener(new MyTextWatcher(nom));
        mail.addTextChangedListener(new MyTextWatcher(mail));
        mdp.addTextChangedListener(new MyTextWatcher(mdp));

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifInscription();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_transition_gauche_e, R.anim.slide_transition_droite_s);
    }

    //FONCTIONS Personnalisées

    private void verifInscription() {

        if (validerMail() && validerMdp() && validerNom()) {
            validerInscription();
        }

    }

    private void validerInscription() {

        float alp = 0.7F;
        inscription.setBackgroundColor(getResources().getColor(R.color.fondBlanc));
        inscription.setAlpha(alp);
        chargement.setVisibility(View.VISIBLE);

        String mailNv = mail.getText().toString().trim();
        String mdpNv = mdp.getText().toString().trim();
        final String nomNv = nom.getText().toString();

        Log.d(TAG, "création de compte");
            auth.createUserWithEmailAndPassword(mailNv, mdpNv)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() && auth.getCurrentUser() != null) {
                                // Connexion réussie
                                Log.d(TAG, "createUserWithEmail:succès");
                                Toast.makeText(PageInscription.this, "Bienvenue : "+nomNv,Toast.LENGTH_SHORT).show();

                                String uid = auth.getCurrentUser().getUid();

                                DatabaseReference utilisateurConnecte = databaseReference.child(uid);

                                utilisateurConnecte.child("utilisateur").setValue(nomNv);
                                utilisateurConnecte.child("image").setValue("aucune");
                                chargement.setVisibility(View.INVISIBLE);
                            }
                            // si la connexion a échouée, on affiche un message d'erreur
                            if (!task.isSuccessful()) {
                                try
                                {
                                    if (task.getException() != null) {
                                        throw task.getException();
                                    }
                                }
                                catch (FirebaseAuthWeakPasswordException mdpFaible)
                                {
                                    Log.d(TAG, "onComplete: faible_mdp");

                                    Toast.makeText(PageInscription.this, "Mot de passe trop faible",Toast.LENGTH_SHORT).show();
                                }
                                // si l'adresse mail est déjà utilisée pour un autre compte
                                catch (FirebaseAuthUserCollisionException mailUtilise)
                                {
                                    Log.d(TAG, "onComplete: mail_deja_utilise");

                                    Toast.makeText(PageInscription.this, "Cette adresse mail est déjà lié à un compte", Toast.LENGTH_SHORT).show();
                                }
                                // si l'exception n'est pas déjà gérée
                                catch (Exception e)
                                {
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                    Toast.makeText(PageInscription.this, "Une erreur est survenue :\n"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            chargement.setVisibility(View.INVISIBLE);
                        }
                    });
    }

    private boolean validerNom() {
        String nom_ = nom.getText().toString();
        if (nom_.isEmpty()) {
            nomLayout.setError(getString(R.string.nom_requis));
            return false;
        } else {
            nomLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validerMail() {
        String email = mail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            mailLayout.setError(getString(R.string.connexion_hint_mail));
            requestFocus(mail);
            return false;
        } else {
            mailLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validerMdp() {
        if (mdp.getText().toString().trim().isEmpty()) {
            mdpLayout.setError(getString(R.string.connexion_hint_mdp));
            requestFocus(mdp);
            return false;
        } else {
            mdpLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
    private void onClickChargement() {

    }
    */

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    //CLASSES

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.connexion_mail:
                    validerMail();
                    break;
                case R.id.connexion_mdp:
                    validerMdp();
                    break;
            }
        }
    }
}
