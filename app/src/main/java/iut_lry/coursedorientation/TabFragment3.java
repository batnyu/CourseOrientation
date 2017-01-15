package iut_lry.coursedorientation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class TabFragment3 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    DBController controller;
    LinearLayout interfaceMain;
    TextView noParcours;

    Button buttonSend;
    //Progress Dialog Object
    ProgressDialog prgDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);

        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Envoi en cours de votre parcours vers le serveur.\n" +
                "Patientez svp...");
        prgDialog.setCancelable(false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        controller = new DBController(getActivity());

        //pour afficher la base déjà téléchargé quand on redémarre l'appli
        updateList();

    }

    public void updateList() {
        //TEST
        interfaceMain = (LinearLayout) getActivity().findViewById(R.id.main);
        noParcours = (TextView) getActivity().findViewById(R.id.noParcours);

        controller = new DBController(getActivity());
        // create the grid item mapping
        String[] from = new String[] {"num_balise", "temps", "suivante", "points"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };

        ArrayList<HashMap<String, String>> userList;
        // Get User records from SQLite DB

        userList = controller.getAllUsers();

        System.out.println("nombre de ligne de la table à afficher : " + userList.size());
        // If users exists in SQLite DB
        if (userList.size() != 0) {

            //Test
            interfaceMain.setVisibility(LinearLayout.VISIBLE);
            noParcours.setVisibility(LinearLayout.GONE);

            // Set the User Array list in ListView
            ListAdapter adapter = new SpecialAdapter(getActivity(), userList, R.layout.grid_item, from, to);
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
                        .setMessage("Etes-vous sur ?")
                        .setCancelable(false)
                        .setPositiveButton("Envoyer le parcours", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                buttonSend.setEnabled(false);
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
        ArrayList<HashMap<String, String>> userList =  controller.getAllUsers();
        if(userList.size()!=0){
            //if(controller.dbSyncCount() != 0){
                prgDialog.show();

                client.setConnectTimeout(5000);
                //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
                // est repéré par onFailure contrairement à host unreachable
                // à étudié c'est relou
                client.setResponseTimeout(5000); // as above
                client.setTimeout(5000); // both connection and socket timeout
                client.setMaxRetriesAndTimeout(1, 100); // times, delay

                params.put("resultatsJSON", controller.composeJSONfromSQLite());
                Log.d("tag", controller.composeJSONfromSQLite().toString());
                client.post("http://192.168.1.52:80/testProjet/insertuserPDONEW.php",params ,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        System.out.println(response);
                        prgDialog.hide();
                        buttonSend.setEnabled(true);
                        Toast.makeText(getActivity().getApplicationContext(), "Le parcours a bien été envoyé !", Toast.LENGTH_LONG).show();
/*
                        //Convertir byte[] en String
                        String responseString = null;
                        try {
                            responseString = new String(response, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONArray arr = new JSONArray(responseString);
                            System.out.println(arr.length());
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                System.out.println(obj.get("id"));
                                System.out.println(obj.get("status"));
                                //controller.updateSyncStatus(obj.get("id").toString(),obj.get("status").toString());
                            }
                            Toast.makeText(getActivity(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getActivity(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        // Hide ProgressBar
                        prgDialog.hide();
                        buttonSend.setEnabled(true);
                        if (statusCode == 404) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nRequested resource not found", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nSomething went wrong at server end", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nUnexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getActivity(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }
        //}else{
          //  Toast.makeText(getActivity(), "No data in SQLite DB, please do enter User name to perform Sync action", Toast.LENGTH_LONG).show();
        //}
    }
}