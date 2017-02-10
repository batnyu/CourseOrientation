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
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cz.msebera.android.httpclient.Header;

import static android.graphics.Color.WHITE;
import static android.graphics.Color.rgb;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;
    private Button btnFtoA;
    private Button btnFtoF;

    EditText numEquipe;

    String numEquipeStr;
    String numEquipeStrActuel;

    String ipServer;

    private Button dllPlayers;
    ProgressBar spinnerCheckPlayers;

    RelativeLayout chargement;
    private Button dllParkour;
    ProgressBar spinner;

    LinearLayout joueursTab;
    TextView header;
    ListView listViewPlayers;
    String nomEquipe;
    Button buttonCheckDate;
    EditText date;
    String dateStr;
    String dateStrActuel;
    ProgressBar spinnerCheckDate;

    ScrollView scroll;

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


        dllPlayers = (Button) view.findViewById(R.id.dllPlayers);
        dllPlayers.setOnClickListener(this);

        spinnerCheckPlayers = (ProgressBar) view.findViewById(R.id.progressBar3);
        spinnerCheckPlayers.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        spinnerCheckPlayers.setVisibility(View.GONE);

        joueursTab = (LinearLayout) view.findViewById(R.id.joueursTab);
        joueursTab.setVisibility(View.GONE);
        header = (TextView) view.findViewById(R.id.itemHeader);
        listViewPlayers = (ListView) view.findViewById(R.id.listViewPlayers);
        buttonCheckDate = (Button) view.findViewById(R.id.buttonCheckDate);
        buttonCheckDate.setOnClickListener(this);
        date = (EditText) view.findViewById(R.id.date);
        spinnerCheckDate = (ProgressBar) view.findViewById(R.id.progressBar4);
        spinnerCheckDate.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        spinnerCheckDate.setVisibility(View.GONE);

        dllParkour = (Button) view.findViewById(R.id.dllParkour);
        dllParkour.setOnClickListener(this);
        dllParkour.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        dllParkour.setTextColor(WHITE);
        dllParkour.setVisibility(View.GONE);

        spinner = (ProgressBar) view.findViewById(R.id.progressBar2);
        spinner.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        spinner.setVisibility(View.GONE);

        //Scroll down quand on appuie sur l'edittext Date
        scroll = (ScrollView) view.findViewById(R.id.scroll);

        //a faire
        /*date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                scroll.smoothScrollTo(0,dllPlayers.getBottom());
            }

        });*/
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controller = new DBController(getActivity());

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

            case R.id.dllPlayers:
                numEquipeStrActuel=numEquipe.getText().toString();

                if(!numEquipeStrActuel.equals(numEquipeStr))
                {
                    numEquipe.clearFocus();
                    mCallback.hideKeyboard();
                    ipServer = getWifiApIpAddress();
                    if(!ipServer.equals("erreur"))
                    {
                        getPlayersAndRace();
                    }
                }
                else
                {
                    //mCallback.showToast("Vous n'avez pas changé de numéro d'équipe !");
                }
                break;

            case R.id.buttonCheckDate:
                dateStrActuel=date.getText().toString();

                if(!dateStrActuel.equals(dateStr))
                {
                    date.clearFocus();
                    mCallback.hideKeyboard();
                    ipServer = getWifiApIpAddress();
                    if(!ipServer.equals("erreur"))
                    {
                        checkDate();
                    }
                }
                else
                {
                    //mCallback.showToast("Vous n'avez pas changé de date de naissance !");
                }
                break;

            case R.id.dllParkour:
                ipServer = getWifiApIpAddress();
                if(!ipServer.equals("erreur"))
                {
                    syncSQLiteMySQLDB();
                }
                break;
/*
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
                break;*/
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
            mCallback.showToast("Veuillez activer votre Wi-fi, connectez-vous au réseau de l'organisateur et réessayez.");
            myIP = "erreur";
        }

        return myIP;
    }



    public String getWifiNetworkName() {
        WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String name = wifiInfo.getSSID();
        return name;
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount() ; i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void getPlayersAndRace()
    {
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        spinnerCheckPlayers.setVisibility(View.VISIBLE);
        //dllPlayers.setVisibility(View.INVISIBLE);
        dllPlayers.setEnabled(false);
        dllPlayers.setText("");

        numEquipeStr = numEquipe.getText().toString();
        System.out.println(numEquipeStr);

        params.put("numEquipe", numEquipeStr);
        //Log.d("tag", controller.composeJSONfromSQLite().toString());
        client.post("http://" + ipServer + ":80/testProjet/getPlayersTeamRace.php",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                //Convertir byte[] en String
                String responseString = null;
                try {
                    responseString = new String(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                System.out.println(responseString);

                spinnerCheckPlayers.setVisibility(View.GONE);
                //dllPlayers.setVisibility(View.VISIBLE);
                dllPlayers.setEnabled(true);
                dllPlayers.setText("Afficher");

                if(!responseString.equals("erreur")) {
                    afficherJoueurs(responseString);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Erreur, l'équipe est introuvable", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 404) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nRequested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nSomething went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nUnexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }

                spinnerCheckPlayers.setVisibility(View.GONE);
                dllPlayers.setEnabled(true);
                dllPlayers.setText("Afficher");
            }
        });

    }

    public void afficherJoueurs(String response){

        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());

            ArrayList<HashMap<String, String>> listPlayers = new ArrayList<HashMap<String, String>>();;

            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("joueurs",obj.get("prenom").toString() + " " + obj.get("nom").toString());
                    listPlayers.add(map);

                    if(i==0){
                        nomEquipe = obj.get("nom_equipe").toString();
                    }
                }

                String[] from = new String[] {"joueurs"};
                int[] to = new int[] { R.id.item1};

                // Set the User Array list in ListView
                ListAdapter adapter = new SpecialAdapter(getActivity(), listPlayers, R.layout.grid_item_one, from, to);
                listViewPlayers.setAdapter(adapter);
                setListViewHeightBasedOnChildren(listViewPlayers); //75 height avant ca
                //registerForContextMenu(listViewPlayers);


                //changer le header du tableau pour mettre le nom de l'équipe
                header.setText("Joueurs de l'équipe \""+ nomEquipe +"\"");
                //afficher le tableau et le truc pr rentrer la date de naissance
                joueursTab.setVisibility(View.VISIBLE);

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

    public void checkDate(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        spinnerCheckDate.setVisibility(View.VISIBLE);
        buttonCheckDate.setText("");
        buttonCheckDate.setEnabled(false);

        numEquipeStr = numEquipe.getText().toString();
        System.out.println(numEquipeStr);

        dateStr = date.getText().toString();

        params.put("numEquipe", numEquipeStr);
        params.put("date", dateStr);
        System.out.println(params);

        client.post("http://" + ipServer + ":80/testProjet/checkDate.php",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                //Convertir byte[] en String
                String responseString = null;
                try {
                    responseString = new String(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                System.out.println(responseString);

                spinnerCheckDate.setVisibility(View.GONE);
                buttonCheckDate.setText("Vérifier");
                buttonCheckDate.setEnabled(true);

                if(!responseString.equals("erreur")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Date de naissance OK !", Toast.LENGTH_LONG).show();
                    dllParkour.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Erreur, la date de naissance ne correspond pas.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 404) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nRequested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nSomething went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nUnexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }

                spinnerCheckDate.setVisibility(View.GONE);
                buttonCheckDate.setText("Vérifier");
                buttonCheckDate.setEnabled(true);
            }
        });

    }

    // Method to Sync MySQL to SQLite DB
    public void syncSQLiteMySQLDB() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();

        //cacher le bouton et afficher le spinner
        spinner.setVisibility(View.VISIBLE);
        dllParkour.setEnabled(false);
        dllParkour.setText("Téléchargement en cours");

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        // Make Http call to getusers.php, Ne pas oublier le port sinon ca bug
        client.post("http://" + ipServer + ":80/testProjet/getParcours.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"

                //cacher le spinner et afficher le bouton
                spinner.setVisibility(View.GONE);
                dllParkour.setEnabled(true);
                dllParkour.setText("Télécharger le parcours");

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
                dllParkour.setEnabled(true);
                dllParkour.setText("Télécharger le parcours");

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
