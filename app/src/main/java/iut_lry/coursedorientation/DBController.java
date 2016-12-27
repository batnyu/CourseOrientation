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


public class DBController  extends SQLiteOpenHelper {

    public DBController(Context applicationcontext) {
        super(applicationcontext, "course.db", null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE parcoursLite ( id INTEGER, numCourse INTEGER, numEquipe INTEGER, balise INTEGER, temps INTEGER)";
        database.execSQL(query);

    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS parcoursLite";
        database.execSQL(query);
        onCreate(database);
    }

    public void deleteTable(String TABLE_NAME) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query;
        database.execSQL("delete from "+ TABLE_NAME);
    }

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", queryValues.get("id"));
        values.put("numCourse", queryValues.get("numCourse"));
        values.put("numEquipe", queryValues.get("numEquipe"));
        values.put("balise", queryValues.get("balise"));
        values.put("temps", queryValues.get("temps"));
        database.insert("parcoursLite", null, values);
        database.close();
    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllUsers() {

        ArrayList<HashMap<String, String>> usersList;
        usersList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM parcoursLite ORDER BY temps";

        String test;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("numCourse", cursor.getString(1));
                map.put("numEquipe", cursor.getString(2));
                map.put("balise", cursor.getString(3));
                map.put("temps", cursor.getString(4));
                usersList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();

        database.close();

        return usersList;
    }

    public int checkBaliseUpdateTemps(String balise, String temps, boolean departOK) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT balise,temps FROM parcoursLite WHERE balise = ?", new String[]{balise});

        if(cursor.moveToFirst()){

            String colonne2 = cursor.getString(1);
            if(colonne2.equals("null"))
            {
                if(cursor.getString(0).equals("1"))
                {
                    cursor.close();
                    database.close();
                    return 3;
                }
                else if(departOK)
                {
                    cursor.close();
                    database.close();
                    return 1;
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
        }
        else
        {
            cursor.close();
            database.close();
            return 0;
        }
    }

    public void UpdateTemps(String balise, String temps) {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT balise,temps FROM parcoursLite WHERE balise = ?", new String[]{balise});

        if(cursor.moveToFirst()) {

            //Version plus safe
            ContentValues newValues = new ContentValues();
            newValues.put("temps", temps);
            String[] args = new String[]{balise};
            database.update("parcoursLite", newValues, "balise=?", args);
            //database.execSQL("UPDATE parcoursLite SET temps = ? WHERE balise = ?", new String[]{temps,balise});

        }
        cursor.close();
        database.close();
    }

    public void updateCourseEquipe(String numCourse, String numEquipe) {

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT numCourse, numEquipe FROM parcoursLite ", null);
        if(cursor.moveToFirst()){
            do {
                ContentValues newValues = new ContentValues();
                newValues.put("numCourse",numCourse);
                database.update("parcoursLite",newValues, null, null);

                newValues.put("numEquipe",numEquipe);
                database.update("parcoursLite",newValues, null, null);

            }while(cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM parcoursLite ORDER BY temps";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //map.put("id", cursor.getString(0));
                map.put("numCourse", cursor.getString(1));
                map.put("numEquipe", cursor.getString(2));
                map.put("balise", cursor.getString(3));
                map.put("temps", cursor.getString(4));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

}
