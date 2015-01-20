package main;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
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
import java.util.HashMap;

import connection.BTConnection;
import connection.IConnection;
import generic.ImageAdapter;
import observer.Gui;
import observer.Project;


public class MainActivity extends Activity {

    //Felder
    private static ArrayList<Project> AllProjects = new ArrayList<Project>();
    private static Project currentProject;

    private static ArrayList<IConnection> AllConnections = new ArrayList<IConnection>();
    private static IConnection currentConnection;
    private static HashMap<Integer, Integer> ProjectConnection = new HashMap<Integer, Integer>();
    public static ImageAdapter imgadapt;

    private MainFragment dataFragment;
    private final int REQUEST_CODE_NEW_CON = 100;
    private final int REQUEST_CODE_NEW_PRO = 120;
    private final String ELEMENT_NAME = "element";
    private static int elementCount = 0;
    private final String LOG_TAG = "MainActivity";

    //Getter Setter

    public static ArrayList<IConnection> getAllConnections() {
        return AllConnections;
    }

    public static void setAllConnections(ArrayList<IConnection> allConnections) {
        AllConnections = allConnections;
    }

    public static Project getCurrentProject() {
        return currentProject;
    }

    public static void setCurrentProject(Project currentProject) {
        MainActivity.currentProject = currentProject;
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

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();

        }

        currentProject =new Project(new Gui(this,2,(GridView)findViewById(R.id.gridview)),"projekt X", 11, imgadapt);

        Project pro1 = new Project(new Gui(getBaseContext(),2,(GridView)(findViewById(R.id.gridview))),"Projekt1",1, imgadapt);
        Project pro2 = new Project(new Gui(getBaseContext(),2,(GridView)(findViewById(R.id.gridview))),"Projekt2",2, imgadapt);
        Project pro3 = new Project(new Gui(getBaseContext(),2,(GridView)(findViewById(R.id.gridview))),"Projekt3",3, imgadapt);

        AllProjects.add(pro1);
        AllProjects.add(pro2);
        AllProjects.add(pro3);

        //soll angezeigt werden wenn es noch kein einziges projekt gibt
        if(AllProjects.isEmpty()) {
            final Dialog popDialog = new Dialog(this);
            popDialog.setCancelable(true);

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
                    AllProjects.add(currentProject);
                    popDialog.cancel();
                }

            });

        }


        //Alle Projekte haben eine ID. Die ID wird beim Erzeugen eines neuen Projekts gesetzt (im Konstruktor
        //von Projekt), das heißt das letute erzeuget Projekt hat die höchste ID. Diese projekt wird gesucht und
        //auf CurrentProjekt gesetzt

        else {
            currentProject = AllProjects.get(AllProjects.size()-1);
        }

        imgadapt = new ImageAdapter(this);

        Log.d(LOG_TAG,"Vor Gui");
        currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection);
        Toast.makeText(getBaseContext(), "In der onCreate !", Toast.LENGTH_SHORT).show();
        ShowName();

        createDummyData();


    }

    @Override
    protected void onResume() {
        super.onResume();

        ShowName();
        currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection);
        Toast.makeText(getBaseContext(), "In der Resume !", Toast.LENGTH_SHORT).show();

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
        // Wenn die activity zerstört wird, soll das currentProject im Fragment gespeichert werden.

        Toast.makeText(getBaseContext()," In der OnDestroy", Toast.LENGTH_SHORT).show();
        // dataFragment.setData(currentProject);
    }


    public void ShowName(){
        TextView view = (TextView)findViewById(R.id.textView3);
        TextView view2 = (TextView)findViewById(R.id.textView2);

        //Wird nur gesetzt wenn der Name des Projekts nicht leer ist,
        //standardmäßig wird Project angezeigt
        if (!currentProject.getName().equals(null)){
            view.setText(currentProject.getName());
        }

      /*  if (!currentConnection.equals(null)){
            view2.setText(currentConnection.getConName());
        }
*/
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

    public void startActivityConnection() {
        Log.d(LOG_TAG, "ConnectionActivity wird gestartet...");
        Intent newConIntent = new Intent(this, ConnectionActivity.class);

        // listView
//        newConIntent.putExtra("listAvailableCons", currentProject.getListAllCons()); // ArrayList<String> mitgeben

        ArrayList<String> allConsType = new ArrayList<String>();
        ArrayList<String> allConsHeader = new ArrayList<String>(); // Name der Verbindungen - wird als aufklappbares Feld angezeigt
        ArrayList<String> allConsAddress = new ArrayList<String>();

        for (IConnection c : AllConnections) {
            if (c instanceof BTConnection) {
                allConsType.add(getString(R.string.description_btCon));
                allConsHeader.add(((BTConnection) c).getConNameDeclaration());
                allConsAddress.add(((BTConnection) c).getConAddressDeclaration());
            } // TODO else if c instanceof Ethernetconnection
        }

//        Toast.makeText(this, "Anzahl Header: " + allConsHeader.size() + " \nAnzahl Children: " + allConsAddress.size(), Toast.LENGTH_LONG).show();

        newConIntent.putExtra("allConsType", allConsType);
        newConIntent.putExtra("allConsHeader", allConsHeader);
        newConIntent.putExtra("allConsAddress", allConsAddress);

        startActivityForResult(newConIntent, REQUEST_CODE_NEW_CON);
    }

    public void startActivityProject(){
        Intent newProIntent = new Intent(this, ProjectActivity.class);

        // listView
//        newConIntent.putExtra("listAvailableCons", currentProject.getListAllCons()); // ArrayList<String> mitgeben

        ArrayList<String> allProName = new ArrayList<String>();
        ArrayList<String> allProElements = new ArrayList<String>();

        for (Project c : AllProjects) {

            allProName.add(c.getName());
//            allProElements.addToObservers(Integer.toString(c.getAllElements().size()));
            allProElements.add(Integer.toString(c.getMapAllViewModels().size()));
        }
//        Toast.makeText(this, "Anzahl Header: " + allConsHeader.size() + " \nAnzahl Children: " + allConsAddress.size(), Toast.LENGTH_LONG).show();

        newProIntent.putExtra("allProName", allProName);
        newProIntent.putExtra("allProElements", allProElements);

        startActivityForResult(newProIntent, REQUEST_CODE_NEW_PRO);
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
                currentProject.getGui().initializeUI(currentProject, imgadapt, currentConnection);

            }
        });

    }

    /**
     * Diese Methode wird immer aufgerufen, wenn eine von dieser Activity aufgerufene Activity ein Result zurückliefert.
     * Beim Aufrufen einer Activity, die ein Result liefern soll, wird ein request code angegeben. Hier muss abgefragt werden,
     * ob dieser Code die Methode ausgelöst hat (bei mehreren Activities wird auch immer diese eine Methode aufgerufen).
     * REQUEST_CODE_NEW_CON: Code der NewConnectionActivity
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

            if (requestCode == REQUEST_CODE_NEW_PRO) {
                String proName = "";
                if (data.getExtras().containsKey("name")) {
                    proName = data.getExtras().getString("name");
                    currentProject.setName(proName);
                }

            }
        }

    }

    public void createDummyData(){
        // Dummy Daten für ExpandableListView
        IConnection c1 = BTConnection.createAttributeCon("BSibo1", "98:D3:31:B1:F6:82");
        IConnection c2 = BTConnection.createAttributeCon("BSibo2", "98:D3:31:B1:F6:82");
        IConnection c3 = BTConnection.createAttributeCon("BSibo3", "98:D3:31:B1:F6:82");
        IConnection c4 = BTConnection.createAttributeCon("BLuggi1", "98:D3:31:B1:F4:7A");
        IConnection c5 = BTConnection.createAttributeCon("BLuggi2", "98:D3:31:B1:F4:7A");

        AllConnections.add(c1);
        AllConnections.add(c2);
        AllConnections.add(c3);
        AllConnections.add(c4);
        AllConnections.add(c5);

    }


    public static void SetCurrentProjByName(String name){
        for (Project p:AllProjects   ) {
            if (p.getName().equals(name)) {
                currentProject = p;

            } else {
                //es wird kein Projekt gefunden
            }
        }
    }
}