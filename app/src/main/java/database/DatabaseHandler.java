package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.arduinogui.R;

import java.util.ArrayList;

import connection.BTConnection;
import connection.EthernetConnection;
import connection.IConnection;

/**
 * Created by Lukas on 04.01.2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TABLE_CONNECTIONS = "connections";
    public static final String COLUMN_ID = "_id";

    private static final String DATABASE_NAME = "DbArduinoGui";
    private static final int DATABASE_VERSION = 1;
    private final String LOG_TAG = "DatabaseHandler";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableConnections(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        createTableConnections(db);
    }

    private void createTableConnections(SQLiteDatabase db) {
        db.execSQL("create table if not exists connections (" +
                "connection_id integer primary key," +
                "type text not null," +
                "name text not null," +
                "address text not null" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void updateConnections(ArrayList<String> allConsName, ArrayList<String> allConsType, ArrayList<String> allConsAddress, SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
        createTableConnections(db);

        for (int i = 0; i < allConsName.size(); i++) {
            SQLiteStatement cmdStoreCon = db.compileStatement("INSERT INTO " + TABLE_CONNECTIONS + " VALUES (null, ?, ?, ?)");
            cmdStoreCon.bindString(1, allConsType.get(i));
            cmdStoreCon.bindString(2, allConsName.get(i));
            cmdStoreCon.bindString(3, allConsAddress.get(i));
            cmdStoreCon.execute();

            Log.d(LOG_TAG, "Connection " + allConsName.get(i) + " in DB eingetragen");
        }
    }


    public ArrayList<IConnection> selectAllCons(SQLiteDatabase db, Context context) {
        ArrayList<IConnection> allConsFromDb = new ArrayList<IConnection>();
        Cursor c = db.query(TABLE_CONNECTIONS, new String[]{"connection_id", "type", "name", "address"}, null, null, null, null, null);

        while (c.moveToNext()) {
            Log.d(LOG_TAG, "hier");
            int id = c.getInt(0);
            String type = c.getString(1);
            String name = c.getString(2);
            String address = c.getString(3);

            if(type.equals(context.getString(R.string.description_btCon)))
                allConsFromDb.add(BTConnection.createAttributeCon(name, address));
            else if (type.equals(context.getString(R.string.description_ethernetCon)))
                allConsFromDb.add(EthernetConnection.createAttributeCon(name, address));

            Log.d(LOG_TAG, "DB ID: " + id + " " + type + " " + name + " " + address);
        }
        return allConsFromDb;
    }
}