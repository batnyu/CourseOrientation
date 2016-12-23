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


public class DBController  extends SQLiteOpenHelper {

    public DBController(Context applicationcontext) {
        super(applicationcontext, "course.db", null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE parcoursLite ( id INTEGER, balise INTEGER, temps INTEGER)";
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
                map.put("balise", cursor.getString(1));
                map.put("temps", cursor.getString(2));
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
                    database.execSQL("UPDATE parcoursLite SET temps = ? WHERE balise = ?", new String[]{temps,balise});
                    cursor.close();
                    database.close();
                    return 3;
                }
                else if(departOK)
                {
                    database.execSQL("UPDATE parcoursLite SET temps = ? WHERE balise = ?", new String[]{temps,balise});
                    cursor.close();
                    database.close();
                    return 1;
                }
                else
                {
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

}
