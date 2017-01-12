package iut_lry.coursedorientation;

import android.content.Context;
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

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;
    private Button btnFtoA;
    private Button btnFtoF;

    EditText numCourse;
    EditText numEquipe;

    String numCourseStr;
    String numEquipeStr;

    String ipServer;
    TextView infosWifi;
    Button buttonWifi;
    private Button dllParkour;

    // DB Class to perform DB related operations
    DBController controller;

    HashMap<String, String> queryValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        numCourse = (EditText) view.findViewById(R.id.numCourse);
        numEquipe = (EditText) view.findViewById(R.id.numEquipe);


        //A completer pour enlever le focus quand on enleve le truc ou alors quand on appuie sur le bouton
        numCourse.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(numCourse.getWindowToken(), 0);
                    numCourse.setFocusable(false);
                    numCourse.setFocusableInTouchMode(true);
                    return true;
                } else {
                    return false;
                }
            }
        });

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

        dllParkour = (Button) view.findViewById(R.id.dllParkour);
        dllParkour.setOnClickListener(this);

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
                dllParkour.setEnabled(false);
                dllParkour.setText("Téléchargement en cours");
                syncSQLiteMySQLDB();
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

        mCallback.afficherProgressBar();

        client.setConnectTimeout(5000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou
        client.setResponseTimeout(5000); // as above
        client.setTimeout(5000); // both connection and socket timeout
        client.setMaxRetriesAndTimeout(1, 100); // times, delay

        // Make Http call to getusers.php, Ne pas oublier le port sinon ca bug
        client.post("http://192.168.1.52:80/testProjet/getusersPDO.php", params, new AsyncHttpResponseHandler() {
            //http://192.168.1.13/testProjetV2/getusersPDO.php
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"

                mCallback.cacherProgressBar();
                dllParkour.setText("Télécharger le parcours");
                dllParkour.setEnabled(true);
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

                mCallback.cacherProgressBar();
                dllParkour.setEnabled(true);
                dllParkour.setText("Télécharger le parcours");
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
                NumberFormat percentage = NumberFormat.getPercentInstance();
                prgDialog.setProgress((int)progressPercentage);
                prgDialog.setProgressPercentFormat(percentage);
                //a revoir si on veut la barre de progression mais galere
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

            updateSQLite(responseStringThread);

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
        }

    }


    public void updateSQLite(String response){

        //test pour reset table qd télécharge le parcours
        controller.deleteTable("parcoursLite");

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
                    System.out.println(obj.get("id"));
                    System.out.println(obj.get("numCourse"));
                    System.out.println(obj.get("numEquipe"));
                    System.out.println(obj.get("balise"));
                    System.out.println(obj.get("temps"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add userID extracted from Object
                    queryValues.put("id", obj.get("id").toString());
                    // Add userName extracted from Object
                    queryValues.put("numCourse", obj.get("numCourse").toString());
                    // Add userName extracted from Object
                    queryValues.put("numEquipe", obj.get("numEquipe").toString());
                    // Add userName extracted from Object
                    queryValues.put("balise", obj.get("balise").toString());
                    // Add temps extracted from Object
                    queryValues.put("temps", obj.get("temps").toString());
                    // Insert User into SQLite DB
                    controller.insertUser(queryValues);
                }

                //On récupère la course et l'équipe
                numCourse = (EditText) getActivity().findViewById(R.id.numCourse);
                numEquipe = (EditText) getActivity().findViewById(R.id.numEquipe);

                numCourseStr = numCourse.getText().toString();
                numEquipeStr = numEquipe.getText().toString();

                controller.updateCourseEquipe(numCourseStr, numEquipeStr);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
