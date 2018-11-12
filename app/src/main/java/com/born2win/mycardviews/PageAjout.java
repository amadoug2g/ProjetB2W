package com.born2win.mycardviews;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PageAjout extends AppCompatActivity {

    private static final String TAG = "PageAjout";

    //Variables

    private EditText ajoutTitre, ajoutDesc, ajoutSousTitre, ajoutContenu;
    private TextInputLayout ajoutTitreLayout, ajoutDescLayout, ajoutSousTitreLayout;

    private ImageView ajout_image;
    private Uri imageUri;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private ProgressDialog barreProgres;

    Button btnPost;
    Toolbar bar_daction;

    FirebaseAuth auth;

    private FirebaseUser util;
    private DatabaseReference dataUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout_article);

        auth = FirebaseAuth.getInstance();

        util = auth.getCurrentUser();

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Articles");
        dataUtil = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(util.getUid());

        barreProgres = new ProgressDialog(this);

        ajout_image = findViewById(R.id.ajout_image);

        ajout_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageIntent = new Intent();
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, GALLERY_REQUEST);

            }
        });

        //récupération de la barre d'action
        bar_daction = findViewById(R.id.ajout_toolbar);
        //mise en place de la barre comme barre par défaut
        setSupportActionBar(bar_daction);

        //afficher le bouton retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Publier un article");
            getSupportActionBar().setElevation(25);
        }

        //retour à la page précédente lorsque l'on appuie sur le bouton
        bar_daction.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
             }
        });

        //récupération des différentes vues de la page
        ajoutTitre = findViewById(R.id.ajout_titre);
        ajoutDesc = findViewById(R.id.ajout_desc);
        ajoutSousTitre = findViewById(R.id.ajout_sous_titre);
        ajoutContenu = findViewById(R.id.ajout_contenu);

        ajoutTitreLayout =  findViewById(R.id.ajout_titre_layout);
        ajoutDescLayout = findViewById(R.id.ajout_desc_layout);
        ajoutSousTitreLayout = findViewById(R.id.ajout_sous_titre_layout);

        btnPost = findViewById(R.id.ajout_btn);

        ajoutTitre.addTextChangedListener(new MyTextWatcher(ajoutTitre));
        ajoutDesc.addTextChangedListener(new MyTextWatcher(ajoutDesc));

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifPost();
            }
        });

        AlertDialog.Builder nePlusAfficher = new AlertDialog.Builder(PageAjout.this);
        View maVue = getLayoutInflater().inflate(R.layout.fenetre, null);
        CheckBox maCheckBox = maVue.findViewById(R.id.checkBox);

        nePlusAfficher.setTitle("Contenu de l'article");
        nePlusAfficher.setMessage("Si vous laissez le contenu de l'article vide, un texte fictif sera attribué à l'article que vous pourrez modifier par la suite en accédant à l'article.");
        nePlusAfficher.setView(maVue);
        nePlusAfficher.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog fenetre = nePlusAfficher.create();
        fenetre.show();

        maCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    stockerValeur(true);
                }else{
                    stockerValeur(false);
                }
            }
        });

        if(recupValeur()){
            fenetre.hide();
        }else{
            fenetre.show();
        }
    }

    //Sélection de l'image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                ajout_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_transition_gauche_e, R.anim.slide_transition_droite_s);
    }

    /**
     * Validating form
     */


    private boolean validerTitre() {
        String titre = ajoutTitre.getText().toString().trim();

        if (titre.isEmpty()) {
            ajoutTitreLayout.setError(getString(R.string.err_msg_titre));
            requestFocus(ajoutTitre);
            Log.d(TAG,"Titre non valide");
            return false;
        } else {
            ajoutTitreLayout.setErrorEnabled(false);
            Log.d(TAG,"Titre valide");
            return true;
        }
    }

    private boolean validerSousTitre() {
        String sousTitre = ajoutSousTitre.getText().toString().trim();

        if (sousTitre.isEmpty()) {
            ajoutSousTitreLayout.setError(getString(R.string.err_msg_sous_titre));
            requestFocus(ajoutSousTitre);
            Log.d(TAG,"Sous-Titre non valide");
            return false;
        } else {
            ajoutSousTitreLayout.setErrorEnabled(false);
            Log.d(TAG,"Sous-Titre valide");
            return true;
        }
    }

    private boolean validerDesc() {
        String description = ajoutDesc.getText().toString().trim();

        if (description.isEmpty()) {
            ajoutDescLayout.setError(getString(R.string.err_msg_desc));
            requestFocus(ajoutDesc);
            Log.d(TAG,"Description non valide");
            return false;
        } else {
            ajoutDescLayout.setErrorEnabled(false);
            Log.d(TAG,"Description valide");
            return true;
        }
    }

    private boolean validerImage() {
        if (imageUri == null) {
            //Toast.makeText(getApplicationContext(), "Veuillez choisir une vignette.", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Si vous ne choissisez pas d'image, une vignette par défaut sera assignée à votre article.", Toast.LENGTH_LONG).show();
            //ajout_image.setImageResource(R.drawable.logo_b2w_noir);
            //imageUri = Uri.parse("res:////" + R.drawable.logo_b2w_noir);

            new android.app.AlertDialog.Builder(this)
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

            Log.d(TAG,"Image non valide");
            return false;
        } else {
            Log.d(TAG,"Image valide");
            return true;
        }
    }

    private void verifPost() {
        if (validerTitre() &&  validerDesc() && validerImage() && validerSousTitre()) {
            Log.d(TAG,"Post valide");
            if (ajoutContenu.getText().toString().isEmpty()) {
                final String contenu = getString(R.string.texte_carte_2);
                publierArticle(contenu);
            } else {
                final String contenu = ajoutContenu.getText().toString().trim();
                publierArticle(contenu);
            }
        }
        else {
            Log.d(TAG, "Post non valide");
        }
    }

    private void publierArticle(final String contenu) {

        barreProgres.setMessage("Publication de l'article");
        barreProgres.show();

        final String titre = ajoutTitre.getText().toString().trim();
        final String desc = ajoutDesc.getText().toString().trim();
        final String sousTitre = ajoutSousTitre.getText().toString().trim();

        final StorageReference chemin = storageRef.child("Images_articles").child(imageUri.getLastPathSegment());

        chemin.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    Uri dUrl = task.getResult();
                    final String lien = dUrl.toString();

                    chemin.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final DatabaseReference nvArticle = databaseRef.push();

                            //nvArticle.child("utilisateur").setValue(dataUtil.child("nom"));

                            dataUtil.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    nvArticle.child("titre").setValue(titre);
                                    nvArticle.child("description").setValue(desc);
                                    nvArticle.child("image").setValue(lien);
                                    nvArticle.child("uid").setValue(util.getUid());
                                    //nvArticle.child("date").setValue(util.getUid());
                                    nvArticle.child("sousTitre").setValue(sousTitre);
                                    nvArticle.child("contenu").setValue(contenu);
                                    nvArticle.child("auteur").setValue(dataSnapshot.child("utilisateur").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent main = new Intent(PageAjout.this, MainActivity.class);
                                                startActivity(main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                finish();
                                                Toast.makeText(PageAjout.this, "Nouveau post crée", Toast.LENGTH_SHORT).show();
                                            } else if (!task.isSuccessful()) {
                                                Toast.makeText(PageAjout.this, "La publication n'a pas été publiée", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(PageAjout.this, "La publication n'a pas été publiée", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e != null) {
                        Toast.makeText(PageAjout.this, "Une erreur est survenue\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                chemin.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(PageAjout.this, "La publication n'a pas été publiée",Toast.LENGTH_SHORT).show();

                    }
                });
                barreProgres.dismiss();
            }
        });

    }

    private void stockerValeur(boolean isChecked){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("item", isChecked);
        mEditor.apply();
    }

    private boolean recupValeur(){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("item", false);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

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
                case R.id.ajout_titre:
                    validerTitre();
                    break;
                case R.id.ajout_desc:
                    validerDesc();
                    break;
            }
        }
    }
}
