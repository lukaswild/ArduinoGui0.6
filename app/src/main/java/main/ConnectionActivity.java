package main;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import connection.BTConnection;
import connection.EthernetConnection;
import connection.ExpandableListAdapter;
import connection.IConnection;

public class ConnectionActivity extends Activity {

    // Neue Verbindung - Dialogfenster
    private final static String LOG_TAG = "ConnectionActivity";
    private EditText etConName;
    private TextView tvConAddress;
    private EditText etConAddress;
    private Button btnSubmit;
    private int conType = 0; // Art der Verbindung - 0: nichts ausgew�hlt, 1: Bluetooth, 2: Ethernet

    // Verfügbare Verbindungen anzeigen - standardmäßig bei Start der Activity
    Dialog dialogNewCon;
    private StableArrayAdapter conAdapter; // nur für ListView benötigt - sollte nicht mehr benötigt werden

    // ExpandableListView
    private ExpandableListView expListView;
    private ArrayList<String> listDataHeader;
    static HashMap<String, ArrayList<String>> mapListDataChild; // TODO passt static ??
    ExpandableListAdapter expListAdapter;


    public HashMap<String, ArrayList<String>> getMapListDataChild() {
        return mapListDataChild;
    }

    public void setMapListDataChild(HashMap<String, ArrayList<String>> mapListDataChild) {
        this.mapListDataChild = mapListDataChild;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        Intent parentIntent = getIntent();

        ArrayList<String> allConsType = getIntentExtra(parentIntent, "allConsType");
        ArrayList<String> allConsHeader = getIntentExtra(parentIntent, "allConsHeader");
        ArrayList<String> allConsAddress = getIntentExtra(parentIntent, "allConsAddress");

        mapListDataChild = new HashMap<String, ArrayList<String>>();

        // Für jede Kindview eine eigene Liste anlegen und zur Liste listChildren hinzufügen
        fillHashMap(allConsType, allConsHeader, allConsAddress, mapListDataChild);

        expListView = (ExpandableListView) findViewById(R.id.listViewAvailableCons);
        expListAdapter = new ExpandableListAdapter(this, allConsHeader, mapListDataChild);


        // der ExpandableListView den Adapter übergeben
        expListView.setAdapter(expListAdapter);
        Log.d(LOG_TAG, "Adapter wurde ListView hinzugefügt");
    }

    private ArrayList<String> getIntentExtra(Intent intent, String key) {
        ArrayList<String> listExtra = new ArrayList<String>();
        if(intent.getExtras().containsKey(key))
            listExtra = intent.getExtras().getStringArrayList(key);
        return listExtra;
    }


    private void fillHashMap(ArrayList<String> allConsType, ArrayList<String> allConsHeader, ArrayList<String> allConsAddress, HashMap<String, ArrayList<String>> mapListDataChild) {
        for(int i = 0; i < allConsType.size(); i++) {
            ArrayList<String> child = new ArrayList<String>(); // TODO als String-Array, da die Größe immer 2 ist
            child.add(allConsType.get(i));
            child.add(allConsAddress.get(i));

            mapListDataChild.put(allConsHeader.get(i), child);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
            View rootView = inflater.inflate(R.layout.fragment_connection,
                    container, false);
            return rootView;
        }
    }


    public void createNewConnection(View v) {
        dialogNewCon = new Dialog(this);
        dialogNewCon.setContentView(R.layout.new_connection);
        dialogNewCon.setTitle("Neue Verbindung");
        etConName = (EditText) dialogNewCon.findViewById(R.id.etConName);
        tvConAddress = (TextView) dialogNewCon.findViewById(R.id.tvConAddress);
        etConAddress = (EditText) dialogNewCon.findViewById(R.id.etConAddress);
        btnSubmit = (Button) dialogNewCon.findViewById(R.id.btnSubmit);
        setSpinnerListener(dialogNewCon);
        Button btnSubmit = (Button) dialogNewCon.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmitClicked(v);
            }
        });

        Button btnCancel = (Button) dialogNewCon.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancelClicked(dialogNewCon);
            }
        });
        dialogNewCon.show();
    }


    /**
     * Hinzuf�gen eines OnItemSelectedListeners zu dem Spinner (Drop-Down-Men�),
     * �ber welches der Typ der Verbindung (Bluetooth oder Ethernet) ausgew�hlt wird
     */
    private void setSpinnerListener(Dialog dialog) {
        Spinner spinnerConType = (Spinner) dialog.findViewById(R.id.spinnerConType);
        spinnerConType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View v, int position, long arg3) {
                Log.d(LOG_TAG, "Es wurde Position " + position + " geklickt");

				/*
				 * Wert von position:
				 * 0: Aufforderung, etwas auszuw�hlen (prompt)
				 * 1: Bluetooth
				 * 2: Ethernet
				 */
                switch (position) {
                    case 0:
                        conType = 0;
                        etConName.setEnabled(false);
                        tvConAddress.setText("");
                        etConAddress.setHint("");
                        etConName.setEnabled(false);
                        tvConAddress.setVisibility(View.INVISIBLE);
                        etConAddress.setEnabled(false);
                        btnSubmit.setEnabled(false);

                        break;

                    case 1:
                        conType = 1;
                        tvConAddress.setText("MAC-Adresse des Bluetooth-Moduls");
                        etConAddress.setHint("MAC-Adresse");
                        showConViews(etConName, tvConAddress, etConAddress, btnSubmit);
                        break;

                    case 2:
                        conType = 2;
                        tvConAddress.setText("IP-Adresse des Arduinos");
                        etConAddress.setHint("IP-Adresse");
                        showConViews(etConName, tvConAddress, etConAddress, btnSubmit);
                        break;
                } // es gibt kein default, da nur 0, 1 oder 2 ausgew�hlt werden kann
            }

            /**
             * Diese Methode aktiviert alle editierbaren Textfelder und zeigt die TextView an, welche dem Benutzer mitteilt,
             * ob er eine MAC- oder IP-Adresse eingeben soll.
             * Ist im Drop-Down-Men� nichts (Eintrag 0) ausgew�hlt, so ist der Button OK disabled.
             * @param etConName
             * @param tvConAddress
             * @param etConAddress
             * @param btnSubmit - OK-Button
             */
            private void showConViews(EditText etConName, TextView tvConAddress, EditText etConAddress, Button btnSubmit) {
                etConName.setEnabled(true);
                tvConAddress.setVisibility(View.VISIBLE);
                etConAddress.setEnabled(true);
                btnSubmit.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getApplicationContext(), "Nichts ausgew�hlt", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Methode wird ausgef�hrt, wenn Button "OK" im Dialogfenster (neue Verbindung einrichten) angeklickt wurde
     * @param v
     */
    public void btnSubmitClicked(View v) {
        String strConType = "";
        String conName = etConName.getText().toString();
        String conAddress = etConAddress.getText().toString();
        HashMap<String, ArrayList<String>> mapExpListView = expListAdapter.getMapDataChild();
        ArrayList<String> listChildrenToAdd;

        // Es sind nur MAC-Adressen mit Separator ":" erlaubt (IP-Addressen werden mit einem Punkt getrennt, diese werden nicht ersetzt)
        conAddress.replaceAll("-", ":");
        conAddress.trim();

        switch (conType) { // 0 (nichts ausgewählt), 1 (BT), oder 2 (Ethernet)
//            conAdapter.add(conName);
//            conAdapter.add(conAddress);
//               conAdapter.notifyDataSetChanged();

            case 0: // nichts ausgewählt - sollte nicht vorkommene
                Toast.makeText(getApplicationContext(), "Es wurde nichts ausgewählt. ", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Es wurde kein Verbindungstyp ausgewählt");
                break;

            case 1: // Bluetooth-Verbindung ausgewählt
                strConType = "Bluetooth-Verbindung";
                IConnection conToAddB = BTConnection.createAttributeCon(conName, conAddress);
                addNewConnection(strConType, conName, conAddress, mapExpListView, conToAddB);
                break;

            case 2: // Ethernet-Verbindung ausgewählt
                strConType = "Ethernet-Verbindung";
                IConnection conToAddE = EthernetConnection.createAttributeCon(conName, conAddress);
                addNewConnection(strConType, conName, conAddress, mapExpListView, conToAddE);
                break;
        }

        expListAdapter.notifyDataSetChanged(); // Die ExpandableListView aktualisieren
        Log.i(LOG_TAG, "Liste aller Connections aktualisiert");
    }

    private void addNewConnection(String strConType, String conName, String conAddress,
                                  HashMap<String, ArrayList<String>> mapExpListView, IConnection conToAdd) {
        MainActivity.getAllConnections().add(conToAdd); // Die neue Connection zur Liste mit allen Connections hinzufügen
        expListAdapter.getListDataHeader().add(conName);
        ArrayList<String> listChildrenToAdd = createListChildrenToAdd(strConType, conAddress);
        mapExpListView.put(conName, listChildrenToAdd);
        dialogNewCon.cancel();
    }

    private ArrayList<String> createListChildrenToAdd(String strConType, String conAddress) {
        ArrayList<String> listChildrenToAdd;
        listChildrenToAdd = new ArrayList<String>();
        listChildrenToAdd.add(strConType);
        listChildrenToAdd.add(conAddress);
        return listChildrenToAdd;
    }


    /**
     * Methode wird ausgef�hrt, wenn Button "Abbrechen" geklickt wurde
     * @param dialog
     */
    public void btnCancelClicked(Dialog dialog) {
        dialog.cancel();
    }


    /**
     * Diese Methode liefert an die aufrufende Activity den Namen und die Adresse der Connection zur�ck.
     * Name und Adresse werden dem Intent als Extras �bergeben, anschlie�end wird Methode onActivityResult in
     * aufrufender Activity aufgerufen und diese Activity beendet.
     * @param conType - Art der Verbindung: 1: Bluetooth, 2: Ethernet
     * @param name - Der Name der Connection
     * @param address - Die Adresse der Connection
     */
    private void setResultToActivity(int conType, String name, String address) {
        Intent resultIntent = getIntent(); // Liefert den Intent, welcher diese Activity gestartet hat
        resultIntent.putExtra("conType", conType);
        resultIntent.putExtra("name", name);
        resultIntent.putExtra("address", address);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    String isActivatedStr = "aus";

//    public void tglBtnChooseCloseConClicked(View v) {
//        ToggleButton tglBtnChooseCon = (ToggleButton) findViewById(R.id.tglBtnChooseCon);
//        boolean isChecked = tglBtnChooseCon.isChecked();
//
//        if(isChecked)
//            isActivatedStr = "ein";
//        else
//            isActivatedStr = "aus";
//
//        Toast.makeText(this, isActivatedStr, Toast.LENGTH_SHORT).show();
//
////        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutListGroup);
////        for(int i = 0; i < relativeLayout.getChildCount(); i++) {
////            View child = relativeLayout.getChildAt(i);
////            Log.d(LOG_TAG, child.getClass().toString() + " " +  child.getId());
////        }
//    }

    /**
     * Hiermit wird eine Verbindung aufgebaut, wenn ein Schalter (ToggleButton) neben einer
     * Verbindung angeklickt wurde
     * @param key - Der Key des Eintrags in der HashMap
     */
    public static boolean chooseConnection(String key) {
        ArrayList<String> clickedEntry = (ArrayList<String>) mapListDataChild.get(key);
        String clickedConType = clickedEntry.get(0);
        String clickedConName = key;
        String clickedConAddress = clickedEntry.get(1);
        boolean initialisingSuccessful = false;
        Log.d(LOG_TAG, clickedConType);
        Log.d(LOG_TAG, clickedConName);

        if(clickedConType.equals("Bluetooth-Verbindung"))
            initialisingSuccessful = BTConnection.initialiseConnection(clickedConName, clickedConAddress);
        else
            initialisingSuccessful = EthernetConnection.initialiseConnection(clickedConName, clickedConAddress);

        return initialisingSuccessful;
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
    }
}