package observer;

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
import elements.PushButtonModel;
import elements.PwmElement;
import elements.PwmInputModel;
import elements.PwmModel;
import elements.SwitchModel;
import generic.ComObjectStd;


public class DatabaseHandler extends SQLiteOpenHelper implements IObserver {

    private static final String TABLE_CONNECTIONS = "connections";
    private static final String TABLE_PROJECTS = "projects";
    private static final String TABLE_ELEMENTS = "elements";
    private static final String COLUMN_ID = "_id"; // Die ID soll in SQLite mit einem Underscore beginnen

    private static final String DATABASE_NAME = "DbArduinoGui";
    private static final int DATABASE_VERSION = 1;
    private final String LOG_TAG = "DatabaseHandler";
    private SQLiteDatabase db;

    public static final int ACTION_INSERT_ELEMENT = 0;
    public static final int ACTION_UPDATE_ELEMENT_TYPE = 4;
    public static final int ACTION_UPDATE_ELEMENT = 1;
    public static final int ACTION_REMOVE_ELEMENT = 2;
    public static final int ACTION_UPDATE_IDENTIFIER = 3;
    public static final int ACTION_NOTHING = 5;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase getDb() {
        return this.db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELEMENTS);

        Log.d(LOG_TAG, "Erzeugen der Datenbank...");
        this.db = db;
        // db=this.getWritableDatabase();
        createTableConnections(db);
        createTableProjects(db);
        createTableElements(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(LOG_TAG, "Öffnen der Datenbank...");
        //db.execSQL("DROP TABLE IF EXISTS connections");
        // db.execSQL("DROP TABLE IF EXISTS projects");
        // db.execSQL("DROP TABLE IF EXISTS elements");
        createTableConnections(db);
        createTableProjects(db);
        createTableElements(db);
    }


    private void createTableConnections(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONNECTIONS + " (" +
                COLUMN_ID + " integer primary key AUTOINCREMENT," +
                "type text NOT NULL," +
                "conName text NOT NULL," +
                "address text NOT NULL" +
                ")");
    }

    private void createTableProjects(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PROJECTS + " (" +
                COLUMN_ID + " integer primary key AUTOINCREMENT," +
                "projName text," +
                "internal_id integer," +
                "creationDate text NOT NULL," +
                "lastModifiedDate text NOT NULL," +
                "lastOpenedDate text NOT NULL" +
                ")");
    }

    private void createTableElements(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ELEMENTS + " (" +
                COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT," +
                "kind text NOT NULL," + // Bool oder Pwm
                "type text NOT NULL," + // z.B. SwitchModel, LedModel,...
                "position integer NOT NULL," +
                "status integer NOT NULL," +
                "identifier text NULL," + // Wenn identifier noch nicht gesetzt wurde, so darf er null sein
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
    }


    public void insertConnection(IConnection connection, SQLiteDatabase db) {
        SQLiteStatement cmdStoreCon = db.compileStatement("INSERT INTO " + TABLE_CONNECTIONS + " VALUES (null, ?, ?, ?)");
        String conType = "Ethernet-Verbindung";
        if(connection instanceof BTConnection)
            conType = "Bluetooth-Verbindung";
        cmdStoreCon.bindString(1, conType);
        cmdStoreCon.bindString(2, connection.getConNameDeclaration());
        cmdStoreCon.bindString(3, connection.getConAddressDeclaration());
        cmdStoreCon.execute();
        Log.d(LOG_TAG, "Verbindung eingetragen: " + conType + ", " + connection.getConNameDeclaration() + ", " + connection.getConAddressDeclaration());
    }

    public void updateConnectionDb(IConnection connection, String oldName) {
        db.execSQL("UPDATE " + TABLE_CONNECTIONS + " SET conName = '" + connection.getConNameDeclaration() + "', address = '" + connection.getConAddressDeclaration() + "' WHERE conName = '" + oldName + "'");
    }


    // Alte Methode, um Daten am Ende in der onDestroy der MainActivity gesammelt in die DB zu speichern
    public void updateProjects(ArrayList<Project> allProjects, SQLiteDatabase db, Context context) {
//        db.delete("projects", "", null);
//        db.delete("elements", "", null);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELEMENTS);
        onCreate(db);

//        allProjects.clear();

        for (Project p : allProjects) {
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
            while (iteratorMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iteratorMap.next();
                Integer key = (Integer) entry.getKey();
                Element element = (Element) entry.getValue();
                String elementKind = "";
                String elementType = element.getClass().toString();
                long status = 0;

                if (element instanceof BoolElement) {
                    elementKind = "Bool";
                    if (((BoolElement) element).isStatusHigh())
                        status = 1;
                    else
                        status = 0;
                    Log.d(LOG_TAG, "ELEMENT STATUS " + status);
                } else if (element instanceof PwmElement) {
                    elementKind = "Pwm";
                    status = ((PwmElement) element).getCurrentPwm();
                } else if (element instanceof EmptyElement) {
                    elementKind = "NULL";
                }

                SQLiteStatement cmdInsertElements = db.compileStatement("INSERT INTO " + TABLE_ELEMENTS + " VALUES ( null, ?, ?, ?, ?, ?, ?, ? )");
                cmdInsertElements.bindString(1, elementKind); // Bool- oder Pwm-Element
                cmdInsertElements.bindString(2, elementType); // z.B. Switch, Led,...
                cmdInsertElements.bindLong(3, key);
                cmdInsertElements.bindLong(4, status);
                if (element.getIdentifier() != null)
                    cmdInsertElements.bindString(5, element.getIdentifier());
                else
                    cmdInsertElements.bindNull(5);
                cmdInsertElements.bindLong(6, element.getResource());
                Log.d(LOG_TAG, "Ressource: " + element.getResource());
                cmdInsertElements.bindLong(7, p.getId());
                cmdInsertElements.execute();
                Log.d(LOG_TAG, "Element eingetragen: " + elementType + " Position: " + key + " Projekt: " + p.getId());
            }
        }
//        ArrayList<Project> allProsFromDb = selectAllPros(db, context);
    }


    public void deleteConnectionDb(String conName) {
        db.delete(TABLE_CONNECTIONS, "conName = ?", new String[] {(conName)});
    }

    public ArrayList<IConnection> selectAllCons(SQLiteDatabase db, Context context) {
        ArrayList<IConnection> allConsFromDb = new ArrayList<IConnection>();
        Cursor c = db.query(TABLE_CONNECTIONS, new String[]{"_id", "type", "conName", "address"}, null, null, null, null, null);

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String type = c.getString(1);
            String name = c.getString(2);
            String address = c.getString(3);

            if (type.equals(context.getString(R.string.description_btCon)))
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
        Cursor cProjects = db.query(TABLE_PROJECTS, new String[]{"_id", "projName", "internal_id", "creationDate", "lastModifiedDate", "lastOpenedDate"}, null, null, null, null, null);
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
            Cursor cElements = db.rawQuery(queryAllElements, new String[]{Integer.toString(pInternalId)});
            while ((cElements.moveToNext())) {
                int eId = cElements.getInt(0);
                String eKind = cElements.getString(1);
                String eType = cElements.getString(2);
                int position = cElements.getInt(3); // Key in HashMap
                int status = cElements.getInt(4);
                String statusStr = cElements.getString(4);
                String identifier = cElements.getString(5);
                int resource = cElements.getInt(6);
                int project_fk = cElements.getInt(7);

                Log.d(LOG_TAG, pName + " ID: " + eId + " Art: " + eKind + " Typ: " + eType + " Position: " + position +
                        " Status: " + statusStr + "Identifier: " + identifier + " Ressource: " + resource + " ProjectFk: " + project_fk);

                // Erzeugen eines neuen Elements mit genau diesen Daten, um die HashMap zu füllen
                Element e;

                if (eType.equals(context.getString(R.string.classSwitchModel)))
                    e = new SwitchModel();
                else if (eType.equals(context.getString(R.string.classLedModel)))
                    e = new LedModel();
                else if (eType.equals(context.getString(R.string.classPushButtonModel)))
                    e = new PushButtonModel();
                else if (eType.equals(context.getString(R.string.classPwmInputModel)))
                    e = new PwmInputModel();
                else if (eType.equals(context.getString(R.string.classPwmModel)))
                    e = new PwmModel();
                else
                    e = new EmptyElement();
                e.setIdentifier(identifier);
                e.setResource(resource);

                if (eKind.equals("Bool")) {
                    boolean boolStatus = false;
                    if (status == 1)
                        boolStatus = true;
                    ((BoolElement) e).setStatusHigh(boolStatus);
                } else if (eKind.equals("Pwm")) {
                    ((PwmElement) e).setCurrentPwm(status);
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


    @Override
    public void update(Observable senderClass, Object msg) {

        if(msg instanceof ComObjectStd) {
            ComObjectStd comObj = (ComObjectStd) msg;
            Element modelInput = comObj.getModelInput();
            Element modelOutput = comObj.getModelOutput();
            int inputElementPosition = comObj.getInputElementPosition();
            int outputElementPosition = comObj.getOutputElementPosition();
            int projectId = comObj.getProjectId();
            int actionNr = comObj.getActionNr();


            if (actionNr != ACTION_NOTHING)
                Log.d(LOG_TAG, "Updaten der DB über Observer");

            switch (actionNr) {

                case ACTION_INSERT_ELEMENT:
                    insertElementDb(modelOutput, outputElementPosition, projectId);
                    break;

                case ACTION_UPDATE_ELEMENT_TYPE:
                    replaceElementDb(modelOutput, outputElementPosition, projectId);
                    break;

                case ACTION_UPDATE_ELEMENT:
                    updateElementDb(modelInput, modelOutput, inputElementPosition, outputElementPosition);
                    break;

                case ACTION_UPDATE_IDENTIFIER:
                    updateElementIdentifier(modelInput, modelOutput, inputElementPosition, outputElementPosition);
                    break;

                case ACTION_REMOVE_ELEMENT:
                    deleteElementDb(outputElementPosition, projectId);
                    break;

                case ACTION_NOTHING:
                    break;
            }
        }
    }

    private void replaceElementDb(Element modelToUpdate, int outputElementPosition, int projectId) {
        deleteElementDb(outputElementPosition, projectId);
        insertElementDb(modelToUpdate, outputElementPosition, projectId);
    }

    private void deleteElementDb(int outputElementPosition, int projectId) {
        db.delete(TABLE_ELEMENTS, "project_fk = ? AND position = ?", new String[] {(Integer.toString(projectId)), Integer.toString(outputElementPosition)});
    }

    private void insertElementDb(Element modelToUpdate, int outputElementPosition, int projectId) {
        String elementKind = "";
        int status = 0;
        if (modelToUpdate instanceof BoolElement) {
            elementKind = "Bool";
            if (((BoolElement) modelToUpdate).isStatusHigh())
                status = 1;
            else
                status = 0;
            Log.d(LOG_TAG, "ELEMENT STATUS " + status);
        } else if (modelToUpdate instanceof PwmElement) {
            elementKind = "Pwm";
            status = ((PwmElement) modelToUpdate).getCurrentPwm();
        } else if (modelToUpdate instanceof EmptyElement) {
            elementKind = "NULL";
        }

        SQLiteStatement cmdInsertElements = db.compileStatement("INSERT INTO " + TABLE_ELEMENTS + " VALUES ( null, ?, ?, ?, ?, ?, ?, ? )");
        cmdInsertElements.bindString(1, elementKind); // Bool- oder Pwm-Element
        cmdInsertElements.bindString(2, modelToUpdate.getClass().toString()); // z.B. Switch, Led,...
        cmdInsertElements.bindLong(3, outputElementPosition);
        cmdInsertElements.bindLong(4, status);
        if (modelToUpdate.getIdentifier() != null)
            cmdInsertElements.bindString(5, modelToUpdate.getIdentifier());
        else
            cmdInsertElements.bindNull(5);
        cmdInsertElements.bindLong(6, modelToUpdate.getResource());
//                Log.d(LOG_TAG, "Ressource: " + modelToUpdate.getResource());
        cmdInsertElements.bindLong(7, projectId);
        cmdInsertElements.execute();
        Log.d(LOG_TAG, "Element eingetragen: " + modelToUpdate.getClass().toString() + " Position: " + outputElementPosition + " Projekt: " + projectId);
    }


    private void updateElementDb(Element modelInput, Element modelToUpdate, int inputElementPosition, int outputElementPosition) {
        // TODO update über Methode update (schöner)
        if (modelToUpdate instanceof BoolElement) {
            int statusInt;
            if (((BoolElement) modelToUpdate).isStatusHigh())
                statusInt = 1;
            else
                statusInt = 0;
//            values.put("projects join elements using ", ((BoolElement)modelToUpdate).isStatusHigh());

//            SQLiteStatement updateOutputEl = db.compileStatement("UPDATE ? SET status = ?, resource = ? WHERE position = ?");
//            updateOutputEl.bindString(1, TABLE_ELEMENTS);
//            updateOutputEl.bindLong(2, statusInt);
//            updateOutputEl.bindLong(3, modelToUpdate.getResource());
//            updateOutputEl.bindLong(4, outputElementPosition);
//            updateOutputEl.execute();
            db.execSQL("UPDATE " + TABLE_ELEMENTS + " SET status = " + statusInt + ", identifier = '" + modelToUpdate.getIdentifier() + "', resource = " + modelToUpdate.getResource() + " WHERE position = " + outputElementPosition);
            db.execSQL("UPDATE " + TABLE_ELEMENTS + " SET status = " + statusInt + ", identifier = '" + modelToUpdate.getIdentifier() + "', resource = " + modelInput.getResource() + " WHERE position = " + inputElementPosition);
//                  db.update(TABLE_ELEMENTS, values, "position = ?", new String[] {outputElementPosition + ""});
            Log.d(LOG_TAG, "DB aktualisiert");
        } else if (modelToUpdate instanceof PwmElement) {
            db.execSQL("UPDATE " + TABLE_ELEMENTS + " SET status = " + ((PwmElement) modelToUpdate).getCurrentPwm() + ", identifier = '" + modelToUpdate.getIdentifier() + "', resource = " + modelToUpdate.getResource() + " WHERE position = " + outputElementPosition);
            db.execSQL("UPDATE " + TABLE_ELEMENTS + " SET status = " + ((PwmElement) modelInput).getCurrentPwm() + ", identifier = '" + modelInput.getIdentifier() + "', resource = " + modelInput.getResource() + " WHERE position = " + inputElementPosition);
        }
    }

    private void updateElementIdentifier(Element modelInput, Element modelToUpdate, int inputElementPosition, int outputElementPosition) {
        // TODO update über Methode update (schöner)
        if (modelToUpdate instanceof BoolElement) {
            int statusInt;
            if (((BoolElement) modelToUpdate).isStatusHigh())
                statusInt = 1;
            else
                statusInt = 0;
//            values.put("projects join elements using ", ((BoolElement)modelToUpdate).isStatusHigh());

//            SQLiteStatement updateOutputEl = db.compileStatement("UPDATE ? SET status = ?, resource = ? WHERE position = ?");
//            updateOutputEl.bindString(1, TABLE_ELEMENTS);
//            updateOutputEl.bindLong(2, statusInt);
//            updateOutputEl.bindLong(3, modelToUpdate.getResource());
//            updateOutputEl.bindLong(4, outputElementPosition);
//            updateOutputEl.execute();
            Log.d(LOG_TAG, modelToUpdate.getIdentifier());
            db.execSQL("UPDATE " + TABLE_ELEMENTS + " SET status = " + statusInt + ", identifier = '" + modelToUpdate.getIdentifier() + "', resource = " + modelToUpdate.getResource() + " WHERE position = " + outputElementPosition);

//                  db.update(TABLE_ELEMENTS, values, "position = ?", new String[] {outputElementPosition + ""});
            Log.d(LOG_TAG, "DB aktualisiert");
        } else if (modelToUpdate instanceof PwmElement) {
            db.execSQL("UPDATE " + TABLE_ELEMENTS + " SET status = " + ((PwmElement) modelToUpdate).getCurrentPwm() + ", identifier = '" + modelToUpdate.getIdentifier() + "', resource = " + modelToUpdate.getResource() + " WHERE position = " + outputElementPosition);
            Log.d(LOG_TAG, "DB aktualisiert");
        }
    }

    /*
        COLUMN_ID + " integer primary key AUTOINCREMENT," +
        "projName text," +
        "internal_id integer," +
        "creationDate text NOT NULL," +
        "lastModifiedDate text NOT NULL," +
        "lastOpenedDate text NOT NULL" +
    */
    public void addProjectToDb(Project project) {
        SQLiteStatement cmdInsertPro = db.compileStatement("INSERT INTO " + TABLE_PROJECTS + " VALUES (null, ?, ?, ?, ?, ?)" );
        cmdInsertPro.bindString(1, project.getName());
        cmdInsertPro.bindLong(2, project.getId());
        cmdInsertPro.bindString(3, project.getDateString(project.getCreationDate()));
        cmdInsertPro.bindString(4, project.getDateString(project.getLastModifiedDate()));
        cmdInsertPro.bindString(5, project.getDateString(project.getLastOpenedDate()));
        cmdInsertPro.execute();
    }


    public void deleteProjectDb(String proName) {
        db.delete(TABLE_PROJECTS, "projName = ?", new String[] {proName});
    }

    public void updateProjectDb(String newProName, String oldProName) {
        // TODO mit Prepared statement oder mit vorgefertigter Methode update (Da Benutzer hier selbst den Namen eingibt ist es mit der derzeitigen Lösung eine Sicherheitslücke)
        db.execSQL("UPDATE " + TABLE_PROJECTS + " SET projName = '" + newProName + "' WHERE projName = '" + oldProName + "'");
    }
}