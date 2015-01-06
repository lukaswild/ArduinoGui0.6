package connection;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import main.ConnectionActivity;

/**
 * Created by Simon on 02.01.2015.
 * Quelle: http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> listDataHeader; // header titles
    private static int idCount = 0;
    private final String LOG_TAG = "ExpandableListAdapter";

    // child data in format of header title, child title
    private HashMap<String, ArrayList<String>> mapDataChild;

    public ExpandableListAdapter(Context context, ArrayList<String> listDataHeader, HashMap<String, ArrayList<String>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.mapDataChild = listChildData;
    }


    public HashMap<String, ArrayList<String>> getMapDataChild() {
        return mapDataChild;
    }

    public void setMapDataChild(HashMap<String, ArrayList<String>> mapDataChild) {
        this.mapDataChild = mapDataChild;
    }

    public ArrayList<String> getListDataHeader() {
        return listDataHeader;
    }

    public void setListDataHeader(ArrayList<String> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mapDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,  boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.expListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mapDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }


        final TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvExpListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        // Listener zu ToggleButton hinzufügen
        final ToggleButton tglBtnChooseCloseCon = (ToggleButton) convertView.findViewById(R.id.tglBtnChooseCon);
        final View finalConvertView = convertView;
        tglBtnChooseCloseCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tglBtnChooseCloseCon.isChecked()) { // Wenn Verbindung aufgebaut werden soll
                    Log.d(LOG_TAG, "Verbindung wird aufgebaut...");
                    String key = listDataHeader.get(groupPosition).toString();
                    boolean initialisingNewConSuccessful = ConnectionActivity.chooseConnection(key);
                    // Wenn bereits eine Verbindung vorhanden ist, so wird keine neue Verbindung aufgebaut --> ToggleButton soll nicht aktiviert werden
                    if(!initialisingNewConSuccessful) {
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

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
