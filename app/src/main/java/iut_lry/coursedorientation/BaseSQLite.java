package iut_lry.coursedorientation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Baptiste on 02/12/2016 oui.
 */
public class BaseSQLite extends SQLiteOpenHelper {

    private static final String TABLE_CHECKPOINTS = "table_checkpoints";
    private static final String COL_ID = "ID";
    private static final String COL_BALISEACTUELLE = "Balise à pointer";
    private static final String COL_BALISESUIVANTE = "Balise suivante";
    private static final String COL_INDICATION = "Indication";
    private static final String COL_POSTE = "Poste";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_CHECKPOINTS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_BALISEACTUELLE + " TEXT NOT NULL, "
            + COL_BALISESUIVANTE + " TEXT NOT NULL, " + COL_INDICATION + " TEXT NOT NULL, "
            + COL_POSTE + " TEXT NOT NULL);";

    public BaseSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE_CHECKPOINTS + ";");
        onCreate(db);
    }

}
