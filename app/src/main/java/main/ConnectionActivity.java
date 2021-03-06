package main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import connection.BTConnection;
import connection.EthernetConnection;
import connection.IConnection;
import connection.IPAddressValidator;
import generic.ExpListAdapterAllCons;
import observer.DatabaseHandler;

public class ConnectionActivity extends Activity {

    private final static String LOG_TAG = "ConnectionActivity";
    private EditText etConName;
    private TextView tvConAddress;
    private EditText etConAddress;
    private Button btnSubmit;
    private Button btnScanBtDevices;
    private int conType = 0; // Art der Verbindung - 0: nichts ausgew�hlt, 1: Bluetooth, 2: Ethernet

    private ExpandableListView expListView; // ExpandableListView
    private static HashMap<String, ArrayList<String>> mapListDataChild;
    private ExpListAdapterAllCons expListAdapter;
    private AlertDialog.Builder dialogScannedDevs;

    public HashMap<String, ArrayList<String>> getMapListDataChild() {
        return mapListDataChild;
    }
    private Dialog dialogNewCon; // Dialog zum Hinzufügen einer neuen Verbindung
    private Dialog dialogAlterCon; // Dialog zum Bearbeiten einer Verbindung

    DatabaseHandler dbHandler;
    BroadcastReceiver bcFindBtDevs;

    final BroadcastReceiver bcScanDevFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Scan beendet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    final BroadcastReceiver bcScanDevStarted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Nach Geräten scannen...", Toast.LENGTH_SHORT).show();
            }
        }
    };



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
        final ArrayList<String> allConsType = getIntentExtra(parentIntent, "allConsType");
        final ArrayList<String> allConsHeader = getIntentExtra(parentIntent, "allConsHeader");
        final ArrayList<String> allConsAddress = getIntentExtra(parentIntent, "allConsAddress");
        int currentConPosition = -1;
        if(parentIntent.getExtras().containsKey("currentConPosition")) {
            currentConPosition = parentIntent.getExtras().getInt("currentConPosition");
        }

        mapListDataChild = new HashMap<String, ArrayList<String>>();

        // Für jede Kindview eine eigene Liste anlegen und zur Liste listChildren hinzufügen
        fillHashMap(allConsType, allConsHeader, allConsAddress, mapListDataChild);

        expListView = (ExpandableListView) findViewById(R.id.listViewAvailableCons);
        expListAdapter = new ExpListAdapterAllCons(this, allConsHeader, mapListDataChild, currentConPosition);


        // der ExpandableListView den Adapter übergeben
        expListView.setAdapter(expListAdapter);
        Log.d(LOG_TAG, "Adapter wurde ListView hinzugefügt");

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                if(MainActivity.getCurrentConnection() == null) {

                    final int positionFinal = position;
                    final PopupMenu popupConOptions = new PopupMenu(getApplicationContext(), view);
                    popupConOptions.inflate(R.menu.menu_popup_entries);
                    final String keyChosen = parent.getItemAtPosition(position).toString();

                    if (allConsHeader.contains(keyChosen)) {
                        popupConOptions.show();

                        Log.d(LOG_TAG, view.getClass().toString());
                        Log.d(LOG_TAG, parent.getClass().toString());
                        Log.d(LOG_TAG, parent.getItemAtPosition(position).getClass().toString());
                        Log.d(LOG_TAG, keyChosen);

                        popupConOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {

                                    case R.id.removeEntry:
                                        if (mapListDataChild.containsKey(keyChosen)) {
                                            mapListDataChild.remove(keyChosen);
                                            int pos = 0;
                                            for (int i = 0; i < allConsHeader.size(); i++) {
                                                if (allConsHeader.get(i).equals(keyChosen)) {
                                                    try {
                                                        allConsHeader.remove(i);
                                                        allConsAddress.remove(i);
                                                        allConsType.remove(i);
                                                    } catch (Exception e) {
                                                        Toast.makeText(getBaseContext(), "Fehler beim Löschen. Beenden Sie die aktuelle Verbindung und versuchen es erneut", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            MainActivity.removeConnection(keyChosen);

                                            dbHandler.deleteConnectionDb(keyChosen);
                                        }
                                        Log.d(LOG_TAG, "Entfernen des Eintrags " + keyChosen);
                                        expListAdapter.notifyDataSetChanged();
                                        return true;


                                    case R.id.alterEntry:

                                        refreshListData(allConsType, allConsAddress, allConsHeader);

                                        IConnection con = null;
                                        Log.d(LOG_TAG, MainActivity.getAllConnections().size() + "ddd");
                                        for (IConnection c : MainActivity.getAllConnections()) {
                                            if (c.getConNameDeclaration().equals(keyChosen))
                                                con = c;
                                        }

                                        if (con == null) {
                                            Toast.makeText(getBaseContext(), "Es ist leider ein Fehler aufgetreten", Toast.LENGTH_SHORT).show();
                                            Log.e(LOG_TAG, "Bearbeiten nicht möglich, kein passender Eintrag in Liste gefunden");
                                            return false;
                                        }

                                        dialogAlterCon = new Dialog(ConnectionActivity.this);
                                        dialogAlterCon.setTitle("Verbindung bearbeiten");
                                        dialogAlterCon.setContentView(R.layout.alter_connection);

                                        final IConnection conFinal = con;
                                        TextView tvConAddressAlter = (TextView) dialogAlterCon.findViewById(R.id.tvConAddressAlter);
                                        TextView tvConNameAlter = (TextView) dialogAlterCon.findViewById(R.id.tvConNameAlter);
                                        final EditText etConNameAlter = (EditText) dialogAlterCon.findViewById(R.id.etConNameAlter);
                                        final EditText etConAddressAlter = (EditText) dialogAlterCon.findViewById(R.id.etConAddressAlter);
                                        Button btnSubmitAlter = (Button) dialogAlterCon.findViewById(R.id.btnSubmitAlter);
                                        Button btnCancelAlter = (Button) dialogAlterCon.findViewById(R.id.btnCancelAlter);

                                        if (conFinal instanceof BTConnection) {
                                            tvConAddressAlter.setText(getBaseContext().getString(R.string.txtMacAddressBT));
                                            etConAddressAlter.setHint(getBaseContext().getString(R.string.txtMacAddress));
                                        } else {
                                            tvConAddressAlter.setText(getBaseContext().getString(R.string.txtIpAddressArduino));
                                            etConAddressAlter.setHint(getBaseContext().getString(R.string.txtIpAddress));
                                        }

                                        etConNameAlter.setText(con.getConNameDeclaration());
                                        etConAddressAlter.setText(con.getConAddressDeclaration());


                                        setButtonSubmitAlterOnClickListener(conFinal, etConNameAlter, etConAddressAlter, btnSubmitAlter,
                                                position, positionFinal, allConsHeader, allConsAddress, keyChosen, allConsType);

                                        btnCancelAlterSetOnClickListener(btnCancelAlter);
                                        dialogAlterCon.show();
                                        return true;
                                }
                                return false;
                            }
                        });
                    }
                } else
                Toast.makeText(getBaseContext(), "Um Verbindung bearbeiten zu können bitte zuerst aktuelle Verbindung beenden", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        dbHandler = new DatabaseHandler(this);
        dbHandler.setDb(dbHandler.getWritableDatabase());
    }


    private void setButtonSubmitAlterOnClickListener(final IConnection conFinal, final EditText etConNameAlter,
                                                     final EditText etConAddressAlter, Button btnSubmitAlter, final int position,
                                                     final int positionFinal, final ArrayList<String> allConsHeader,
                                                     final ArrayList<String> allConsAddress, final String keyChosen,
                                                     final ArrayList<String> allConsType) {

        btnSubmitAlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newConName = etConNameAlter.getText().toString();
                boolean isUnique = isConNameUnique(newConName, conFinal.getConNameDeclaration());
                if(isUnique) {
                    if(!newConName.equals("")) {
                        String newConAddress = etConAddressAlter.getText().toString();

                        conFinal.setConNameDeclaration(newConName);
                        conFinal.setConAddressDeclaration(newConAddress);

                        MainActivity.getAllConnections().set(position, conFinal);

                        // Ändern in allConsHeader und mapListDataChild
                        Log.d(LOG_TAG, positionFinal + "");
                        Log.d(LOG_TAG, allConsHeader.get(position));
                        allConsHeader.set(position, newConName);
                        allConsAddress.set(position, newConAddress);
                        mapListDataChild.remove(keyChosen);

                        ArrayList<String> child = new ArrayList<String>();
                        child.add(allConsType.get(position));
                        child.add(newConAddress);
                        mapListDataChild.put(newConName, child);

                        dbHandler.updateConnectionDb(conFinal, keyChosen);

                        expListAdapter.notifyDataSetChanged();
                        dialogAlterCon.dismiss();
                    } else
                        Toast.makeText(getBaseContext(), getString(R.string.errorNameEmpty), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getBaseContext(), getString(R.string.errorNameNotUnique), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshListData(ArrayList<String> allConsType, ArrayList<String> allConsAddress, ArrayList<String> allConsHeader) {
        allConsType.clear();
        allConsAddress.clear();
        allConsHeader.clear();
        for (IConnection c : MainActivity.getAllConnections()) {
            if (c instanceof BTConnection) {
                allConsType.add(getString(R.string.description_btCon));
                allConsHeader.add( c.getConNameDeclaration());
                allConsAddress.add( c.getConAddressDeclaration());
            }
            else if (c instanceof EthernetConnection) {
                allConsType.add(getString(R.string.description_ethernetCon));
                allConsHeader.add(c.getConNameDeclaration());
                allConsAddress.add(c.getConAddressDeclaration());
            }
        }
    }


    private void btnCancelAlterSetOnClickListener(Button btnCancelAlter) {
        btnCancelAlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlterCon.cancel();
            }
        });
    }


    private ArrayList<String> getIntentExtra(Intent intent, String key) {
        ArrayList<String> listExtra = new ArrayList<String>();
        if(intent.getExtras().containsKey(key))
            listExtra = intent.getExtras().getStringArrayList(key);
        return listExtra;
    }


    private void fillHashMap(ArrayList<String> allConsType, ArrayList<String> allConsHeader, ArrayList<String> allConsAddress, HashMap<String, ArrayList<String>> mapListDataChild) {
        for(int i = 0; i < allConsType.size(); i++) {
            ArrayList<String> child = new ArrayList<String>();
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

    @Override
    public void onDestroy() {
        //  Abspeichern der Connections in der DB
        super.onDestroy();

        try {
            unregisterReceiver(bcFindBtDevs);
        } catch (Exception e) {}
        try {
            unregisterReceiver(bcScanDevFinished);
        } catch(Exception e) {}
        try {
            unregisterReceiver(bcScanDevStarted);
        } catch(Exception e) {}
    }



    public void createNewConnection(View v) {

        dialogNewCon = new Dialog(this);
        dialogNewCon.setContentView(R.layout.new_connection);
        dialogNewCon.setTitle("Neue Verbindung");
        etConName = (EditText) dialogNewCon.findViewById(R.id.etConName);
        tvConAddress = (TextView) dialogNewCon.findViewById(R.id.tvConAddress);
        etConAddress = (EditText) dialogNewCon.findViewById(R.id.etConAddress);
        btnSubmit = (Button) dialogNewCon.findViewById(R.id.btnSubmit);
        btnScanBtDevices = (Button) dialogNewCon.findViewById(R.id.btnScanDevices);
        setSpinnerListener(dialogNewCon);
        Button btnSubmit = (Button) dialogNewCon.findViewById(R.id.btnSubmit);
        dialogNewCon.show();

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

        // Es soll ein weiteres Dialogfenster erscheinen, welches nach verfügbaren BT-Geräten sucht und diese auflistet.
        btnScanBtDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanForDevicesAndList(v);
            }
        });
    }

    // TODO Progress circle während Scanvorgang
    private void showDialogAvCons(final ArrayAdapter<String> adapterAvCons, final HashMap<String, String> mapAllDevsWithAddresses) {
        dialogScannedDevs = new AlertDialog.Builder( ConnectionActivity.this);
        dialogScannedDevs.setTitle("Verfügbare Geräte");

        dialogScannedDevs.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogScannedDevs.setAdapter(adapterAvCons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                String devName = adapterAvCons.getItem(index);
                String devAddress = mapAllDevsWithAddresses.get(devName);
                etConName.setText(devName);
                etConAddress.setText(devAddress);
            }
        });
        dialogScannedDevs.show();
    }


    private void scanForDevicesAndList(View v) {
        final ArrayAdapter<String> adapterAvCons = new ArrayAdapter<String>(ConnectionActivity.this, android.R.layout.select_dialog_item);

        /* In diese HashMap werden alle Verbindungen mit Namen und Adressen gespeichert. Im Dialog wird nur der Name angezeigt,
        *  aus der HashMap wird von der ausgewählten Verbindung folglich die Adresse geholt
        */
        final HashMap<String, String> mapAllDevsWithAddresses = new HashMap<String, String>(); //Key: Name, Data: Address

        /*final BroadcastReceiver */bcFindBtDevs = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d(LOG_TAG, device.getName() + " " + device.getAddress());
                    mapAllDevsWithAddresses.put(device.getName(), device.getAddress()); //
                    adapterAvCons.add(device.getName());
                    adapterAvCons.notifyDataSetChanged();
                }
            }
        };

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.isDiscovering()) // the button is pressed when it discovers, so cancel the discovery
            adapter.cancelDiscovery();
        else {
            adapter.startDiscovery();
            registerReceiver(bcFindBtDevs, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(bcScanDevFinished, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            registerReceiver(bcScanDevStarted, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        }
        showDialogAvCons(adapterAvCons, mapAllDevsWithAddresses);
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
                        btnScanBtDevices.setVisibility(View.INVISIBLE);
                        btnScanBtDevices.setEnabled(false);
                        break;

                    case 1:
                        conType = 1;
                        tvConAddress.setText(getString(R.string.txtMacAddressBT));
                        etConAddress.setHint(getString(R.string.txtMacAddress));
                        btnScanBtDevices.setVisibility(View.VISIBLE);
                        btnScanBtDevices.setEnabled(true);
                        showConViews(etConName, tvConAddress, etConAddress, btnSubmit);
                        break;

                    case 2:
                        conType = 2;
                        tvConAddress.setText(getString(R.string.txtIpAddressArduino));
                        etConAddress.setHint(getString(R.string.txtIpAddress));
                        showConViews(etConName, tvConAddress, etConAddress, btnSubmit);
                        btnScanBtDevices.setVisibility(View.INVISIBLE);
                        btnScanBtDevices.setEnabled(true);
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
        conName.trim();
        boolean isUnique = isConNameUnique(conName);
        if(isUnique) {
            if(!conName.equals("")) {
                String conAddress = etConAddress.getText().toString();
                HashMap<String, ArrayList<String>> mapExpListView = expListAdapter.getMapDataChild();
                ArrayList<String> listChildrenToAdd;

                // Es sind nur MAC-Adressen mit Separator ":" erlaubt (IP-Addressen werden mit einem Punkt getrennt, diese werden nicht ersetzt)
                conAddress.replaceAll("-", ":");
                conAddress.trim();

                switch (conType) { // 0 (nichts ausgewählt), 1 (BT), oder 2 (Ethernet)

                    case 0: // nichts ausgewählt - sollte nicht vorkommene
                        Toast.makeText(getApplicationContext(), "Es wurde nichts ausgewählt. ", Toast.LENGTH_SHORT).show();
                        Log.e(LOG_TAG, "Es wurde kein Verbindungstyp ausgewählt");
                        break;

                    case 1: // Bluetooth-Verbindung ausgewählt
                        if(BluetoothAdapter.checkBluetoothAddress(conAddress)) {
                            strConType = "Bluetooth-Verbindung";
                            IConnection conToAddB = BTConnection.createAttributeCon(conName, conAddress);
                            addNewConnection(strConType, conName, conAddress, mapExpListView, conToAddB);
                        } else
                            Toast.makeText(getBaseContext(), getString(R.string.errInvalidMacAddress), Toast.LENGTH_SHORT).show();
                        break;

                    case 2: // Ethernet-Verbindung ausgewählt
                        IPAddressValidator validator = new IPAddressValidator();
                        if(validator.isValid(conAddress)) {
                            strConType = "Ethernet-Verbindung";
                            IConnection conToAddE = EthernetConnection.createAttributeCon(conName, conAddress);
                            addNewConnection(strConType, conName, conAddress, mapExpListView, conToAddE);
                        } else
                            Toast.makeText(getBaseContext(), "Ungültige IP-Adresse", Toast.LENGTH_SHORT).show();
                        break;
                }

                expListAdapter.notifyDataSetChanged(); // Die ExpandableListView aktualisieren
                Log.i(LOG_TAG, "Liste aller Connections aktualisiert");

            } else
                Toast.makeText(this, getString(R.string.errorNameEmpty), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, getString(R.string.errorNameNotUnique), Toast.LENGTH_SHORT).show();
    }

    private boolean isConNameUnique(String conName) {
        boolean isUnique = false;
        for(IConnection c : MainActivity.getAllConnections()) {
            if(c.getConNameDeclaration().equals(conName)) {
                isUnique = false;
                break;
            } else
                isUnique = true;
        }
        if(MainActivity.getAllConnections().size() == 0)
            isUnique = true;

        return isUnique;
    }

    private boolean isConNameUnique(String conNameNew, String conNameOld) {
        boolean isUnique = false;
        ArrayList<String> conNames = new ArrayList<String>();
        for(IConnection ic : MainActivity.getAllConnections()) {
            conNames.add(ic.getConNameDeclaration());
        }
        conNames.remove(conNameOld);
        for(String s : conNames) {
            if(s.equals(conNameNew)) {
                isUnique = false;
                break;
            }
            else
                isUnique = true;
        }
        if(conNames.size() == 0)
            isUnique = true;

        return isUnique;
    }

    private void addNewConnection(String strConType, String conName, String conAddress,
                                  HashMap<String, ArrayList<String>> mapExpListView, IConnection conToAdd) {
        MainActivity.getAllConnections().add(conToAdd); // Die neue Connection zur Liste mit allen Connections hinzufügen
        expListAdapter.getListDataHeader().add(conName);
        ArrayList<String> listChildrenToAdd = createListChildrenToAdd(strConType, conAddress);
        mapExpListView.put(conName, listChildrenToAdd);
        dbHandler.insertConnection(conToAdd, dbHandler.getDb());
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
        boolean initialisingSuccessful;
        Log.d(LOG_TAG, clickedConType);
        Log.d(LOG_TAG, clickedConName);

        if(clickedConType.equals("Bluetooth-Verbindung"))
            initialisingSuccessful = BTConnection.initialiseConnection(clickedConName, clickedConAddress);
        else
            initialisingSuccessful = EthernetConnection.initialiseConnection(clickedConName, clickedConAddress);

        return initialisingSuccessful;
    }

    public void makeToastBtDisabled() {
        Toast.makeText(this, "Bitte Bluetooth einschalten", Toast.LENGTH_SHORT).show();
    }
}