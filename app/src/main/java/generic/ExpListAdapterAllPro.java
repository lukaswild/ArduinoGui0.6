package generic;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import main.MainActivity;

/**
 * Created by Lukas on 12.01.2015.
 */
public class ExpListAdapterAllPro extends ExpandableListAdapterGeneric {

    public ExpListAdapterAllPro(Context context, ArrayList<String> listDataHeader, HashMap<String, ArrayList<String>> listChildData) {
        super(context, listDataHeader, listChildData);
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Allgemein notwendig
        String headerTitle = (String) getGroup(groupPosition);
        convertView = inflateLayoutGroup(convertView, R.layout.list_group_pro);
       final TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvExpListHeader2);
       setListHeader(headerTitle, lblListHeader);

        final Button BtnChoose = getButton(convertView);
        BtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = listDataHeader.get(groupPosition).toString();
                //Das ausgewählte Projekt wird als Current Projekt gesetzt
                MainActivity.setCurrentProjByName(key);
                MainActivity.loadImgRes();
                ((Activity)context).finish();
            }
        });


        return convertView;
    }


}
