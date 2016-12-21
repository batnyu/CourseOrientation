package iut_lry.coursedorientation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Context;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TabFragment2 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    private Button scanButton;
    IntentIntegrator integrator;
    Calendar rightNow;
    baliseHeureAdapter adapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        scanButton = (Button) view.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        // Construct the data source
        ArrayList<baliseHeure> arrayOfbaliseHeure = new ArrayList<baliseHeure>();
        // Create the adapter to convert the array to views
        adapter = new baliseHeureAdapter(getActivity(), arrayOfbaliseHeure);
        // Attach the adapter to a ListView
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scan_button){

            //On lance le scanner au clic sur notre bouton
            IntentIntegrator.forSupportFragment(this)
                    .setCaptureActivity(CaptureActivityAnyOrientation.class)
                    .setPrompt("Encadrez le code-barres d'une balise pour la valider !")
                    .setBeepEnabled(false)
                    .setOrientationLocked(false).initiateScan();
        }
    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan annulé !", Toast.LENGTH_LONG).show();

                //TODO jouez son echec scan

            } else {
                //Récupération du contenu du scan
                String scanContent = result.getContents();
                String scanFormat = result.getFormatName();

                //Affichage dans les TextViews
                TextView scan_format = (TextView) getActivity().findViewById(R.id.scan_format);
                TextView scan_content = (TextView) getActivity().findViewById(R.id.scan_content);

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


                Toast.makeText(getActivity(), "Scanné : " + result.getContents(), Toast.LENGTH_LONG).show();


                //Joué un son -> BUG
                final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.zxing_beep);
                mp.setVolume(10,10);
                mp.start();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
        Toast.makeText(getActivity(), "Fragment 2: Refresh called.",
                Toast.LENGTH_SHORT).show();
    }

    public void fragmentCommunication2() {
        //mTextView1.setText("Hello from Tab Fragment 1");

    }
}