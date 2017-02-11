package iut_lry.coursedorientation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static android.R.id.progress;

public class TabFragment2 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    private Button scanButton;
    IntentIntegrator integrator;
    Calendar rightNow;
    Chronometer timeElapsed;

    DBController controller;

    boolean departOK;

    IntentResult result;
    String scanContent;
    String scanFormat;
    String temps;
    int resultat;

    TextView totalBalises;
    ProgressBar progressTotal;
    int[] scannéSurTotal;

    TextView baliseDepart;
    String nbBaliseDepart;
    TextView balisePointee;
    String nbBalisePointee;
    TextView baliseSuivante;
    String nbBaliseSuivante;

    String[] baliseActuelle;

    LinearLayout interface2;
    TextView noParcours2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        scanButton = (Button) view.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        controller = new DBController(getActivity());

        totalBalises = (TextView) view.findViewById(R.id.textView_total_balises_nb);
        progressTotal = (ProgressBar) view.findViewById(R.id.progressBarTotal);

        baliseDepart = (TextView) view.findViewById(R.id.textView_balise_depart_nb);
        balisePointee = (TextView) view.findViewById(R.id.textView_balise_pointee_nb);
        baliseSuivante = (TextView) view.findViewById(R.id.textView_balise_suivante_nb);

        interface2 = (LinearLayout) view.findViewById(R.id.interface2);
        noParcours2 = (TextView) view.findViewById(R.id.noParcours2);

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

        fragmentCommunication2();
        //vérif tableau.

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

            }
            else {

                //Récupération du contenu du scan
                scanContent = result.getContents();
                scanFormat = result.getFormatName();

                /*
                //récupère le temps actuel SERT PLUS A RIEN
                rightNow = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                temps = format.format(rightNow.getTime());
                */

                ReceiveData test = new ReceiveData();
                test.execute();

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

    public class ReceiveData extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
        /*
         *    do things before doInBackground() code runs
         *    such as preparing and showing a Dialog or ProgressBar
        */
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        /*
         *    updating data
         *    such a Dialog or ProgressBar
        */
            if(values[0] == 1) {
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
            }
            else if(values[0] == 3)
            {
                temps = timeElapsed.getText().toString();
                Toast.makeText(getActivity(), "La balise n°" + scanContent + " a été scanné ! " + temps, Toast.LENGTH_LONG).show();

                timeElapsed.setBase(SystemClock.elapsedRealtime());
                timeElapsed.start();
                departOK = true;
            }
            else if(values[0] == 2)
            {
                Toast.makeText(getActivity(), "La balise n°" + scanContent + " a déjà été scanné !", Toast.LENGTH_LONG).show();
            }
            else if(values[0] == 4)
            {
                Toast.makeText(getActivity(), "La balise n°" + scanContent + " n'est pas la balise de départ !", Toast.LENGTH_LONG).show();
            }
            else if(values[0] == 5)
            {
                Toast.makeText(getActivity(), "La balise n°" + scanContent + " n'est pas la balise suivante !", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(), "La balise n°" + scanContent + " n'est pas dans le parcours !", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Void doInBackground(String... parametres) {
            //do your work here



            resultat = controller.checkBalise(scanContent, temps, departOK, nbBaliseDepart, nbBaliseSuivante);

            publishProgress(resultat);

            //On doit attendre pour que le programme ait le temps de mettre à jour la variable temps dans onProgressUpdate
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(resultat == 1 || resultat == 3)
            {
                //Update du temps dans la base de données
                controller.UpdateTemps(scanContent,temps);

                //pour pouvoir vérifier si la suivante a déjà été pointé
                //mettre à jour la dernière balise pointée et sa suivante
                baliseActuelle = controller.getBaliseActuelle();
                //stocker la variable pour vérifier quand on scanne.
                nbBaliseSuivante = baliseActuelle[1];

                controller.updateNextAlreadyChecked(nbBaliseSuivante);
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
            //Affichage dans les TextViews
            TextView scan_format = (TextView) getActivity().findViewById(R.id.scan_format);
            TextView scan_content = (TextView) getActivity().findViewById(R.id.scan_content);

            scan_format.setText("FORMAT: " + scanFormat);
            scan_content.setText("CONTENT: " + scanContent);

            if(resultat == 1 || resultat == 3)
            {
                //On update la listView du fragment 3 (onglet parcours)
                mCallback.communicateToFragment3();
                //on update les infos de l'onglet "infos"
                updateInfos();

            }
            //au hasard
            resultat = 54;

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

    public void updateInfos() {

        //faire un thread

        //on récupère les données dans la base
        scannéSurTotal = controller.getNbCheckpoints();

        //on met *100 pour voir l'animation
        progressTotal.setMax(scannéSurTotal[1] * 100);
        //sans animation
        //progressTotal.setProgress(scannéSurTotal[0]);
        //avec animation
        ObjectAnimator animation = ObjectAnimator.ofInt(progressTotal, "progress", scannéSurTotal[0]*100);
        animation.setDuration(1000); // 1.5 second
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        //pour mettre à jour le numéro à la fin de l'animation
        animation.addListener(new AnimatorListenerAdapter() {
              public void onAnimationEnd(Animator animation) {
                  totalBalises.setText(scannéSurTotal[0] + "/" + scannéSurTotal[1]);
              }
        });


        //mettre à jour la balise de départ
        nbBaliseDepart = controller.getFirstBalise();
        baliseDepart.setText(nbBaliseDepart);

        //mettre à jour la dernière balise pointée et sa suivante
        baliseActuelle = controller.getBaliseActuelle();

        balisePointee.setText(baliseActuelle[0]);
        baliseSuivante.setText(baliseActuelle[1]);

        //stocker la variable pour vérifier quand on scanne.
        nbBaliseSuivante = baliseActuelle[1];
    }

    public void fragmentCommunication2() {

        ArrayList<HashMap<String, String>> baliseList;
        // Get User records from SQLite DB

        baliseList = controller.getAllBalises();

        if (baliseList.size() != 0) {

            //afficher l'interface du deuxieme onglet et cacher la phrase
            interface2.setVisibility(LinearLayout.VISIBLE);
            noParcours2.setVisibility(LinearLayout.GONE);

            //si la base de données est déjà remplie, on update les infos
            updateInfos();

            //fonction a mettre en place pour récupérer le bon chrono si l'activité s'arrete.
            departOK = true;

        }
        else
        {
            timeElapsed.stop();
            timeElapsed.setText("00:00:00");
            departOK = false;

            //cacher l'interface du deuxieme onglet et afficher la phrase
            interface2.setVisibility(LinearLayout.GONE);
            noParcours2.setVisibility(LinearLayout.VISIBLE);
        }
    }

}