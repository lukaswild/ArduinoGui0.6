package main;

import Views.ImageAdapter;
import Views.MyImageView;
import connection.BTConnection;
import connection.EthernetConnection;
import connection.IConnection;
import elements.Element;
import elements.LedModel;
import elements.SwitchModel;
import observer.Gui;
import observer.Project;
import com.example.arduinogui.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    public static Project project;
    public static ImageAdapter imgadapt;
    public static Gui gui;

    private MainFragment dataFragment;
    private final int REQUEST_CODE_NEW_CON = 100;
    private final int REQUEST_CODE_NEW_PRO = 120;
    private final String ELEMENT_NAME = "element";
    private static int elementCount = 0;
    private final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();

        }

        FragmentManager fm= getFragmentManager();
        dataFragment=(MainFragment)fm.findFragmentByTag("data");

        if (  dataFragment==null){
            dataFragment = new MainFragment();
            fm.beginTransaction().add(dataFragment,"data").commit();

            dataFragment.setData(project);
        }


        if (gui==null){
            gui=new Gui(getBaseContext(),1,(GridView)findViewById(R.id.gridview));
        }
        else {
            gui = dataFragment.getData().getGui();
        }


        if (project==null){
            project = new Project(gui,imgadapt);
        }
        else{
            project= dataFragment.getData();
        }
        if (imgadapt==null){
            imgadapt = new ImageAdapter(this);
        }


        InitializeUI();
       // ShowName();



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
        // Wenn die activity zerstört wird, soll das project im Fragment gespeichert werden.

        dataFragment.setData(project);
    }

    public void InitializeUI()  {

        //Löscht zuerst einmal den Inhalt von Grdiview
        project.getGui().getGridView().clearAnimation();
        gui.getGridView().setAdapter(imgadapt);
        gui.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                switch (imgadapt.getItemInt(position)) {
                    case R.drawable.switch_off:
                        imgadapt.Update(R.drawable.switch_on,position);
                        imgadapt.notifyDataSetInvalidated();
                        project.sendDataUpdateGui(v);
                        break;

                    case R.drawable.switch_on:
                        imgadapt.Update(R.drawable.switch_off,position);
                        imgadapt.notifyDataSetInvalidated();
                        project.sendDataUpdateGui(v);
                        break;

                    //Button funktioniert zurzeit gleich wie ein switch, -> schlecht, besser alle click im onTouchListener realisieren,
                    //es kann auf drücken, bzw. loslassen der views geschaut werden, funktioniert derzeit noch nicht
                    //github comment
                    case R.drawable.button_off:
                        imgadapt.Update(R.drawable.button_on,position);
                        imgadapt.notifyDataSetInvalidated();
                        project.sendDataUpdateGui(v);
                        break;

                    case R.drawable.button_on:
                        imgadapt.Update(R.drawable.button_off,position);
                        imgadapt.notifyDataSetInvalidated();

                        //Update an GUI funktioniert noch nicht
                        project.sendDataUpdateGui(v);
                        break;

                    case R.drawable.lamp_off:
                        Toast.makeText(getBaseContext(), "Element " + position + " ist kein Eingabelement!", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        // Toast.makeText(getBaseContext(), "Fehler bei pos/" + position, Toast.LENGTH_SHORT).show();
                        for(Element e : project.getAllElements()) {
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
        gui.getGridView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

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

                                            if (SwitchIdentfiyer(item2, position)) return true;
                                            return false;



                                        }
                                    });

                                    return true;

                                case R.id.delete:
                                    imgadapt.Update(R.drawable.add1, position);
                                    imgadapt.notifyDataSetChanged();
                                    return true;


                                default:
                                   /* for (Element var : project.getAllElements()) {
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

    private boolean SwitchIdentfiyer(MenuItem item2, int position) {
        switch (item2.getItemId()){

            case R.id.p1:
                project.getElementByName("element"+position).setIdentifier("P1");
                return true;
            case R.id.p2:
                project.getElementByName("element"+position).setIdentifier("P2");
                return true;
            case R.id.p3:
                project.getElementByName("element"+position).setIdentifier("P3");
                return true;
            case R.id.p4:
                project.getElementByName("element"+position).setIdentifier("P4");
                return true;
            case R.id.p5:
                project.getElementByName("element"+position).setIdentifier("P5");
                return true;
            case R.id.p6:
                project.getElementByName("element"+position).setIdentifier("P6");
                return true;
            case R.id.p7:
                project.getElementByName("element"+position).setIdentifier("P7");
                return true;
            case R.id.p8:
                project.getElementByName("element"+position).setIdentifier("P8");
                return true;
            case R.id.p9:
                project.getElementByName("element"+position).setIdentifier("P9");
                return true;
            case R.id.p10:
                project.getElementByName("element"+position).setIdentifier("P10");
                return true;
            case R.id.p11:
                project.getElementByName("element"+position).setIdentifier("P11");
                return true;
            case R.id.p12:
                project.getElementByName("element"+position).setIdentifier("P12");
                return true;
            case R.id.p13:
                project.getElementByName("element"+position).setIdentifier("P13");
                return true;
            case R.id.p14:
                project.getElementByName("element"+position).setIdentifier("P14");
                return true;
            case R.id.p15:
                project.getElementByName("element"+position).setIdentifier("P15");
                return true;
            case R.id.a1:
                project.getElementByName("element"+position).setIdentifier("A1");
                return true;
            case R.id.a2:
                project.getElementByName("element"+position).setIdentifier("A2");
                return true;
            case R.id.a3:
                project.getElementByName("element"+position).setIdentifier("A3");
                return true;
            case R.id.a4:
                project.getElementByName("element"+position).setIdentifier("A4");
                return true;
            case R.id.a5:
                project.getElementByName("element"+position).setIdentifier("A5");
                return true;

        }
        return false;
    }

    public void ShowName(){
        TextView view = (TextView)findViewById(R.id.textView3);

        //Wird nur gesetzt wenn der Name des Projekts nicht leer ist,
        //standardmäßig wird Project angezeigt
        if (!project.getName().equals(null)){
            view.setText(project.getName());
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
            createNewProject();
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

    public void createNewProject() {
        Intent newProIntent = new Intent(this, ProjectActivity.class);
        startActivityForResult(newProIntent, REQUEST_CODE_NEW_PRO);
    }

    public void startActivityConnection() {
        Log.d(LOG_TAG, "ConnectionActivity wird gestartet...");
        Intent newConIntent = new Intent(this, ConnectionActivity.class);

        // listView
//        newConIntent.putExtra("listAvailableCons", project.getListAllCons()); // ArrayList<String> mitgeben

        ArrayList<String> allConsType = new ArrayList<String>();
        ArrayList<String> allConsHeader = new ArrayList<String>(); // Name der Verbindungen - wird als aufklappbares Feld angezeigt
        ArrayList<String> allConsAddress = new ArrayList<String>();

        for(IConnection c : project.getListAllCons()) {
            if(c instanceof BTConnection) {
                allConsType.add("Bluetooth-Verbindung");
                allConsHeader.add( ((BTConnection) c).getConNameDeclaration());
                allConsAddress.add(((BTConnection) c).getConAddressDeclaration());
            } // TODO else if c instanceof Ethernetconnection
        }

//        Toast.makeText(this, "Anzahl Header: " + allConsHeader.size() + " \nAnzahl Children: " + allConsAddress.size(), Toast.LENGTH_LONG).show();

        newConIntent.putExtra("allConsType", allConsType);
        newConIntent.putExtra("allConsHeader", allConsHeader);
        newConIntent.putExtra("allConsAddress", allConsAddress);

        startActivityForResult(newConIntent, REQUEST_CODE_NEW_CON);
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


        seek2.setProgress(project.getGui().getGridView().getNumColumns());
        seek2.setDrawingCacheBackgroundColor(Color.DKGRAY);

        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                             @Override
                                             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                 project.getGui().getGridView().setNumColumns(progress);

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
                project.getGui().getGridView().clearAnimation();
                popDialog.cancel();
                InitializeUI();

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
                        //  BTConnection btConnection = BTConnection.initialiseConnection(name, address); // TODO wenn Verbindung am Arduino unterbrochen --> wird nicht mehr aufgebaut, da bei App bereits instantiiert
                        // project.setCurrentConnection(btConnection);


                    }
                    if (conTypeInt == 2) { // Ethernet
                        EthernetConnection ethernetConnection = EthernetConnection.initialiseConnection(name, address);
                        project.setCurrentConnection(ethernetConnection);
                    }
                    Toast.makeText(getApplicationContext(), project.getCurrentConnection().getConName() + "\n" +
                            project.getCurrentConnection().getAddress(), Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == REQUEST_CODE_NEW_PRO) {
                String proName = "";
                if (data.getExtras().containsKey("name")) {
                    proName = data.getExtras().getString("name");
                    project.setName(proName);
                }

            }
        }

    }


}
