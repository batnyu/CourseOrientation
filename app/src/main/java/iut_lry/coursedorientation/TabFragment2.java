package iut_lry.coursedorientation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Context;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class TabFragment2 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    private Button scanButton;
    Chronometer timeElapsed;

    // Declare your shared preference file name
    private static final String PREF_NAME = "info";

    DBController controller;

    boolean departOK;

    String scanContent = "";
    String scanFormat;
    String temps;


    TextView totalBalises;

    String nbBaliseDepart;

    String baliseSuivante;
    String nbBaliseSuivante;
    String pocheActuelle;

    String[] baliseActuelle;

    String infoScan = "";

    boolean activiteRedemarre;

    int resultat = 54;

    LinearLayout liaisonsBalise;
    LinearLayout pochesBalise;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        scanButton = (Button) view.findViewById(R.id.scan_button);

        scanButton.setOnClickListener(this);

        controller = new DBController(getActivity());

        totalBalises = (TextView) view.findViewById(R.id.textView_total_balises_nb);

        liaisonsBalise = (LinearLayout) view.findViewById(R.id.liaisonsBalise);
        pochesBalise = (LinearLayout) view.findViewById(R.id.pocheBalise);

/*        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextField.setText("done!");
            }
        }.start();*/

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

        activiteRedemarre = true;

        fragmentCommunication2();
        //vérif tableau.
    }

    @Override
    public void onResume() {
        super.onResume();

        //Create a object SharedPreferences from getSharedPreferences("name_file",MODE_PRIVATE) of Context
        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        long heureExit = preferences.getLong("heureExit",0);
        long tempsChrono = preferences.getLong("tempsChrono",0);


        Log.d("ALLLLLLLLLLLLLOOOOOOOOO", "onResume: heureExit = "+heureExit +"\n tempsChrono = " + tempsChrono);

        if(heureExit != 0 && tempsChrono != 0)
        {
            Calendar now = Calendar.getInstance();
           /* SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");*/
            long tempsActuel = now.getTimeInMillis();

            //On calcule le délai pr l'ajouter au chrono.
            long diff = tempsActuel - heureExit;

            //On ajoute le délai au chrono enregistré
            long bonneHeure = tempsChrono + diff;

            int h   = (int)(bonneHeure /3600000);
            int m = (int)(bonneHeure - h*3600000)/60000;
            int s= (int)(bonneHeure - h*3600000- m*60000)/1000 ;
            String hh = h < 10 ? "0"+h: h+"";
            String mm = m < 10 ? "0"+m: m+"";
            String ss = s < 10 ? "0"+s: s+"";
            String bonneHeureString = hh+":"+mm+":"+ss;

            timeElapsed.setText(bonneHeureString);
            timeElapsed.setBase(SystemClock.elapsedRealtime() - (1000*s + 1000*60*m + 1000*60*60*h));
            timeElapsed.start();
            //Utils.showToast(getActivity(),"Parcours repris à " + bonneHeureString,"long");
            departOK = true;

            //On supprime les données enregistrées.
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

/*            SharedPreferences.Editor editor2 = preferences.edit();
            editor2.putLong("heureExit",0);
            editor2.putLong("tempsChrono",0);
            editor2.apply();*/

            Log.d("ALLLLLLLLLLLLLOOOOOOOOO", "onActivityCreated: ON remet LES TEMPS");

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

                //BALISE TEST
                if(scanContent.equals("Balise TEST")) {
                    Utils.showToast(getActivity(),"La balise TEST a été scanné !","court");
                    infoScan = "[<font color='#2f9e00'>OK</font>] La balise TEST a été scanné ! ";
                    ((TextView) view.findViewById(R.id.textView_infoScan)).setText(fromHtml(infoScan), TextView.BufferType.SPANNABLE);

                } else {
                    ReceiveData test = new ReceiveData();
                    test.execute();
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

    public class ReceiveData extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
        /*
         *    do things before doInBackground() code runs
         *    such as preparing and showing a Dialog or ProgressBar
        */
            //empêcher un autre scan en même temps.
            scanButton.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... parametres) {
            //do your work here

            resultat = controller.checkBalise(scanContent, departOK, nbBaliseDepart, baliseSuivante, nbBaliseSuivante, pocheActuelle);

            //On remplace le contenu du QrCode (String aléatoire) par le num de la balise correspondante
            scanContent = controller.getNumBalise(scanContent);


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        /*
         *    do something with data here
         *    display it or send to mainactivity
         *    close any dialogs/ProgressBars/etc...
        */

            if(resultat == 1 || resultat == 12) {
                //Si on fait ca, on a le temps quand on appuie sur le bouton SCAN
                //et pas quand on obtient le résultat
                //temps = timeElapsed.getText().toString();
                //Donc obligé de refaire ça :
                long timeChrono = SystemClock.elapsedRealtime() - timeElapsed.getBase();
                int h   = (int)(timeChrono /3600000);
                int m = (int)(timeChrono - h*3600000)/60000;
                int s= (int)(timeChrono - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                temps = hh+":"+mm+":"+ss;

                if(resultat == 1)
                {
                    infoScan = "[<font color='#2f9e00'>OK</font>] La balise n°" + scanContent + " a été scanné ! " + temps;
                    Utils.showToast(getActivity(),"La balise n°" + scanContent + " a été scanné ! " + temps,"long");
                }
                else
                {
                    infoScan = "[<font color='#2f9e00'>OK</font>] La balise n°" + scanContent + " a été scanné ! " + temps + "\nVous avez quitté la poche " + pocheActuelle;
                    Utils.showToast(getActivity(),"La balise n°" + scanContent + " a été scanné ! " + temps + "\nVous avez quitté la poche " + pocheActuelle,"long");
                }


            }
            else if(resultat == 3)
            {
                temps = timeElapsed.getText().toString();
                infoScan = "[<font color='#2f9e00'>OK</font>] La balise n°" + scanContent + " a été scanné ! " + temps;
                Utils.showToast(getActivity(),"La balise n°" + scanContent + " a été scanné ! " + temps,"long");

                timeElapsed.setBase(SystemClock.elapsedRealtime());
                timeElapsed.start();
                departOK = true;
            }
            else if(resultat == 2)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] La balise n°" + scanContent + " a déjà été scanné !";
                Utils.showToast(getActivity(),"La balise n°" + scanContent + " a déjà été scanné !","long");

            }
            else if(resultat == 4)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] La balise n°" + scanContent + " n'est pas la balise de départ !";
                Utils.showToast(getActivity(),"La balise n°" + scanContent + " n'est pas la balise de départ !","long");

            }
            else if(resultat == 5)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] La balise n°" + scanContent + " n'est pas la balise suivante !";
                Utils.showToast(getActivity(),"La balise n°" + scanContent + " n'est pas la balise suivante !","long");

            }
            else if(resultat == 6)
            {
                infoScan =  "[<font color='#db1500'>Erreur</font>] Vous avez déjà fini le parcours!";
                Utils.showToast(getActivity(),"Vous avez déjà fini le parcours!","long");

            }
            else if(resultat == 7)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] Vous n'êtes pas rentré dans la poche de cette balise!";
                Utils.showToast(getActivity(),"Vous n'êtes pas rentré dans la poche de cette balise!","long");

            }
            else if(resultat == 8)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] La balise ne fait pas partie de la poche actuelle! Veuillez sortir de la poche.";
                Utils.showToast(getActivity(),"La balise ne fait pas partie de la poche actuelle! Veuillez sortir de la poche.","long");

            }
            else if(resultat == 15)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] La balise n'est pas la bonne suivante et vous n'avez pas quitté la poche!";
                Utils.showToast(getActivity(),"La balise n'est pas la bonne suivante et vous n'avez pas quitté la poche!","long");

            }
            else if(resultat == 10)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] Cas non traité!";
                Utils.showToast(getActivity(),"Cas non traité!","long");

            }
            else if(resultat !=54)
            {
                infoScan = "[<font color='#db1500'>Erreur</font>] La balise scannée n'est pas dans le parcours !";
                Utils.showToast(getActivity(),"La balise scannée n'est pas dans le parcours !","long");
            }

            if(resultat == 1 || resultat == 3 || resultat == 12)
            {
                //on update les infoScan de l'onglet "infoScan"
                updateInfos();
            }

            //update des erreurs
            ((TextView) view.findViewById(R.id.textView_infoScan)).setText(fromHtml(infoScan), TextView.BufferType.SPANNABLE);

            System.out.println("resultat = " + resultat);

            //réactiver le bouton
            scanButton.setEnabled(true);
        }
    }

    public void updateInfos() {

        UpdateInfosThread thread = new UpdateInfosThread();
        thread.execute();

    }

    public class UpdateInfosThread extends AsyncTask<String, Integer, Void> {

        String nbBaliseArrivee;

        String baliseRemainingPoche;
        String baliseCheckedPoche;
        String baliseSortiePoche;
        String pocheStr;

        String nbPoints;
        String liaisons;

        int[] scannéSurTotal;

        @Override
        protected void onPreExecute() {
        /*
         *    do things before doInBackground() code runs
         *    such as preparing and showing a Dialog or ProgressBar
        */

        }

        @Override
        protected Void doInBackground(String... parametres) {
            //do your work here

            //retarder l'update qd l'app demarre mais regle rien.
            if(activiteRedemarre){
                activiteRedemarre = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(resultat == 1 || resultat == 3 || resultat == 12)
            {
                //Update du temps dans la base de données
                controller.UpdateTemps(scanContent,temps);
            }

            //pour pouvoir vérifier si la suivante a déjà été pointé
            //mettre à jour la dernière balise pointée et sa suivante
            baliseActuelle = controller.getBaliseActuelle();
            //stocker la variable pour vérifier quand on scanne.
            nbBaliseSuivante = baliseActuelle[2];
            controller.updateNextAlreadyChecked(nbBaliseSuivante);

            //on récupère les données dans la base
            scannéSurTotal = controller.getNbCheckpoints();

            //Afficher soit la balise de départ avant le départ et la balise d'arrivée pendant la course
            if(departOK){
                nbBaliseArrivee = controller.getLastBalise();
            } else {
                nbBaliseDepart = controller.getFirstBalise();
            }

            //Si on scanne la sortie de la poche, la deuxieme condition sert quand on redémarre l'appli =
            if(resultat == 12 || baliseActuelle[0].equals(baliseSortiePoche))
            {
                //Quand on scanne la balise de sortie d'une poche, on quitte la poche
                baliseActuelle[6] = "null";
            }

            //poche
            baliseSortiePoche = controller.getSortiePoche(baliseActuelle[6]);
            baliseRemainingPoche = controller.getRemainingPoche(baliseActuelle[6]);
            baliseCheckedPoche = controller.getCheckedPoche(baliseActuelle[6]);

            //poche test
            pocheStr = controller.getPoche(baliseActuelle[6]);

            //liaisons
            liaisons = controller.getLiaisons();

            //points avec liaisons
            nbPoints = controller.calculerPoints();

            //update les points dans la table équipe
            controller.UpdatePoints(nbPoints);

            //on attend pas si on refresh qd on télécharge
            if(resultat != 54)
            {
                //on attend un peu avant de finir le thread pour laisser le temps au joueur de voir les infoScan bouger
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

            ProgressBar progressTotal = (ProgressBar) view.findViewById(R.id.progressBarTotal);
            //on met *100 pour voir l'animation
            progressTotal.setMax(scannéSurTotal[1] * 100);
            //sans animation
            //progressTotal.setProgress(scannéSurTotal[0]);
            //avec animation
            ObjectAnimator animation = ObjectAnimator.ofInt(progressTotal, "progress", scannéSurTotal[0]*100);
            animation.setDuration(600); // 1.5 second
            animation.setInterpolator(new LinearInterpolator());
            animation.start();


            //pour mettre à jour le numéro à la fin de l'animation
            animation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    totalBalises.setText(scannéSurTotal[0] + "/" + scannéSurTotal[1]);
                }
            });

            //Afficher soit la balise de départ avant le départ et la balise d'arrivée pendant la course
            if(departOK){
                ((TextView) view.findViewById(R.id.textView_balise_arrivee_nb)).setText("n°" + nbBaliseArrivee);
                //cacher la balise de départ et afficher celle d'arrivee
                view.findViewById(R.id.startBalise).setVisibility(View.GONE);
                view.findViewById(R.id.endBalise).setVisibility(View.VISIBLE);

            } else {
                ((TextView) view.findViewById(R.id.textView_balise_depart_nb)).setText("n°" + nbBaliseDepart);
                //cacher la balise d'arrivee et afficher celle de départ
                view.findViewById(R.id.startBalise).setVisibility(View.VISIBLE);
                view.findViewById(R.id.endBalise).setVisibility(View.GONE);
            }

            //balise suivante
            TextView txtBaliseSuivante = (TextView) view.findViewById(R.id.textView_balise_suivante_nb);
            if((baliseActuelle[1].equals("obligatoire") || baliseActuelle[1].equals("optionnelle")) && baliseActuelle[3].equals("non"))
            {
                //Quand prochaine n'est pas azimut
                txtBaliseSuivante.setText("n°" + baliseActuelle[2] + " -> " + baliseActuelle[1] + "\n" +
                                          "Poste : " + baliseActuelle[8]);
            }
            else if(baliseActuelle[3].equals("oui"))
            {
                //Quand prochaine est azimut
                txtBaliseSuivante.setText("n°" + baliseActuelle[2] + " -> " + baliseActuelle[1] + ", Poste : " + baliseActuelle[8] + "\n" +
                                          "Indication : Azimut " + baliseActuelle[4] + "° " + baliseActuelle[5] +
                                          "m");

                Utils.vibrer(getActivity());
            }
            else
            {
                //Quand prochaine est au choix ou aucune
                txtBaliseSuivante.setText(baliseActuelle[1] + "\n");
            }

            //poche
            TextView balisePoche = ((TextView) view.findViewById(R.id.textView_poche_nb));
/*            if(baliseActuelle[6].equals("")) { //si on vient de dll le parkour
                ((TextView) view.findViewById(R.id.textView_poche)).setText("Poche actuelle");
                balisePoche.setText("\n\n");
            } else if(!baliseActuelle[6].equals("null")){ //si la balise actuelle fait partie d'une poche
                ((TextView) view.findViewById(R.id.textView_poche)).setText("Poche actuelle : " + baliseActuelle[6]);
                balisePoche.setText("sortie : " + baliseSortiePoche
                                  + "\nrestantes : " + baliseRemainingPoche
                                  + "\nscannées : " + baliseCheckedPoche);

                pocheActuelle = baliseActuelle[6];
            } else { //si la balise actuelle n'a pas de poche
                ((TextView) view.findViewById(R.id.textView_poche)).setText("Poche actuelle");
                balisePoche.setText("aucune\n\n");
                pocheActuelle = "null";
            }*/

            balisePoche.setText(fromHtml(pocheStr), TextView.BufferType.SPANNABLE);
            pocheActuelle = baliseActuelle[6];

            //liaisons
            if(liaisons.equals("") && departOK){
                ((TextView) view.findViewById(R.id.textView_liaisons_nb)).setText("aucune");
            } else if(liaisons.equals("")){
                ((TextView) view.findViewById(R.id.textView_liaisons_nb)).setText("");
            } else {
                ((TextView) view.findViewById(R.id.textView_liaisons_nb)).setText(fromHtml(liaisons), TextView.BufferType.SPANNABLE);
            }

            //points
            ((TextView) view.findViewById(R.id.textView_points_nb)).setText(nbPoints);

            //stocker la variable pour vérifier quand on scanne.
            baliseSuivante = baliseActuelle[1];
            nbBaliseSuivante = baliseActuelle[2];


            //regarder si la balise est la dernière.
            if(baliseActuelle[1].equals("aucune") && departOK){
                timeElapsed.stop();
                //jai eu une seconde de diff une fois entre frag2 (-1) et frag 3 donc test
                //ca
                timeElapsed.setText(baliseActuelle[7]);

                departOK=false;
                infoScan = "[<font color='#2f9e00'>OK</font>] Balise fin scannée. Parcours terminé !";
                Utils.showToast(getActivity(),"Vous avez scanné la balise de fin !","long");
            }

            ((TextView) view.findViewById(R.id.textView_infoScan)).setText(fromHtml(infoScan), TextView.BufferType.SPANNABLE);

            //Si un temps a été ajouté, on update la listView du fragment 3 (onglet parcours)
            if(resultat == 1 || resultat == 3 || resultat == 12)
            {
                mCallback.communicateToFragment3();
            }

            resultat = 54;

        }
    }

    public void fragmentCommunication2() {

        boolean pochesDsParcours;
        boolean liaisonsDsParcours;
        boolean parcoursFull;

        parcoursFull = controller.checkParcours();

        timeElapsed.stop();
        timeElapsed.setText("00:00:00");

        if (parcoursFull) {

            //afficher l'interface du deuxieme onglet et cacher la phrase
            view.findViewById(R.id.interface2).setVisibility(LinearLayout.VISIBLE);
            view.findViewById(R.id.noParcours2).setVisibility(LinearLayout.GONE);

/*            pochesDsParcours = controller.checkPoches();
            liaisonsDsParcours = controller.checkLiaisons();

            //Afficher que les poches s'il n'y pas de liaisons et l'inverse
            if(pochesDsParcours && !liaisonsDsParcours)
            {
                liaisonsBalise.setVisibility(View.GONE);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(0,0,0,0);
                pochesBalise.setLayoutParams(params);
            }
            else if(!pochesDsParcours && liaisonsDsParcours)
            {
                pochesBalise.setVisibility(View.GONE);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(0,0,0,0);
                liaisonsBalise.setLayoutParams(params);
            }*/

            //on check si la balise depart a déjà été scanné, si oui on met departOK a true.
            departOK = controller.checkFirstBalise();

            //on réinitialise infosScan
            infoScan = "";

            //si la base de données est déjà remplie, on update les infoScan
            updateInfos();

            //fonction a mettre en place pour récupérer le bon chrono si l'activité s'arrete.
        }
        else
        {
            //timeElapsed.stop();
            //timeElapsed.setText("00:00:00");
            departOK = false;

            //cacher l'interface du deuxieme onglet et afficher la phrase
            view.findViewById(R.id.interface2).setVisibility(LinearLayout.GONE);
            view.findViewById(R.id.noParcours2).setVisibility(LinearLayout.VISIBLE);
        }
    }

    //Si l'user quitte l'application pendant la course
    @Override
    public void onPause() {
        super.onPause();

        if(departOK) { //si le parcours est commencé

            //On récupère l'heure du téléphone en ms
            Calendar rightNow = Calendar.getInstance();
            long heureExit = rightNow.getTimeInMillis();

            //On récupère le temps du chrono en ms
            long heureChrono = SystemClock.elapsedRealtime() - timeElapsed.getBase();

            SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("heureExit",heureExit);
            editor.putLong("tempsChrono",heureChrono);
            editor.apply();
            Log.d("test", "onPause: heureExit : " + heureExit +"\n tempsChrono : " + heureChrono);
        }



    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

}