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
            "num_course INTEGER )";

    private static final String CREATE_TABLE_COURSE = "CREATE TABLE course ( " +
            "id INTEGER PRIMARY KEY, " +
            "date TEXT, " +
            "temps TEXT )";

    private static final String CREATE_TABLE_PARCOURS = "CREATE TABLE parcours ( " +
            "id INTEGER PRIMARY KEY, " +
            "num_course INTEGER, " +
            "categorie TEXT, " +
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
            "groupe TEXT, " +
            "points INTEGER, " +
            "temps TEXT )";

    private static final String CREATE_TABLE_BALISE = "CREATE TABLE balise ( " +
            "num INTEGER PRIMARY KEY, " +
            "coord_gps INTEGER, " +
            "poste TEXT )";

    private static final String CREATE_TABLE_GROUPE = "CREATE TABLE groupe ( " +
            "nom_groupe TEXT PRIMARY KEY, " +
            "balise_entree INTEGER, " +
            "balise_sortie INTEGER, " +
            "points_bonus INTEGER )";

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
            database.insert("equipe", null, values);
        }
        else if(queryValues.get("course.id") != null)
        {
            System.out.println("course");
            //course
            values.put("id", queryValues.get("course.id"));
            values.put("date", queryValues.get("course.date"));
            values.put("temps", queryValues.get("course.temps"));
            database.insert("course", null, values);
        }
        else if(queryValues.get("parcours.id") != null)
        {
            System.out.println("parcours");
            //parcours
            values.put("id", queryValues.get("parcours.id"));
            values.put("num_course", queryValues.get("parcours.num_course"));
            values.put("categorie", queryValues.get("parcours.categorie"));
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
            values.put("coord_gps", queryValues.get("balise.coord_gps"));
            values.put("poste", queryValues.get("balise.poste"));
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
            values.put("points_bonus", queryValues.get("groupe.points_bonus"));
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

    public boolean checkParcours(){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id FROM parcours", null);

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

        String selectQuery = "SELECT liste_balises.num_balise,liste_balises.temps,liste_balises.suivante,liste_balises.num_suivante," +
                             "balise.poste,liste_balises.azimut,liste_balises.azimut_degre,liste_balises.azimut_distance" +
                             " FROM liste_balises INNER JOIN balise ON liste_balises.num_balise = balise.num" +
                             parametres +
                             " ORDER BY CASE WHEN temps = '' THEN 2 ELSE 1 END, temps";


        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("num_balise", cursor.getString(0));
                map.put("temps", cursor.getString(1));
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

    public int checkBalise(String balise, boolean departOK, String baliseDepart, String baliseSuivante, String nbBaliseSuivante, String pocheActuelle) {

        boolean scanPossible = false;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT num_balise,temps,groupe,balise_entree,balise_sortie FROM liste_balises " +
                                          "LEFT JOIN groupe ON groupe = nom_groupe WHERE num_balise = ?", new String[]{balise});

        if (cursor.moveToFirst())
        {

            String tempsReq = cursor.getString(1);
            String poche = cursor.getString(2);
            String baliseEntree = cursor.getString(3);
            String baliseSortie = cursor.getString(4);

            if (tempsReq.equals(""))
            {

                if (cursor.getString(0).equals(baliseDepart))//si la balise de depart est scanné
                {
                    cursor.close();
                    database.close();
                    return 3;
                }
                else if (departOK && (balise.equals(nbBaliseSuivante) || nbBaliseSuivante.equals("") || baliseSuivante.equals("optionnelle"))) //si la première balise a déjà été scanné
                {
                    //Si la balise qu'on scanne n'a pas de poche, mettre ses balises à null
                    if(poche.equals("null"))
                    {
                        baliseEntree = "null";
                        baliseSortie = "null";
                    }
                    System.out.println("baliseEntree = " + baliseEntree);
                    System.out.println("balise = " + balise);

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
                    cursor.close();
                    database.close();
                    return 5; //si c'est pas la balise suivante et que le parcours a commencé
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

    public String updateOngletParametres() {

        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT course.id, equipe.id, prenom, nom, nom_equipe, parcours.categorie, parcours.id, date_naissance FROM joueurs " +
                             "INNER JOIN equipe ON joueurs.num_equipe = equipe.id " +
                             "INNER JOIN course ON equipe.num_course = course.id " +
                             "INNER JOIN parcours ON course.id = parcours.num_course ";

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
                    "FROM balise WHERE num = ?", new String[]{cursor.getString(2)});

            if(cursor25.moveToFirst()) {
                balise[8] = cursor25.getString(0);
            }
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

    public boolean checkSortiePoche(String pocheActuelle){ //essai autrement
        SQLiteDatabase database = this.getReadableDatabase();

        String temps = "";

        Cursor cursor = database.rawQuery("SELECT num_balise,temps,groupe,balise_entree FROM liste_balises " +
                "LEFT JOIN groupe ON groupe = nom_groupe WHERE num_balise = ?", new String[]{pocheActuelle});

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

    public String getSortiePoche(String pocheActuelle) {

        SQLiteDatabase database = this.getReadableDatabase();

        String sortiePoche = "";

        Cursor cursor = database.rawQuery("SELECT balise_sortie FROM groupe WHERE nom_groupe=?", new String[]{pocheActuelle});

        if (cursor.moveToFirst()) {
            sortiePoche = cursor.getString(0);
        }

        cursor.close();
        database.close();

        return sortiePoche;
    }

    public String getRemainingPoche(String pocheActuelle) {

        SQLiteDatabase database = this.getReadableDatabase();

        String poche = "";

        Cursor cursor = database.rawQuery("SELECT num_balise FROM liste_balises WHERE groupe=? AND temps = ''", new String[]{pocheActuelle});

        if (cursor.moveToFirst()) {
            do {
                if(poche.equals("")) {
                    poche = cursor.getString(0);
                } else {
                    poche = poche + "-" + cursor.getString(0);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return poche;
    }

    public String getCheckedPoche(String pocheActuelle) {

        SQLiteDatabase database = this.getReadableDatabase();

        String poche = "";

        Cursor cursor = database.rawQuery("SELECT num_balise FROM liste_balises WHERE groupe=? AND temps != ''", new String[]{pocheActuelle});

        if (cursor.moveToFirst()) {
            do {
                if(poche.equals("")) {
                    poche = cursor.getString(0);
                } else {
                    poche = poche + "-" + cursor.getString(0);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

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
        Cursor cursor2 = database.rawQuery("SELECT num FROM liste_liaisons", null);

        if (cursor2.moveToFirst()) {
            do {

                String liaisonActuelle = cursor2.getString(0);
                System.out.println("liaisonActuelle = " + liaisonActuelle);

                sens = determinerSensLiaison(database, liaisonActuelle);

                //pour faire dans les deux sens
                if(sens.equals("ASC") || sens.equals("DESC"))
                {
                    Cursor cursor3 = database.rawQuery("SELECT liste_liaisons.num,liaison.balise,liaison.ordre," +
                            "liste_liaisons.points FROM liste_liaisons INNER JOIN liaison " +
                            "ON liste_liaisons.num = liaison.num " +
                            "WHERE liste_liaisons.num = ? " +
                            "ORDER BY liaison.ordre " + sens, new String[]{liaisonActuelle});

                    System.out.println("stp : " + cursor3.getCount());

                    if (cursor3.moveToFirst())
                    {
                        int longueurTest = 0;

                        int longueur = cursor3.getCount();

                        Cursor cursor4 = database.rawQuery("SELECT num_balise,temps FROM liste_balises WHERE temps != '' " +
                                "ORDER BY temps", null);
                        if (cursor4.moveToFirst()) {
                            do {
                                if (longueurTest == 54)
                                {
                                    cursor4.moveToPrevious();
                                    longueurTest = 0;
                                }

                                if (cursor3.getString(1).equals(cursor4.getString(0)))
                                {
                                    longueurTest++;
                                    System.out.println("Truc égal à balise !");

                                    if (longueurTest == longueur)
                                    {
                                        somme = somme + Integer.parseInt(cursor3.getString(3));
                                        longueurTest = 0;
                                        cursor3.moveToFirst();
                                        //une fois que t'as trouvé la liaison, normalement tu peux l'avoir qu'une seule fois
                                        // donc là, faudrait quitter la boucle pour opti.
                                    }
                                    else
                                    {
                                        cursor3.moveToNext();
                                    }

                                }
                                else if(longueurTest > 0)
                                {
                                    longueurTest = 54;
                                    cursor3.moveToFirst();
                                }

                            } while (cursor4.moveToNext());
                        }
                    }
                    cursor3.close();
                }



            } while (cursor2.moveToNext());
        }

        cursor2.close();


        //FIN TEST


        database.close();

        return String.valueOf(somme);
    }

    public String determinerSensLiaison(SQLiteDatabase database, String liaisonActuelle)
    {
        //DETERMINATION DU SENS ASC OU DESC
        String sens = "";

        Cursor cursor44 = database.rawQuery("SELECT num FROM liaison WHERE num = ?", new String[]{liaisonActuelle});

        Cursor cursor45 = database.rawQuery("SELECT num_balise,liaison.ordre FROM liste_balises " +
                "INNER JOIN parcours ON liste_balises.num_parcours = parcours.id " +
                "INNER JOIN liste_liaisons ON parcours.id = liste_liaisons.num_parcours " +
                "INNER JOIN liaison ON liste_liaisons.num = liaison.num " +
                "WHERE liste_liaisons.num = ? AND temps != '' AND num_balise = balise " +
                "ORDER BY temps", new String[]{liaisonActuelle});

        if(cursor45.moveToFirst())
        {
            System.out.println("nb balise liaison = " + cursor44.getCount());
            System.out.println("num_balise =  " + cursor45.getString(0) + "  liaison_ordre = " + cursor45.getString(1));

            if(Integer.parseInt(cursor45.getString(1)) == cursor44.getCount())
            {
                sens = "DESC";
            }
            else if(Integer.parseInt(cursor45.getString(1)) == 1)
            {
                sens = "ASC";
            }

        }
        cursor44.close();
        cursor45.close();

        return sens;
    }

    public String getLiaisons() {

        SQLiteDatabase database = this.getReadableDatabase();


        String liaisons = "";
        String sens = "aucun";
        String reussi = "";
        String encours = "";
        String rate = "";

        Cursor cursor2 = database.rawQuery("SELECT num FROM liste_liaisons", null);

        if (cursor2.moveToFirst()) {
            do {

                String liaisonActuelle = cursor2.getString(0);
                System.out.println("liaisonActuelle = " + liaisonActuelle);

                //déterminer le sens de la liaison
                sens = determinerSensLiaison(database, liaisonActuelle);

                if(sens.equals("ASC") || sens.equals("DESC"))
                {

                    Cursor cursor3 = database.rawQuery("SELECT liste_liaisons.num,liaison.balise,liaison.ordre," +
                            "liste_liaisons.points FROM liste_liaisons INNER JOIN liaison " +
                            "ON liste_liaisons.num = liaison.num " +
                            "WHERE liste_liaisons.num = ? " +
                            "ORDER BY liaison.ordre " + sens, new String[]{liaisonActuelle});

                    if (cursor3.moveToFirst())
                    {
                        int longueurTest = 0;

                        int longueur = cursor3.getCount();

                        System.out.println("SENS = " + sens);

                        Cursor cursor4 = database.rawQuery("SELECT num_balise,temps FROM liste_balises " +
                                "ORDER BY CASE WHEN temps = '' THEN 2 ELSE 1 END, temps", null);
                        if (cursor4.moveToFirst()) {
                            do {

                                //Si la bonne balise a été scanné
                                if (cursor3.getString(1).equals(cursor4.getString(0)) && !cursor4.getString(1).equals(""))
                                {
                                    longueurTest++;
                                    System.out.println("OUI et longueurTest = " + longueurTest);

                                    if (longueurTest == longueur)
                                    {
                                        reussi = reussi + "[<font color='#2f9e00'>OK</font>] ";

                                        if(cursor3.moveToFirst())
                                        {
                                            do {
                                                if(!cursor3.isLast())
                                                    reussi = reussi + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>-";
                                                else
                                                    reussi = reussi + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>" +
                                                            " : " + cursor3.getString(3) + " pts<br/>";

                                            } while(cursor3.moveToNext());
                                        }

                                        cursor4.moveToLast();
                                    }
                                    else
                                    {
                                        cursor3.moveToNext();
                                    }

                                }
                                //Si une bonne balise a été scanné, et que les suivantes ne l'ont pas encore été
                                else if(longueurTest > 0 && longueurTest < longueur && cursor4.getString(1).equals(""))
                                {
                                    encours = encours + "[<font color='#ffa100'>...</font>] ";
                                    System.out.println("LA DEDANS");

                                    if(cursor3.moveToFirst())
                                    {
                                        int compteur=0;
                                        do {
                                            if(!cursor3.isLast() && compteur < longueurTest)
                                                encours = encours + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>-";
                                            else if(!cursor3.isLast())
                                                encours = encours + "<font color='#ffa100'>" + cursor3.getString(1) + "</font>-";
                                            else
                                                encours = encours + "<font color='#ffa100'>" + cursor3.getString(1) + "</font>" +
                                                        " : " + cursor3.getString(3) + " pts<br/>";

                                            compteur++;
                                        } while(cursor3.moveToNext());
                                        cursor4.moveToLast();
                                    }
                                }
                                //Si une bonne balise a été scanné et que la suivante n'est pas la bonne
                                else if(longueurTest > 0 && longueurTest < longueur && !cursor3.getString(1).equals(cursor4.getString(0)))
                                {
                                    rate = rate + "[<font color='#db1500'>KO</font>] ";
                                    System.out.println("LA DEDANS");

                                    if(cursor3.moveToFirst())
                                    {
                                        int compteur=0;
                                        do {
                                            if(!cursor3.isLast() && compteur < longueurTest)
                                                rate = rate + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>-";
                                            else if(!cursor3.isLast())
                                                rate = rate + "<font color='#db1500'>" + cursor3.getString(1) + "</font>-";
                                            else
                                                rate = rate + "<font color='#db1500'>" + cursor3.getString(1) + "</font>" +
                                                        " : <strike>" + cursor3.getString(3) + " pts</strike><br/>";

                                            compteur++;
                                        } while(cursor3.moveToNext());
                                        cursor4.moveToLast();
                                    }
                                }
                                //réorganisation de l'affichage
                                liaisons = encours + reussi + rate;

                            } while (cursor4.moveToNext());
                        }
                    }
                    cursor3.close();
                }



            } while (cursor2.moveToNext());
        }

        cursor2.close();


        //FIN TEST


        database.close();

        return liaisons;
    }

    public String getLiaisonsTest() {

        SQLiteDatabase database = this.getReadableDatabase();


        String liaisons = "";
        String sens;
        String statut;

        Cursor cursor2 = database.rawQuery("SELECT num FROM liste_liaisons", null);

        if (cursor2.moveToFirst()) {
            do {

                String liaisonActuelle = cursor2.getString(0);
                System.out.println("liaisonActuelle = " + liaisonActuelle);

                //pour faire dans les deux sens
                for(int i=0;i<2;i++)
                {
                    if(i==0){
                        sens = "ASC";
                    } else {
                        sens = "DESC";
                    }

                    Cursor cursor3 = database.rawQuery("SELECT liste_liaisons.num,liaison.balise,liaison.ordre," +
                            "liste_liaisons.points FROM liste_liaisons INNER JOIN liaison " +
                            "ON liste_liaisons.num = liaison.num " +
                            "WHERE liste_liaisons.num = ? " +
                            "ORDER BY liaison.ordre " + sens, new String[]{liaisonActuelle});

                    System.out.println("stp : " + cursor3.getCount());
                    //Cursor cursor3 = database.rawQuery("SELECT * FROM liste_liaisons",null);

                    if (cursor3.moveToFirst())
                    {
                        //do {
                        int longueurTest = 0;

                        int longueur = cursor3.getCount();

                        Cursor cursor4 = database.rawQuery("SELECT num_balise,temps FROM liste_balises " +
                                "ORDER BY temps", null);
                        if (cursor4.moveToFirst()) {
                            do {
/*                                if(cursor3.getString(1).equals(cursor4.getString(0)) && !cursor4.getString(1).equals(""))
                                {
                                    longueurTest++;
                                    cursor3.moveToNext();
                                }
                                else if(longueurTest > 0 && longueurTest < longueur && cursor4.getString(1).equals(""))
                                {
                                    //incomplète
                                    statut = "incomplete";

                                    liaisons = liaisons + "[<font color='#ffa100'>En cours</font>] ";
                                    if(cursor3.moveToFirst())
                                    {
                                        do {
                                            if(!cursor3.isLast())
                                                liaisons = liaisons + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>-";
                                            else
                                                liaisons = liaisons + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>" +
                                                        " : " + cursor3.getString(3) + " pts";

                                        } while(cursor3.moveToNext());
                                    }


                                }
                                else if(longueurTest > 0 && !cursor3.getString(1).equals(cursor4.getString(0)))
                                {
                                    //raté
                                }*/




                                if (longueurTest == 54)
                                {
                                    cursor4.moveToPrevious();
                                    longueurTest = 0;
                                }

                                if (cursor3.getString(1).equals(cursor4.getString(0)) && !cursor4.getString(1).equals(""))
                                {
                                    longueurTest++;
                                    System.out.println("Truc égal à balise !");

                                    if (longueurTest == longueur)
                                    {
                                        liaisons = liaisons + "[<font color='#2f9e00'>OK</font>] ";
                                        if(cursor3.moveToFirst())
                                        {
                                            do {
                                                if(!cursor3.isLast())
                                                    liaisons = liaisons + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>-";
                                                else
                                                    liaisons = liaisons + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>" +
                                                            " : " + cursor3.getString(3) + " pts";

                                            } while(cursor3.moveToNext());
                                        }

                                        longueurTest = 0;
                                        cursor3.moveToFirst();
                                        //une fois que t'as trouvé la liaison, normalement tu peux l'avoir qu'une seule fois
                                        // donc là, faudrait quitter la boucle pour opti.
                                    }
                                    else
                                    {
                                        cursor3.moveToNext();
                                    }

                                }
                                else if(longueurTest > 0)
                                {
                                    longueurTest = 54;
                                    cursor3.moveToFirst();
                                }

                                if(longueurTest > 0 && longueurTest < longueur && cursor4.getString(1).equals(""))
                                {
                                    liaisons = liaisons + "[<font color='#ffa100'>En cours</font>] ";

                                    if(cursor3.moveToFirst())
                                    {
                                        int compteur=0;
                                        do {
                                            if(!cursor3.isLast() && compteur < longueurTest)
                                                liaisons = liaisons + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>-";
                                            else if(!cursor3.isLast())
                                                liaisons = liaisons + cursor3.getString(1) + "-";
                                            else
                                                liaisons = liaisons + "<font color='#2f9e00'>" + cursor3.getString(1) + "</font>" +
                                                        " : " + cursor3.getString(3) + " pts";

                                            compteur++;
                                        } while(cursor3.moveToNext());
                                    }
                                }


                            } while (cursor4.moveToNext());
                        }

                        //faire resultats ici

                        //} while (cursor3.moveToNext());
                    }
                    cursor3.close();
                }



            } while (cursor2.moveToNext());
        }

        cursor2.close();


        //FIN TEST


        database.close();

        return liaisons;
    }


    public String getLiaisonsPossibles() {

        SQLiteDatabase database = this.getReadableDatabase();

        String sens;
        String liaisons = "";
        String statut;
        String pointsActuels = "";
        boolean atLeastOneBalise = false;

        Cursor cursor2 = database.rawQuery("SELECT num FROM liste_liaisons", null);

        if (cursor2.moveToFirst()) {
            do {

                String liaisonActuelle = cursor2.getString(0);
                System.out.println("liaisonActuelle = " + liaisonActuelle);

                //pour faire dans les deux sens
                for(int i=0;i<2;i++)
                {
                    if(i==0){
                        sens = "ASC";
                    } else {
                        sens = "DESC";
                    }

                    Cursor cursor3 = database.rawQuery("SELECT liste_liaisons.num,liaison.balise,liaison.ordre," +
                            "liste_liaisons.points FROM liste_liaisons INNER JOIN liaison " +
                            "ON liste_liaisons.num = liaison.num " +
                            "WHERE liste_liaisons.num = ? " +
                            "ORDER BY liaison.ordre " + sens, new String[]{liaisonActuelle});

                    if (cursor3.moveToFirst())
                    {
                        do {
                            int longueurTest = 0;

                            int longueur = cursor3.getCount();
                            pointsActuels = cursor3.getString(3);
                            atLeastOneBalise = false;

                            Cursor cursor4 = database.rawQuery("SELECT num_balise,temps FROM liste_balises " +
                                    "ORDER BY temps", null);
                            if (cursor4.moveToFirst()) {
                                do {

                                    if (cursor3.getString(1).equals(cursor4.getString(0)) && !cursor4.getString(1).equals(""))
                                    {
                                        atLeastOneBalise = true;
                                        liaisons = liaisons + "<font color='#2f9e00'>" + cursor4.getString(0) + "</font>-";
                                        i=1;

                                    }
                                    else
                                    {
                                        if(atLeastOneBalise)
                                        {
                                            if(cursor4.getString(1).equals(""))
                                            {
                                                liaisons = liaisons + "<font color='black'>" + cursor4.getString(0) + "</font>-";
                                                statut = "incomplet";
                                            }
                                            else
                                            {
                                                liaisons = liaisons + "<font color='#af1400'>" + cursor4.getString(0) + "</font>-";
                                                statut = "raté";
                                            }
                                        }

                                    }



                                } while (cursor4.moveToNext());
                            }
                        } while(cursor3.moveToNext());


                    }
                    cursor3.close();
                }

                if(!pointsActuels.equals(""))
                    liaisons = liaisons + " : " + pointsActuels + " pts\n";

            } while (cursor2.moveToNext());
        }

        cursor2.close();


        //FIN TEST


        database.close();

        return liaisons;
    }

    public String getLiaisonsPossiblesNuLLE(String lastBalise) {

        SQLiteDatabase database = this.getReadableDatabase();

        String liaisons = "";
        String sens;


        Cursor cursor = database.rawQuery("SELECT description,points FROM liste_liaisons INNER JOIN liaison " +
                                          "ON liste_liaisons.num = liaison.num " +
                                          "WHERE liaison.balise=?", new String[]{lastBalise});

        if (cursor.moveToFirst()) {
            do {
                liaisons = liaisons + cursor.getString(0) + " -> " + cursor.getString(1) + " pts" + "\n";
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return liaisons;
    }


    /**
     * Compose JSON out of SQLite records
     *
     * @return
     */
    public String composeJSONfromSQLite() { //a modifier
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT course.id, parcours.id, equipe.id, liste_balises.num_balise, liste_balises.temps FROM liste_balises " +
                             "INNER JOIN parcours ON liste_balises.num_parcours = parcours.id " +
                             "INNER JOIN course ON parcours.num_course = course.id " +
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
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

}
