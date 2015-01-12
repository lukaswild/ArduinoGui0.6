package main;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import connection.BTConnection;
import connection.IConnection;
import elements.Element;
import elements.LedModel;
import elements.SwitchModel;
import generic.ImageAdapter;
import observer.Gui;
import observer.Project;


public class MainActivity extends Activity {

    //Felder
    private static ArrayList<Project> AllProjects = new ArrayList<Project>();
    private static Project CurrentProject;

    private static ArrayList<IConnection> AllConnections = new ArrayList<IConnection>();
    private static IConnection CurrentConnection;

    private static HashMap<Integer, Integer> ProjectConnection = new HashMap<Integer, Integer>();
    public static HashMap<Integer, String> ElementIdentifyer = new HashMap<Integer, String>();

    public static ImageAdapter imgadapt;
    public static Gui gui;

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
        return CurrentProject;
    }

    public static void setCurrentProject(Project currentProject) {
        CurrentProject = currentProject;
    }

    public static IConnection getCurrentConnection() {
        return CurrentConnection;
    }

    public static void setCurrentConnection(IConnection currentConnection) {
        CurrentConnection = currentConnection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();

        }


        CurrentProject=new Project(new Gui(this,2,(GridView)findViewById(R.id.gridview)),"projekt X");
/*
//        FragmentManager fm= getFragmentManager();
//        dataFragment=(MainFragment)fm.findFragmentByTag("data");
//
//        if (  dataFragment==null){
//            dataFragment = new MainFragment();
//            fm.beginTransaction().add(dataFragment,"data").commit();
//
//            dataFragment.setData(CurrentProject);
//        }
             */

        Project pro1 = new Project(new Gui(getBaseContext(),2,(GridView)(findViewById(R.id.gridview))),"Projekt1",1);
        Project pro2 = new Project(new Gui(getBaseContext(),2,(GridView)(findViewById(R.id.gridview))),"Projekt2",2);
        Project pro3 = new Project(new Gui(getBaseContext(),2,(GridView)(findViewById(R.id.gridview))),"Projekt3",3);
        AllProjects.add(pro1);
        AllProjects.add(pro2);
        AllProjects.add(pro3);

        //soll angezeigt werden wenn es noch kein einziges projekt gibt
        if(AllProjects.isEmpty()) {
            final Dialog popDialog = new Dialog(this);
            popDialog.setCancelable(true);

            // View Viewlayout = inflater.inflate(R.layout.dialog_project,(ViewGroup)findViewById(R.id.dialog_projekt));

            popDialog.setContentView(R.layout.dialog_project);
            popDialog.setTitle("Projekt festlegen");
            popDialog.show();



            Button btn = (Button) popDialog.findViewById(R.id.DialogProBtnSubmit);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String string = "";
                    EditText edit = (EditText) popDialog.findViewById(R.id.proNamePopup);
                    //CurrentProject = new Project(new Gui(getBaseContext(),1,(GridView)findViewById(R.id.gridview)),edit.getText().toString());
                    CurrentProject.setName(edit.getText().toString());
                    AllProjects.add(CurrentProject);
                    popDialog.cancel();

                }

            });

        }



        //Alle Projekte haben eine ID. Die ID wird beim Erzeugen eines neuen Projekts gesetzt (im Konstruktor
        //von Projekt), das heißt das letute erzeuget Projekt hat die höchste ID. Diese projekt wird gesucht und
        //auf CurrentProjekt gesetzt

        else{

            CurrentProject=AllProjects.get(AllProjects.size()-1);
        }

        imgadapt = new ImageAdapter(this);

/*
        Switch sw = (Switch)findViewById(R.id.switch1);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext()," switch 1", Toast.LENGTH_SHORT).show();
            }
        });
*/
        // CurrentProject=new Project(new Gui(this,2,(GridView)findViewById(R.id.gridview)),"projekt 1");
       // InitializeUI(CurrentProject); nur mehr in der onResume, da
        Toast.makeText(getBaseContext(), "In der onCreate !", Toast.LENGTH_SHORT).show();
        ShowName();

        createDummyData();


    }

    @Override
    protected void onResume() {
        super.onResume();

        ShowName();
        InitializeUI(CurrentProject);
        Toast.makeText(getBaseContext(), "In der Resume !", Toast.LENGTH_SHORT).show();

    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);

        InitializeUI();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Wenn die activity zerstört wird, soll das CurrentProject im Fragment gespeichert werden.

        Toast.makeText(getBaseContext()," In der OnDestroy", Toast.LENGTH_SHORT).show();
        // dataFragment.setData(CurrentProject);
    }

    public void InitializeUI(final Project project)  {

        //Löscht zuerst einmal den Inhalt von Grdiview
        //  project.getGui().getGridView().clearAnimation();
        project.getGui().getGridView().setAdapter(imgadapt);
        project.getGui().getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                switch (imgadapt.getItemInt(position)) {
                    case R.drawable.switch_off:
                        imgadapt.Update(R.drawable.switch_on,position);
                        imgadapt.notifyDataSetInvalidated();
                        project.sendDataUpdateGui(v,CurrentConnection);
                        break;

                    case R.drawable.switch_on:
                        imgadapt.Update(R.drawable.switch_off,position);
                        imgadapt.notifyDataSetInvalidated();
                        project.sendDataUpdateGui(v,CurrentConnection);
                        break;

                    //Button funktioniert zurzeit gleich wie ein switch, -> schlecht, besser alle click im onTouchListener realisieren,
                    //es kann auf drücken, bzw. loslassen der views geschaut werden, funktioniert derzeit noch nicht
                    //github comment
                    case R.drawable.button_off:
                        imgadapt.Update(R.drawable.button_on,position);
                        imgadapt.notifyDataSetInvalidated();
                        project.sendDataUpdateGui(v,CurrentConnection);
                        break;

                    case R.drawable.button_on:
                        imgadapt.Update(R.drawable.button_off,position);
                        imgadapt.notifyDataSetInvalidated();

                        //Update an GUI funktioniert noch nicht
                        project.sendDataUpdateGui(v,CurrentConnection);
                        break;

                    case R.drawable.lamp_off:
                        Toast.makeText(getBaseContext(), "Element " + position + " ist kein Eingabelement!", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        // Toast.makeText(getBaseContext(), "Fehler bei pos/" + position, Toast.LENGTH_SHORT).show();
                        for(Element e : CurrentProject.getAllElements()) {
                            Toast.makeText(getBaseContext(), e.getName(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                }

            }
        });

  /*      gui.getGridView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                MyImageView view = (MyImageView) v;

                if ((event.getAction() == MotionEvent.ACTION_DOWN) && (int) v.getId() == (int) R.drawable.switch_off) {
                    imgadapt.Update(R.drawable.switch_on,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }
                if ((event.getAction() == MotionEvent.ACTION_DOWN) && (int) v.getId() == (int) R.drawable.switch_on) {
                    imgadapt.Update(R.drawable.switch_off,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }
                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
                    imgadapt.Update(R.drawable.switch_on,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }
                if ((event.getAction() == MotionEvent.ACTION_UP) && (int) v.getId() == (int) R.drawable.button_off) {
                    imgadapt.Update(R.drawable.button_off,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }


                Toast.makeText(getBaseContext(),"id:"+view.getName(),Toast.LENGTH_SHORT).show();
                return  false;
            }
        });*/

        // gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        project.getGui().getGridView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View v,final int position, long id) {
                final Context context = getBaseContext();

                if ((int) imgadapt.getItemInt(position) == R.drawable.add1) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.menu_popup);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.AddButton:
                                    imgadapt.Update(R.drawable.button_off, position);
                                    imgadapt.notifyDataSetChanged();
                                    // TODO Model für PushButton der Liste im Project adden
                                    return true;

                                case R.id.AddLED:
                                    //es wird das Bild geändert, und dem imageadapt wir gesagt, dass sich etwas geändert hat -> er soll neu zeichnen
                                    imgadapt.Update(R.drawable.lamp_off, position);
                                    imgadapt.notifyDataSetChanged();
                                    // Hinzufügen eines neuen ModelElements in die Liste aller Models im Project
                                    project.addElement(new LedModel(ELEMENT_NAME + Integer.toString(position), false));
                                    return true;

                                case R.id.AddSwitch:
                                    imgadapt.Update(R.drawable.switch_off, position);
                                    imgadapt.notifyDataSetChanged();
                                    project.addElement(new SwitchModel(ELEMENT_NAME + Integer.toString(position), false));
                                    return true;

                                default:
                                    return false;

                            }
                        }
                    });
                    return false;
                } else {

                    final PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.menu_popup_clickoptions);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem item) {

                            int itemId = item.getItemId();
                            switch (itemId) {

                                case R.id.identifyer:

                                    PopupMenu popupMenu2 = new PopupMenu(getApplicationContext(), v);
                                    popupMenu2.inflate(R.menu.menu_popup_identifyer);
                                    popupMenu2.show();
                                    popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


                                        @Override
                                        public boolean onMenuItemClick(MenuItem item2) {

                                            //auf die Eingabe reagierne, und dementsprechend den Identifyer des Elements setzen

                                            if (SwitchIdentfiyer(item2, position, project)) return true;
                                            return false;



                                        }
                                    });

                                    return true;

                                case R.id.delete:
                                    imgadapt.Update(R.drawable.add1, position);
                                    imgadapt.notifyDataSetChanged();
                                    return true;


                                default:
                                   /* for (Element var : CurrentProject.getAllElements()) {
                                        if (var.getName() == ("element" + position)) {
                                            var.setIdentifier(Integer.toString(item.getItemId()));


                                        Toast.makeText(getBaseContext(),"ID" + var.getIdentifier(),Toast.LENGTH_SHORT);
                                            return  true;
                                     }
                                    }*/
                                    return false;

                            }
                            //return false;
                        }
                    });
                    return false;
                }

            }
        });
    }

    private boolean SwitchIdentfiyer(MenuItem item2, int position, Project project) {

        createHashmap();
        if (ElementIdentifyer.containsKey(item2.getItemId())){
            project.getElementByName("element" + position).setIdentifier(ElementIdentifyer.get(item2.getItemId()));
            return true;
        }
        else{
            return false;
        }

    }

    public void ShowName(){
        TextView view = (TextView)findViewById(R.id.textView3);
        TextView view2 = (TextView)findViewById(R.id.textView2);

        //Wird nur gesetzt wenn der Name des Projekts nicht leer ist,
        //standardmäßig wird Project angezeigt
        if (!CurrentProject.getName().equals(null)){
            view.setText(CurrentProject.getName());
        }

      /*  if (!CurrentConnection.equals(null)){
            view2.setText(CurrentConnection.getConName());
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
//        newConIntent.putExtra("listAvailableCons", CurrentProject.getListAllCons()); // ArrayList<String> mitgeben

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
//        newConIntent.putExtra("listAvailableCons", CurrentProject.getListAllCons()); // ArrayList<String> mitgeben

        ArrayList<String> allProName = new ArrayList<String>();
        ArrayList<String> allProElements = new ArrayList<String>();

        for (Project c : AllProjects) {

            allProName.add(c.getName());
            allProElements.add(Integer.toString(c.getAllElements().size()));

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


        seek2.setProgress(CurrentProject.getGui().getGridView().getNumColumns());
        seek2.setDrawingCacheBackgroundColor(Color.DKGRAY);

        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                             @Override
                                             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                 CurrentProject.getGui().getGridView().setNumColumns(progress);

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
                CurrentProject.getGui().getGridView().clearAnimation();
                popDialog.cancel();
                InitializeUI(CurrentProject);

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
                        // Verbindung von CurrentProject holen
                        //  BTConnection btConnection = BTConnection.initialiseConnection(name, address); // TODO wenn Verbindung am Arduino unterbrochen --> wird nicht mehr aufgebaut, da bei App bereits instantiiert
                        // CurrentProject.setCurrentConnection(btConnection);


                    }
                    if (conTypeInt == 2) { // Ethernet
//                        EthernetConnection ethernetConnection = EthernetConnection.initialiseConnection(name, address);
//                        CurrentProject.setCurrentConnection(ethernetConnection);
                    }
                    Toast.makeText(getApplicationContext(), CurrentConnection.getConName() + "\n" +
                            CurrentConnection.getAddress(), Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == REQUEST_CODE_NEW_PRO) {
                String proName = "";
                if (data.getExtras().containsKey("name")) {
                    proName = data.getExtras().getString("name");
                    CurrentProject.setName(proName);
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

    public void createHashmap(){
        ElementIdentifyer.put(R.id.p1,"P1");
        ElementIdentifyer.put(R.id.p2,"P2");
        ElementIdentifyer.put(R.id.p3,"P3");
        ElementIdentifyer.put(R.id.p4,"P4");
        ElementIdentifyer.put(R.id.p5,"P5");
        ElementIdentifyer.put(R.id.p6,"P6");
        ElementIdentifyer.put(R.id.p7,"P7");
        ElementIdentifyer.put(R.id.p8,"P8");
        ElementIdentifyer.put(R.id.p9,"P9");
        ElementIdentifyer.put(R.id.p10,"P10");
        ElementIdentifyer.put(R.id.p11,"P11");
        ElementIdentifyer.put(R.id.p12,"P12");
        ElementIdentifyer.put(R.id.p13,"P13");
        ElementIdentifyer.put(R.id.p14,"P14");
        ElementIdentifyer.put(R.id.p15,"P15");
        ElementIdentifyer.put(R.id.a1,"A1");
        ElementIdentifyer.put(R.id.a2,"A2");
        ElementIdentifyer.put(R.id.a3,"A3");
        ElementIdentifyer.put(R.id.a4,"A4");
        ElementIdentifyer.put(R.id.a5,"A5");
    }
}
