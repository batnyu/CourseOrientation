package iut_lry.coursedorientation;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Context;
import android.widget.Button;
import android.widget.Chronometer;
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
    Chronometer timeElapsed;

    DBController controller;

    boolean departOK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        scanButton = (Button) view.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        timeElapsed  = (Chronometer) getActivity().findViewById(R.id.chronometer1);
        timeElapsed.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(hh+":"+mm+":"+ss);
            }
        });
        timeElapsed.setText("00:00:00");
        departOK = false;

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

                //récupère le temps actuel SERT PLUS A RIEN
                rightNow = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                String temps = format.format(rightNow.getTime());

                controller = new DBController(getActivity());
                int resultat = controller.checkBaliseUpdateTemps(scanContent, temps, departOK);

                if(resultat == 1)
                {
                    //Si on fait ca, on a le temps quand on appuie sur le bouton SCAN
                    //et pas quand on obtient le résultat
                    //temps = timeElapsed.getText().toString();
                    //Donc obligé de refaire ça :
                    long time = SystemClock.elapsedRealtime() - timeElapsed.getBase();
                    int h   = (int)(time /3600000);
                    int m = (int)(time - h*3600000)/60000;
                    int s= (int)(time - h*3600000- m*60000)/1000 ;
                    String hh = h < 10 ? "0"+h: h+"";
                    String mm = m < 10 ? "0"+m: m+"";
                    String ss = s < 10 ? "0"+s: s+"";
                    temps = hh+":"+mm+":"+ss;


                    Toast.makeText(getActivity(), "La balise n°" + scanContent + " a été scanné ! " + temps, Toast.LENGTH_LONG).show();
                    //Update de le temps dans la base de données
                    controller.UpdateTemps(scanContent,temps);
                    //Update l'affichage de la liste du fragment 3
                    mCallback.communicateToFragment3();
                }
                else if(resultat == 3)
                {
                    temps = timeElapsed.getText().toString();
                    Toast.makeText(getActivity(), "La balise n°" + scanContent + " a été scanné ! " + temps, Toast.LENGTH_LONG).show();
                    //Update de le temps dans la base de données
                    controller.UpdateTemps(scanContent,temps);
                    //Update l'affichage de la liste du fragment 3
                    mCallback.communicateToFragment3();
                    timeElapsed.setBase(SystemClock.elapsedRealtime());
                    timeElapsed.start();
                    departOK = true;
                }
                else if(resultat == 2)
                {
                    Toast.makeText(getActivity(), "La balise n°" + scanContent + " a déjà été scanné !", Toast.LENGTH_LONG).show();
                }
                else if(resultat == 4)
                {
                    Toast.makeText(getActivity(), "La balise n°" + scanContent + " n'est pas la balise de départ !", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "La balise n°" + scanContent + " n'est pas dans le parcours !", Toast.LENGTH_LONG).show();
                }

                /*//Joué un son -> BUG
                final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.zxing_beep);
                mp.setVolume(10,10);
                mp.start();*/

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