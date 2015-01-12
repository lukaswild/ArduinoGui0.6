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
import main.ConnectionActivity;

/**
 * Created by Simon on 02.01.2015.
 * Quelle: http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial
 */
public class ExpListAdapterAllCons extends ExpandableListAdapterGeneric {

    private final String LOG_TAG = "ExpListAdapterAllCons";

    public ExpListAdapterAllCons(Context context, ArrayList<String> listDataHeader, HashMap<String, ArrayList<String>> listChildData) {
        super(context, listDataHeader, listChildData);
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Allgemein notwendig
        String headerTitle = (String) getGroup(groupPosition);
        convertView = inflateLayoutGroup(convertView);
        final TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvExpListHeader);
        setListHeader(headerTitle, lblListHeader);

        final ToggleButton tglBtnChooseCloseCon = getToggleButton(convertView);
        setStateTexts(tglBtnChooseCloseCon, "auswählen", "trennen"); // Text für Zustände ein und aus des ToggleButtons setzen

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
                        Toast.makeText(context, "Verbinden nicht möglich \nBereits bestehende Verbindung beenden" +
                                " oder Adresse überprüfen", Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Es besteht entweder bereits eine Verbindung oder die MAC- bzw. IP-Adresse ist falsch");
                    }
                } else { // Wenn Verbindung beendet werden soll
                    BTConnection.closeConnection();
                    Log.d(LOG_TAG, "Verbindung wird beendet");
                }
            }
        });

        return convertView;
    }
}