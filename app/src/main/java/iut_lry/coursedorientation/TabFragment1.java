package iut_lry.coursedorientation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class TabFragment1 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;
    private Button btnFtoA;
    private Button btnFtoF;
    String ipServer;
    TextView adresseIP;
    Button test;
    private Button dllParkour;

    //TEST
    // DB Class to perform DB related operations
    DBController controller;
    //DBController controller = ((MainActivity)this.getActivity()).getController();
    // Progress Dialog Object
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;

    public boolean activiteCreated = false;
    DrawerLayout mDrawer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        btnFtoA = (Button) view.findViewById(R.id.button);
        btnFtoF = (Button) view.findViewById(R.id.button2);
        btnFtoA.setOnClickListener(this);
        btnFtoF.setOnClickListener(this);

        adresseIP = (TextView) view.findViewById(R.id.textView3);
        test = (Button) view.findViewById(R.id.button3);
        test.setOnClickListener(this);

        dllParkour = (Button) view.findViewById(R.id.dllParkour);
        dllParkour.setOnClickListener(this);

        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(getActivity());
        //prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");
        prgDialog.setMessage("Transfert en cours du parcours présent sur le serveur.\nPatientez svp...");
        prgDialog.setCancelable(false);

        /*// BroadCase Receiver Intent Object
        Intent alarmIntent = new Intent(getActivity().getApplicationContext(), SampleBC.class);
        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        // Remote MySQL DB
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 10 * 1000, pendingIntent);
        */

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controller = new DBController(getActivity());

        activiteCreated = true;

        afficherInfoWifi();

        /*
        //TEST MACHIN BDD
        ArrayList<HashMap<String, String>> userList;
        // Get User records from SQLite DB

        userList = controller.getAllUsers();
        // If users exists in SQLite DB
        if (userList.size() != 0) {

            // Set the User Array list in ListView
            ListAdapter adapter = new SimpleAdapter(getActivity(), userList, R.layout.item_baliseheure, new String[] {
                    "id", "balise", "temps" }, new int[] {0, R.id.numBalise, R.id.heureBalise });
            ListView myList = (ListView) getActivity().findViewById(R.id.listView2);
            myList.setAdapter(adapter);

        }*/
    }

    //mettre à jour les infos wifi quand l'application est repris sans avoir été arreté
    @Override
    public void onResume() {
        afficherInfoWifi();
        super.onResume();
    }

    //update les infos wifi quand on active l'onglet
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && activiteCreated) {
            afficherInfoWifi();
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
        Toast.makeText(getActivity(), "Fragment 1: Refresh called.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                mCallback.showToast("Hello from Fragment 1");
                break;

            case R.id.button2:
                mCallback.communicateToFragment2();
                break;

            case R.id.button3:
                afficherInfoWifi();
                break;

            case R.id.dllParkour:
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
            adresseIP.setText("Vous êtes connecté au réseau Wifi " + nameServer +
                    " situé à l'adresse IP " + ipServer);
        }
        else
        {
            adresseIP.setText("Vous n'êtes connecté à aucun réseau wifi !");
        }
    }

    // Method to Sync MySQL to SQLite DB
    public void syncSQLiteMySQLDB() {


        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //prgDialog.setProgressNumberFormat("");
        //prgDialog.setTitle("Téléchargement du parcours");
        prgDialog.show();

        client.setConnectTimeout(1000);
        //en mettant un temps de 1sec, on déclenche l'erreur connectTimeoutException qui
        // est repéré par onFailure contrairement à host unreachable
        // à étudié c'est relou

        // Make Http call to getusers.php
        client.post("http://192.168.1.52/testProjet/getusersPDO.php", params, new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            // called before request is started
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            // called when response HTTP status is "200 OK"
            // Hide ProgressBar
            //prgDialog.setMessage("Le parcours a été téléchargé !");
            //prgDialog.setCancelable(true);
            //prgDialog.setCanceledOnTouchOutside(true);
            prgDialog.hide();
            Toast.makeText(getActivity().getApplicationContext(), "Le parcours a bien été téléchargé !", Toast.LENGTH_LONG).show();

            // Update SQLite DB with response sent by getusers.php
            String responseString = null;
            try {
                responseString = new String(response, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            updateSQLite(responseString);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            // Hide ProgressBar
            prgDialog.hide();
            if (statusCode == 404) {
                Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nRequested resource not found", Toast.LENGTH_LONG).show();
            } else if (statusCode == 500) {
                Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nSomething went wrong at server end", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Error " + statusCode + "\nUnexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
        }

        @Override //marche pas
        public void onUserException(java.lang.Throwable error) {
            prgDialog.hide();
            Toast.makeText(getActivity().getApplicationContext(), "Erreur : " + error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void updateSQLite(String response){
        //test pour reset table qd télécharge le parcours
        controller.deleteTable("parcoursLite");

        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
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
                    System.out.println(obj.get("balise"));
                    System.out.println(obj.get("temps"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add userID extracted from Object
                    queryValues.put("id", obj.get("id").toString());
                    // Add userName extracted from Object
                    queryValues.put("balise", obj.get("balise").toString());
                    // Add temps extracted from Object
                    queryValues.put("temps", obj.get("temps").toString());
                    // Insert User into SQLite DB
                    controller.insertUser(queryValues);
                    /*
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("id").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                    */
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                //updateMySQLSyncSts(gson.toJson(usersynclist));
                // Reload the Fragment
                reloadFragment();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Reload MainActivity
    public void reloadFragment() {
        //pour reload l'activité
        //Intent objIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        //startActivity(objIntent);
        //CA MARCHE qu'avec un reload du fragment! c'est plus fluide a lecran
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
