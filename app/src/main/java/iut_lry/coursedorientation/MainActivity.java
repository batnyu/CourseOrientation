package iut_lry.coursedorientation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    public void showToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void hideKeyboard(){
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

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
