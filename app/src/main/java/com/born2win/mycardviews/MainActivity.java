package com.born2win.mycardviews;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.born2win.mycardviews.Articles.Article;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {


    //Variables

    private RecyclerView recyclerView;
    private DrawerLayout menu_lateral;
    private ActionBarDrawerToggle drawerToggle;
    private TextView retour;
    private static final String TAG = "MainActivity";
    private FirebaseAuth authentification;
    private FirebaseAuth.AuthStateListener listener;
    private boolean connexion;
    private ProgressDialog bar_progres;
    private DatabaseReference baseDeDonneesUtil;

    Toolbar toolbar;
    FloatingActionButton fab;


    //FONCTIONS OVERRIDE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        bar_progres = new ProgressDialog(this);

        authentification = FirebaseAuth.getInstance();
        baseDeDonneesUtil = FirebaseDatabase.getInstance().getReference().child("Utilisateurs");

        fab = findViewById(R.id.nvPost);

        //Vérification de l'état de connexion de l'utilisateur
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.d(TAG,"non connecte");

                    fab.setVisibility(View.INVISIBLE);
                }
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG,"connecte");

                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent ajout = new Intent(MainActivity.this, PageAjout.class);
                            startActivity(ajout);
                            overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                        }
                    });
                }
            }
        };

        //Récupération du recycler à afficher
        recyclerView = findViewById(R.id.recyclerviewLay);
        //fixe la taille des vignettes, sont prendre en compte leur contenu
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),0,true);

        //Mise en place de la barre d'action de l'application
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //afficher le bouton retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Mise en place du menu latéral
        menu_lateral = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this,this.menu_lateral,0,0);
        menu_lateral.addDrawerListener(drawerToggle);

        /*
        //Mise en place du bouton pour remonter au top de la liste
        retour = findViewById(R.id.retourTop);
        retour.setVisibility(View.INVISIBLE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && retour.getVisibility() == View.VISIBLE) {
                    cacherText();
                } else if (dy < 0 && retour.getVisibility() != View.VISIBLE) {
                    afficherText();
                    retour.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recyclerView.smoothScrollToPosition(0);
                        }
                    });
                }
            }
        });
        */

        //supprimerUtilisteur();

    }

    @Override
    protected void onStart() {
        super.onStart();

        authentification.addAuthStateListener(listener);

        Query dataRef = FirebaseDatabase.getInstance().getReference().child("Articles");

        FirebaseRecyclerOptions<Article> options = new FirebaseRecyclerOptions.Builder<Article>()
                .setQuery(dataRef, Article.class)
                .build();

        FirebaseRecyclerAdapter<Article, ArticleViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(options) {

            @NonNull
            @Override
            public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cards, parent, false);
                return new ArticleViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ArticleViewHolder holder, int position, @NonNull final Article model) {

                final String cle_article = getRef(position).getKey();

                holder.setTitre(model.getTitre());
                holder.setDescription(model.getDescription());
                holder.setImage(getApplicationContext() ,model.getImage());
                holder.setAuteur(model.getAuteur());

                View v = recyclerView.getChildAt(0);

                final String affTitre = model.getTitre();
                final String affDescription = model.getDescription();
                final String affImage = model.getImage();
                final String affAuteur = model.getAuteur();

                final String affSousTitre = model.getSousTitre();
                final String affContenu = model.getContenu();
                final String affUid = model.getUid();

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent articleIntent = new Intent (MainActivity.this, PageArticle.class);
                        articleIntent.putExtra("titre",affTitre);
                        articleIntent.putExtra("description",affDescription);
                        articleIntent.putExtra("sousTitre",affSousTitre);
                        articleIntent.putExtra("contenu",affContenu);
                        articleIntent.putExtra("image",affImage);
                        articleIntent.putExtra("auteur",affAuteur);
                        articleIntent.putExtra("uid",affUid);
                        articleIntent.putExtra("cle",cle_article);
                        startActivity(articleIntent);
                        overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                    }
                });
            }

        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    /* Afficher les icônes de la barre d'action sur la page principale */
    public boolean onCreateOptionsMenu(Menu menu) {
        // On affiche les options disponibles en étant connecté



        // Si l'utilisateur est connecté
        if (authentification.getCurrentUser() != null) {
            Log.d(TAG,"menu_connecte");
            // On affiche les options disponibles en étant connecté
            getMenuInflater().inflate(R.menu.menu_page_main, menu);

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView recherche = (SearchView) menu.findItem(R.id.action_recherche).getActionView();
            if (manager != null) recherche.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            recherche.setQueryHint(getString(R.string.recherche_champ));
            recherche.setDrawingCacheBackgroundColor(getResources().getColor(R.color.fondNoir));
            recherche.setIconifiedByDefault(true);
            recherche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recherche.setIconified(false);
                    recherche.requestFocus();
                }
            });


            return true;
        } else
            // Si l'utilisateur n'est pas connecté
        {
            Log.d(TAG,"menu_deconnecte");
            // On affiche les options disponibles en étant déconnecté
            getMenuInflater().inflate(R.menu.menu_page_main_deco, menu);

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView recherche = (SearchView) menu.findItem(R.id.action_recherche).getActionView();
            if (manager != null) recherche.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            recherche.setQueryHint(getString(R.string.recherche_champ));
            recherche.setDrawingCacheBackgroundColor(getResources().getColor(R.color.fondNoir));
            recherche.setIconifiedByDefault(true);
            recherche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recherche.setIconified(false);
                    recherche.requestFocus();
                }
            });
            return true;
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // synchroniser le drawerToggle après la restauration via onRestoreInstanceState
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    /* Actions des icônes de la barre d'action sur la page principale */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Affichage du panneau latéral
                menu_lateral.openDrawer(GravityCompat.START);
                break;
            case R.id.tiroir_btnConnexion:
                startActivity(new Intent(MainActivity.this, PageConnexion.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                finish();
                break;
            case R.id.tiroir_btnProfil:
                if (authentification.getCurrentUser() != null) {
                    Intent profilIntent = new Intent(MainActivity.this, PageProfilUtil.class);
                    startActivity(profilIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Vous n'êtes pas connecté(e)",Toast.LENGTH_SHORT).show();
                }
                /*
                startActivity(new Intent(MainActivity.this, PageProfilUtil.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                finish();
                */
                break;
            case R.id.tiroir_btnParametres:
                Toast.makeText(MainActivity.this,"Page des paramètres",Toast.LENGTH_SHORT).show();
                default:
                    break;


        }
        //Action lorsqu'on appuie sur le bouton "Recharger"
        if (item.getItemId() == R.id.action_reload) {
            recharger();
        }
        //Action lorsqu'on appuie sur le bouton "Profil"
        if (item.getItemId() == R.id.action_profil) {
            startActivity(new Intent(MainActivity.this, PageProfilUtil.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        //Action lorsqu'on appuie sur le bouton "Déconnexion"
        if (item.getItemId() == R.id.action_deco) {
            deco();
        }
        //action lorsqu'on appuie sur le bouton "Connexion"
        if (item.getItemId() == R.id.action_co) {
            if (authentification.getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, PageConnexion.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Vous êtes déjà connecté(e) !",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_transition_gauche_e, R.anim.slide_transition_droite_s);
    }


    //FONCTIONS Personnalisées

    /* Afficher le message */
    private void afficherText() {
        //animation pour afficher le texte
        Animation afficher = AnimationUtils.loadAnimation(MainActivity.this, R.anim.cacher_texte);
        //retour.startAnimation(afficher);
        retour.setVisibility(View.VISIBLE);
        retour.getOffsetForPosition(100,100);
    }

    /* Cacher le message */
    private void cacherText() {
        //animation pour cacher le texte
        Animation cacher = AnimationUtils.loadAnimation(MainActivity.this, R.anim.cacher_texte);
        retour.startAnimation(cacher);
        retour.setVisibility(View.INVISIBLE);
    }

    private void supprimerUtilisteur() {
        boolean valeur = getIntent().hasExtra("suppression");
        Log.d(TAG, "Utilisateur : "+valeur);

        if (authentification.getCurrentUser() != null && valeur) {
            String uid = authentification.getCurrentUser().getUid();
            Log.d(TAG, "Utilisateur : "+uid);

            //Suppression de l'utilisateur
            authentification.getCurrentUser().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this,"Compte supprimé",Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Compte supprimé");
                            }
                        }
                    });

            //On supprime toutes les valeurs attachées à l'utilisateur pour supprimer l'utilisateur lui-même
            baseDeDonneesUtil.child(uid).child("image").removeValue();
            baseDeDonneesUtil.child(uid).child("utilisateur").removeValue();

            deco();
            Toast.makeText(MainActivity.this,"Compte supprimé",Toast.LENGTH_SHORT).show();
        }
    }

    /* Déconnecter l'utilisateur */
    private void deco() {
        authentification.signOut();
        bar_progres.setMessage(getString(R.string.deco));
        bar_progres.show();
        recharger();
        bar_progres.dismiss();

        Toast.makeText(MainActivity.this,"Déconnexion réussie",Toast.LENGTH_SHORT).show();
    }

    /* Recharger la page principale */
    private void recharger() {
        finish();
        startActivity(getIntent());
        overridePendingTransition(0,0);

        /*
        Date currentTime = Calendar.getInstance().getTime();
        Toast.makeText(MainActivity.this, "Date : \n" + currentTime,Toast.LENGTH_LONG).show();
        */
    }

    public void afficherTuto(@NonNull ArticleViewHolder holder, int position, @NonNull final Article model) {
        //Affichage du tutoriel au premier lancement
        String Tuto = "Tutoriel";

        Intent revoir = getIntent();

        String rediff = revoir.getStringExtra("Tuto");

        Tuto += rediff;

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(250);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, Tuto);

        sequence.setConfig(config);

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                //Selection de l'item
                .setTarget(findViewById(R.id.cardview_image))
                //Titre de l'écran
                .setTitleText("Fil d'actualité")
                //Description de l'item
                .setContentText("Les articles sont présentés sous forme de liste.")
                //Message de fin
                .setDismissText("OK    (1/3)")
                //Affichage de l'écran
                .build());

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                //Selection de l'item
                .setTarget(findViewById(R.id.nvPost))
                //Titre de l'écran
                .setTitleText("Nouvel article")
                //Description de l'item
                .setContentText("Ce bouton sert aux utilisateurs connectés de créer de nouveaux articles.")
                //Message de fin
                .setDismissText("OK    (2/3)")
                //Affichage de l'écran
                .build());

        /*
        sequence.addSequenceItem (new MaterialShowcaseView.Builder(this)
                //Selection de l'item
                .setTarget(findViewById(R.id.action_reload))
                //Titre de l'écran
                .setTitleText("Recharger")
                //Description de l'item
                .setContentText("Ce bouton sert à recharger la page afin de voir de nouveaux articles.")
                //Message de fin
                .setDismissText("OK    (3/3)")
                //Affichage de l'écran
                .build());

        /*
        sequence.addSequenceItem (new MaterialShowcaseView.Builder(this)
                //Selection de l'item
                .setTarget(findViewById(R.id.button_parametre))
                //Titre de l'écran
                .setTitleText("Paramètres")
                //Description de l'item
                .setContentText("Ce bouton sert à accéder aux paramètres")
                //Message de fin
                .setDismissText("OK    (4/4)")
                //Affichage de l'écran
                .build());
                */

        sequence.start(); //problème de superposition
    }

    //CLASSES

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {

        View v;

        public ArticleViewHolder(View view) {
            super(view);

            v = view;
        }

        public void setTitre(String titreNv) {
            TextView titre = v.findViewById(R.id.cardview_titre);
            titre.setText(titreNv);
            Log.d(TAG,"Titre chargé");
        }

        public void setDescription(String descriptionNv) {
            TextView description = v.findViewById(R.id.cardview_description);
            description.setText(descriptionNv);
            Log.d(TAG,"Description chargée");
        }

        public void setImage(Context ctx, final String imageNv) {

            ImageView image = v.findViewById(R.id.cardview_image);

            Glide.with(ctx)
                    .asBitmap()
                    .load(imageNv)
                    .into(image);

            Log.d(TAG,"Image chargée");
        }

        public void setAuteur(String auteurNv) {
            TextView auteur = v.findViewById(R.id.cardview_auteur);
            auteur.setText(auteurNv);
            Log.d(TAG,"Auteur chargé(e)");
        }
    }
}
