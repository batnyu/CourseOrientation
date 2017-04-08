package iut_lry.coursedorientation;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static android.graphics.Color.rgb;

public class TabFragment3 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    DBController controller;
    LinearLayout interfaceMain;
    TextView noParcours;

    CheckBox checkBoxNotChecked;
    boolean notCheckedBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        checkBoxNotChecked = (CheckBox) view.findViewById(R.id.checkBoxNotChecked);
        checkBoxNotChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                updateList();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        controller = new DBController(getActivity());

        //pour afficher la base déjà téléchargé quand on redémarre l'appli
        updateList();

    }
    //faire un thread pour update list et attendre un peu avant de refresh tout pour opti
    public void updateList() {
        //TEST
        interfaceMain = (LinearLayout) getActivity().findViewById(R.id.main);
        noParcours = (TextView) getActivity().findViewById(R.id.noParcours);



        controller = new DBController(getActivity());
        // create the grid item mapping
        String[] from = new String[] {"num_balise", "temps", "suivante", "poste"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };

        ArrayList<HashMap<String, String>> baliseList;
        // Get User records from SQLite DB
        boolean courseDownloaded;

        if(checkBoxNotChecked.isChecked())
        {
            notCheckedBox = true;
        }
        else
        {
            notCheckedBox = false;
        }

        baliseList = controller.getAllBalises(notCheckedBox);
        courseDownloaded = controller.checkCourse();

        System.out.println("nombre de ligne de la table à afficher : " + baliseList.size());
        // If users exists in SQLite DB
        if (courseDownloaded) {

            //Test
            interfaceMain.setVisibility(LinearLayout.VISIBLE);
            noParcours.setVisibility(LinearLayout.GONE);

            // Set the User Array list in ListView ( on peut utiliser specialadapter si envie tryhard couleur truc checké)
            SpecialAdapter adapter = new SpecialAdapter(getActivity(), baliseList, R.layout.grid_item, from, to);
            ListView myList = (ListView) getActivity().findViewById(R.id.listview);
            myList.setAdapter(adapter);
            registerForContextMenu(myList);
        }
        else
        {
            //Test
            interfaceMain.setVisibility(LinearLayout.GONE);
            noParcours.setVisibility(LinearLayout.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (IFragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    public void onRefresh() {
        Toast.makeText(getActivity(), "Fragment 3: Refresh called.",
                Toast.LENGTH_SHORT).show();
    }

    public void fragmentCommunication3() {

        updateList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


}