package iut_lry.coursedorientation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static android.graphics.Color.rgb;

public class NewParkour extends AppCompatActivity implements View.OnClickListener{

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


    Button dllParkour;
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

    HashMap<String, String> queryValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_parkour);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(0xFFFFFFFF);

        numCourse = (EditText) findViewById(R.id.numCourse);

        numEquipe = (EditText) findViewById(R.id.numEquipe);

        dllPlayers = (Button) findViewById(R.id.dllPlayers);
        dllPlayers.setOnClickListener(this);

        spinnerCheckPlayers = (ProgressBar) findViewById(R.id.progressBar3);
        spinnerCheckPlayers.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutSpinnerCheckPlayers = (LinearLayout) findViewById(R.id.layoutSpinnerCheckPlayers);
        layoutSpinnerCheckPlayers.setVisibility(View.GONE);

        joueursTab = (LinearLayout) findViewById(R.id.joueursTab);
        joueursTab.setVisibility(View.GONE);
        header = (TextView) findViewById(R.id.itemHeader);
        buttonCheckDate = (Button) findViewById(R.id.buttonCheckDate);
        buttonCheckDate.setOnClickListener(this);
        date = (EditText) findViewById(R.id.date);
        spinnerCheckDate = (ProgressBar) findViewById(R.id.progressBar4);
        spinnerCheckDate.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutSpinnerCheckDate = (LinearLayout) findViewById(R.id.layoutSpinnerCheckDate);
        layoutSpinnerCheckDate.setVisibility(View.GONE);

        dllParkour = (Button) findViewById(R.id.dllParkour);
        dllParkour.setOnClickListener(this);
        dllParkour.setVisibility(View.GONE);

        spinner = (ProgressBar) findViewById(R.id.progressBar2);
        spinner.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutDllParkour = (LinearLayout) findViewById(R.id.layoutDllParkour);
        layoutDllParkour.setVisibility(View.GONE);

        layoutPlayers = (LinearLayout) findViewById(R.id.layoutPlayers);

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
                    Utils.hideKeyboard(NewParkour.this);
                    essaiDate = 0;
                    ipServer = Utils.getWifiApIpAddress(NewParkour.this);
                    if(!ipServer.equals("erreur"))
                    {
                        //si le bouton télécharger est visible, on le recache
                        if(dllParkour.getVisibility() == View.VISIBLE)
                        {
                            dllParkour.setVisibility(View.GONE);
                        }

                        getPlayersAndRace();
                    }
                }
                else
                {
                    Utils.showToast(NewParkour.this,"Veuillez changer les données avant de refaire une requête.","court");
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
                    Utils.hideKeyboard(NewParkour.this);
                    ipServer = Utils.getWifiApIpAddress(NewParkour.this);
                    if(!ipServer.equals("erreur"))
                    {
                        checkDate();
                    }
                }
                else
                {
                    Utils.showToast(NewParkour.this,"Veuillez changer de date avant de refaire une requête.","court");
                }
                break;

            case R.id.dllParkour:
                ipServer = Utils.getWifiApIpAddress(NewParkour.this);
                if(!ipServer.equals("erreur"))
                {
                    syncSQLiteMySQLDB();
                }
                break;
        }
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
        System.out.println("ipServ : " + ipServer);

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
                    afficherJoueurs(responseString,"onVerify",header,layoutPlayers);
                } else {
                    //on vide la liste des joueurs et on cache tout
                    if(layoutPlayers.getChildCount() > 0){
                        layoutPlayers.removeAllViews();
                        joueursTab.setVisibility(View.GONE);
                        dllParkour.setVisibility(View.GONE);
                        date.setText("");
                    }
                    if(responseString.equals("erreur")) {
                        Utils.showToast(NewParkour.this,"Erreur, l'équipe est introuvable","long");
                    }
                    else if(responseString.equals("erreurParcours")) {
                        Utils.showToast(NewParkour.this,"La course n'a pas de parcours pour votre catégorie.","long");
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 404) {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nUnexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet]","long");
                }

                layoutSpinnerCheckPlayers.setVisibility(View.GONE);
                dllPlayers.setVisibility(View.VISIBLE);

                //on ré-initialise le numEquipe si fail pour pouvoir redemander.
                numEquipeStr="aucune";
            }
        });

    }

    public void afficherJoueurs(String response, String choix, TextView headerTxt, LinearLayout layout){

        try {
            //vider la liste des joueurs
            if(layout.getChildCount() > 0){
                layout.removeAllViews();
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
                    }

                    TextView joueur = new TextView(this);
                    joueur.setText(obj.get("prenom").toString() + " " + obj.get("nom").toString());
                    joueur.setPadding(20,10,0,10);
                    joueur.setBackgroundResource(R.drawable.bg_players);
                    joueur.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    layout.addView(joueur);

                }

                //changer le header du tableau pour mettre le nom de l'équipe
                headerTxt.setText("Joueurs de l'équipe \""+ nomEquipe +"\" catégorie " + categorie);
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
                    Utils.showToast(NewParkour.this,"Date de naissance OK !\nVous pouvez télécharger le parcours !","court");
                    dllParkour.setVisibility(View.VISIBLE);
                } else {
                    Utils.showToast(NewParkour.this,"Erreur : la date de naissance ne correspond pas.","court");
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
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nUnexpected Error occcured! " +
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

                dllParkour.setVisibility(View.GONE);

                //Toast.makeText(getActivity().getApplicationContext(), "Le parcours a bien été téléchargé !", Toast.LENGTH_LONG).show();
                Utils.showToast(NewParkour.this,"Le parcours a bien été téléchargé !","long");

                // Update SQLite DB with response sent by getusers.php
                //Convertir byte[] en String
                String responseString = null;
                try {
                    responseString = new String(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                ReceiveData thread = new ReceiveData();
                thread.execute(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                //cacher le spinner et afficher le bouton
                dllParkour.setVisibility(View.VISIBLE);
                layoutDllParkour.setVisibility(View.GONE);

                if (statusCode == 404) {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    Utils.showToast(NewParkour.this,"Error " + statusCode + "\nUnexpected Error occcured! " +
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

            //on renvoie le résultat 111 et on ferme l'activité,
            //dans le activity result du fragment, si le resultCode est à 2, on reload tous les fragments.
            Intent intent=new Intent();
            setResult(111,intent);
            finish();

        }

    }

    public void updateParcours(String response){

        DBController controller = new DBController(this);

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
                        queryValues.put("balise.hash", obj.get("balise.hash").toString());
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
}
