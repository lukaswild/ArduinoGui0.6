package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.GridView;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import connection.BTConnection;
import connection.EthernetConnection;
import connection.IConnection;
import elements.BoolElement;
import elements.Element;
import elements.EmptyElement;
import elements.LedModel;
import elements.PwmElement;
import elements.SwitchModel;
import observer.Gui;
import observer.Project;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TABLE_CONNECTIONS = "connections";
    private static final String TABLE_PROJECTS = "projects";
    private static final String TABLE_ELEMENTS = "elements";
    private static final String COLUMN_ID = "_id"; // Die ID soll in SQLite mit einem Underscore beginnen

    private static final String DATABASE_NAME = "DbArduinoGui";
    private static final int DATABASE_VERSION = 1;
    private final String LOG_TAG = "DatabaseHandler";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELEMENTS);

        Log.d(LOG_TAG, "Erzeugen der Datenbank...");
        createTableConnections(db);
        createTableProjects(db);
        createTableElements(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(LOG_TAG, "Öffnen der Datenbank...");
//        createTableConnections(db);
//        createTableProjects(db);
//        createTableElements(db);
    }


    private void createTableConnections(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONNECTIONS + " (" +
                COLUMN_ID + " integer primary key AUTOINCREMENT," +
                "type text NOT NULL," +
                "conName text NOT NULL," +
                "address text NOT NULL" +
                ")" );
    }

    private void createTableProjects(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PROJECTS + " (" +
                COLUMN_ID + " integer primary key AUTOINCREMENT," +
                "projName text," +
                "internal_id integer," +
                "creationDate text NOT NULL," +
                "lastModifiedDate text NOT NULL," +
                "lastOpenedDate text NOT NULL" +
                ")" );
    }

    private void createTableElements(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ELEMENTS + " (" +
                COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT," +
                "kind text NOT NULL," + // Bool oder Pwm
                "type text NOT NULL," + // z.B. SwitchModel, LedModel,...
                "position integer NOT NULL," +
                "status integer NOT NULL," +
                "identifier integer NULL," + // Wenn identifier noch nicht gesetzt wurde, so darf er null sein
                "resource int NULL," + // EmptyElement hat keine Ressource
                "project_fk int NOT NULL," +
                "CONSTRAINT project_fk FOREIGN KEY (project_fk) " +
                "REFERENCES projects (project_id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void updateConnections(ArrayList<String> allConsName,
                                  ArrayList<String> allConsType, ArrayList<String> allConsAddress, SQLiteDatabase db) {
//        db.delete("connections", "", null); // alle vorhandenen Datensätze löschen
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
        onCreate(db);

        for (int i = 0; i < allConsName.size(); i++) {
            SQLiteStatement cmdStoreCon = db.compileStatement("INSERT INTO " + TABLE_CONNECTIONS + " VALUES (null, ?, ?, ?)");
            cmdStoreCon.bindString(1, allConsType.get(i));
            cmdStoreCon.bindString(2, allConsName.get(i));
            cmdStoreCon.bindString(3, allConsAddress.get(i));
            cmdStoreCon.execute();

            Log.d(LOG_TAG, "Connection " + allConsName.get(i) + " in DB eingetragen");
        }

        //testTableProjects(db);
    }


    public void updateProjects(ArrayList<Project> allProjects, SQLiteDatabase db, Context context) {
//        db.delete("projects", "", null);
//        db.delete("elements", "", null);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS); // TODO nur Daten löschen oder ganze Tabelle löschen? Vorteil ganze Tabelle: id beginnt von vorne
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELEMENTS);
        onCreate(db);

//        allProjects.clear();

        for(Project p : allProjects) {
            // Projekt eintragen
            SQLiteStatement cmdInsertProj = db.compileStatement("INSERT INTO " + TABLE_PROJECTS + " VALUES ( null, ?, ?, ?, ?, ? )");
            cmdInsertProj.bindString(1, p.getName());
            cmdInsertProj.bindLong(2, p.getId());
            cmdInsertProj.bindString(3, p.getDateString(p.getCreationDate()));
            cmdInsertProj.bindString(4, p.getDateString(p.getLastModifiedDate()));
            cmdInsertProj.bindString(5, p.getDateString(p.getLastOpenedDate()));
            cmdInsertProj.execute();
            Log.d(LOG_TAG, "Projekt eingetragen: " + p.getName() + " Id: " + p.getId() + " Creation date: " +
                    p.getDateString(p.getCreationDate()) + " Last modified: " + p.getDateString(p.getLastModifiedDate()) +
                    " Last opened: " + p.getDateString(p.getLastOpenedDate()));

            // Zugehörige Elemente eintragen
            HashMap<Integer, Element> mapAllViewModels = p.getMapAllViewModels();
            Iterator iteratorMap = mapAllViewModels.entrySet().iterator();
            while(iteratorMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iteratorMap.next();
                Integer key = (Integer) entry.getKey();
                Element element = (Element) entry.getValue();
                String elementKind = "";
                String elementType = element.getClass().toString();
                long status = 0;

                if(element instanceof BoolElement) {
                    elementKind = "Bool";
                    if (((BoolElement) element).isStatusHigh())
                        status = 1;
                    else
                        status = 0;
                }
                else if(element instanceof PwmElement) {
                    elementKind = "Pwm";
                    status = ((PwmElement) element).getCurrentPwm();
                }

                else if (element instanceof EmptyElement){
                    elementKind="NULL";
                }

                SQLiteStatement cmdInsertElements = db.compileStatement("INSERT INTO " + TABLE_ELEMENTS + " VALUES ( null, ?, ?, ?, ?, ?, ?, ? )" );
                cmdInsertElements.bindString(1, elementKind); // Bool- oder Pwm-Element
                cmdInsertElements.bindString(2, elementType); // z.B. Switch, Led,...
                cmdInsertElements.bindLong(3, key);
                cmdInsertElements.bindLong(4, status);
                if(element.getIdentifier() != null)
                    cmdInsertElements.bindString(5, element.getIdentifier());
                else
                    cmdInsertElements.bindNull(5);
                cmdInsertElements.bindLong(6, element.getRessource());
                cmdInsertElements.bindLong(7, p.getId());
                cmdInsertElements.execute();
                Log.d(LOG_TAG, "Element eingetragen: " + elementType + " Position: " + key + " Projekt: " + p.getId());
            }
        }
//        ArrayList<Project> allProsFromDb = selectAllPros(db, context);
    }


    private void testTableProjects(SQLiteDatabase db) {
        db.execSQL("INSERT INTO projects VALUES (null, 'test1', '2015-2-7', '2015-2-6', '2015-2-6')");
        db.execSQL("INSERT INTO projects VALUES (null, 'test2', '2065-2-7', '2015-2-6', '2015-2-6')");
        db.execSQL("INSERT INTO projects VALUES (null, 'test3', '2014-2-7', '2015-2-6', '2015-2-8')");
        db.execSQL("INSERT INTO projects VALUES (null, 'test4', '2015-2-7', '2015-2-6', '2015-2-6')");
        db.execSQL("INSERT INTO projects VALUES (null, 'test5', '2055-2-7', '2015-2-6', '2015-2-11')");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest1', 2, 'on', 'P1', 1)");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest2', 3, 'off', 'P1', 2)");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest3', 4, 'on', 'P2', 1)");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest4', 10, 'off', 'A5', 2)");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest5', 1, 'off', 'A5', 1)");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest6', 5, 'off', 'P5', 3)");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest7', 15, 'off', 'P5', 3)");
        db.execSQL("INSERT INTO elements VALUES (null, 'Bool', 'elementTest8', 13, 'off', 'P6', 1)");


        Cursor c = db.query(TABLE_PROJECTS, new String[]{"_id, projName, creationDate, lastModifiedDate"}, null, null, null, null, null);

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String creationDate = c.getString(2);
            String lastModifiedDate = c.getString(3);

            Log.d(LOG_TAG, id + "");
            Log.d(LOG_TAG, name);
            Log.d(LOG_TAG, creationDate);
            Log.d(LOG_TAG, lastModifiedDate);
        }

        Cursor cc = db.query(TABLE_ELEMENTS, new String[]{"_id, type, position, status, project_fk"}, null, null, null, null, null);

        while (cc.moveToNext()) {
            int id = cc.getInt(0);
            String name = cc.getString(1);
            int position = cc.getInt(2);
            String status = cc.getString(3);
            int project_id = cc.getInt(4);

            Log.d(LOG_TAG, id + "");
            Log.d(LOG_TAG, name);
            Log.d(LOG_TAG, position + "");
            Log.d(LOG_TAG, status);
            Log.d(LOG_TAG, project_id + "");
        }


        Log.d(LOG_TAG, "ELEMENTS");


        /////////// So bekommt man zu allen Projekten die zugehörigen Elemente ////////////

        String queryAllProjects = "select _id, projName " +
                "from projects ";

        String queryAllElements = "select type, project_fk " +
                "from elements " +
                "where project_fk = ?";

        Cursor cProjects = db.rawQuery(queryAllProjects, null);
        while(cProjects.moveToNext()) {
            int pId = cProjects.getInt(0);
            String pName = cProjects.getString(1);
            Log.d(LOG_TAG, "Project id: " + pId + " Project name: " + pName);

            Cursor cElements = db.rawQuery(queryAllElements, new String[] {Integer.toString(pId)});
            while ((cElements.moveToNext())) {
                String elementName = cElements.getString(0);
                int projFk = cElements.getInt(1);
                Log.d(LOG_TAG, pName + " " + elementName + " " + projFk);
            }
        }

        ////////////////////////////////
    }


    public ArrayList<IConnection> selectAllCons(SQLiteDatabase db, Context context) {
        ArrayList<IConnection> allConsFromDb = new ArrayList<IConnection>();
        Cursor c = db.query(TABLE_CONNECTIONS, new String[]{"_id", "type", "conName", "address"}, null, null, null, null, null);

        while (c.moveToNext()) {
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


    public ArrayList<Project> selectAllPros(SQLiteDatabase db, Context context, GridView gridView) {
        Log.d(LOG_TAG, "ABFRAGE PROJEKTE MIT ELEMENTEN");
        ArrayList<Project> allProjsFromDb = new ArrayList<Project>();

        String queryAllElements = "select * " +
                "from elements " +
                "where project_fk = ?";

        // Alle Projekte aus DB holen
        Cursor cProjects = db.query(TABLE_PROJECTS, new String[] {"_id", "projName", "internal_id", "creationDate", "lastModifiedDate", "lastOpenedDate"}, null, null, null, null, null);
        while (cProjects.moveToNext()) {

            int pId = cProjects.getInt(0);
            String pName = cProjects.getString(1);
            int pInternalId = cProjects.getInt(2);
            String creationDate = cProjects.getString(3);
            String lastModifiedDate = cProjects.getString(4);
            String lastOpenedDate = cProjects.getString(5);
            Log.d(LOG_TAG, pId + " " + pName + " " + creationDate + " " + lastModifiedDate + " " + lastOpenedDate);

            String[] creationDateSplit = creationDate.split("/");
            String[] lastModifiedDateSplit = lastModifiedDate.split("/");
            String[] lastOpenedDateSplit = lastOpenedDate.split("/");
            Calendar calCreationDate = Calendar.getInstance();
            Calendar calLastModifiedDate = Calendar.getInstance();
            Calendar calLastOpenedDate = Calendar.getInstance();
            setCorrectDatesFromDb(creationDateSplit, lastModifiedDateSplit, lastOpenedDateSplit, calCreationDate, calLastModifiedDate, calLastOpenedDate);

            HashMap<Integer, Element> mapAllViewModels = new HashMap<Integer, Element>();

            // Alle zugehörigen Elemente zu diesem Projekt holen
            Cursor cElements = db.rawQuery(queryAllElements, new String[] {Integer.toString(pInternalId)});
            while ((cElements.moveToNext())) {
                int eId = cElements.getInt(0);
                String eKind = cElements.getString(1);
                String eType = cElements.getString(2);
                int position = cElements.getInt(3); // Key in HashMap
                int status = cElements.getInt(4);
                String identifier = cElements.getString(5);
                int resource = cElements.getInt(6);
                int project_fk = cElements.getInt(7);

                Log.d(LOG_TAG, pName + " ID: " + eId + " Art: " + eKind + " Typ: " + eType + " Position: " + position +
                        " Status: " + status + "Identifier: " + identifier + " Ressource: " + resource + " ProjectFk: " + project_fk);

                // Erzeugen eines neuen Elements mit genau diesen Daten, um die HashMap zu füllen
                Element e = new Element();

                if(eType.equals(context.getString(R.string.classSwitchModel)))
                    e = new SwitchModel();
                else if (eType.equals(context.getString(R.string.classLedModel)))
                    e = new LedModel(); // TODO mehrere Elemente
                else if(eType.equals(context.getString(R.string.classEmptyElement)))
                    e=new EmptyElement();
                e.setIdentifier(identifier);
                e.setRessource(resource);

                if(eKind.equals("Bool")) {
                    boolean boolStatus = false;
                    if(status == 1)
                        boolStatus = true;
                    ((BoolElement)e).setStatusHigh(boolStatus);
                }

                if (eKind.equals("Pwm")){
                    ((PwmElement)e).setCurrentPwm(status);
                }

                else {
                    //TODO es ist ein empty emelemnt. Status ??
                }


                mapAllViewModels.put(position, e);
                Log.d(LOG_TAG, eType + " auf Position " + position + " aus DB geholt");
            }

            Gui pGui = new Gui(context, 2, gridView);
            Project p = new Project(pGui, pInternalId, pName, calCreationDate, calLastModifiedDate, calLastOpenedDate, mapAllViewModels);
            allProjsFromDb.add(p);
            Log.d(LOG_TAG, "Projekt " + pName + " aus DB geholt");
        }
        return allProjsFromDb;
    }


    private void setCorrectDatesFromDb(String[] creationDateSplit, String[] lastModifiedDateSplit, String[] lastOpenedDateSplit,
                                       Calendar calCreationDate, Calendar calLastModifiedDate, Calendar calLastOpenedDate) {

        calCreationDate.set(Integer.parseInt(creationDateSplit[0]), Integer.parseInt(creationDateSplit[1]) - 1,
                Integer.parseInt(creationDateSplit[2]), Integer.parseInt(creationDateSplit[3]),
                Integer.parseInt(creationDateSplit[4]), Integer.parseInt(creationDateSplit[5]));

        calLastModifiedDate.set(Integer.parseInt(lastModifiedDateSplit[0]), Integer.parseInt(lastModifiedDateSplit[1]) - 1,
                Integer.parseInt(lastModifiedDateSplit[2]), Integer.parseInt(lastModifiedDateSplit[3]),
                Integer.parseInt(lastModifiedDateSplit[4]), Integer.parseInt(lastModifiedDateSplit[5]));

        calLastOpenedDate.set(Integer.parseInt(lastOpenedDateSplit[0]), Integer.parseInt(lastOpenedDateSplit[1]) - 1,
                Integer.parseInt(lastOpenedDateSplit[2]), Integer.parseInt(lastOpenedDateSplit[3]),
                Integer.parseInt(lastOpenedDateSplit[4]), Integer.parseInt(lastOpenedDateSplit[5]));
    }
}