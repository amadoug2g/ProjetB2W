package com.born2win.mycardviews.Articles;

public class Article {
    private String titre, description, sousTitre, contenu, image, auteur, uid;

    public Article(String titre, String sousTitre, String contenu ,String description, String image, String auteur, String uid) {
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.description = description;
        this.contenu = contenu;
        this.image = image;
        this.auteur = auteur;
        this.uid = uid;
    }

    public Article(String titre, String sousTitre, String contenu, String image, String auteur) {
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.contenu = contenu;
        this.image = image;
        this.auteur = auteur;
    }

    public Article(String titre, String sousTitre, String contenu) {
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.contenu = contenu;
    }

    public Article() {
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
