package iut_lry.coursedorientation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class DBController extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_JOUEURS = "CREATE TABLE joueurs ( " +
            "id INTEGER PRIMARY KEY, " +
            "prenom TEXT, " +
            "nom TEXT, " +
            "date_naissance TEXT, " +
            "num_equipe INTEGER )";

    private static final String CREATE_TABLE_EQUIPE = "CREATE TABLE equipe ( " +
            "id INTEGER PRIMARY KEY, " +
            "nom_equipe TEXT, " +
            "categorie TEXT, " +
            "num_course INTEGER, " +
            "points INTEGER )";

    private static final String CREATE_TABLE_COURSE = "CREATE TABLE course ( " +
            "id INTEGER PRIMARY KEY, " +
            "date TEXT, " +
            "temps TEXT," +
            "categorie TEXT," +
            "num_parcours INTEGER," +
            "sync INTEGER )";

    private static final String CREATE_TABLE_PARCOURS = "CREATE TABLE parcours ( " +
            "id INTEGER PRIMARY KEY, " +
            "description TEXT)";

    private static final String CREATE_TABLE_LISTE_BALISES = "CREATE TABLE liste_balises ( " +
            "id INTEGER PRIMARY KEY, " +
            "num_parcours INTEGER, " +
            "num_balise INTEGER, " +
            "suivante TEXT, " +
            "num_suivante INTEGER, " +
            "azimut TEXT, " +
            "azimut_distance INTEGER, " +
            "azimut_degre INTEGER, " +
            "depart INTEGER, " +
            "arrivee INTEGER, " +
            "liaison TEXT, " +
            "groupe TEXT," +
            "coord_gps TEXT," +
            "poste TEXT, " +
            "points INTEGER, " +
            "temps TEXT )";

    private static final String CREATE_TABLE_BALISE = "CREATE TABLE balise ( " +
            "num INTEGER PRIMARY KEY, " +
            "hash TEXT )";

    private static final String CREATE_TABLE_GROUPE = "CREATE TABLE groupe ( " +
            "nom_groupe TEXT PRIMARY KEY, " +
            "balise_entree INTEGER, " +
            "balise_sortie INTEGER)";

    private static final String CREATE_TABLE_LISTE_LIAISONS = "CREATE TABLE liste_liaisons ( " +
            "num INTEGER PRIMARY KEY, " +
            "num_parcours INTEGER, " +
            "description TEXT, " +
            "points INTEGER )";

    private static final String CREATE_TABLE_LIAISON = "CREATE TABLE liaison ( " +
            "num INTEGER,  " +
            "balise INTEGER, " +
            "ordre INTEGER )";


    public DBController(Context applicationcontext) {
        super(applicationcontext, "course.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(CREATE_TABLE_JOUEURS);
        database.execSQL(CREATE_TABLE_EQUIPE);
        database.execSQL(CREATE_TABLE_COURSE);
        database.execSQL(CREATE_TABLE_PARCOURS);
        database.execSQL(CREATE_TABLE_LISTE_BALISES);
        database.execSQL(CREATE_TABLE_BALISE);
        database.execSQL(CREATE_TABLE_GROUPE);
        database.execSQL(CREATE_TABLE_LISTE_LIAISONS);
        database.execSQL(CREATE_TABLE_LIAISON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS joueurs, equipe, course, parcours," +
                " liste_balises, balise, groupe, liste_liaisons, liaison";
        database.execSQL(query);
        onCreate(database);
    }

    public void deleteTable(String TABLE_NAME) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from " + TABLE_NAME);
    }

    public void insertDataParcours(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(queryValues.get("joueurs.id") != null)
        {
            System.out.println("joueurs");
            //joueurs
            values.put("id", queryValues.get("joueurs.id"));
            values.put("prenom", queryValues.get("joueurs.prenom"));
            values.put("nom", queryValues.get("joueurs.nom"));
            values.put("date_naissance", queryValues.get("joueurs.date_naissance"));
            values.put("num_equipe", queryValues.get("joueurs.num_equipe"));
            database.insert("joueurs", null, values);
        }
        else if(queryValues.get("equipe.id") != null)
        {
            System.out.println("equipe");
            //equipe
            values.put("id", queryValues.get("equipe.id"));
            values.put("nom_equipe", queryValues.get("equipe.nom_equipe"));
            values.put("categorie", queryValues.get("equipe.categorie"));
            values.put("num_course", queryValues.get("equipe.num_course"));
            values.put("points", "");
            database.insert("equipe", null, values);
        }
        else if(queryValues.get("course.id") != null)
        {
            System.out.println("course");
            //course
            values.put("id", queryValues.get("course.id"));
            values.put("date", queryValues.get("course.date"));
            values.put("temps", queryValues.get("course.temps"));
            values.put("categorie", queryValues.get("course.categorie"));
            values.put("num_parcours", queryValues.get("course.num_parcours"));
            values.put("sync", "non");
            database.insert("course", null, values);
        }
        else if(queryValues.get("parcours.id") != null)
        {
            System.out.println("parcours");
            //parcours
            values.put("id", queryValues.get("parcours.id"));
            values.put("description", queryValues.get("parcours.description"));
            database.insert("parcours", null, values);
        }
        else if(queryValues.get("liste_balises.id") != null)
        {
            System.out.println("liste_balises");
            //liste_balises
            //values = new ContentValues();
            values.put("id", queryValues.get("liste_balises.id"));
            values.put("num_parcours", queryValues.get("liste_balises.num_parcours"));
            values.put("num_balise", queryValues.get("liste_balises.num_balise"));
            values.put("suivante", queryValues.get("liste_balises.suivante"));
            values.put("num_suivante", queryValues.get("liste_balises.num_suivante"));
            values.put("azimut", queryValues.get("liste_balises.azimut"));
            values.put("azimut_distance", queryValues.get("liste_balises.azimut_distance"));
            values.put("azimut_degre", queryValues.get("liste_balises.azimut_degre"));
            values.put("depart", queryValues.get("liste_balises.depart"));
            values.put("arrivee", queryValues.get("liste_balises.arrivee"));
            values.put("liaison", queryValues.get("liste_balises.liaison"));
            values.put("groupe", queryValues.get("liste_balises.groupe"));
            values.put("coord_gps", queryValues.get("liste_balises.coord_gps"));
            values.put("poste", queryValues.get("liste_balises.poste"));
            values.put("points", queryValues.get("liste_balises.points"));
            values.put("temps", "");
            database.insert("liste_balises", null, values);
        }
        else if(queryValues.get("balise.num") != null)
        {
            System.out.println("balise");
            //balise
            //values = new ContentValues();
            values.put("num", queryValues.get("balise.num"));
            values.put("hash", queryValues.get("balise.hash"));
            database.insert("balise", null, values);
        }
        else if(queryValues.get("groupe.nom_groupe") != null)
        {
            System.out.println("groupe");
            //groupe
            //values = new ContentValues();
            values.put("nom_groupe", queryValues.get("groupe.nom_groupe"));
            values.put("balise_entree", queryValues.get("groupe.balise_entree"));
            values.put("balise_sortie", queryValues.get("groupe.balise_sortie"));
            database.insert("groupe", null, values);
        }
        else if(queryValues.get("liste_liaisons.num") != null)
        {
            System.out.println("liste_liaisons");
            //liste_liaisons
            //values = new ContentValues();
            values.put("num", queryValues.get("liste_liaisons.num"));
            values.put("num_parcours", queryValues.get("liste_liaisons.num_parcours"));
            values.put("description", queryValues.get("liste_liaisons.description"));
            values.put("points", queryValues.get("liste_liaisons.points"));
            database.insert("liste_liaisons", null, values);
        }
        else if(queryValues.get("liaison.num") != null)
        {
            System.out.println("liaison");
            //liaison
            //values = new ContentValues();
            values.put("num", queryValues.get("liaison.num"));
            values.put("balise", queryValues.get("liaison.balise"));
            values.put("ordre", queryValues.get("liaison.ordre"));
            database.insert("liaison", null, values);
        }

        database.close();
    }

    public boolean checkCourse(){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id FROM course", null);

        if (cursor.moveToFirst()) {
            cursor.close();
            database.close();
            return true;
        }
        else {
            cursor.close();
            database.close();
            return false;
        }
    }

    /**
     * Get list of Users from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllBalises(boolean notChecked) {

        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();

        String parametres = "";

        //on vérifie le parametre pr les différents options de classement
        if(notChecked)
        {
            parametres = " WHERE temps = ''";
        }

        String selectQuery = "SELECT num_balise,temps,suivante,num_suivante," +
                             "poste,azimut,azimut_degre,azimut_distance," +
                             "depart,arrivee" +
                             " FROM liste_balises" +
                             parametres +
                             " ORDER BY CASE WHEN temps = '' THEN 2 ELSE 1 END, temps";


        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //Colonne num_balise
                if(cursor.getString(8).equals("1") && cursor.getString(9).equals("0")) //départ
                {
                    map.put("num_balise", cursor.getString(0) + " (départ)");
                }
                else if(cursor.getString(8).equals("0") && cursor.getString(9).equals("1")) //arrivee
                {
                    map.put("num_balise", cursor.getString(0) + " (arrivée)");
                }
                else
                {
                    map.put("num_balise", cursor.getString(0));
                }

                //Colonne temps
                map.put("temps", cursor.getString(1));
                //Colonne suivante
                if(cursor.getString(2).equals("au choix") || cursor.getString(2).equals("aucune"))
                {
                    map.put("suivante", cursor.getString(2));
                }
                else if(cursor.getString(5).equals("non") && !cursor.getString(2).equals("au choix"))
                {
                    map.put("suivante", cursor.getString(3) + " (" + cursor.getString(2) + ")");
                }
                else
                {
                    map.put("suivante", cursor.getString(6) +"° " + cursor.getString(7) + "m" + " (" + cursor.getString(2) + ")");
                }

                map.put("poste", cursor.getString(4));
                //Ajout de toutes les données dans le Hashmap
                usersList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();

        database.close();

        return usersList;
    }

    public void updateNextAlreadyChecked(String baliseSuivante) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT num_balise,temps FROM liste_balises " +
                                          "WHERE num_balise = ? AND temps != ''", new String[]{baliseSuivante});

        Cursor cursor2 = database.rawQuery("SELECT num_balise,temps,suivante,num_suivante FROM liste_balises " +
                "WHERE num_suivante = ?", new String[]{baliseSuivante});

        if(cursor.moveToFirst())
        {
            if(cursor2.moveToFirst()){
                ContentValues newValues = new ContentValues();
                newValues.put("suivante", "au choix");
                newValues.put("num_suivante", "");
                String[] args = new String[]{baliseSuivante};
                database.update("liste_balises", newValues, "num_suivante=?", args);
                cursor2.close();
            }
            cursor.close();
        }
        database.close();
    }

    public int checkBalise(String hash, boolean departOK, String baliseDepart, String baliseSuivante, String nbBaliseSuivante, String pocheActuelle) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT num_balise,temps,groupe,balise_entree,balise_sortie FROM liste_balises " +
                                          "LEFT JOIN groupe ON groupe = nom_groupe " +
                                          "INNER JOIN balise ON num_balise = num " +
                                          "WHERE hash = ?", new String[]{hash});

        if (cursor.moveToFirst())
        {

            String balise = cursor.getString(0);
            String tempsReq = cursor.getString(1);
            String poche = cursor.getString(2);
            String baliseEntree = cursor.getString(3);
            String baliseSortie = cursor.getString(4);

            //Si la balise qu'on scanne n'a pas de poche, mettre ses balises à null
            if(poche.equals("null"))
            {
                baliseEntree = "null";
                baliseSortie = "null";
            }

            if (tempsReq.equals(""))
            {
                System.out.println("departOK = " + departOK);
                System.out.println("balise = " + balise);
                System.out.println("nbBaliseSuivante = " + nbBaliseSuivante);
                System.out.println("baliseSuivante = " + baliseSuivante);
                System.out.println("baliseSortie = " + baliseSortie);

                System.out.println("poche = " + poche);
                System.out.println("poche actuelle = " + pocheActuelle);

                if (cursor.getString(0).equals(baliseDepart))//si la balise de depart est scanné
                {
                    cursor.close();
                    database.close();
                    return 3;
                }
                else if (departOK && (balise.equals(nbBaliseSuivante) || nbBaliseSuivante.equals("") || baliseSuivante.equals("optionnelle") || (baliseSortie.equals(balise) && poche.equals(pocheActuelle)))) //si la première balise a déjà été scanné
                {

                    if(baliseSortie.equals(balise) && poche.equals(pocheActuelle))
                    {
                        cursor.close();
                        database.close();
                        return 12; //si on scanne la balise de sortie de la poche
                    }
                    else if(poche.equals(pocheActuelle) || baliseEntree.equals(balise))
                    {
                        cursor.close();
                        database.close();
                        return 1; //si la balise scanné appartient a la poche actuelle ou a la balise d'entrée
                    }
                    else if(!poche.equals(pocheActuelle) && pocheActuelle.equals("null"))
                    {
                        cursor.close();
                        database.close();
                        return 7; //Vous n'êtes pas rentré dans la poche de cette balise!
                    }
                    else if(!poche.equals(pocheActuelle) && !pocheActuelle.equals("null"))
                    {
                        cursor.close();
                        database.close();
                        return 8; //La balise ne fait pas partie de la poche actuelle!
                    }
                    else
                    {
                        cursor.close();
                        database.close();
                        return 10; //cas non traité
                    }

                }
                else if(!balise.equals(nbBaliseSuivante) && departOK)
                {
                    if(!poche.equals(pocheActuelle) && !pocheActuelle.equals("null"))
                    {
                        cursor.close();
                        database.close();
                        return 15; //si c'est pas la balise suivante et que le parcours a commencé
                    }
                    else
                    {
                        cursor.close();
                        database.close();
                        return 5; //si c'est pas la balise suivante et que le parcours a commencé
                    }
                }
                else if(nbBaliseSuivante.equals("aucune"))
                {
                    cursor.close();
                    database.close();
                    return 6; //s'il a scanné la balise de fin
                }
                else
                {
                    cursor.close();
                    database.close();
                    return 4; //la balise n'est pas la balise de départ
                }
            }
            else //Quand la balise a déjà été scanné
            {
                cursor.close();
                database.close();
                return 2;
            }
        }
        else
        { //si la balise n'est pas trouvé dans la base
            cursor.close();
            database.close();
            return 0;
        }
    }

    public String getNumBalise(String hash) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT num FROM balise WHERE hash = ?", new String[]{hash});

        String numBalise = "";

        if (cursor.moveToFirst()) {
            numBalise = cursor.getString(0);
            cursor.close();
            database.close();
        }

        return numBalise;
    }

    public void UpdateTemps(String balise, String temps) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT num_balise,temps FROM liste_balises WHERE num_balise = ?", new String[]{balise});

        if (cursor.moveToFirst()) {

            //Version plus safe
            ContentValues newValues = new ContentValues();
            newValues.put("temps", temps);
            String[] args = new String[]{balise};
            database.update("liste_balises", newValues, "num_balise=?", args);
            //database.execSQL("UPDATE parcours SET temps = ? WHERE balise = ?", new String[]{temps,balise});

        }
        cursor.close();
        database.close();
    }

    public void UpdatePoints(String points) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id,points FROM equipe", null);

        if (cursor.moveToFirst()) {

            //Version plus safe
            ContentValues newValues = new ContentValues();
            newValues.put("points", points);
            String[] args = new String[]{cursor.getString(0)};
            database.update("equipe", newValues, "id=?", args);

        }
        cursor.close();
        database.close();
    }

    public String updateOngletParametres() {

        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT course.id, equipe.id, prenom, nom, nom_equipe, course.categorie, course.id, date_naissance FROM joueurs " +
                             "INNER JOIN equipe ON joueurs.num_equipe = equipe.id " +
                             "INNER JOIN course ON equipe.num_course = course.id " +
                             "INNER JOIN parcours ON course.num_parcours = parcours.id";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("numCourse", cursor.getString(0));
                map.put("numEquipe", cursor.getString(1));
                map.put("prenom", cursor.getString(2));
                map.put("nom", cursor.getString(3));
                map.put("nom_equipe", cursor.getString(4));
                map.put("categorie", cursor.getString(5));
                map.put("num_parcours", cursor.getString(6));
                map.put("date_naissance", cursor.getString(7));
                wordList.add(map);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    public int[] getNbCheckpoints() {

        SQLiteDatabase database = this.getReadableDatabase();

        int[] scannéSurTotal = new int[2];

        scannéSurTotal[0] = 0;
        scannéSurTotal[1] = 0;

        Cursor cursor = database.rawQuery("SELECT temps FROM liste_balises", null);

        if (cursor.moveToFirst()) {

            do {
                String colonne = cursor.getString(0);
                if (!colonne.equals("")) {
                    scannéSurTotal[0]++;
                }
            } while (cursor.moveToNext());
        }

        scannéSurTotal[1] = cursor.getCount();

        cursor.close();
        database.close();

        return scannéSurTotal;
    }

    public boolean checkFirstBalise(){
        SQLiteDatabase database = this.getReadableDatabase();

        String temps = "";

        Cursor cursor = database.rawQuery("SELECT temps,depart FROM liste_balises WHERE depart=1", null);

        if (cursor.moveToFirst()) {

            temps = cursor.getString(0);
        }

        cursor.close();
        database.close();

        if(temps.equals("")){
            return false;
        }
        else
        {
            return true;
        }

    }

    public String getFirstBalise() {

        SQLiteDatabase database = this.getReadableDatabase();

        String baliseDepart = "0";

        Cursor cursor = database.rawQuery("SELECT num_balise,depart FROM liste_balises WHERE depart=1", null);

        if (cursor.moveToFirst()) {

            baliseDepart = cursor.getString(0);
        }

        cursor.close();
        database.close();

        return baliseDepart;
    }

    public String getLastBalise() {

        SQLiteDatabase database = this.getReadableDatabase();

        String baliseArrivee = "0";

        Cursor cursor = database.rawQuery("SELECT num_balise,depart FROM liste_balises WHERE arrivee=1", null);

        if (cursor.moveToFirst()) {

            baliseArrivee = cursor.getString(0);
        }

        cursor.close();
        database.close();

        return baliseArrivee;
    }

    public String[] getBaliseActuelle() {
        //Changer en json ou faire objet balise
        SQLiteDatabase database = this.getReadableDatabase();

        String[] balise = new String[9];

        Cursor cursor = database.rawQuery("SELECT num_balise,suivante,num_suivante,azimut,azimut_degre,azimut_distance,groupe,temps " +
                                          "FROM liste_balises WHERE temps != '' " +
                                          "AND temps=(SELECT max(temps) FROM liste_balises) ", null);

        if (cursor.moveToFirst()) {

            for(int i=0;i<8;i++)
            {
                balise[i] = cursor.getString(i);
            }

            Cursor cursor25 = database.rawQuery("SELECT poste " +
                    "FROM liste_balises WHERE num_balise = ?", new String[]{cursor.getString(2)});

            if(cursor25.moveToFirst()) {
                balise[8] = cursor25.getString(0);
            }

            cursor25.close();
        }
        else
        {
            for(int i=0;i<9;i++)
            {
                balise[i] = "";
            }
        }

        cursor.close();
        database.close();

        return balise;
    }

    public String getPoche(String pocheActuelle)
    {
        SQLiteDatabase database = this.getReadableDatabase();

        String poche = "";

        Cursor cursor1 = database.rawQuery("SELECT nom_groupe FROM groupe",null);

        if(cursor1.moveToFirst())
        {
            do {
                Cursor balisesGroupe = database.rawQuery("SELECT num_balise,temps FROM liste_balises " +
                                                   "INNER JOIN groupe ON groupe = nom_groupe " +
                                                   "WHERE groupe = ? AND num_balise = balise_entree",
                                                   new String[]{cursor1.getString(0)});

                Cursor cursor4 = database.rawQuery("SELECT num_balise,temps,groupe FROM liste_balises " +
                                "INNER JOIN groupe ON groupe = nom_groupe " +
                                "WHERE groupe = ? AND num_balise = balise_sortie",
                                new String[]{cursor1.getString(0)});

                Cursor cursor2 = database.rawQuery("SELECT num_balise,temps,groupe,balise_entree,balise_sortie " +
                        "FROM liste_balises INNER JOIN groupe ON groupe = nom_groupe " +
                        "WHERE groupe = ? AND num_balise != balise_entree AND num_balise != balise_sortie",
                        new String[]{cursor1.getString(0)});

                if(cursor2.moveToFirst())
                {
                    poche = poche + cursor1.getString(0) + " : ";

                    if(balisesGroupe.moveToFirst())
                    {
                        if(!balisesGroupe.getString(1).equals(""))
                        {
                            poche = poche + "<font color='#2f9e00'>" + balisesGroupe.getString(0) + "</font>->";
                        }
                        else
                        {
                            poche = poche + balisesGroupe.getString(0) + "->";
                        }
                    }
                    balisesGroupe.close();

                    do {

                        if(!cursor2.isLast())
                        {
                            if(!cursor2.getString(1).equals(""))
                                poche = poche + "<font color='#2f9e00'>" + cursor2.getString(0) + "</font>-";
                            else
                                poche = poche + cursor2.getString(0) + "-";
                        }
                        else
                        {
                            if(!cursor2.getString(1).equals(""))
                                poche = poche + "<font color='#2f9e00'>" + cursor2.getString(0) + "</font>";
                            else
                                poche = poche + cursor2.getString(0);
                        }


                    } while(cursor2.moveToNext());

                    if(cursor4.moveToFirst())
                    {
                        if(!cursor4.getString(1).equals(""))
                        {
                            poche = poche + "-><font color='#2f9e00'>" + cursor4.getString(0) + "</font>";

                        }
                        else
                        {
                            poche = poche + "->" + cursor4.getString(0);
                        }

                        if(cursor4.getString(2).equals(pocheActuelle))
                        {
                            poche = poche + " (en cours)";
                        }

                        poche = poche + "<br/>";
                    }
                    cursor4.close();
                }
                cursor2.close();

            } while(cursor1.moveToNext());

        }
        cursor1.close();



        return poche;
    }

    public String calculerPoints() {

        SQLiteDatabase database = this.getReadableDatabase();

        int somme = 0;
        String sens = "";

        //CALCUL POINTS BALISES
        Cursor cursor = database.rawQuery("SELECT points FROM liste_balises WHERE temps != ''", null);
        if (cursor.moveToFirst()) {
            do {
                somme = somme + Integer.parseInt(cursor.getString(0));

            } while (cursor.moveToNext());
        }
        cursor.close();

        //CALCUL POINTS LIAISONS
        Cursor listesLiaisons = database.rawQuery("SELECT num FROM liste_liaisons", null);

        if (listesLiaisons.moveToFirst()) {
            do {

                String liaisonActuelle = listesLiaisons.getString(0);
                System.out.println("liaisonActuelle = " + liaisonActuelle);

                sens = determinerSensLiaison(database, liaisonActuelle);

                //pour faire dans les deux sens
                if(sens.equals("ASC") || sens.equals("DESC"))
                {
                    Cursor balisesLiaison = database.rawQuery("SELECT liste_liaisons.num,liaison.balise," +
                            "liaison.ordre," +
                            "liste_liaisons.points FROM liste_liaisons INNER JOIN liaison " +
                            "ON liste_liaisons.num = liaison.num " +
                            "WHERE liste_liaisons.num = ? " +
                            "ORDER BY liaison.ordre " + sens, new String[]{liaisonActuelle});

                    System.out.println("stp : " + balisesLiaison.getCount());

                    if (balisesLiaison.moveToFirst())
                    {
                        int longueurTest = 0;

                        int longueur = balisesLiaison.getCount();

                        Cursor listeBalises = database.rawQuery("SELECT num_balise,temps " +
                                "FROM liste_balises WHERE temps != '' " +
                                "ORDER BY temps", null);
                        if (listeBalises.moveToFirst()) {
                            do {
                                if (longueurTest == 54)
                                {
                                    listeBalises.moveToPrevious();
                                    longueurTest = 0;
                                }

                                if (balisesLiaison.getString(1).equals(listeBalises.getString(0)))
                                {
                                    longueurTest++;
                                    System.out.println("Truc égal à balise !");

                                    if (longueurTest == longueur)
                                    {
                                        somme = somme + Integer.parseInt(balisesLiaison.getString(3));
                                        longueurTest = 0;
                                        balisesLiaison.moveToFirst();
                                        //quitter la boucle pour opti?
                                    }
                                    else
                                    {
                                        balisesLiaison.moveToNext();
                                    }

                                }
                                else if(longueurTest > 0)
                                {
                                    longueurTest = 54;
                                    balisesLiaison.moveToFirst();
                                }

                            } while (listeBalises.moveToNext());
                        }
                        listeBalises.close();
                    }
                    balisesLiaison.close();
                }
            } while (listesLiaisons.moveToNext());
        }

        listesLiaisons.close();


        //FIN TEST
        database.close();

        return String.valueOf(somme);
    }

    public String determinerSensLiaison(SQLiteDatabase database, String liaisonActuelle)
    {
        //DETERMINATION DU SENS ASC OU DESC
        String sens = "";

        Cursor liaison = database.rawQuery("SELECT num FROM liaison WHERE num = ?", new String[]{liaisonActuelle});

        if(liaison.getCount() != 0) //pour afficher quand il y a une liaison même vierge par défaut dans l'ordre ASC
        {
            sens = "ASC";
        }

        Cursor LiaisonBaliseScanne = database.rawQuery("SELECT num_balise,liaison.ordre FROM liste_balises " +
                "INNER JOIN parcours ON liste_balises.num_parcours = parcours.id " +
                "INNER JOIN liste_liaisons ON parcours.id = liste_liaisons.num_parcours " +
                "INNER JOIN liaison ON liste_liaisons.num = liaison.num " +
                "WHERE liste_liaisons.num = ? AND temps != '' AND num_balise = balise " +
                "ORDER BY temps", new String[]{liaisonActuelle});

        if(LiaisonBaliseScanne.moveToFirst())
        {
            //si la premiere balise scannée d'une liaison est la dernière de la liaison, ORDRE DESC
            if(Integer.parseInt(LiaisonBaliseScanne.getString(1)) == liaison.getCount())
            {
                sens = "DESC";
            }
            //si la premiere balise scannée d'une liaison est la première de la liaison, ORDRE ASC
            else if(Integer.parseInt(LiaisonBaliseScanne.getString(1)) == 1)
            {
                sens = "ASC";
            }
        }

        liaison.close();
        LiaisonBaliseScanne.close();

        return sens;
    }

    public String getLiaisons() {

        SQLiteDatabase database = this.getReadableDatabase();

        String liaisonsStr = "";
        String sens;
        String reussi = "";
        String encours = "";
        String rate = "";
        String aFaire = "";

        Cursor liaisons = database.rawQuery("SELECT num FROM liste_liaisons", null);

        if (liaisons.moveToFirst()) {
            do {

                String liaisonActuelle = liaisons.getString(0);
                System.out.println("liaisonActuelle = " + liaisonActuelle);

                //déterminer le sens de la liaison
                sens = determinerSensLiaison(database, liaisonActuelle);

                if(sens.equals("ASC") || sens.equals("DESC"))
                {
                    Cursor balisesLiaisons = database.rawQuery("SELECT liste_liaisons.num,liaison.balise,liaison.ordre," +
                            "liste_liaisons.points FROM liste_liaisons INNER JOIN liaison " +
                            "ON liste_liaisons.num = liaison.num " +
                            "WHERE liste_liaisons.num = ? " +
                            "ORDER BY liaison.ordre " + sens, new String[]{liaisonActuelle});

                    if (balisesLiaisons.moveToFirst())
                    {
                        int longueurTest = 0;

                        int longueur = balisesLiaisons.getCount();

                        System.out.println("SENS = " + sens);

                        Cursor balises = database.rawQuery("SELECT num_balise,temps FROM liste_balises " +
                                "ORDER BY CASE WHEN temps = '' THEN 2 ELSE 1 END, temps", null);
                        if (balises.moveToFirst()) {
                            do {
                                //Si la bonne balise a été scanné
                                if (balisesLiaisons.getString(1).equals(balises.getString(0)) && !balises.getString(1).equals(""))
                                {
                                    longueurTest++;
                                    System.out.println("OUI et longueurTest = " + longueurTest);

                                    if (longueurTest == longueur)
                                    {
                                        reussi = reussi + "[<font color='#2f9e00'>OK</font>] ";

                                        if(balisesLiaisons.moveToFirst())
                                        {
                                            do {
                                                if(!balisesLiaisons.isLast())
                                                    reussi = reussi + "<font color='#2f9e00'>" + balisesLiaisons.getString(1) + "</font>-";
                                                else
                                                    reussi = reussi + "<font color='#2f9e00'>" + balisesLiaisons.getString(1) + "</font>" +
                                                            " : " + balisesLiaisons.getString(3) + " pts<br/>";

                                            } while(balisesLiaisons.moveToNext());
                                        }

                                        balises.moveToLast();
                                    }
                                    else
                                    {
                                        balisesLiaisons.moveToNext();
                                    }
                                }
                                //Si une bonne balise a été scanné, et que les suivantes ne l'ont pas encore été
                                else if(longueurTest > 0 && longueurTest < longueur && balises.getString(1).equals(""))
                                {
                                    encours = encours + "[<font color='#ffa100'>-!-</font>] ";
                                    System.out.println("LA DEDANS");

                                    if(balisesLiaisons.moveToFirst())
                                    {
                                        int compteur=0;
                                        do {
                                            if(!balisesLiaisons.isLast() && compteur < longueurTest)
                                                encours = encours + "<font color='#2f9e00'>" + balisesLiaisons.getString(1) + "</font>-";
                                            else if(!balisesLiaisons.isLast())
                                                encours = encours + "<font color='#ffa100'>" + balisesLiaisons.getString(1) + "</font>-";
                                            else
                                                encours = encours + "<font color='#ffa100'>" + balisesLiaisons.getString(1) + "</font>" +
                                                        " : " + balisesLiaisons.getString(3) + " pts<br/>";

                                            compteur++;
                                        } while(balisesLiaisons.moveToNext());
                                        balises.moveToLast();
                                    }
                                }
                                //Si une bonne balise a été scanné et que la suivante n'est pas la bonne
                                else if(longueurTest > 0 && longueurTest < longueur && !balisesLiaisons.getString(1).equals(balises.getString(0)))
                                {
                                    rate = rate + "[<font color='#db1500'>KO</font>] ";
                                    System.out.println("LA DEDANS");

                                    if(balisesLiaisons.moveToFirst())
                                    {
                                        int compteur=0;
                                        do {
                                            if(!balisesLiaisons.isLast() && compteur < longueurTest)
                                                rate = rate + "<font color='#2f9e00'>" + balisesLiaisons.getString(1) + "</font>-";
                                            else if(!balisesLiaisons.isLast())
                                                rate = rate + "<font color='#db1500'>" + balisesLiaisons.getString(1) + "</font>-";
                                            else
                                                rate = rate + "<font color='#db1500'>" + balisesLiaisons.getString(1) + "</font>" +
                                                        " : <strike>" + balisesLiaisons.getString(3) + " pts</strike><br/>";

                                            compteur++;
                                        } while(balisesLiaisons.moveToNext());
                                        balises.moveToLast();
                                    }
                                }
                                //Si la liaison n'a pas été commencé
                                else if(longueurTest == 0 && balises.getString(1).equals(""))
                                {
                                    aFaire = aFaire + "[---] ";
                                    System.out.println("LA DEDANS");

                                    if(balisesLiaisons.moveToFirst())
                                    {
                                        do {
                                            if(!balisesLiaisons.isLast())
                                                aFaire = aFaire + balisesLiaisons.getString(1) + "-";
                                            else
                                                aFaire = aFaire + balisesLiaisons.getString(1) +
                                                        " : " + balisesLiaisons.getString(3) + " pts<br/>";

                                        } while(balisesLiaisons.moveToNext());
                                        balises.moveToLast();
                                    }
                                }
                                //réorganisation de l'affichage
                                liaisonsStr = encours + aFaire + reussi + rate;
                                //Suppression du saut de ligne en trop
                                if(liaisonsStr.length() > 0)
                                    liaisonsStr = liaisonsStr.substring(0,liaisonsStr.length()-5);

                            } while (balises.moveToNext());
                        }
                        balises.close();
                    }
                    balisesLiaisons.close();
                }
            } while (liaisons.moveToNext());
        }
        liaisons.close();

        database.close();

        return liaisonsStr;
    }

    public boolean checkPoches() {

        SQLiteDatabase database = this.getReadableDatabase();

        boolean pocheDsParcours = false;

        Cursor cursor = database.rawQuery("SELECT nom_groupe FROM groupe", null);

        if (cursor.moveToFirst()) {

            pocheDsParcours = true;
        }

        cursor.close();
        database.close();

        return pocheDsParcours;
    }

    public boolean checkLiaisons() {

        SQLiteDatabase database = this.getReadableDatabase();

        boolean liaisonsDsParcours = false;

        Cursor cursor = database.rawQuery("SELECT num FROM liste_liaisons", null);

        if (cursor.moveToFirst()) {

            liaisonsDsParcours = true;
        }

        cursor.close();
        database.close();

        return liaisonsDsParcours;
    }

    /**
     * Compose JSON out of SQLite records
     *
     * @return
     */
    public String composeJSONfromSQLite() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        //récupération du nombre de balises scannées
        int[] scannéSurTotal = getNbCheckpoints();
        String nbScanne = Integer.toString(scannéSurTotal[0]);

        //récupération du temps de la dernière balise
        String tempsLast = checkLastBalise();
        //variable parcours fini
        String fini= "oui";
        if(tempsLast.equals(""))
        {
            fini = "non";
        }

        String selectQuery = "SELECT course.id, parcours.id, equipe.id, equipe.points " +
                             "FROM parcours " +
                             "INNER JOIN course ON parcours.id = course.num_parcours " +
                             "INNER JOIN equipe ON course.id = equipe.num_course";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("numCourse", cursor.getString(0));
            map.put("numParcours", cursor.getString(1));
            map.put("numEquipe", cursor.getString(2));
            map.put("numBalise", nbScanne);
            map.put("temps", tempsLast);
            map.put("points", cursor.getString(3));
            map.put("fini", fini);
            wordList.add(map);
        }
        cursor.close();
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    public String checkLastBalise(){
        SQLiteDatabase database = this.getReadableDatabase();

        String temps = "";

        Cursor cursor = database.rawQuery("SELECT temps,depart FROM liste_balises WHERE arrivee=1", null);

        if (cursor.moveToFirst()) {

            temps = cursor.getString(0);
        }

        cursor.close();
        database.close();

        return temps;
    }

    public void updateSyncStatus(String sync) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id,sync FROM course", null);

        if (cursor.moveToFirst()) {

            //Version plus safe
            ContentValues newValues = new ContentValues();
            newValues.put("sync", sync);
            String[] args = new String[]{cursor.getString(0)};
            database.update("course", newValues, "id=?", args);

        }
        cursor.close();
        database.close();
    }

    public boolean checkSync(){
        SQLiteDatabase database = this.getReadableDatabase();

        boolean sync = false;

        Cursor cursor = database.rawQuery("SELECT sync FROM course", null);

        if (cursor.moveToFirst()) {

            if(cursor.getString(0).equals("oui")) {
                sync = true;
            }
        }

        cursor.close();
        database.close();

        return sync;
    }

    //Inutilisé remplacé par l'envoi des résultats direct
    public String composeJSONfromSQLiteNul() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT course.id, parcours.id, equipe.id, liste_balises.num_balise, liste_balises.temps, equipe.points " +
                "FROM liste_balises " +
                "INNER JOIN parcours ON liste_balises.num_parcours = parcours.id " +
                "INNER JOIN course ON parcours.id = course.num_parcours " +
                "INNER JOIN equipe ON course.id = equipe.num_course " +
                "ORDER BY CASE WHEN liste_balises.temps = '' THEN 2 ELSE 1 END, liste_balises.temps";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("numCourse", cursor.getString(0));
                map.put("numParcours", cursor.getString(1));
                map.put("numEquipe", cursor.getString(2));
                map.put("numBalise", cursor.getString(3));
                map.put("temps", cursor.getString(4));
                map.put("points", cursor.getString(5));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    public String getTimeOfRace() {

        SQLiteDatabase database = this.getReadableDatabase();

        String temps = "";

        Cursor cursor = database.rawQuery("SELECT temps FROM course", null);

        if (cursor.moveToFirst()) {
            temps = cursor.getString(0);
        }
        cursor.close();
        database.close();

        return temps;
    }

    public void ResetTemps() {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT num_balise,temps FROM liste_balises WHERE temps != ''", null);

        if (cursor.moveToFirst()) {
            do {

                ContentValues newValues = new ContentValues();
                newValues.put("temps", "");
                String[] args = new String[]{cursor.getString(0)};
                database.update("liste_balises", newValues, "num_balise=?", args);

            } while(cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    public String dllTest() {

        SQLiteDatabase database = this.getWritableDatabase();

        String[] tabPoste = {"Riviere", "Arbre", "Rocher" , "Lac" , "Maison"};

        String course = "1";

        //joueurs
        ContentValues values = new ContentValues();
        System.out.println("joueurs");
        values.put("id", "1");
        values.put("prenom", "Baptiste");
        values.put("nom", "Vrignaud");
        values.put("date_naissance", "20/09/1993");
        values.put("num_equipe", "1");
        database.insert("joueurs", null, values);

        //equipe
        values = new ContentValues();
        System.out.println("equipe");
        values.put("id", "1");
        values.put("nom_equipe", "bOss");
        values.put("categorie", "adulte");
        values.put("num_course", "1");
        values.put("points", "");
        database.insert("equipe", null, values);

        //course
        values = new ContentValues();
        System.out.println("course");
        values.put("id", "1");
        values.put("date", "08/04/2017");
        values.put("temps", "00:01:00");
        values.put("categorie", "adulte");
        values.put("num_parcours", "1");
        values.put("sync", "non");
        database.insert("course", null, values);


        //parcours
        values = new ContentValues();
        System.out.println("parcours");
        values.put("id", "1");
        values.put("description", "test");
        database.insert("parcours", null, values);

        //liste_balises
        System.out.println("liste_balises");
        for(int i=1;i<=5;i++) {
            values = new ContentValues();
            values.put("id", String.valueOf(i));
            values.put("num_parcours", "1");
            values.put("num_balise", String.valueOf(i));

            if(i != 5) {
                values.put("suivante", "au choix");
            } else {
                values.put("suivante", "aucune");
            }

            values.put("num_suivante", "");
            values.put("azimut", "non");
            values.put("azimut_distance", "0");
            values.put("azimut_degre", "0");
            if(i==1)
                values.put("depart", "1");
            else
                values.put("depart", "0");
            if(i==5)
                values.put("arrivee", "1");
            else
                values.put("arrivee", "0");

            values.put("liaison", "non");
            values.put("groupe", "null");
            values.put("coord_gps", "486546");
            values.put("poste", tabPoste[i-1]);
            values.put("points", "10");
            values.put("temps", "");
            database.insert("liste_balises", null, values);
        }

        //balise
        System.out.println("balise");
        for(int i=1;i<=5;i++) {
            values = new ContentValues();
            values.put("num", String.valueOf(i));
            values.put("hash", String.valueOf(i));
            database.insert("balise", null, values);
        }

/*        System.out.println("groupe");
        //groupe
        //values = new ContentValues();
        values.put("nom_groupe", queryValues.get("groupe.nom_groupe"));
        values.put("balise_entree", queryValues.get("groupe.balise_entree"));
        values.put("balise_sortie", queryValues.get("groupe.balise_sortie"));
        database.insert("groupe", null, values);

        System.out.println("liste_liaisons");
        //liste_liaisons
        //values = new ContentValues();
        values.put("num", queryValues.get("liste_liaisons.num"));
        values.put("num_parcours", queryValues.get("liste_liaisons.num_parcours"));
        values.put("description", queryValues.get("liste_liaisons.description"));
        values.put("points", queryValues.get("liste_liaisons.points"));
        database.insert("liste_liaisons", null, values);

        System.out.println("liaison");
        //liaison
        //values = new ContentValues();
        values.put("num", queryValues.get("liaison.num"));
        values.put("balise", queryValues.get("liaison.balise"));
        values.put("ordre", queryValues.get("liaison.ordre"));
        database.insert("liaison", null, values);*/


        database.close();

        return course;

    }



}
