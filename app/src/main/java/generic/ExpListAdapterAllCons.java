package generic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import connection.BTConnection;
import connection.EthernetConnection;
import main.ConnectionActivity;
import main.MainActivity;

/**
 * Created by Simon on 02.01.2015.
 * Quelle: http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial
 */
public class ExpListAdapterAllCons extends ExpandableListAdapterGeneric {

    private final String LOG_TAG = "ExpListAdapterAllCons";
    private ArrayList<String> listDataHeader;
    private int currentConPosition;

    public ExpListAdapterAllCons(Context context, ArrayList<String> listDataHeader, HashMap<String, ArrayList<String>> listChildData, int currentConPosition) {
        super(context, listDataHeader, listChildData);
        this.listDataHeader = listDataHeader;
        this.currentConPosition = currentConPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Allgemein notwendig
        String headerTitle = (String) getGroup(groupPosition);
        convertView = inflateLayoutGroup(convertView, R.layout.list_group);
        final TextView tvListHeader = (TextView) convertView.findViewById(R.id.tvExpListHeader);
        setListHeader(headerTitle, tvListHeader);

        final ToggleButton tglBtnChooseCloseCon = getToggleButton(convertView);
        setStateTexts(tglBtnChooseCloseCon, "auswählen", "trennen"); // Text für Zustände ein und aus des ToggleButtons setzen

        if(currentConPosition != -1) {
            if(groupPosition == currentConPosition) {
                tglBtnChooseCloseCon.toggle();
            }
        }



        // Listener zu ToggleButton hinzufügen
        tglBtnChooseCloseCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tglBtnChooseCloseCon.isChecked()) { // Wenn Verbindung aufgebaut werden soll
                    Log.d(LOG_TAG, "Verbindung wird aufgebaut...");
                    String key = listDataHeader.get(groupPosition).toString();
                    boolean initialisingNewConSuccessful = ConnectionActivity.chooseConnection(key);
                    // Wenn bereits eine Verbindung vorhanden ist, so wird keine neue Verbindung aufgebaut --> ToggleButton soll nicht aktiviert werden
                    if (!initialisingNewConSuccessful) {
                        tglBtnChooseCloseCon.setChecked(false);
                        Toast.makeText(context, "Fehler. Mögliche Ursachen: \n" +
                                "- Bluetooth ausgeschaltet \n" +
                                "- Keine Netzwerkverbindung \n" +
                                "- Es besteht bereits eine Verbindung\n" +
                                "- Falsche Adresse", Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Es besteht entweder bereits eine Verbindung, die MAC- bzw. IP-Adresse ist falsch oder " +
                                "BT ist disabled");
                    }
                } else { // Wenn Verbindung beendet werden soll
                    if (MainActivity.getCurrentConnection() instanceof BTConnection)
                        BTConnection.closeConnection();
                    else
                        EthernetConnection.closeConnection();

                    MainActivity.setCurrentConnection(null);
                    Log.d(LOG_TAG, "Verbindung wird beendet");
                }
            }
        });

        return convertView;
    }
}