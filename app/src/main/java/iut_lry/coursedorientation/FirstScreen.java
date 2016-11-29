package iut_lry.coursedorientation;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// on importe les classes IntentIntegrator et IntentResult

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FirstScreen extends AppCompatActivity implements View.OnClickListener {

    IntentIntegrator integrator;

    Calendar rightNow;

    baliseHeureAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        Button scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        // Construct the data source
        ArrayList<baliseHeure> arrayOfbaliseHeure = new ArrayList<baliseHeure>();
        // Create the adapter to convert the array to views
        adapter = new baliseHeureAdapter(this, arrayOfbaliseHeure);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //header, on verra plus tard
        /*TextView header = new TextView(getBaseContext());
        header.setTextColor(Color.BLACK);
        header.setText("Numéro Balise     ->     Temps");
        lv.addHeaderView(header);*/
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scan_button){

            //On lance le scanner au clic sur notre bouton
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
            integrator.setOrientationLocked(false);
            integrator.setPrompt("Encadrez le code-barres d'une balise pour la valider !");
            integrator.setBeepEnabled(false);
            integrator.initiateScan();

        }
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Scan annulé !", Toast.LENGTH_LONG).show();

                //TODO jouez son echec scan

            } else {
                //Récupération du contenu du scan
                String scanContent = result.getContents();
                String scanFormat = result.getFormatName();


                //Affichage dans les TextViews
                TextView scan_format = (TextView) findViewById(R.id.scan_format);
                TextView scan_content = (TextView) findViewById(R.id.scan_content);

                scan_format.setText("FORMAT: " + scanFormat);
                scan_content.setText("CONTENT: " + scanContent);


                //récupère le temps actuel
                rightNow = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                String temps = format.format(rightNow.getTime());

                baliseHeure baliseHeure = new baliseHeure(scanContent,temps);

                //Ajout dans la liste et refresh
                adapter.add(baliseHeure);
                listView.setAdapter(adapter);



                Toast.makeText(this, "Scanné : " + result.getContents(), Toast.LENGTH_LONG).show();


                //Joué un son
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.zxing_beep);
                mp.start();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
