package iut_lry.coursedorientation;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import static android.graphics.Color.rgb;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    EditText numCourse;
    EditText numEquipe;

    String numCourseStr;
    String numCourseStrActuel;

    String numEquipeStr;
    String numEquipeStrActuel;

    String ipServer;

    private Button dllPlayers;
    ProgressBar spinnerCheckPlayers;
    LinearLayout layoutSpinnerCheckPlayers;


    private Button dllParkour;
    ProgressBar spinner;
    LinearLayout layoutDllParkour;

    LinearLayout joueursTab;
    TextView header;
    String nomEquipe;
    String categorie;
    String num_parcours;
    Button buttonCheckDate;
    EditText date;
    int longueur;
    String dateStr;
    String dateStrActuel;
    ProgressBar spinnerCheckDate;
    LinearLayout layoutSpinnerCheckDate;

    int essaiDate;

    ScrollView scroll;
    LinearLayout layoutPlayers;

    // DB Class to perform DB related operations
    DBController controller;

    HashMap<String, String> queryValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        numCourse = (EditText) view.findViewById(R.id.numCourse);

        numEquipe = (EditText) view.findViewById(R.id.numEquipe);

        dllPlayers = (Button) view.findViewById(R.id.dllPlayers);
        dllPlayers.setOnClickListener(this);

        spinnerCheckPlayers = (ProgressBar) view.findViewById(R.id.progressBar3);
        spinnerCheckPlayers.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutSpinnerCheckPlayers = (LinearLayout) view.findViewById(R.id.layoutSpinnerCheckPlayers);
        layoutSpinnerCheckPlayers.setVisibility(View.GONE);

        joueursTab = (LinearLayout) view.findViewById(R.id.joueursTab);
        joueursTab.setVisibility(View.GONE);
        header = (TextView) view.findViewById(R.id.itemHeader);
        buttonCheckDate = (Button) view.findViewById(R.id.buttonCheckDate);
        buttonCheckDate.setOnClickListener(this);
        date = (EditText) view.findViewById(R.id.date);
        spinnerCheckDate = (ProgressBar) view.findViewById(R.id.progressBar4);
        spinnerCheckDate.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutSpinnerCheckDate = (LinearLayout) view.findViewById(R.id.layoutSpinnerCheckDate);
        layoutSpinnerCheckDate.setVisibility(View.GONE);

        dllParkour = (Button) view.findViewById(R.id.dllParkour);
        dllParkour.setOnClickListener(this);
        dllParkour.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        dllParkour.setVisibility(View.GONE);

        spinner = (ProgressBar) view.findViewById(R.id.progressBar2);
        spinner.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutDllParkour = (LinearLayout) view.findViewById(R.id.layoutDllParkour);
        layoutDllParkour.getBackground().setColorFilter(rgb(58,114,173), PorterDuff.Mode.MULTIPLY);
        layoutDllParkour.setVisibility(View.GONE);


        //Scroll down quand on appuie sur l'edittext Date
        scroll = (ScrollView) view.findViewById(R.id.scroll);
        layoutPlayers = (LinearLayout) view.findViewById(R.id.layoutPlayers);

        //a faire
        /*date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                scroll.smoothScrollTo(0,dllPlayers.getBottom());
            }

        });*/

        //pour ajouter et enlever les / automatiquement pour la date
        longueur = 0;

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = date.getText().toString();
                if(s.length()> longueur){

                    if(text.length() == 2 || text.length() == 5){
                        date.append("/");
                    }
                    longueur = text.length();
                }
                else {
                    if(text.length() == 2 || text.length() == 5){
                        date.getText().delete(text.length() - 1, text.length());
                    }
                    longueur=0;
                }

            }
        });

        //initialiser les essais de requêtes pour la date.
        essaiDate = 0;
        //initialisation
        numEquipeStrActuel = "aucune";
        dateStrActuel = "aucune";

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateParametres thread = new updateParametres();
        thread.execute();

    }

    //pr tenter de régler "IInputConnectionWrapper: showStatusIcon on inactive InputConnection" quand on quitte l'appli quand on a un edittext sélectionné
/*    @Override
    public void onResume() {
        RelativeLayout layoutFocus = (RelativeLayout) getView().findViewById(R.id.layoutFocus);
        layoutFocus.requestFocus();
        super.onResume();
    }*/

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

            case R.id.dllPlayers:

                numCourseStrActuel=numCourse.getText().toString();
                numEquipeStrActuel=numEquipe.getText().toString();

                if(!numEquipeStrActuel.equals(numEquipeStr) || !numCourseStrActuel.equals(numCourseStr))
                {
                    numCourse.clearFocus();
                    numEquipe.clearFocus();
                    mCallback.hideKeyboard();
                    essaiDate = 0;
                    ipServer = mCallback.getWifiApIpAddress();
                    if(!ipServer.equals("erreur"))
                    {
                        getPlayersAndRace();
                    }
                }
                else
                {
                    mCallback.showToast("Veuillez changer les données avant de refaire une requête.","court");
                }
                break;

            case R.id.buttonCheckDate:

                dateStrActuel=date.getText().toString();

                if(!dateStrActuel.equals(dateStr))
                {
                    essaiDate = 0;
                }

                if(essaiDate < 2)
                {
                    date.clearFocus();
                    mCallback.hideKeyboard();
                    ipServer = mCallback.getWifiApIpAddress();
                    if(!ipServer.equals("erreur"))
                    {
                        checkDate();
                    }
                }
                else
                {
                    mCallback.showToast("Veuillez changer de date avant de refaire une requête.","court");
                }
                break;

            case R.id.dllParkour:
                ipServer = mCallback.getWifiApIpAddress();
                if(!ipServer.equals("erreur"))
                {
                    syncSQLiteMySQLDB();
                }
                break;
        }
    }


    public String getWifiNetworkName() {
        WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getSSID();
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

        layoutSpinnerCheckPlayers.setVisibility(View.VISIBLE);
        dllPlayers.setVisibility(View.INVISIBLE);

        //on récupère le num de la course
        numCourseStr = numCourse.getText().toString();
        System.out.println("course : " + numCourseStr);

        //on récupère le num de l'équipe
        numEquipeStr = numEquipe.getText().toString();
        System.out.println("equipe : " + numEquipeStr);

        params.put("numCourse", numCourseStr);
        params.put("numEquipe", numEquipeStr);
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

                layoutSpinnerCheckPlayers.setVisibility(View.GONE);
                dllPlayers.setVisibility(View.VISIBLE);

                //changer dllPlayer pour afficher le bon msg si on vient de dll.
                dllParkour.setEnabled(true);
                dllParkour.setText("Télécharger le parcours");

                if(!responseString.equals("erreur") && !responseString.equals("erreurParcours")) {
                    afficherJoueurs(responseString,"onVerify");
                } else {
                    //on vide la liste des joueurs et on cache tout
                    if(layoutPlayers.getChildCount() > 0){
                        layoutPlayers.removeAllViews();
                        joueursTab.setVisibility(View.GONE);
                        dllParkour.setVisibility(View.GONE);
                        date.setText("");
                    }
                    if(responseString.equals("erreur")) {
                        Toast.makeText(getActivity().getApplicationContext(), "Erreur, l'équipe est introuvable", Toast.LENGTH_LONG).show();
                    }
                    else if(responseString.equals("erreurParcours")) {
                        Toast.makeText(getActivity().getApplicationContext(), "La course n'a pas de parcours pour votre catégorie.", Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 404) {
                    mCallback.showToast("Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    mCallback.showToast("Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    mCallback.showToast("Error " + statusCode + "\nUnexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet]","long");
                }

                layoutSpinnerCheckPlayers.setVisibility(View.GONE);
                dllPlayers.setVisibility(View.VISIBLE);

                //on ré-initialise le numEquipe si fail pour pouvoir redemander.
                numEquipeStr="aucune";
            }
        });

    }

    public void afficherJoueurs(String response, String choix){

        try {
            //vider la liste des joueurs
            if(layoutPlayers.getChildCount() > 0){
                layoutPlayers.removeAllViews();
            }


            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            //System.out.println(arr.length());


            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);

                    if(i==0){
                        nomEquipe = obj.get("nom_equipe").toString();
                        categorie = obj.get("categorie").toString();
                        num_parcours = obj.get("num_parcours").toString();
                        if(choix.equals("onStart")){
                            numCourse.setText(obj.get("numCourse").toString());
                            numEquipe.setText(obj.get("numEquipe").toString());
                            date.setText(obj.get("date_naissance").toString());

                            /*//pour que qd on restarte, on ne puisse pas retester la course et equipe deja téléchargé
                            numCourseStr = obj.get("numCourse").toString();
                            numEquipeStr = obj.get("numEquipe").toString();*/
                        }
                    }

                    TextView joueur = new TextView(getActivity());
                    joueur.setText(obj.get("prenom").toString() + " " + obj.get("nom").toString());
                    joueur.setPadding(20,10,0,10);
                    joueur.setBackgroundResource(R.drawable.bg_players);
                    joueur.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    layoutPlayers.addView(joueur);

                }

                //changer le header du tableau pour mettre le nom de l'équipe
                header.setText("Joueurs de l'équipe \""+ nomEquipe +"\" catégorie " + categorie);
                //afficher le tableau et le truc pr rentrer la date de naissance
                joueursTab.setVisibility(View.VISIBLE);

            }
        } catch (JSONException e) {
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

        layoutSpinnerCheckDate.setVisibility(View.VISIBLE);
        buttonCheckDate.setVisibility(View.INVISIBLE);

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

                layoutSpinnerCheckDate.setVisibility(View.GONE);
                buttonCheckDate.setVisibility(View.VISIBLE);

                if(!responseString.equals("erreur")) {
                    mCallback.showToast("Date de naissance OK !\nVous pouvez télécharger le parcours !","court");
                    dllParkour.setVisibility(View.VISIBLE);
                } else {
                    mCallback.showToast("Erreur : la date de naissance ne correspond pas.","court");
                }
                //on incrémente l'essai date pour limiter à 3 essais pr la meme date.
                if(dateStrActuel.equals(dateStr))
                {
                    essaiDate++;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 404) {
                    mCallback.showToast("Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    mCallback.showToast("Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    mCallback.showToast("Error " + statusCode + "\nUnexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet]","long");
                }

                layoutSpinnerCheckDate.setVisibility(View.GONE);
                buttonCheckDate.setVisibility(View.VISIBLE);

                //on ré-initialise la date enregistré si fail pour pouvoir redemander.
                dateStr = "aucune";
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
        dllParkour.setVisibility(View.INVISIBLE);
        layoutDllParkour.setVisibility(View.VISIBLE);

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        System.out.println("num parcours : " + num_parcours);
        System.out.println("num course : " + numCourseStr);
        System.out.println("num equipe : " + numEquipeStr);
        params.put("num_parcours", num_parcours);
        params.put("num_course", numCourseStr);
        params.put("num_equipe", numEquipeStr);
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
                dllParkour.setVisibility(View.VISIBLE);
                layoutDllParkour.setVisibility(View.GONE);

                //Eviter que le joueur télécharge 10 000 fois le parcours pour rien.

                dllParkour.setEnabled(false);
                dllParkour.setText("Parcours téléchargé");

                //Toast.makeText(getActivity().getApplicationContext(), "Le parcours a bien été téléchargé !", Toast.LENGTH_LONG).show();
                mCallback.showToast("Le parcours a bien été téléchargé !","long");

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
                dllParkour.setVisibility(View.VISIBLE);
                layoutDllParkour.setVisibility(View.GONE);

                if (statusCode == 404) {
                    mCallback.showToast("Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    mCallback.showToast("Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    mCallback.showToast("Error " + statusCode + "\nUnexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet]","long");
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
        controller.deleteTable("joueurs");
        controller.deleteTable("equipe");
        controller.deleteTable("course");
        controller.deleteTable("parcours");
        controller.deleteTable("liste_balises");
        controller.deleteTable("balise");
        controller.deleteTable("groupe");
        controller.deleteTable("liste_liaisons");
        controller.deleteTable("liaison");

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

                    if(!obj.isNull("joueurs.id"))
                    {
                        queryValues.put("joueurs.id", obj.get("joueurs.id").toString());
                        queryValues.put("joueurs.prenom", obj.get("joueurs.prenom").toString());
                        queryValues.put("joueurs.nom", obj.get("joueurs.nom").toString());
                        queryValues.put("joueurs.date_naissance", obj.get("joueurs.date_naissance").toString());
                        queryValues.put("joueurs.num_equipe", obj.get("joueurs.num_equipe").toString());
                    }
                    else if(!obj.isNull("equipe.id"))
                    {
                        queryValues.put("equipe.id", obj.get("equipe.id").toString());
                        queryValues.put("equipe.nom_equipe", obj.get("equipe.nom_equipe").toString());
                        queryValues.put("equipe.categorie", obj.get("equipe.categorie").toString());
                        queryValues.put("equipe.num_course", obj.get("equipe.num_course").toString());
                    }
                    else if(!obj.isNull("course.id"))
                    {
                        queryValues.put("course.id", obj.get("course.id").toString());
                        queryValues.put("course.date", obj.get("course.date").toString());
                        queryValues.put("course.temps", obj.get("course.temps").toString());
                    }
                    else if(!obj.isNull("parcours.id"))
                    {
                        queryValues.put("parcours.id", obj.get("parcours.id").toString());
                        queryValues.put("parcours.num_course", obj.get("parcours.num_course").toString());
                        queryValues.put("parcours.categorie", obj.get("parcours.categorie").toString());
                        queryValues.put("parcours.description", obj.get("parcours.description").toString());
                    }
                    else if(!obj.isNull("liste_balises.id"))
                    {
                        queryValues.put("liste_balises.id", obj.get("liste_balises.id").toString());
                        queryValues.put("liste_balises.num_parcours", obj.get("liste_balises.num_parcours").toString());
                        queryValues.put("liste_balises.num_balise", obj.get("liste_balises.num_balise").toString());
                        queryValues.put("liste_balises.suivante", obj.get("liste_balises.suivante").toString());
                        queryValues.put("liste_balises.num_suivante", obj.get("liste_balises.num_suivante").toString());
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
                        queryValues.put("liste_liaisons.num_parcours", obj.get("liste_liaisons.num_parcours").toString());
                        queryValues.put("liste_liaisons.description", obj.get("liste_liaisons.description").toString());
                        queryValues.put("liste_liaisons.points", obj.get("liste_liaisons.points").toString());
                    }
                    else if(!obj.isNull("liaison.num"))
                    {
                        queryValues.put("liaison.num", obj.get("liaison.num").toString());
                        queryValues.put("liaison.balise", obj.get("liaison.balise").toString());
                        queryValues.put("liaison.ordre", obj.get("liaison.ordre").toString());
                    }


                    System.out.println("query : "+queryValues);
                    // Insert User into SQLite DB
                    controller.insertDataParcours(queryValues);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class updateParametres extends AsyncTask<Void, Void, Void> {

        ArrayList<HashMap<String, String>> baliseList;
        String parametres;

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
        protected Void doInBackground(Void... settings) {
            //do your work here

            controller = new DBController(getActivity());

            baliseList = controller.getAllBalises(false);


            if (baliseList.size() != 0)
            {
                parametres = controller.updateOngletParametres();
                System.out.println("PARAMETRES : " + parametres);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        /*
         *    do something with data here
         *    display it or send to mainactivity
         *    close any dialogs/ProgressBars/etc...
        */
            if (baliseList.size() != 0)
            {
                afficherJoueurs(parametres,"onStart");

                dllParkour.setVisibility(View.VISIBLE);
                dllParkour.setEnabled(false);
                dllParkour.setText("Parcours téléchargé");

            }
            else
            {

            }
        }

    }

}
