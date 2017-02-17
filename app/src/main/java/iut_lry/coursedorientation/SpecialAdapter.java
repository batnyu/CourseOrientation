package iut_lry.coursedorientation;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SpecialAdapter extends SimpleAdapter {

    private List<HashMap<String, String>> itemsLocal;

    public SpecialAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);

        itemsLocal = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        HashMap<String,String> map = itemsLocal.get(position);

        String value = map.get("temps");

        if(!value.equals(""))
        {
            view.setBackgroundResource(R.color.checkedTab);
        }
        else
        {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }
}
