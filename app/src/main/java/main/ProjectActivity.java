package main;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import generic.ExpListAdapterAllPro;

/**
 * Created by Lukas on 04.01.2015.
 */
public class ProjectActivity extends  Activity {


    private final String LOG_TAG = "ProjectActivity";
    EditText proName;
    Button btnSubmit;

    Dialog dialogNewPro;

    private ExpandableListView expListView;
    private ArrayList<String> listDataHeader;
    static HashMap<String, ArrayList<String>> mapListDataChild; // TODO passt static ??
    ExpListAdapterAllPro expListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();

        }

        proName = (EditText) findViewById(R.id.proName);
        btnSubmit = (Button) findViewById(R.id.proBtnSubmit);

        Intent parentIntent = getIntent();

        ArrayList<String> allProName = getIntentExtra(parentIntent, "allProName");
        ArrayList<String> allProElements = getIntentExtra(parentIntent, "allProElements");

        mapListDataChild = new HashMap<String, ArrayList<String>>();

        // Für jede Kindview eine eigene Liste anlegen und zur Liste listChildren hinzufügen
        fillHashMap(allProElements, allProName, mapListDataChild);

        expListView=(ExpandableListView) findViewById(R.id.listViewAvailablePros);
        expListAdapter=new ExpListAdapterAllPro(this,allProName,mapListDataChild);

        expListView.setAdapter(expListAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_project, menu);
        return true;
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project, container,
                    false);
            return rootView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.pro_action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillHashMap(ArrayList<String> allProElements, ArrayList<String> allProName, HashMap<String, ArrayList<String>> mapListDataChild) {
        for(int i = 0; i < allProElements.size(); i++) {
            ArrayList<String> child = new ArrayList<String>(); // TODO als String-Array, da die Größe immer 2 ist
            child.add(allProElements.get(i));

            mapListDataChild.put(allProName.get(i), child);
        }
    }
    public void createNewProject(View v) {
        dialogNewPro = new Dialog(this);
        dialogNewPro.setContentView(R.layout.new_project);
        dialogNewPro.setTitle("Neues Projekt");

        proName= (EditText) dialogNewPro.findViewById(R.id.proName);
        btnSubmit = (Button) dialogNewPro.findViewById(R.id.proBtnSubmit);

        Button btnSubmit = (Button) dialogNewPro.findViewById(R.id.proBtnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProBtnSubmitClicked(v);
            }
        });

        Button btnCancel = (Button) dialogNewPro.findViewById(R.id.proBtnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProBtnCancelClicked(dialogNewPro);
            }
        });
        dialogNewPro.show();
    }

    public void ProBtnSubmitClicked(View v) {
        String ProName = proName.getText().toString();


        if (ProName != "") { // Bluetooth oder Ethernet
            // Log.d(LOG_TAG, Integer.toString(conType));
            dialogNewPro.dismiss();
            setResultToActivity(ProName); // Variablen conType, strConName und address zur�ckliefern
        }
        else // nichts ausgew�hlt - sollte �berhaupt nicht vorkommen (als Absicherung wird es abgefangen)
            Toast.makeText(getApplicationContext(), "Es wurde nichts ausgew�hlt. ", Toast.LENGTH_SHORT).show();
    }

    private void setResultToActivity(String name) {
        Intent resultIntent = getIntent(); // Liefert den Intent, welcher diese Activity gestartet hat
        resultIntent.putExtra("name", name);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    /**
     * Methode wird ausgef�hrt, wenn Button "Abbrechen" geklickt wurde
     * @param
     */
    public void ProBtnCancelClicked(Dialog dialog) {
        dialog.cancel();
    }

    private ArrayList<String> getIntentExtra(Intent intent, String key) {
        ArrayList<String> listExtra = new ArrayList<String>();
        if(intent.getExtras().containsKey(key))
            listExtra = intent.getExtras().getStringArrayList(key);
        return listExtra;
    }


    public static void dismiss() {

    }

}
