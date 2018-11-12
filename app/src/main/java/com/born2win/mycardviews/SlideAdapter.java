package com.born2win.mycardviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SlideAdapter extends PagerAdapter {

    Context ctx;
    LayoutInflater layoutInflater;

    public SlideAdapter(Context ctx) {
        this.ctx = ctx;
    }

    //Images de présentation
    private int[] images = {
            R.drawable.icon_notepad,
            R.drawable.icon_magnifier,
            R.drawable.firebase_logo_vertical,
            R.drawable.icon_pencil,
    };

    //Titres des images
    private String[] titres = {
            "Consulter des articles",
            "Rechercher du contenu",
            "Créer un compte",
            "Rédiger un article",
    };

    //Descriptions des images
    private String[] desc = {
            "Born2Win News vous permet de lire des articles sur les sportifs ainsi que les évènements footballistique qui vous tiennent à cœur.\n",

            "Vous avez la possibilité de rechercher le contenu qui vous intéresse pour avoir une expérience personnalisée au sein de l'application.\n",

            "Créer un compte vous donne l'occasion de sauvegarder vos données et vos préférences, et de consulter les articles sans connexion internet.\n" +
            "Notre application utilise Firebase, le service de gestion de données de Google pour s'occuper et sécuriser de vos données en temps réel.",

            "Grâce à votre compte, vous pourrez également écrire vous même des articles sur le sujet de votre choix, que ce soit sur un club ou un joueur.",
    };

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.intro_slides, container, false);

        ImageView slide_image= v.findViewById(R.id.slide_image);
        TextView slide_titre = v.findViewById(R.id.slide_titre);
        TextView slide_desc = v.findViewById(R.id.slide_desc);

        slide_image.setImageResource(images[position]);
        slide_titre.setText(titres[position]);
        slide_desc.setText(desc[position]);

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout) object);

    }
}
