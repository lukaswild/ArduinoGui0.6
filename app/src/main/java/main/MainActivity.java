package main;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import connection.BTConnection;
import connection.EthernetConnection;
import connection.IConnection;
import database.DatabaseHandler;
import elements.EmptyElement;
import generic.ImageAdapter;
import observer.Gui;
import observer.Project;


public class MainActivity extends Activity {

    //Felder
    private static ArrayList<Project> allProjects = new ArrayList<Project>();
    private static Project currentProject;

    private static ArrayList<IConnection> allConnections = new ArrayList<IConnection>();
    private static IConnection currentConnection;
    private static HashMap<Integer, Integer> ProjectConnection = new HashMap<Integer, Integer>();
    public static HashMap<Integer, String> ElementIdentifyer = new HashMap<Integer, String>();

    public static ImageAdapter imgadapt;
    public static Gui gui;

    private MainFragment dataFragment;
    private final int REQUEST_CODE_NEW_CON = 100;
    private final int REQUEST_CODE_NEW_PRO = 120;
    private final String ELEMENT_NAME = "element";
    private static int elementCount = 0;
    private final static String LOG_TAG = "MainActivity";
    private static DatabaseHandler dbHandler; // TODO passt static ?
//    private SQLiteDatabase db;
    private boolean editmode = false;


    //Getter Setter

    public static ArrayList<IConnection> getAllConnections() {
        return allConnections;
    }

    public static ArrayList<Project> getAllProjects() {
        return allProjects;
    }

    public static void setAllConnections(ArrayList<IConnection> allConnections) {
        MainActivity.allConnections = allConnections;
    }

    public static Project getCurrentProject() {
        return currentProject;
    }

    public static void setCurrentProject(Project currentProject) {
        MainActivity.currentProject = currentProject;
        currentProject.setLastOpenedDate(Calendar.getInstance()); // Aktuelles Datum setzen
        currentProject.addToObservers(dbHandler); // TODO richtig hier ?
    }

    public static IConnection getCurrentConnection() {
        return currentConnection;
    }

    public static void setCurrentConnection(IConnection currentConnection) {
        MainActivity.currentConnection = currentConnection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //wdb.execSQL("INSERT INTO connections VALUES (null,'HC-05','89', 13, 'off', 'P6', 1)");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();

        }

        imgadapt = new ImageAdapter(this);
        setCurrentProject(new Project(new Gui(this,2,(GridView)findViewById(R.id.gridview)),"projekt X",  imgadapt));

        // Auslesen aus der Datenbank
        GridView gridView = (GridView) findViewById(R.id.gridview);
        try {
            dbHandler = new DatabaseHandler(this);
//            db = dbHandler.getWritableDatabase();
            dbHandler.setDb(dbHandler.getWritableDatabase());
            allConnections = dbHandler.selectAllCons(dbHandler.getDb(), this); // funktioniert
            allProjects = dbHandler.selectAllPros(dbHandler.getDb(), this, gridView);
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Datenbank bzw. Tabellen nicht gefunden");
        } catch(Exception ex) {
            Log.e(LOG_TAG, "Fehler ");
            ex.printStackTrace();
        }
        Log.d(LOG_TAG, "Größe von allProjects: " + allProjects.size());

        //soll angezeigt werden wenn es noch kein einziges projekt gibt
        if(allProjects.isEmpty()) {
            final Dialog popDialog = new Dialog(this);
            popDialog.setCancelable(false);

            popDialog.setContentView(R.layout.dialog_project);
            popDialog.setTitle("Projekt festlegen");
            popDialog.show();

            Button btn = (Button) popDialog.findViewById(R.id.DialogProBtnSubmit);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String string = "";
                    EditText edit = (EditText) popDialog.findViewById(R.id.proNamePopup);

                    currentProject.setName(edit.getText().toString());
                    allProjects.add(currentProject);
                    SetCurrentProjByName(edit.getText().toString());
                    popDialog.dismiss();
                }

            });

        }

        else {
            // Current Projekt setzen, welches zuletzt geöffnet war
            setProjectLastOpened();
        }

        currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection, editmode);
        showName();

    }

    private void setProjectLastOpened() {
        ArrayList<Calendar> allLastOpenedDates = new ArrayList<Calendar>();
        for(Project p : allProjects) {
            allLastOpenedDates.add(p.getLastOpenedDate());
        }
        Calendar maxDate = Collections.max(allLastOpenedDates);

        for(Project p : allProjects) {
            Log.d(LOG_TAG, p.getName() + " " + p.getDateString(p.getLastOpenedDate()));
            if(p.getLastOpenedDate().equals(maxDate)) {
                setCurrentProject(p);
                loadImgRes();
                Log.d(LOG_TAG, "Current Project gesetzt: " + p.getName() + ", zuletzt geöffnet: " + p.getDateString(maxDate));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        showName();
//        setProjectLastOpened();
        currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection, editmode);
        loadImgRes();
//        Toast.makeText(getBaseContext(), "In der Resume !", Toast.LENGTH_SHORT).show();
    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);

        initializeUI();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(BTConnection.isConnected())
            BTConnection.closeConnection();
        currentConnection = null;

        // Abspeichern der Connections in der DB
//        storeDataInDb();

//       allProjects.clear();
 //      db.execSQL("DROP TABLE IF EXISTS connections");
  //     db.execSQL("DROP TABLE IF EXISTS projects");
  //      db.execSQL("DROP TABLE IF EXISTS elements");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Toast.makeText(this, "in onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(this, "in onStop", Toast.LENGTH_SHORT).show();
//        storeDataInDb();
    }

    private void storeDataInDb() {
        ArrayList<String> allConsName = new ArrayList<String>();
        ArrayList<String> allConsType = new ArrayList<String>();
        ArrayList<String> allConsAddress = new ArrayList<String>();
        splitConsIntoLists(allConsType, allConsName, allConsAddress);
        dbHandler.updateConnections(allConsName, allConsType, allConsAddress, dbHandler.getDb());

        // Eintragen der Projekte in die DB
        dbHandler.updateProjects(allProjects, dbHandler.getDb(), this);
    }


    public void showName(){
        TextView view = (TextView)findViewById(R.id.textView3);
        TextView view2 = (TextView)findViewById(R.id.textView2);

        //Wird nur gesetzt wenn der Name des Projekts nicht leer ist,
        //standardmäßig wird Project angezeigt
        if (!currentProject.getName().equals(null)){
            view.setText(currentProject.getName());
        }

        if (!(allConnections.size()==0)){
            try {
                view2.setText(currentConnection.getConName());
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "Aktuell keine Verbindung ausgewählt");
            }

       }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ShowDialog();

            return  false;
        }
        if (id == R.id.connection) {
            startActivityConnection();
            return true;
        }
        if (id == R.id.newproject) {
            startActivityProject();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void EditMode(View view) {
        if (editmode ==true){
            editmode =false;
            //Beim Ändern des editmode muss die initialize ui neu aufgerufen werden, da sonst die initilize ui mit dem "alte" edit mode arbietet,
            //mitdem sie vorher aufgerufen wurde, das produziert fehler
            currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection, editmode);

        }
        else{
            editmode =true;
            currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection, editmode);
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }

    private void startActivityConnection() {
        Log.d(LOG_TAG, "ConnectionActivity wird gestartet...");
        Intent newConIntent = new Intent(this, ConnectionActivity.class);

        // listView
//        newConIntent.putExtra("listAvailableCons", currentProject.getListAllCons()); // ArrayList<String> mitgeben

        ArrayList<String> allConsType = new ArrayList<String>();
        ArrayList<String> allConsHeader = new ArrayList<String>(); // Name der Verbindungen - wird als aufklappbares Feld angezeigt
        ArrayList<String> allConsAddress = new ArrayList<String>();

        splitConsIntoLists(allConsType, allConsHeader, allConsAddress);

        newConIntent.putExtra("allConsType", allConsType);
        newConIntent.putExtra("allConsHeader", allConsHeader);
        newConIntent.putExtra("allConsAddress", allConsAddress);

        try {
            Log.d(LOG_TAG, "Current connection name: " + currentConnection.getConName());
            for(int i = 0; i < allConsHeader.size(); i++) {
                if(currentConnection.getConName().equals(allConsHeader.get(i))) {
                    newConIntent.putExtra("currentConPosition", i);
                }
            }

        } catch  (Exception e) {
            Log.d(LOG_TAG, "Es gibt aktuell keine aufgebaute Verbindung");
        }

        startActivityForResult(newConIntent, REQUEST_CODE_NEW_CON);
    }

    private void splitConsIntoLists(ArrayList<String> allConsType, ArrayList<String> allConsHeader, ArrayList<String> allConsAddress) {
        for (IConnection c : allConnections) {
            if (c instanceof BTConnection) {
                allConsType.add(getString(R.string.description_btCon));
                allConsHeader.add(((BTConnection) c).getConNameDeclaration());
                allConsAddress.add(((BTConnection) c).getConAddressDeclaration());
            }
            else if (c instanceof EthernetConnection) {
                allConsType.add(getString(R.string.description_ethernetCon));
                allConsHeader.add(((EthernetConnection) c).getConNameDeclaration());
                allConsAddress.add(((EthernetConnection)c).getConAddressDeclaration());
            }
        }
    }

    private void startActivityProject(){
        Intent newProIntent = new Intent(this, ProjectActivity.class);
        ArrayList<String> allProName = new ArrayList<String>();
        ArrayList<String> allProCreationDates = new ArrayList<String>();
        ArrayList<String> allProLastModifiedDates = new ArrayList<String>();

        for (Project c : allProjects) {
            allProName.add(c.getName());
            allProCreationDates.add(c.getDateStringFormatted(c.getCreationDate()));
            allProLastModifiedDates.add(c.getDateStringFormatted(c.getLastModifiedDate()));
        }

        newProIntent.putExtra("allProName", allProName);
        newProIntent.putExtra("allProCreationDates", allProCreationDates);
        newProIntent.putExtra("allProLastModifiedDates", allProLastModifiedDates);

        startActivityForResult(newProIntent, REQUEST_CODE_NEW_PRO);
    }


    public static boolean removeConnection(String conName) {
        for (IConnection c: allConnections) {
            if(c.getConNameDeclaration().equals(conName)) {
                if(allConnections.remove(c)) {
                    Log.d(LOG_TAG, "Connection " + conName + " von Liste entfernt");
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeProject(String proName) {
        for(Project p : allProjects) {
            if(p.getName().equals(proName)) {
                if(allProjects.remove(p)) {
                    Log.d(LOG_TAG, "Projekt " + proName + " von Liste entfernt");
                    return true;
                }
            }
        }
        return false;
    }


    public void ShowDialog(){

        final Dialog popDialog = new Dialog(this);
        popDialog.setCancelable(true);

        final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final  View Viewlayout = inflater.inflate(R.layout.seekbar,(ViewGroup)findViewById(R.id.dialog_seekbar));

        popDialog.setContentView(Viewlayout);
        popDialog.setTitle("Einstellungen");
        popDialog.show();

        final SeekBar seek1 = (SeekBar)Viewlayout.findViewById(R.id.seekBar1);
        seek1.setProgress(imgadapt.getLength()-50);
        seek1.setDrawingCacheBackgroundColor(Color.DKGRAY);

        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                             @Override
                                             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                 //Die Seekbar ghet von 0-150; da es keine Sin macht die Länge auf 0 zu setzten wird die Skala verschoben
                                                 //indem die Eingabe mit 50 addiert wird -> somit geht die Seekbar von 100-200
                                                 imgadapt.setLength(progress+50);

                                             }

                                             @Override
                                             public void onStartTrackingTouch(SeekBar seekBar) {

                                             }

                                             @Override
                                             public void onStopTrackingTouch(SeekBar seekBar) {

                                             }
                                         }


        );
        final SeekBar seek2 = (SeekBar)Viewlayout.findViewById(R.id.seekBar2);


        seek2.setProgress(currentProject.getGui().getGridView().getNumColumns());
        seek2.setDrawingCacheBackgroundColor(Color.DKGRAY);

        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                             @Override
                                             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                 currentProject.getGui().getGridView().setNumColumns(progress);

                                             }

                                             @Override
                                             public void onStartTrackingTouch(SeekBar seekBar) {

                                             }

                                             @Override
                                             public void onStopTrackingTouch(SeekBar seekBar) {

                                             }
                                         }
        );


        final Button button = (Button)Viewlayout.findViewById(R.id.buttonOK);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgadapt.notifyDataSetChanged();
                currentProject.getGui().getGridView().clearAnimation();
                popDialog.cancel();
                currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection, editmode);

            }
        });

    }

    /**
     * Diese Methode wird immer aufgerufen, wenn eine von dieser Activity aufgerufene Activity ein Result zurückliefert.
     * Beim Aufrufen einer Activity, die ein Result liefern soll, wird ein request code angegeben. Hier muss abgefragt werden,
     * ob dieser Code die Methode ausgelöst hat (bei mehreren Activities wird auch immer diese eine Methode aufgerufen).
     * REQUEST_CODE_NEW_CON: Code der ConnectionActivity
     * REQUEST_CODE_NEW_PRO: Code der ProjectActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(resultCode != 0) {
            super.onActivityResult(requestCode, resultCode, data);

            String name = "";
            String address = "";
            if (requestCode == REQUEST_CODE_NEW_CON) {
                if (data.getExtras().containsKey("name"))
                    name = data.getExtras().getString("name");
                if (data.getExtras().containsKey("address"))
                    address = data.getExtras().getString("address");
                if (data.getExtras().containsKey("conType")) {
                    int conTypeInt = data.getExtras().getInt("conType");
                    if (conTypeInt == 1) { // Bluetooth
                        // Verbindung von currentProject holen
                        //  BTConnection btConnection = BTConnection.initialiseConnection(name, address); // TODO wenn Verbindung am Arduino unterbrochen --> wird nicht mehr aufgebaut, da bei App bereits instantiiert
                        // currentProject.setCurrentConnection(btConnection);

                    }
                    if (conTypeInt == 2) { // Ethernet
//                        EthernetConnection ethernetConnection = EthernetConnection.initialiseConnection(name, address);
//                        currentProject.setCurrentConnection(ethernetConnection);
                    }
                    Toast.makeText(getApplicationContext(), currentConnection.getConName() + "\n" +
                            currentConnection.getAddress(), Toast.LENGTH_LONG).show();
                }
            }

            else if (requestCode == REQUEST_CODE_NEW_PRO) {
                String proName = "";
                if (data.getExtras().containsKey("name")) {
                    proName = data.getExtras().getString("name");
                    Project newProj = new Project(new Gui(getBaseContext(),2,(GridView)(findViewById(R.id.gridview))), proName, imgadapt); // TODO hier darf nicht der mit Elementen versehene imgadapt übergeben werden
                    allProjects.add(newProj);
                    setCurrentProject(newProj);
                    currentProject.setName(proName);
                }
            }
        }
    }

    public static void loadImgRes(){
        //Zuerst muss die hashmap aus dem projekt(int, element) zu einer hashmap im imgadapt(int, int) gecastet werden

        for (int i=0;i<imgadapt.getCount();i++){
            imgadapt.update(R.drawable.add1,i);
        }

        for (int i=0;i<currentProject.getMapAllViewModels().size();i++){
            //Plus hinzuzufügen wäre ja unnötgi, da die hashmap im imagedapter beim Erzeugen sowieso mit plus befüllt wird
            //es soll nur ausgetauscht werden, was kein Plus ist.

            if (!(currentProject.getElementFromMap(i) instanceof EmptyElement)){
                imgadapt.update(currentProject.getRessourceFromMap(i),i);
                Log.d(LOG_TAG, "Ressource: " + currentProject.getRessourceFromMap(i));
                Log.d(LOG_TAG, "Led: " + R.drawable.lamp_off + " " + R.drawable.lamp_on);
                Log.d(LOG_TAG, "Led: " + R.drawable.switch_off + " " + R.drawable.switch_on);

            }
        }
        imgadapt.notifyDataSetChanged();
    }


    public static void SetCurrentProjByName(String name){
        for (Project p: allProjects) {
            if (p.getName().equals(name)) {
                setCurrentProject(p);

            } else {
                //es wird kein Projekt gefunden
            }
        }
    }
}