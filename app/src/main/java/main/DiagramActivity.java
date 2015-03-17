package main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.arduinogui.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import elements.Element;
import observer.Project;


public class DiagramActivity extends Activity {

    private static final String LOG_TAG = "DiagramActivity";
    private GraphView graphView;
    private ArrayList<Integer> timeRecord;
    private ArrayList<Integer> dataRecord;
    private String elementClass = "";
    private String elementIdentifier = "";
    private Stack<Integer> graphColorStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);

        initialiseGraphColorStack();


        Intent parentIntent = getIntent();
        getIntentExtras(parentIntent);

        DataPoint[] dataPoints = getDataPointsArr(timeRecord, dataRecord);

        graphView = (GraphView) findViewById(R.id.graph);
        if(timeRecord.isEmpty())
            graphView.setTitle("Keine Daten vorhanden");
        else {
            graphView.setTitle("Verlauf");
            graphView.getLegendRenderer().setVisible(true); // Anzeigen der Legende
            graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP); // TODO wenn möglich Legende unterhalb von Graph
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
            series.setColor(graphColorStack.pop());

            graphView.addSeries(series);
            graphView.setMinimumHeight(graphView.getHeight());

            StringBuilder titleSeries = new StringBuilder();
            if (elementClass.equals(getString(R.string.classLedModel)))
                titleSeries.append("Led ");
            else if (elementClass.equals(getString(R.string.classSwitchModel)))
                titleSeries.append("Schalter ");
            titleSeries.append(elementIdentifier);
            series.setTitle(titleSeries.toString());

        }
        // TODO Graph sollte zoombar sein
    }


    private void initialiseGraphColorStack() {
        graphColorStack = new Stack<Integer>();
        graphColorStack.push(Color.LTGRAY);
        graphColorStack.push(Color.DKGRAY);
        graphColorStack.push(Color.WHITE);
        graphColorStack.push(Color.MAGENTA);
        graphColorStack.push(Color.CYAN);
        graphColorStack.push(Color.BLACK);
        graphColorStack.push(Color.YELLOW);
        graphColorStack.push(Color.RED);
        graphColorStack.push(Color.GRAY);
        graphColorStack.push(Color.GREEN);
        graphColorStack.push(Color.BLUE);
    }

    private DataPoint[] getDataPointsArr(ArrayList<Integer> timeRecord, ArrayList<Integer> dataRecord) {
        DataPoint[] dataPoints = new DataPoint[timeRecord.size()];
        for(int i = 0; i < dataPoints.length; i++) {
            dataPoints[i] = new DataPoint(timeRecord.get(i), dataRecord.get(i));
        }
        return dataPoints;
    }

    private void getIntentExtras(Intent parentIntent) {
        if(parentIntent.getExtras().containsKey("timeRecord"))
            timeRecord = parentIntent.getExtras().getIntegerArrayList("timeRecord");
        if(parentIntent.getExtras().containsKey("dataRecord"))
            dataRecord = parentIntent.getExtras().getIntegerArrayList("dataRecord");
        if(parentIntent.getExtras().containsKey("elementClass"))
            elementClass = parentIntent.getExtras().getString("elementClass");
        if(parentIntent.getExtras().containsKey("elementIdentifier"))
            elementIdentifier = parentIntent.getExtras().getString("elementIdentifier");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_diagram, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addDataPoints(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_datapoint);
        dialog.setTitle("Datenreihe auswählen");
        ListView listView = (ListView) dialog.findViewById(R.id.lvAvailableElements);
        Button btnCancelAddDataPoint = (Button) dialog.findViewById(R.id.btnCancelAddDataPoint);
        Project currentProject = MainActivity.getCurrentProject();
        final ArrayList<Element> allElements = new ArrayList<Element>();

        Iterator iterator = currentProject.getMapAllViewModels().entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Element element = (Element) entry.getValue();
            if(!(element.getIdentifier() == null)) {
                allElements.add(element);

            }
        }

        String[] listData = new String[allElements.size()];
        for(int i = 0; i < allElements.size(); i++) {
            Element e = allElements.get(i);
            listData[i] = e.getKind() + " " + e.getIdentifier();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!graphColorStack.isEmpty()) {

                    Element elementAtPos = allElements.get(position);
                    DataPoint[] dataPointsToAdd = getDataPointsArr(elementAtPos.getTimeRecord(), elementAtPos.getDataRecord());
                    LineGraphSeries<DataPoint> seriesToAdd = new LineGraphSeries<DataPoint>(dataPointsToAdd);
                    StringBuilder titleSeries = new StringBuilder();
                    if(elementAtPos.getKind().equals("Switch"))
                        titleSeries.append("Schalter ");
                    else if (elementAtPos.getKind().equals("Led"))
                        titleSeries.append("Led ");
                    else if (elementAtPos.getKind().equals("Button"))
                        titleSeries.append("Button ");
                    else if (elementAtPos.getKind().equals("Adc-Input"))
                        titleSeries.append("ADC-Regler ");
                    else if (elementAtPos.getKind().equals("Adc-Element"))
                        titleSeries.append("ADC-Anzeige ");
                    titleSeries.append(elementAtPos.getIdentifier());
                    seriesToAdd.setTitle(titleSeries.toString());
                    seriesToAdd.setColor(graphColorStack.pop());
                    graphView.addSeries(seriesToAdd);
                } else
                    Toast.makeText(getBaseContext(), "Maximale Anzahl an Daten erreicht", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        btnCancelAddDataPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


    public void removeDataPoints(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.remove_datapoint);
        dialog.setTitle("Datenreihe entfernen");
        ListView listView = (ListView) dialog.findViewById(R.id.lvAddedElements);
        Button btnCancelRemoveDataPoint = (Button) dialog.findViewById(R.id.btnCancelRemoveDataPoint);
        final ArrayList<Series> allAddedSeries = new ArrayList<Series>();

        for(int i = 0; i < graphView.getSeries().size(); i++) {
            allAddedSeries.add(graphView.getSeries().get(i));
        }

        String[] values = new String[allAddedSeries.size()];
        for(int i = 0; i < allAddedSeries.size(); i++) {
            values[i] = allAddedSeries.get(i).getTitle();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Series seriesToRemove = allAddedSeries.get(position);
                graphView.removeSeries(seriesToRemove);
                graphColorStack.push(seriesToRemove.getColor());
                dialog.dismiss();
            }
        });

        btnCancelRemoveDataPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}