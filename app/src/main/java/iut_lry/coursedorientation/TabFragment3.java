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

    Button buttonSend;
    LinearLayout layoutEnvoiParkour;
    ProgressBar progressBarSend;

    CheckBox checkBoxNotChecked;
    boolean notCheckedBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        buttonSend.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        buttonSend.setOnClickListener(this);

        checkBoxNotChecked = (CheckBox) view.findViewById(R.id.checkBoxNotChecked);
        checkBoxNotChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                updateList();
            }
        });

        progressBarSend = (ProgressBar) view.findViewById(R.id.progressBarSend);
        progressBarSend.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutEnvoiParkour = (LinearLayout) view.findViewById(R.id.layoutEnvoiParkour);
        layoutEnvoiParkour.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        layoutEnvoiParkour.setVisibility(View.GONE);

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

        if(checkBoxNotChecked.isChecked())
        {
            notCheckedBox = true;
        }
        else
        {
            notCheckedBox = false;
        }

        baliseList = controller.getAllBalises(notCheckedBox);

        System.out.println("nombre de ligne de la table à afficher : " + baliseList.size());
        // If users exists in SQLite DB
        if (baliseList.size() != 0) {

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
            case R.id.buttonSend:
                new AlertDialog.Builder(getActivity())
                        .setMessage("Ceci effacera votre parcours sur le téléphone et l'enverra vers le serveur" +
                                    "\nEtes-vous sûr ?")
                        .setCancelable(false)
                        .setPositiveButton("Envoyer le parcours", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                syncSQLiteMySQLDB();
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
                break;
        }
    }

    public void syncSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        buttonSend.setVisibility(View.INVISIBLE);
        layoutEnvoiParkour.setVisibility(View.VISIBLE);

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        String ipServer = mCallback.getWifiApIpAddress();

        params.put("resultatsJSON", controller.composeJSONfromSQLite());
        Log.d("tag", controller.composeJSONfromSQLite());
        client.post("http://" + ipServer + ":80/testProjet/insertResultats.php",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

/*                //Convertir byte[] en String
                String responseString = null;
                try {
                    responseString = new String(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println(responseString);*/

                mCallback.showToast("Le parcours a bien été envoyé !","long");

                buttonSend.setVisibility(View.VISIBLE);
                layoutEnvoiParkour.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                // Hide ProgressBar

                buttonSend.setVisibility(View.VISIBLE);
                layoutEnvoiParkour.setVisibility(View.GONE);

                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 404) {
                    mCallback.showToast("Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    mCallback.showToast("Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    mCallback.showToast("Error " + statusCode + "\nUnexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet]","long");
                }
            }
        });
    }
}