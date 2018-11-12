package com.born2win.mycardviews;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PageArticle extends AppCompatActivity {

    private static final String TAG = "PageArticle";

    private FirebaseAuth authentification;
    //private FirebaseAuth.AuthStateListener listener;
    private DatabaseReference baseDeDonnees;
    //private DatabaseReference dataUtil;
    private ProgressDialog bar_progres;

    android.support.v7.widget.Toolbar bar_daction;

    @Override
    protected void onCreate(Bundle BundleSavedState) {
        super.onCreate(BundleSavedState);
        setContentView(R.layout.page_article);
        Log.d(TAG, "onCreate : check");

        authentification = FirebaseAuth.getInstance();
        bar_progres = new ProgressDialog(this);

        baseDeDonnees = FirebaseDatabase.getInstance().getReference().child("Articles");
        //dataUtil = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(authentification.getUid());
/*
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                connexion = !(firebaseAuth.getCurrentUser() == null) ;
            }
        };
*/
        bar_daction = findViewById(R.id.article_toolbar);
        setSupportActionBar(bar_daction);

        //afficher le bouton retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //retour à la page précédente lorsque l'on appuie sur le bouton
        bar_daction.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                finish();
            }
        });

        redirigerArticle();
        modifierArticle();
    }

    private void redirigerArticle() {
        if(getIntent().hasExtra("titre")
                //&& getIntent().hasExtra("description")
                && getIntent().hasExtra("image")
                && getIntent().hasExtra("auteur")
                && getIntent().hasExtra("sousTitre")
                && getIntent().hasExtra("contenu")) {


            String titre = getIntent().getStringExtra("titre");
            //String desc = getIntent().getStringExtra("description");
            String image = getIntent().getStringExtra("image");
            String auteur = getIntent().getStringExtra("auteur");
            String sousTitre = getIntent().getStringExtra("sousTitre");
            String contenu = getIntent().getStringExtra("contenu");

            //Toast.makeText(getApplicationContext(),"Auteur : \n" + auteur, Toast.LENGTH_SHORT).show();

            afficherArticle(titre, sousTitre, contenu, image, auteur);
        }
    }

    private void afficherArticle(String titre, String sousTitre, String contenu, String image, String auteur) {
        TextView titre_article = findViewById(R.id.article_titre);
        titre_article.setText(titre);

        TextView sousTitre_article = findViewById(R.id.article_sous_titre);
        sousTitre_article.setText(sousTitre);

        TextView contenu_article = findViewById(R.id.article_texte);
        contenu_article.setText(contenu);

        ImageView image_article = findViewById(R.id.article_image);
        //Picasso.get().load(image).into(image_article);

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(image)
                .into(image_article);

        TextView auteur_article = findViewById(R.id.article_auteur);
        String redacteur = "Rédigé par "+auteur;
        auteur_article.setText(redacteur);
    }

    private void modifierArticle() {
        if(getIntent().hasExtra("titreModif")
                && getIntent().hasExtra("sousTitreModif")
                && getIntent().hasExtra("texteModif")
                && getIntent().hasExtra("imageModif")
                && getIntent().hasExtra("auteurModif")) {

            String titre = getIntent().getStringExtra("titreModif");
            String sousTitre = getIntent().getStringExtra("sousTitreModif");
            String texte = getIntent().getStringExtra("texteModif");
            String image = getIntent().getStringExtra("imageModif");
            String auteur = getIntent().getStringExtra("auteurModif");


            Toast.makeText(PageArticle.this,"Article modifié",Toast.LENGTH_SHORT).show();
            afficherArticle(titre, sousTitre, texte, image, auteur);
        }
    }

    private void confirmationSuppression() {
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous supprimer l'article ?")
                .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Suppression de l'article");
                        supprimerArticle();
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

    private void supprimerArticle() {
        String cle = getIntent().getStringExtra("cle");
        bar_progres.setMessage("Suppression de l'article");
        bar_progres.show();

        //On supprime toutes les valeurs attachées à l'article pour supprimer l'article lui-même
        baseDeDonnees.child(cle).child("auteur").removeValue();
        baseDeDonnees.child(cle).child("description").removeValue();
        baseDeDonnees.child(cle).child("image").removeValue();
        baseDeDonnees.child(cle).child("titre").removeValue();
        baseDeDonnees.child(cle).child("sousTitre").removeValue();
        baseDeDonnees.child(cle).child("contenu").removeValue();
        baseDeDonnees.child(cle).child("uid").removeValue();

        Intent articleIntent = new Intent(PageArticle.this, MainActivity.class);
        startActivity(articleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
        finish();

        Toast.makeText(PageArticle.this,"Article supprimé",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //String cle = getIntent().getStringExtra("cle");
        String uid = getIntent().getStringExtra("uid");

        // Affichage des icônes de la barre d'action sur la page principale
        if (authentification.getCurrentUser() != null) {
            String utilisateur = authentification.getCurrentUser().getUid();
            if (utilisateur.equals(uid)) {
                getMenuInflater().inflate(R.menu.menu_page_article, menu);
                return true;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            //Toast.makeText(getApplicationContext(),"Bouton d'édition", Toast.LENGTH_SHORT).show();

            Intent articleIntent = new Intent(PageArticle.this, PageArticleEdition.class);

            articleIntent.putExtra("titre",getIntent().getStringExtra("titre"));
            articleIntent.putExtra("sousTitre",getIntent().getStringExtra("sousTitre"));
            articleIntent.putExtra("contenu",getIntent().getStringExtra("contenu"));
            articleIntent.putExtra("image",getIntent().getStringExtra("image"));
            articleIntent.putExtra("auteur",getIntent().getStringExtra("auteur"));

            String auteur = getIntent().getStringExtra("auteur");
            //Toast.makeText(getApplicationContext(),"Auteur : \n" + auteur, Toast.LENGTH_SHORT).show();

            articleIntent.putExtra("cle",getIntent().getStringExtra("cle"));
            /*

            Log.d(TAG,"Titre : "+ getIntent().getStringExtra("titre"));
            Log.d(TAG,"Sous-titre : "+ getIntent().getStringExtra("sousTitre"));
            Log.d(TAG,"Texte : "+getIntent().getStringExtra("contenu"));
            Log.d(TAG,"Image : "+getIntent().getStringExtra("image"));
            Log.d(TAG,"Auteur : "+getIntent().getStringExtra("auteur"));
            Log.d(TAG,"Cle : "+getIntent().getStringExtra("cle"));
            */
            startActivity(articleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);

        }

        if (item.getItemId() == R.id.action_supprime) {
            //Toast.makeText(getApplicationContext(),"Bouton de suppression",Toast.LENGTH_SHORT).show();
            confirmationSuppression();
        }
        bar_progres.dismiss();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_transition_gauche_e, R.anim.slide_transition_droite_s);
    }

}
