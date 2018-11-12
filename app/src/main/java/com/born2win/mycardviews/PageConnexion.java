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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PageConnexion extends AppCompatActivity {

    //Variables

    private static final String TAG = "PageConnexion";

    private TextInputLayout mailLayout, mdpLayout;
    private LinearLayout chargement;
    private ConstraintLayout connexion;
    private EditText mail, mdp;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    TextView mdpOublie, nouveau;
    Button validation;

    android.support.v7.widget.Toolbar bar_daction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Utilisateurs");
        databaseReference.keepSynced(true);

        mdpOublie = findViewById(R.id.connexion_mdp_oublie);
        nouveau = findViewById(R.id.connexion_nouveau);

        mail = findViewById(R.id.connexion_mail);
        mailLayout = findViewById(R.id.connexion_mail_layout);

        mdp = findViewById(R.id.connexion_mdp);
        mdpLayout = findViewById(R.id.connexion_mdp_layout);
        validation = findViewById(R.id.connexion_bouton);

        bar_daction = findViewById(R.id.connexion_toolbar);
        setSupportActionBar(bar_daction);

        connexion = findViewById(R.id.connexion_layout);
        chargement = findViewById(R.id.connexion_chargement);

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
                startActivity(new Intent(getApplicationContext(),PageAccueil.class));
                //overridePendingTransition(0,0);
                finish();
            }
        });

        mdpOublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PageConnexion.this, "La récupération de mot de passe n'est pas encore disponible", Toast.LENGTH_SHORT).show();
            }
        });

        nouveau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageConnexion.this, PageInscription.class);
                startActivity(intent);
                finish();
            }
        });

        mail.addTextChangedListener(new MyTextWatcher(mail));
        mdp.addTextChangedListener(new MyTextWatcher(mdp));

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifConnexion();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_transition_gauche_e, R.anim.slide_transition_droite_s);
    }



    //FONCTIONS Personnalisées

    private void verifConnexion() {

        if (validerMail() && validerMdp()) {
            validerConnexion();
        }

    }

    private void validerConnexion() {

        //float alp = 0.7F;
        connexion.setBackgroundColor(getResources().getColor(R.color.fondBlanc));
        //connexion.setAlpha(alp);
        chargement.setVisibility(View.VISIBLE);

        String mailNv = mail.getText().toString().trim();
        String mdpNv = mdp.getText().toString().trim();

        auth.signInWithEmailAndPassword(mailNv, mdpNv)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (auth.getCurrentUser() != null && task.isSuccessful()) {
                            // Si la connexion marche, on procède à la personalisation de son compte
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(PageConnexion.this, "Connexion réussie",Toast.LENGTH_SHORT).show();
                            //String uid = auth.getCurrentUser().getUid();


                            verifUtilisateur();

                        }
                        // si la connexion a échouée, on affiche un message d'erreur
                        if (!task.isSuccessful()) {
                            try
                            {
                                if (task.getException() != null) {
                                    throw task.getException();
                                }
                            }
                            // si l'adresse mail n'existe pas dans Firebase
                            catch (FirebaseAuthInvalidUserException invalidEmail)
                            {
                                Log.d(TAG, "onComplete: invalid_email");

                                Toast.makeText(PageConnexion.this, "Aucun compte n'est lié à cette adresse mail", Toast.LENGTH_SHORT).show();
                            }
                            // si le mot de passe ne correspond pas à l'adresse mail
                            catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                            {
                                Log.d(TAG, "onComplete: wrong_password");

                                Toast.makeText(PageConnexion.this, "Mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                            }
                            // si l'adresse mail est déjà utilisée pour un autre compte
                            catch (FirebaseAuthUserCollisionException usedEmail)
                            {
                                Log.d(TAG, "onComplete: already_used_email");

                                Toast.makeText(PageConnexion.this, "Cette adresse mail est déjà lié à un compte", Toast.LENGTH_SHORT).show();
                            }
                            // si l'exception n'est pas déjà gérée
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                                Toast.makeText(PageConnexion.this, "Une erreur est survenue :\n"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                        chargement.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void verifUtilisateur() {

        if (auth.getCurrentUser() != null) {
            final String util_id = auth.getCurrentUser().getUid();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(util_id)) {
                        Intent inscriptionIntent = new Intent(PageConnexion.this, MainActivity.class);
                        inscriptionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(inscriptionIntent);
                        overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                        finish();
                    } else {
                        Toast.makeText(PageConnexion.this,"Veuillez paramétrer votre compte", Toast.LENGTH_SHORT).show();
                        Intent profilIntent = new Intent(PageConnexion.this, PageProfilEdition.class);
                        profilIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(profilIntent);
                        overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "UID nul.", Toast.LENGTH_SHORT).show();
        }
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
