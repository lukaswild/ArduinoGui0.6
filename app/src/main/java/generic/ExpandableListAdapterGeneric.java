package generic;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Simon on 02.01.2015.
 * Quelle: http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial
 */
public abstract class ExpandableListAdapterGeneric extends BaseExpandableListAdapter {

    protected Context context;
    protected ArrayList<String> listDataHeader; // header titles
    protected static int idCount = 0;
    private final String LOG_TAG = "ExpandableListAdapterGeneric";

    // child data in format of header title, child title
    protected HashMap<String, ArrayList<String>> mapDataChild;

    public ExpandableListAdapterGeneric(Context context, ArrayList<String> listDataHeader, HashMap<String, ArrayList<String>> listChildData) {
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

    protected void setListHeader(String headerTitle, final TextView listHeader) {
        listHeader.setTypeface(null, Typeface.BOLD);
        listHeader.setText(headerTitle);
    }

    protected View inflateLayoutGroup(View convertView) {
        if (convertView == null) {
            LayoutInflater inflateLayout = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflateLayout.inflate(R.layout.list_group, null);
        }
        return convertView;
    }

    protected ToggleButton getToggleButton(View convertView) {
        return (ToggleButton) convertView.findViewById(R.id.tglBtnChooseCon);
    }

    protected void setStateTexts(ToggleButton tglBtnChooseCloseCon, String textOff, String textOn) {
        tglBtnChooseCloseCon.setTextOff(textOff);
        tglBtnChooseCloseCon.setTextOn(textOn);
        tglBtnChooseCloseCon.toggle();
        tglBtnChooseCloseCon.toggle(); // Text wird erst gesetzt, sobald tglBtn das erste Mal berührt wurde --> 2 Mal toggeln (1. Mal ein, 2. Mal wieder auf Standardzustand)
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
     public abstract View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent);
}
