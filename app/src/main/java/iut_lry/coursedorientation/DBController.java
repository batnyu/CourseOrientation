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

    private static final String CREATE_TABLE_JOUEURS = "CREATE TABLE" +
            " joueurs ( id INTEGER PRIMARY KEY, prenom TEXT, nom TEXT" +
            " date_naissance TEXT, num_equipe INTEGER )";

    private static final String CREATE_TABLE_EQUIPE = "CREATE TABLE" +
            " equipe ( id INTEGER PRIMARY KEY, nom_equipe TEXT, categorie TEXT" +
            " num_course INTEGER )";

    private static final String CREATE_TABLE_COURSE = "CREATE TABLE" +
            " course ( id INTEGER PRIMARY KEY, parcours INTEGER, date TEXT" +
            " temps TEXT )";

    private static final String CREATE_TABLE_PARCOURS = "CREATE TABLE" +
            " parcours ( id INTEGER PRIMARY KEY, categorie TEXT," +
            " description TEXT, date TEXT )";

    private static final String CREATE_TABLE_LISTE_BALISES = "CREATE TABLE" +
            " liste_balises ( id INTEGER PRIMARY KEY, num_parcours INTEGER," +
            " num_balise INTEGER," +
            " suivante INTEGER, azimut TEXT, azimut_distance INTEGER," +
            " azimut_degre INTEGER," +
            " depart INTEGER, arrivee INTEGER, liaison TEXT," +
            " groupe TEXT, points INTEGER, temps TEXT )";

    private static final String CREATE_TABLE_BALISE = "CREATE TABLE" +
            " balise ( num INTEGER PRIMARY KEY, coord_gps INTEGER," +
            " poste TEXT )";

    private static final String CREATE_TABLE_GROUPE = "CREATE TABLE" +
            " groupe ( nom_groupe TEXT PRIMARY KEY, balise_entree INTEGER," +
            " balise_sortie INTEGER, points_bonus INTEGER )";

    private static final String CREATE_TABLE_LISTE_LIAISONS = "CREATE TABLE" +
            " liste_liaisons (  num INTEGER PRIMARY KEY, description TEXT," +
            " points INTEGER )";

    private static final String CREATE_TABLE_LIAISON = "CREATE TABLE" +
            " liaison (  num INTEGER, balise INTEGER," +
            " ordre INTEGER )";

    private static final String CREATE_TABLE_RESULTATS = "CREATE TABLE" +
            " resultats (  id INTEGER PRIMARY KEY, course INTEGER," +
            " equipe INTEGER, balise INTEGER, temps TEXT )";

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
        database.execSQL(CREATE_TABLE_RESULTATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS joueurs, equipe, course, parcours," +
                " liste_balises, balise, groupe, liste_liaisons, liaison," +
                " resultats";
        database.execSQL(query);
        onCreate(database);
    }

    public void deleteTable(String TABLE_NAME) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from " + TABLE_NAME);
    }

    /**
     * Inserts User into SQLite DB
     *
     * @param queryValues
     */
    public void insertDataEquipe(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //joueurs
        values.put("id", queryValues.get("joueurs.id"));
        values.put("prenom", queryValues.get("joueurs.prenom"));
        values.put("nom", queryValues.get("joueurs.nom"));
        values.put("date_naissance", queryValues.get("joueurs.date_naissance"));
        values.put("num_equipe", queryValues.get("joueurs.num_equipe"));
        database.insert("joueurs", null, values);

        //equipe
        values = new ContentValues();
        values.put("id", queryValues.get("equipe.id"));
        values.put("nom_equipe", queryValues.get("equipe.nom_equipe"));
        values.put("categorie", queryValues.get("equipe.categorie"));
        values.put("num_course", queryValues.get("equipe.num_course"));
        database.insert("equipe", null, values);

        //course
        values = new ContentValues();
        values.put("id", queryValues.get("course.id"));
        values.put("parcours", queryValues.get("course.parcours"));
        values.put("date", queryValues.get("course.date"));
        values.put("temps", queryValues.get("course.temps"));
        database.insert("course", null, values);

        database.close();
    }

    public void insertDataParcours(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //System.out.println(queryValues.get("parcours.id"));

        if(queryValues.get("parcours.id") != null)
        {
            System.out.println("parcours");
            //parcours
            values.put("id", queryValues.get("parcours.id"));
            values.put("categorie", queryValues.get("parcours.categorie"));
            values.put("description", queryValues.get("parcours.description"));
            values.put("date", queryValues.get("parcours.date"));
            database.insert("parcours", null, values);
        }
        else if(queryValues.get("liste_balises.id") != null)
        {
            System.out.println("liste_balises");
            //liste_balises
            values = new ContentValues();
            values.put("id", queryValues.get("liste_balises.id"));
            values.put("num_parcours", queryValues.get("liste_balises.num_parcours"));
            values.put("num_balise", queryValues.get("liste_balises.num_balise"));
            values.put("suivante", queryValues.get("liste_balises.suivante"));
            values.put("azimut", queryValues.get("liste_balises.azimut"));
            values.put("azimut_distance", queryValues.get("liste_balises.azimut_distance"));
            values.put("azimut_degre", queryValues.get("liste_balises.azimut_degre"));
            values.put("depart", queryValues.get("liste_balises.depart"));
            values.put("arrivee", queryValues.get("liste_balises.arrivee"));
            values.put("liaison", queryValues.get("liste_balises.liaison"));
            values.put("groupe", queryValues.get("liste_balises.groupe"));
            values.put("points", queryValues.get("liste_balises.points"));
            values.put("temps", "");
            database.insert("liste_balises", null, values);
        }
        else if(queryValues.get("balise.num") != null)
        {
            System.out.println("balise");
            //balise
            values = new ContentValues();
            values.put("num", queryValues.get("balise.num"));
            values.put("coord_gps", queryValues.get("balise.coord_gps"));
            values.put("poste", queryValues.get("balise.poste"));
            database.insert("balise", null, values);
        }
        else if(queryValues.get("groupe.nom_groupe") != null)
        {
            System.out.println("groupe");
            //groupe
            values = new ContentValues();
            values.put("nom_groupe", queryValues.get("groupe.nom_groupe"));
            values.put("balise_entree", queryValues.get("groupe.balise_entree"));
            values.put("balise_sortie", queryValues.get("groupe.balise_sortie"));
            values.put("points_bonus", queryValues.get("groupe.points_bonus"));
            database.insert("groupe", null, values);
        }
        else if(queryValues.get("liste_liaisons.num") != null)
        {
            System.out.println("liste_liaisons");
            //liste_liaisons
            values = new ContentValues();
            values.put("num", queryValues.get("liste_liaisons.num"));
            values.put("description", queryValues.get("liste_liaisons.description"));
            values.put("points", queryValues.get("liste_liaisons.points"));
            database.insert("liste_liaisons", null, values);
        }
        else if(queryValues.get("liaison.num") != null)
        {
            System.out.println("liaison");
            //liaison
            values = new ContentValues();
            values.put("num", queryValues.get("liaison.num"));
            values.put("balise", queryValues.get("liaison.balise"));
            values.put("ordre", queryValues.get("liaison.ordre"));
            database.insert("liaison", null, values);
        }

        database.close();
    }

    public void insertData(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", queryValues.get("id"));
        values.put("numEquipe", "");
        values.put("balise", queryValues.get("balise"));
        values.put("temps", "");
        database.insert("parcours", null, values);
        database.close();
    }

    /**
     * Get list of Users from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllBalises() {

        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT * FROM parcours ORDER BY CASE WHEN temps = '' THEN 2 ELSE 1 END, temps";
        //String selectQuery = "SELECT * FROM liste_balises ORDER BY CASE WHEN temps = '' THEN 2 ELSE 1 END, temps";
        String selectQuery = "SELECT liste_balises.num_balise,liste_balises.temps,liste_balises.suivante,balise.poste " +
                             "FROM liste_balises INNER JOIN balise ON liste_balises.num_balise = balise.num " +
                             "ORDER BY CASE WHEN temps = '' THEN 2 ELSE 1 END, temps";


        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("num_balise", cursor.getString(0));
                map.put("temps", cursor.getString(1));
                map.put("suivante", cursor.getString(2));
                map.put("poste", cursor.getString(3));
                usersList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();

        database.close();

        return usersList;
    }

    public int checkBalise(String balise, String temps, boolean departOK, String baliseDepart, String baliseSuivante) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT num_balise,temps FROM liste_balises WHERE num_balise = ?", new String[]{balise});

        if (cursor.moveToFirst())
        {

            String colonne2 = cursor.getString(1);
            if (colonne2.equals(""))
            {
                if (cursor.getString(0).equals(baliseDepart))//si la balise de depart est scanné
                {
                    cursor.close();
                    database.close();
                    return 3;
                }
                else if (departOK && (balise.equals(baliseSuivante) || baliseSuivante.equals("0"))) //si la première balise a déjà été scanné
                {
                    cursor.close();
                    database.close();
                    return 1;
                }
                else if(!balise.equals(baliseSuivante) && departOK)
                {
                    cursor.close();
                    database.close();
                    return 5;
                }
                else
                {
                    cursor.close();
                    database.close();
                    return 4;
                }
            }
            else //Quand la balise a déjà été scanné
            {
                cursor.close();
                database.close();
                return 2;
            }
        } else {
            cursor.close();
            database.close();
            return 0;
        }
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

    public void updateNumEquipe(String numEquipe) {

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT numEquipe FROM parcours ", null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues newValues = new ContentValues();
                newValues.put("numEquipe", numEquipe);
                database.update("parcours", newValues, null, null);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    public String getNbCheckpoints() {

        String nb;
        SQLiteDatabase database = this.getReadableDatabase();

        /*Cursor cursor1 = database.rawQuery("SELECT COUNT(*) FROM liste_balises", null);
        int nbBalisesPointeess = cursor1.getCount();
        cursor1.close();

        Cursor cursor2 = database.rawQuery("SELECT COUNT(*) FROM liste_balises", null);
        int nbBalisesTotala = cursor2.getCount();
        cursor2.close();

        database.close();*/

        int nbBalisesPointees = 0;
        int nbBalisesTotal = 0;

        Cursor cursor = database.rawQuery("SELECT temps FROM liste_balises", null);

        if (cursor.moveToFirst()) {

            do {
                String colonne = cursor.getString(0);
                if (!colonne.equals("")) {
                    nbBalisesPointees++;
                }
            } while (cursor.moveToNext());
        }

        nbBalisesTotal = cursor.getCount();

        cursor.close();
        database.close();

        nb = nbBalisesPointees + "/" + nbBalisesTotal;

        return nb;
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

    public String[] getBaliseActuelle() {

        SQLiteDatabase database = this.getReadableDatabase();

        String[] balise = new String[2];

        Cursor cursor = database.rawQuery("SELECT num_balise,suivante FROM liste_balises " +
                "WHERE temps != '' AND temps=(SELECT max(temps) FROM liste_balises) ", null);

        if (cursor.moveToFirst()) {

            balise[0] = cursor.getString(0);
            balise[1] = cursor.getString(1);
        }
        else
        {
            balise[0] = "";
            balise[1] = "";
        }

        cursor.close();
        database.close();

        return balise;
    }

    /**
     * Compose JSON out of SQLite records
     *
     * @return
     */
    public String composeJSONfromSQLite() { //a modifier
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM liste_balises ORDER BY temps";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //map.put("id", cursor.getString(0));
                map.put("numEquipe", cursor.getString(1));
                map.put("balise", cursor.getString(2));
                map.put("temps", cursor.getString(3));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

}
