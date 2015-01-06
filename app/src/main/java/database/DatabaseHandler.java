package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import observer.Gui;

/**
 * Created by Lukas on 04.01.2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {


    // Datenbank Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ArduinoGuiDatabase";

    // Tabellenname
    private static final String TABLE_PROJECTS = "Project";

    // Inhalt der Tabelle Project
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    //private static final Gui KEY_GUI = "gui";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Erzeugen der Tabellen
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PROJECTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);

        // Create tables again
        onCreate(db);
    }
}