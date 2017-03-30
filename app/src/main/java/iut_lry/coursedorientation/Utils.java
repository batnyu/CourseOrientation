package iut_lry.coursedorientation;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by Baptiste on 14/03/2017.
 */

public class Utils {

    public static void showToast(Context context, String msg, String duree) {

        if(duree.equals("long")) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void hideKeyboard(@NonNull Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static String getWifiApIpAddress(Context context) {
        String myIP = null;
        final WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //On récupère les infos du dhcp
        final DhcpInfo dhcp = manager.getDhcpInfo();
        //Conversion pour avoir l'IP en octet
        byte[] myIPAddress = BigInteger.valueOf(dhcp.gateway).toByteArray();
        // you must reverse the byte array before conversion.
        ArrayUtils.reverse(myIPAddress);
        InetAddress myInetIP = null;
        try {
            myInetIP = InetAddress.getByAddress(myIPAddress);
            myIP = myInetIP.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d("erreur ",e.getMessage());
            showToast(context,"Veuillez activer votre Wi-fi, connectez-vous au réseau de " +
                              "l'organisateur et réessayez.","long");
            myIP = "erreur";
        }
        return myIP;
    }

    public static void vibrer(Context context)
    {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 250, 130, 250};
        v.vibrate(pattern, -1);
    }

    public  static String getWifiNetworkName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getSSID();
    }

/*    @Override
    public void communicateToFragment2() {
        TabFragment2 fragment = (TabFragment2) adapter.getFragment(1);
        if (fragment != null) {
            fragment.fragmentCommunication2();
        } else {
            Log.i(LOG_TAG, "Fragment 2 is not initialized");
        }
    }

    @Override
    public void communicateToFragment3() {
        TabFragment3 fragment = (TabFragment3) adapter.getFragment(2);
        if (fragment != null) {
            fragment.fragmentCommunication3();
        } else {
            Log.i(LOG_TAG, "Fragment 3 is not initialized");
        }
    }*/

}
