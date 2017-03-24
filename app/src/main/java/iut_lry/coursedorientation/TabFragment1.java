package iut_lry.coursedorientation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.graphics.Color.rgb;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    View view;
    Button sendButton;
    Button newParcoursButton;
    LinearLayout layoutSendParkour;
    ProgressBar progressBarSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        sendButton = (Button) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        progressBarSend = (ProgressBar) view.findViewById(R.id.progressBarSend);
        progressBarSend.getIndeterminateDrawable().setColorFilter(rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        layoutSendParkour = (LinearLayout) view.findViewById(R.id.layoutSendParkour);
        layoutSendParkour.setVisibility(View.GONE);

        newParcoursButton = (Button) view.findViewById(R.id.newParcoursButton);
        newParcoursButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateParametres thread = new updateParametres();
        thread.execute();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //On regarde si on recoit bie le bon résultat, ce qui veux dire que l'on a téléchargé un parcours
        //dans l'activité NewParkour
        if(resultCode==111 && requestCode==2)
        {
            //On update l'onglet 1
            updateParametres thread = new updateParametres();
            thread.execute();
            //On update la listView du fragment 3 (onglet parcours)
            mCallback.communicateToFragment3();
            //Changer affichage de l'onglet 2
            mCallback.communicateToFragment2();

        }
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

            case R.id.sendButton:
                new AlertDialog.Builder(getActivity())
                        .setMessage("Ceci effacera votre parcours sur le téléphone et l'enverra vers le serveur" +
                                "\nEtes-vous sûr ?")
                        .setCancelable(false)
                        .setPositiveButton("Envoyer le parcours", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                envoyerParcours();
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
                break;

            case R.id.newParcoursButton:
                //On lance l'activité NewParkour avec un contrôle du résultat
                Intent intent = new Intent(getActivity(), NewParkour.class);
                startActivityForResult(intent,2);
                break;
        }
    }

    public void afficherJoueurs(String response, String choix, TextView headerTxt, LinearLayout layout){

        try {
            String nomEquipe = "";
            String categorie = "";
            String num_parcours = "";

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
                        if(choix.equals("onStart")){
/*                            numCourse.setText(obj.get("numCourse").toString());
                            numEquipe.setText(obj.get("numEquipe").toString());
                            date.setText(obj.get("date_naissance").toString());
*/
                            ((TextView) view.findViewById(R.id.textView_parcoursEnCours)).setText(
                                    "Course " + obj.get("numCourse").toString() + "  -  Equipe " + obj.get("numEquipe").toString()
                            );
                        }
                    }

                    TextView joueur = new TextView(getActivity());
                    joueur.setText(obj.get("prenom").toString() + " " + obj.get("nom").toString());
                    joueur.setPadding(20,10,0,10);
                    joueur.setBackgroundResource(R.drawable.bg_players);
                    joueur.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    layout.addView(joueur);

                }

                //changer le header du tableau pour mettre le nom de l'équipe
                headerTxt.setText("Joueurs de l'équipe \""+ nomEquipe +"\" catégorie " + categorie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class updateParametres extends AsyncTask<Void, Void, Void> {

        boolean parcoursFull;
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

            DBController controller = new DBController(getActivity());

            parcoursFull = controller.checkParcours();


            if (parcoursFull)
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
            if (parcoursFull)
            {
                TextView itemHeader2 = (TextView) view.findViewById(R.id.itemHeader2);
                LinearLayout layoutJoueurs = (LinearLayout) view.findViewById(R.id.layoutPlayers2);
                afficherJoueurs(parametres,"onStart",itemHeader2,layoutJoueurs);

                //Afficher le layout parcours Téléchargé
                view.findViewById(R.id.parcoursPresent).setVisibility(View.VISIBLE);

            }
            else
            {
                //cacher le layout parcours Téléchargé
                view.findViewById(R.id.parcoursPresent).setVisibility(View.GONE);
            }
        }
    }

    public void envoyerParcours(){

        DBController controller = new DBController(getActivity());

        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        sendButton.setVisibility(View.INVISIBLE);
        layoutSendParkour.setVisibility(View.VISIBLE);

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        String ipServer = Utils.getWifiApIpAddress(getActivity());

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

                Utils.showToast(getActivity(),"Le parcours a bien été envoyé !","long");

                sendButton.setVisibility(View.VISIBLE);
                layoutSendParkour.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                // Hide ProgressBar

                sendButton.setVisibility(View.VISIBLE);
                layoutSendParkour.setVisibility(View.GONE);

                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 404) {
                    Utils.showToast(getActivity(),"Error " + statusCode + "\nRequested resource not found","long");
                } else if (statusCode == 500) {
                    Utils.showToast(getActivity(),"Error " + statusCode + "\nSomething went wrong at server end","long");
                } else {
                    Utils.showToast(getActivity(),"Error " + statusCode + "\nUnexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet]","long");
                }
            }
        });
    }

}
