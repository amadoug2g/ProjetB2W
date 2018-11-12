package com.born2win.mycardviews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.born2win.mycardviews.Articles.Article;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PageArticleEdition extends AppCompatActivity {

    private static final String TAG = "PageArticleEdition";

    EditText titre, sous_titre ,texte;
    Button validation;
    private DatabaseReference databaseRefArticle;

    android.support.v7.widget.Toolbar bar_daction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_edition);

        String auteur = getIntent().getStringExtra("auteur");
        //Toast.makeText(getApplicationContext(),"Auteur : \n" + auteur, Toast.LENGTH_SHORT).show();

        titre = findViewById(R.id.articleEdition_titre);
        sous_titre = findViewById(R.id.articleEdition_sous_titre);
        texte = findViewById(R.id.articleEdition_texte);


        final String cle = getIntent().getStringExtra("cle");
        databaseRefArticle = FirebaseDatabase.getInstance().getReference().child("Articles").child(cle);

        bar_daction = findViewById(R.id.articleEdition_toolbar);
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
                Intent articleIntent = new Intent(getApplicationContext(),PageArticle.class);
                articleIntent.putExtra("titre",getIntent().getStringExtra("titre"));
                articleIntent.putExtra("sousTitre",getIntent().getStringExtra("sousTitre"));
                articleIntent.putExtra("contenu",getIntent().getStringExtra("contenu"));
                articleIntent.putExtra("image",getIntent().getStringExtra("image"));
                articleIntent.putExtra("auteur",getIntent().getStringExtra("auteur"));
                startActivity(articleIntent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        affichageArticles();

        validation = findViewById(R.id.articleEdition_validation);
        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nTitre = titre.getText().toString();
                String nSousTitre = sous_titre.getText().toString();
                String nTexte = texte.getText().toString();
                String nImage = getIntent().getStringExtra("image");

                modifierArticle(nTitre, nSousTitre, nTexte, nImage);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_transition_gauche_e, R.anim.slide_transition_droite_s);
    }

    private void affichageArticles() {
        String mTitre, mSousTitre, mTexte;

        mTitre = getIntent().getStringExtra("titre");
        mSousTitre = getIntent().getStringExtra("sousTitre");
        mTexte = getIntent().getStringExtra("contenu");

        titre.setText(mTitre);
        sous_titre.setText(mSousTitre);
        texte.setText(mTexte);
    }

    private void modifierArticle(final String titre, final String sousTitre, final String texte, String image) {
            Intent articleIntent = new Intent(PageArticleEdition.this, PageArticle.class);

            //TITRE
            databaseRefArticle.child("titre").setValue(titre);
            articleIntent.putExtra("titreModif",titre);
            Log.d(TAG, "titre : OK\n(" + titre+")");

            //SOUS-TITRE
            databaseRefArticle.child("sousTitre").setValue(sousTitre);
            articleIntent.putExtra("sousTitreModif",sousTitre);
            Log.d(TAG, "sousTitre : OK\n(" + sousTitre+")");

            //TEXTE
            databaseRefArticle.child("contenu").setValue(texte);
            articleIntent.putExtra("texteModif",texte);
            Log.d(TAG, "texte : OK\n(" + texte+")");

            //IMAGE
            Log.d(TAG, "image : OK\n(" + image+")");
            articleIntent.putExtra("imageModif",image);

            //AUTEUR
            String auteur = getIntent().getStringExtra("auteur");
            articleIntent.putExtra("auteurModif",auteur);
            Log.d(TAG, "auteur : OK\n("+ auteur+")");

            //Toast.makeText(getApplicationContext(),"Auteur : \n" + auteur, Toast.LENGTH_SHORT).show();

            startActivity(articleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
            finish();

        Toast.makeText(PageArticleEdition.this,"Article mis à jour",Toast.LENGTH_SHORT).show();
    }
}
