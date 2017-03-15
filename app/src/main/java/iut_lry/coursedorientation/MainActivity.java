package iut_lry.coursedorientation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IFragmentToActivity {
    private final String LOG_TAG = "MainActivity";
    private PagerAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("Paramètres");
        tabs.add("Infos");
        tabs.add("Parcours");
        //tabs.add("Course TABLEAU");
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2); //pour garder les 2 fragments dans le cache
        adapter = new PagerAdapter(getSupportFragmentManager(), tabs);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //j'ai enlevé le menu pour l'instant
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            int position = tabLayout.getSelectedTabPosition();
            Fragment fragment = adapter.getFragment(tabLayout
                    .getSelectedTabPosition());
            if (fragment != null) {
                switch (position) {
                    case 0:
                        ((TabFragment1) fragment).onRefresh();
                        break;
                    case 1:
                        ((TabFragment2) fragment).onRefresh();
                        break;
                    case 2:
                        ((TabFragment3) fragment).onRefresh();
                        break;
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showToast(String msg, String duree) {

        if(duree.equals("long")) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

    }

    public void hideKeyboard(){
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getWifiApIpAddress() {
        String myIP = null;
        final WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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
            showToast("Veuillez activer votre Wi-fi, connectez-vous au réseau de l'organisateur et réessayez.","long");
            myIP = "erreur";
        }

        return myIP;
    }

    public void vibrer()
    {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 250, 130, 250};
        v.vibrate(pattern, -1);
    }

/*    @Override
    public void onResume() {
        super.onResume();
        communicateToFragment2();
        communicateToFragment3();
        Utils.showToast(MainActivity.this,"RESTART","long");
    }*/

    @Override
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Override this method in the activity that hosts the Fragment and call super
        // in order to receive the result inside onActivityResult from the fragment.
        super.onActivityResult(requestCode, resultCode, data);
    }

}
