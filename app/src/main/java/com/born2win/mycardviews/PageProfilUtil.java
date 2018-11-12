package com.born2win.mycardviews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PageProfilUtil extends AppCompatActivity {

    private static final String TAG = "PageProfilUtil";

    private TextView nomUtild, nomUtil, prenomUtil;
    private ImageView image;

    private DatabaseReference databaseReference;
    private FirebaseDatabase donnees;
    private FirebaseAuth auth;
    private FirebaseUser utilisateur;
    private DatabaseReference baseDeDonneesUtil;

    android.support.v7.widget.Toolbar bar_daction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_util);

        bar_daction = findViewById(R.id.profilUtil_toolbar);
        setSupportActionBar(bar_daction);

        auth = FirebaseAuth.getInstance();

        utilisateur = auth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(auth.getCurrentUser().getUid());
        baseDeDonneesUtil = FirebaseDatabase.getInstance().getReference().child("Utilisateurs");

        nomUtild = findViewById(R.id.profilUtil_nomUtilisateur);
        //nomUtil = findViewById(R.id.profilUtil_nom);
        //prenomUtil = findViewById(R.id.profilUtil_prenom);
        image = findViewById(R.id.profilUtil_image);

        image.setImageResource(R.drawable.baseline_account_circle_black_48dp);

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
                overridePendingTransition(0,0);
                finish();
            }
        });

        if (auth.getCurrentUser() != null) {

            afficherUtilisateur();
        }

        //databaseReference.child(auth.getCurrentUser().getUid())
    }

    private void afficherUtilisateur() {


        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nom = dataSnapshot.child("utilisateur").getValue().toString();
                nomUtild.setText(nom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(listener);


    }

    private void confirmationSuppression() {
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous supprimer votre compte ?")
                .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Suppression de l'utilisateur");
                        supprimerUtilisteur();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Suppression annulée");
                    }
                })
                .show();
    }

    private void supprimerUtilisteur() {
        if (auth.getCurrentUser() != null) {
            Intent articleIntent = new Intent(PageProfilUtil.this, MainActivity.class);
            articleIntent.putExtra("suppression",true);
            startActivity(articleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Affichage des icônes de la barre d'action sur la page de profil
        getMenuInflater().inflate(R.menu.menu_page_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Toast.makeText(getApplicationContext(),"L'édition de profil n'est pas encore disponible", Toast.LENGTH_SHORT).show();

        }

        if (item.getItemId() == R.id.action_supprime) {
            Toast.makeText(getApplicationContext(),"La suppression de compte n'est pas encore disponible",Toast.LENGTH_SHORT).show();
            //confirmationSuppression();
        }
        //bar_progres.dismiss();
        return super.onOptionsItemSelected(item);
    }

}
