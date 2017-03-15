package iut_lry.coursedorientation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static android.graphics.Color.rgb;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    View view;
    Button nouveauParcoursButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        nouveauParcoursButton = (Button) view.findViewById(R.id.nouveauParcoursButton);
        nouveauParcoursButton.setOnClickListener(this);


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

            case R.id.nouveauParcoursButton:
                //On lance l'activité NewParkour avec un contrôle du résultat
                Intent intent = new Intent(getActivity(), NewParkour.class);
                startActivityForResult(intent,2);
                break;
        }
    }


    public String getWifiNetworkName() {
        WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getSSID();
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

            }
            else
            {
                //cacher le layout parcours Téléchargé
                view.findViewById(R.id.parcoursPresent).setVisibility(View.GONE);
            }
        }

    }

}
