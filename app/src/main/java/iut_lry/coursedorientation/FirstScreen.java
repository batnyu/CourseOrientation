package iut_lry.coursedorientation;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class FirstScreen extends AppCompatActivity implements View.OnClickListener {

    IntentIntegrator integrator;
    List<String> messagesBalises = new ArrayList<String>();
    private ListView lv;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        Button scanButton = (Button) findViewById(R.id.scan_button);


        scanButton.setOnClickListener(this);

        lv = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                messagesBalises );
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scan_button){

            //On lance le scanner au clic sur notre bouton
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
            integrator.setOrientationLocked(false);
            integrator.setPrompt("Encadrez le code-barres d'une balise pour la valider !");
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
            } else {
                //Récupération du contenu du scan
                String scanContent = result.getContents();
                String scanFormat = result.getFormatName();
                //Affichage dans les TextViews
                TextView scan_format = (TextView) findViewById(R.id.scan_format);
                TextView scan_content = (TextView) findViewById(R.id.scan_content);

                scan_format.setText("FORMAT: " + scanFormat);
                scan_content.setText("CONTENT: " + scanContent);

                //Ajout dans la liste et refresh
                messagesBalises.add(scanContent);
                lv.setAdapter(arrayAdapter);

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
