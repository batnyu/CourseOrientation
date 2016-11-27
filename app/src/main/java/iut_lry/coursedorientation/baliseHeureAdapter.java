package iut_lry.coursedorientation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Baptiste on 27/11/2016.
 */
public class baliseHeureAdapter extends ArrayAdapter<baliseHeure> {
    public baliseHeureAdapter(Context context, ArrayList<baliseHeure> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        baliseHeure baliseHeure = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_baliseheure, parent, false);
        }
        // Lookup view for data population
        TextView numBalise = (TextView) convertView.findViewById(R.id.numBalise);
        TextView heureBalise = (TextView) convertView.findViewById(R.id.heureBalise);
        // Populate the data into the template view using the data object
        numBalise.setText(baliseHeure.balise);
        heureBalise.setText(baliseHeure.heure);
        // Return the completed view to render on screen
        return convertView;
    }
}