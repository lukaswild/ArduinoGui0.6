package main;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
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
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import observer.DatabaseHandler;
import generic.ExpListAdapterAllPro;
import observer.Project;

/**
 * Created by Lukas on 04.01.2015.
 */
public class ProjectActivity extends  Activity {


    private final String LOG_TAG = "ProjectActivity";
    private EditText proName;
    private Button btnSubmit;

    private Dialog dialogNewPro;
    private Dialog dialogAlterPro;

    private ExpandableListView expListView;
    private ArrayList<String> listDataHeader;
    static HashMap<String, ArrayList<String>> mapListDataChild;
    private ExpListAdapterAllPro expListAdapter;
    private DatabaseHandler dbHandler;


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

        final ArrayList<String> allProName = getIntentExtra(parentIntent, "allProName");
        final ArrayList<String> allProCreationDates = getIntentExtra(parentIntent, "allProCreationDates");
        final ArrayList<String> allProLastModifiedDates = getIntentExtra(parentIntent, "allProLastModifiedDates");


        mapListDataChild = new HashMap<String, ArrayList<String>>();

        // Für jede Kindview eine eigene Liste anlegen und zur Liste listChildren hinzufügen
        fillHashMap(allProName, allProCreationDates, allProLastModifiedDates, mapListDataChild);

        expListView=(ExpandableListView) findViewById(R.id.listViewAvailablePros);
        expListAdapter=new ExpListAdapterAllPro(this,allProName,mapListDataChild);

        expListView.setAdapter(expListAdapter);

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int positionFinal = position;
                final PopupMenu popupProOptions = new PopupMenu(getApplicationContext(), view);
                popupProOptions.inflate(R.menu.menu_popup_entries);
                final String keyChosen = parent.getItemAtPosition(position).toString();
                Log.d(LOG_TAG, "keyChosen: " + keyChosen);

                if(allProName.contains(keyChosen)) {
                    popupProOptions.show();

                    popupProOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.removeEntry :
                                    if(mapListDataChild.containsKey(keyChosen)) {
                                        mapListDataChild.remove(keyChosen);
                                        allProName.remove(keyChosen);
                                        MainActivity.removeProject(keyChosen);
                                    }
                                    expListAdapter.notifyDataSetChanged();
                                    dbHandler.deleteProjectDb(keyChosen);
                                    return true;

                                case R.id.alterEntry:

                                    Project pro = null;
                                    for (Project p: MainActivity.getAllProjects()) {
                                        if (p.getName().equals(keyChosen))
                                            pro = p;
                                    }

                                    if(pro == null) {
                                        Toast.makeText(getBaseContext(), "Es ist leider ein Fehler aufgetreten", Toast.LENGTH_SHORT).show();
                                        Log.e(LOG_TAG, "Bearbeiten nicht möglich, kein passender Eintrag in Liste gefunden");
                                        return false;
                                    }


                                    dialogAlterPro = new Dialog(ProjectActivity.this);
                                    dialogAlterPro.setTitle("Projekt bearbeiten");
                                    dialogAlterPro.setContentView(R.layout.alter_project);

                                    final Project proFinal = pro;
                                    final EditText etProNameAlter = (EditText) dialogAlterPro.findViewById(R.id.etProNameAlter);
                                    Button btnSubmitAlter = (Button) dialogAlterPro.findViewById(R.id.btnSubmitProAlter);
                                    Button btnCancelAlter = (Button) dialogAlterPro.findViewById(R.id.btnCancelProAlter);

                                    etProNameAlter.setText(pro.getName());

                                    btnSubmitAlter.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            btnAlterSubmitClicked(etProNameAlter, proFinal, positionFinal, allProName, keyChosen, allProCreationDates, allProLastModifiedDates);
                                        }
                                    });

                                    btnCancelAlter.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogAlterPro.cancel();
                                        }
                                    });

                                    dialogAlterPro.show();
                                    return true;
                            }
                            return false;
                        }
                    });
                }

                return false;
            }
        });

        dbHandler = new DatabaseHandler(this);
        dbHandler.setDb(dbHandler.getWritableDatabase());
    }

    private void btnAlterSubmitClicked(EditText etProNameAlter, Project proFinal, int positionFinal, ArrayList<String> allProName, String keyChosen, ArrayList<String> allProCreationDates, ArrayList<String> allProLastModifiedDates) {
        String newProName = etProNameAlter.getText().toString();
        boolean isUnique = isProNameUnique(newProName, proFinal.getName());
        if(isUnique) {
            if(!newProName.equals("")) {
                proFinal.setName(newProName);
                MainActivity.getAllProjects().set(positionFinal, proFinal);
                allProName.set(positionFinal, newProName);
                mapListDataChild.remove(keyChosen);

                ArrayList<String> child = new ArrayList<String>();
                child.add("Erstellt am: " + allProCreationDates.get(positionFinal));
                child.add("Zuletzt geändert: " + allProLastModifiedDates.get(positionFinal));
                mapListDataChild.put(newProName, child);

                dbHandler.updateProjectDb(newProName, keyChosen);

                expListAdapter.notifyDataSetChanged();
                dialogAlterPro.dismiss();
            }
            else
                Toast.makeText(getBaseContext(), "Name darf nicht leer sein", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getBaseContext(), "Name bereits vorhanden", Toast.LENGTH_SHORT).show();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillHashMap(ArrayList<String> allProName, ArrayList<String> allProCreationDates,
                             ArrayList<String> allProLastModifiedDates, HashMap<String, ArrayList<String>> mapListDataChild) {
        for(int i = 0; i < allProName.size(); i++) {
            ArrayList<String> child = new ArrayList<String>();
            Iterator mapIterator = mapListDataChild.entrySet().iterator();

            child.add("Erstellt am: " + allProCreationDates.get(i));
            child.add("Zuletzt geändert: " + allProLastModifiedDates.get(i));

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
                proBtnSubmitClicked(v);
            }
        });

        Button btnCancel = (Button) dialogNewPro.findViewById(R.id.proBtnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proBtnCancelClicked(dialogNewPro);
            }
        });
        dialogNewPro.show();
    }

    public void proBtnSubmitClicked(View v) {
        String proName = this.proName.getText().toString();
        boolean isUnique = isProNameUnique(proName);

        if(isUnique) {
            if (proName != "") { // Bluetooth oder Ethernet
                // Log.d(LOG_TAG, Integer.toString(conType));
                dialogNewPro.dismiss();
                setResultToActivity(proName); // Variablen conType, strConName und address zur�ckliefern
            } else // nichts ausgew�hlt - sollte �berhaupt nicht vorkommen (als Absicherung wird es abgefangen)
                Toast.makeText(getApplicationContext(), "Es wurde nichts ausgew�hlt. ", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, getString(R.string.errorNameNotUnique), Toast.LENGTH_SHORT).show();
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
    public void proBtnCancelClicked(Dialog dialog) {
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

    private boolean isProNameUnique(String proName) {
        boolean isUnique = false;
        for(Project p : MainActivity.getAllProjects()) {
            if(p.getName().equals(proName)) {
                isUnique = false;
                break;
            } else
                isUnique = true;
        }
        if(MainActivity.getAllProjects().size() == 0)
            isUnique = true;
        return isUnique;
    }

    private boolean isProNameUnique(String proNameNew, String proNameOld) {
        boolean isUnique = false;
        ArrayList<String> proNames = new ArrayList<String>();
        for(Project p : MainActivity.getAllProjects()) {
            proNames.add(p.getName());
        }
        proNames.remove(proNameOld);

        for(String s : proNames) {
            if(s.equals(proNameNew)) {
                isUnique = false;
                break;
            }
            else
                isUnique = true;
        }
        if(proNames.size() == 0)
            isUnique = true;

        return isUnique;
    }



}