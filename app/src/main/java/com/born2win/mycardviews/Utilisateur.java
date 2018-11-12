package com.born2win.mycardviews;

public class Utilisateur {
    private String nom, image;

    public Utilisateur(String nom, String image) {
        this.nom = nom;
        this.image = image;
    }

    public Utilisateur() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
