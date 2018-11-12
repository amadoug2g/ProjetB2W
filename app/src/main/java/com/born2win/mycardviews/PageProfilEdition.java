package com.born2win.mycardviews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PageProfilEdition extends AppCompatActivity {

    private static final String TAG = "PageProfil";

    private EditText nom, nomUtil, prenomUtil;
    private TextInputLayout nomLayout, nomUtilLayout, prenomUtilLayout;
    private ImageView image;
    private Button bouton;
    private Uri imageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private DatabaseReference dataUtilRef;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;
    private StorageReference storageImageRef;
    private ProgressDialog bar_progres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_edition);

        bar_progres = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        dataUtilRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs");
        storageImageRef = FirebaseStorage.getInstance().getReference().child("Image_profils");

        nom = findViewById(R.id.profil_nom);
        nomLayout = findViewById(R.id.profil_nom_layout);

        nomUtil = findViewById(R.id.profil_nomUtil);
        nomUtilLayout = findViewById(R.id.profil_nomUtil_layout);

        prenomUtil = findViewById(R.id.profil_prenom);
        prenomUtilLayout = findViewById(R.id.profil_prenom_layout);

        image = findViewById(R.id.profil_image);
        bouton = findViewById(R.id.profil_valider);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enregistrerProfil();
            }
        });


        nom.addTextChangedListener(new MyTextWatcher(nom));
        nomUtil.addTextChangedListener(new MyTextWatcher(nomUtil));
        prenomUtil.addTextChangedListener(new MyTextWatcher(prenomUtil));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                image.setImageURI(resultUri);
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

    private void enregistrerProfil() {

        final String pseudo = nom.getText().toString();
        final String nomUtilisateur = nomUtil.getText().toString();
        final String prenomUtilisateur = prenomUtil.getText().toString();

        if (auth.getCurrentUser() != null) {
            final String idUtil = auth.getCurrentUser().getUid();

            bar_progres.setMessage("Mise à jour du profil");
            bar_progres.show();

            if (nomVerif() && nomUtilVerif() && prenomVerif() &&imageVerif()) {

                StorageReference chemin = storageImageRef.child(imageUri.getLastPathSegment());

                chemin.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String cheminDL = storageImageRef.getDownloadUrl().toString();
                            //Toast.makeText(PageProfilEdition.this,"DL : "+cheminDL, Toast.LENGTH_SHORT).show();

                            dataUtilRef.child(idUtil).child("utilisateur").setValue(pseudo);
                            dataUtilRef.child(idUtil).child("nom").setValue(nomUtilisateur);
                            dataUtilRef.child(idUtil).child("prénom").setValue(prenomUtilisateur);
                            dataUtilRef.child(idUtil).child("image").setValue(cheminDL);

                        startActivity(new Intent(PageProfilEdition.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(R.anim.slide_transition_droite_e, R.anim.slide_transition_gauche_s);
                        finish();
                    }
                });
            }
        }
        bar_progres.dismiss();
    }

    private boolean nomVerif() {
        String nom_ = nom.getText().toString();
        if (nom_.isEmpty()) {
            nomLayout.setError(getString(R.string.nom_requis));
            return false;
        } else {
            nomLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean nomUtilVerif() {
        String nomUtil_ = nomUtil.getText().toString();
        if (nomUtil_.isEmpty()) {
            nomUtilLayout.setError(getString(R.string.nomUtil_requis));
            return false;
        } else {
            nomUtilLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean prenomVerif() {
        String prenomUtil_ = prenomUtil.getText().toString();
        if (prenomUtil_.isEmpty()) {
            prenomUtilLayout.setError(getString(R.string.prenom_requis));
            return false;
        } else {
            prenomUtilLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean imageVerif() {
        if (imageUri == null) {
            Toast.makeText(PageProfilEdition.this,"Veuillez sélectionner une image",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
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
                case R.id.profil_nom:
                    nomVerif();
                    break;
                case R.id.profil_nomUtil:
                    nomUtilVerif();
                    break;
                    /*
                case R.id.profilUtil_prenom:
                    prenomVerif();
                    break;
                    */
            }
        }
    }


}
