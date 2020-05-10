package com.example.paraperf;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/*
    Cette classe permet d'associer les attributs de chaque fichier aux TextView du nom, des data, de la date et à l'ImageView représentant l'icon du fichier.
*/

public class FileArrayAdapter extends ArrayAdapter<Item> {
    private Context context;
    private int id;
    private List<Item> items;

    public FileArrayAdapter(Context context, int id, List<Item> items) {
        super(context, id, items);
        this.context = context;
        this.id = id;
        this.items = items;
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Permet l'affichage de l'item avec son nom, ses données et sa date de dernière modification
        View v = view;
        if(v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

        final Item item = items.get(position);
        if (item != null) {
            // Nom, information et date de mofication du fichier
            TextView name = (TextView) v.findViewById(R.id.Name);
            TextView data = (TextView) v.findViewById(R.id.Data);
            TextView date = (TextView) v.findViewById(R.id.Date);

            // Récupération de l'icon
            ImageView iconVue = (ImageView) v.findViewById(R.id.fd_Icon1);
            String uri = "drawable/" + item.getIcon();
            int iconResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            Drawable icon = context.getResources().getDrawable(iconResource);
            iconVue.setImageDrawable(icon);

            if (name != null) {
                name.setText(item.getName());
            }
            if (data != null) {
                data.setText(item.getData());
            }
            if (date != null) {
                date.setText(item.getDate());
            }
        }
        return v;
    }
}
