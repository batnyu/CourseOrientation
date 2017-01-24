package iut_lry.coursedorientation;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static android.graphics.Color.WHITE;
import static android.graphics.Color.rgb;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;
    private Button btnFtoA;
    private Button btnFtoF;

    EditText numEquipe;

    String numEquipeStr;

    String ipServer;
    TextView infosWifi;
    Button buttonWifi;
    private Button dllParkour;
    Button buttonDel;
    ProgressBar spinner;

    // DB Class to perform DB related operations
    DBController controller;

    HashMap<String, String> queryValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        numEquipe = (EditText) view.findViewById(R.id.numEquipe);

        //A completer pour enlever le focus quand on enleve le truc ou alors quand on appuie sur le bouton
        numEquipe.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(numEquipe.getWindowToken(), 0);
                    numEquipe.setFocusable(false);
                    numEquipe.setFocusableInTouchMode(true);
                    return true;
                } else {
                    return false;
                }
            }
        });

        infosWifi = (TextView) view.findViewById(R.id.infosWifi);
        buttonWifi = (Button) view.findViewById(R.id.buttonWifi);
        buttonWifi.setOnClickListener(this);
        //buttonWifi.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        //buttonWifi.setTextColor(WHITE);

        dllParkour = (Button) view.findViewById(R.id.dllParkour);
        dllParkour.setOnClickListener(this);
        dllParkour.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        dllParkour.setTextColor(WHITE);

        buttonDel = (Button) view.findViewById(R.id.buttonDel);
        buttonDel.setOnClickListener(this);
        buttonDel.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        buttonDel.setTextColor(WHITE);

        spinner = (ProgressBar) view.findViewById(R.id.progressBar2);
        spinner.getIndeterminateDrawable().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        spinner.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controller = new DBController(getActivity());

        afficherInfoWifi();

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
        Toast.makeText(getActivity(), "Fragment 1: Refresh called.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*
            case R.id.button:
                mCallback.showToast("Hello from Fragment 1");
                break;

            case R.id.button2:
                mCallback.communicateToFragment2();
                break;
                */

            case R.id.buttonWifi:
                afficherInfoWifi();
                break;

            case R.id.dllParkour:
                syncSQLiteMySQLDB();
                break;

            case R.id.buttonDel:
                //test pour reset table qd télécharge le parcours
                controller.deleteTable("parcours");
                controller.deleteTable("liste_balises");
                controller.deleteTable("balise");
                controller.deleteTable("groupe");
                controller.deleteTable("liste_liaisons");
                controller.deleteTable("liaison");
                mCallback.communicateToFragment3();
                mCallback.communicateToFragment2();
                break;
        }
    }

    public String getWifiApIpAddress() {
        String myIP = null;
        final WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        //conversion chelou pour la mettre en string adresse IP
        byte[] myIPAddress = BigInteger.valueOf(dhcp.gateway).toByteArray();
        // you must reverse the byte array before conversion. Use Apache's commons library
        ArrayUtils.reverse(myIPAddress);
        InetAddress myInetIP = null;
        try {
            myInetIP = InetAddress.getByAddress(myIPAddress);
            myIP = myInetIP.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d("erreur ",e.getMessage());
        }

        return myIP;
    }

    public String getWifiNetworkName() {
        WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String name = wifiInfo.getSSID();
        return name;
    }

    public void afficherInfoWifi() {
        ipServer = getWifiApIpAddress();
        String nameServer = getWifiNetworkName();
        if(ipServer != null)
        {
            infosWifi.setText("Vous êtes connecté au réseau Wifi " + nameServer +
                    " situé à l'adresse IP " + ipServer);
        }
        else
        {
            infosWifi.setText("Vous n'êtes connecté à aucun réseau wifi !");
        }
    }

    // Method to Sync MySQL to SQLite DB
    public void syncSQLiteMySQLDB() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();

        //cacher le bouton et afficher le spinner
        spinner.setVisibility(View.VISIBLE);
        dllParkour.setVisibility(View.INVISIBLE);

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        // Make Http call to getusers.php, Ne pas oublier le port sinon ca bug
        client.post("http://192.168.1.52:80/testProjet/getParcours.php", params, new AsyncHttpResponseHandler() {
            //http://192.168.1.13/testProjetV2/getusersPDO.php
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"

                //cacher le spinner et afficher le bouton
                spinner.setVisibility(View.GONE);
                dllParkour.setVisibility(View.VISIBLE);

                afficherInfoWifi();

                //je sais pas encore lequel choisir
                //Toast.makeText(getActivity().getApplicationContext(), "Le parcours a bien été téléchargé !", Toast.LENGTH_LONG).show();
                mCallback.showToast("Le parcours a bien été téléchargé !");

                // Update SQLite DB with response sent by getusers.php
                //Convertir byte[] en String
                String responseString = null;
                try {
                    responseString = new String(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                ReceiveData test = new ReceiveData();
                test.execute(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                //cacher le spinner et afficher le bouton
                spinner.setVisibility(View.GONE);
                dllParkour.setVisibility(View.VISIBLE);

                //pour mettre à jour les trucs wifi
                afficherInfoWifi();

                if (statusCode == 404) {
                    mCallback.showToast("Error " + statusCode + "\nRequested resource not found");
                } else if (statusCode == 500) {
                    mCallback.showToast("Error " + statusCode + "\nSomething went wrong at server end");
                } else {
                    mCallback.showToast("Error " + statusCode + "\nUnexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet]");
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

            /*@Override
            public void onProgress(long bytesWritten, long totalSize) {
                long progressPercentage = (long)100*bytesWritten/totalSize;
                spinner.setProgress((int)progressPercentage);;
            }*/
        });

    }

    public class ReceiveData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
        /*
         *    do things before doInBackground() code runs
         *    such as preparing and showing a Dialog or ProgressBar
        */
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        /*
         *    updating data
         *    such a Dialog or ProgressBar
        */

        }

        @Override
        protected Void doInBackground(String... parametres) {
            //do your work here

            String responseStringThread = parametres[0];

            updateParcours(responseStringThread);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        /*
         *    do something with data here
         *    display it or send to mainactivity
         *    close any dialogs/ProgressBars/etc...
        */
            //On update la listView du fragment 3 (onglet parcours)
            mCallback.communicateToFragment3();
            //Changer affichage de l'onglet 2
            mCallback.communicateToFragment2();
        }

    }

    public void updateJoueurEquipeCourse(String response){

        //test pour reset table qd télécharge le parcours
        controller.deleteTable("joueur");
        controller.deleteTable("equipe");
        controller.deleteTable("course");

        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    //System.out.println(obj.get("id"));
                    //System.out.println(obj.get("balise"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();

                    // Add id extracted from Object
                    queryValues.put("joueurs.id", obj.get("joueurs.id").toString());
                    queryValues.put("joueurs.prenom", obj.get("joueurs.prenom").toString());
                    queryValues.put("joueurs.nom", obj.get("joueurs.nom").toString());
                    queryValues.put("joueurs.date_naissance", obj.get("joueurs.date_naissance").toString());
                    queryValues.put("joueurs.num_equipe", obj.get("joueurs.num_equipe").toString());

                    queryValues.put("equipe.id", obj.get("equipe.id").toString());
                    queryValues.put("equipe.nom_equipe", obj.get("equipe.nom_equipe").toString());
                    queryValues.put("equipe.categorie", obj.get("equipe.categorie").toString());
                    queryValues.put("equipe.num_course", obj.get("equipe.num_course").toString());

                    // Insert User into SQLite DB
                    controller.insertDataEquipe(queryValues);
                }

                //On récupère la course et l'équipe
                numEquipe = (EditText) getActivity().findViewById(R.id.numEquipe);
                numEquipeStr = numEquipe.getText().toString();

                controller.updateNumEquipe(numEquipeStr);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void updateParcours(String response){

        //test pour reset table qd télécharge le parcours
        controller.deleteTable("parcours");
        controller.deleteTable("liste_balises");
        controller.deleteTable("balise");
        controller.deleteTable("groupe");
        controller.deleteTable("liste_liaisons");
        controller.deleteTable("liaison");

        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            System.out.println(arr);
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj);
                    //System.out.println(obj.get("id"));
                    //System.out.println(obj.get("balise"));

                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();

                    // Add parcours.id extracted from Object

                    if(!obj.isNull("parcours.id"))
                    {
                        queryValues.put("parcours.id", obj.get("parcours.id").toString());
                        queryValues.put("parcours.categorie", obj.get("parcours.categorie").toString());
                        queryValues.put("parcours.description", obj.get("parcours.description").toString());
                        queryValues.put("parcours.date", obj.get("parcours.date").toString());
                    }
                    else if(!obj.isNull("liste_balises.id"))
                    {
                        queryValues.put("liste_balises.id", obj.get("liste_balises.id").toString());
                        queryValues.put("liste_balises.num_parcours", obj.get("liste_balises.num_parcours").toString());
                        queryValues.put("liste_balises.num_balise", obj.get("liste_balises.num_balise").toString());
                        queryValues.put("liste_balises.suivante", obj.get("liste_balises.suivante").toString());
                        queryValues.put("liste_balises.azimut", obj.get("liste_balises.azimut").toString());
                        queryValues.put("liste_balises.azimut_distance", obj.get("liste_balises.azimut_distance").toString());
                        queryValues.put("liste_balises.azimut_degre", obj.get("liste_balises.azimut_degre").toString());
                        queryValues.put("liste_balises.depart", obj.get("liste_balises.depart").toString());
                        queryValues.put("liste_balises.arrivee", obj.get("liste_balises.arrivee").toString());
                        queryValues.put("liste_balises.liaison", obj.get("liste_balises.liaison").toString());
                        queryValues.put("liste_balises.groupe", obj.get("liste_balises.groupe").toString());
                        queryValues.put("liste_balises.points", obj.get("liste_balises.points").toString());
                    }
                    else if(!obj.isNull("balise.num"))
                    {
                        queryValues.put("balise.num", obj.get("balise.num").toString());
                        queryValues.put("balise.coord_gps", obj.get("balise.coord_gps").toString());
                        queryValues.put("balise.poste", obj.get("balise.poste").toString());
                    }
                    else if(!obj.isNull("groupe.nom_groupe"))
                    {
                        queryValues.put("groupe.nom_groupe", obj.get("groupe.nom_groupe").toString());
                        queryValues.put("groupe.balise_entree", obj.get("groupe.balise_entree").toString());
                        queryValues.put("groupe.balise_sortie", obj.get("groupe.balise_sortie").toString());
                        queryValues.put("groupe.points_bonus", obj.get("groupe.points_bonus").toString());
                    }
                    else if(!obj.isNull("liste_liaisons.num"))
                    {
                        queryValues.put("liste_liaisons.num", obj.get("liste_liaisons.num").toString());
                        queryValues.put("liste_liaisons.description", obj.get("liste_liaisons.description").toString());
                        queryValues.put("liste_liaisons.points", obj.get("liste_liaisons.points").toString());
                    }
                    else if(!obj.isNull("liaisons.num"))
                    {
                        queryValues.put("liaison.num", obj.get("liaison.num").toString());
                        queryValues.put("liaison.balise", obj.get("liaison.balise").toString());
                        queryValues.put("liaison.ordre", obj.get("liaison.ordre").toString());
                    }


                    System.out.println("query : "+queryValues);
                    // Insert User into SQLite DB
                    controller.insertDataParcours(queryValues);
                }

                //On récupère la course et l'équipe
                numEquipe = (EditText) getActivity().findViewById(R.id.numEquipe);
                numEquipeStr = numEquipe.getText().toString();

                //controller.updateNumEquipe(numEquipeStr);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
