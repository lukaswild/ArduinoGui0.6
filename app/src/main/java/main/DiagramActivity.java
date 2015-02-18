package main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.arduinogui.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class DiagramActivity extends Activity {

    private static final String LOG_TAG = "DiagramActivity";
    GraphView graphView;
    ArrayList<Integer> timeRecord;
    ArrayList<Integer> dataRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);

        Intent parentIntent = getIntent();
        if(parentIntent.getExtras().containsKey("timeRecord"))
            timeRecord = (ArrayList<Integer>) parentIntent.getExtras().get("timeRecord");
        if(parentIntent.getExtras().containsKey("dataRecord"))
            dataRecord = (ArrayList<Integer>) parentIntent.getExtras().get("dataRecord");

        DataPoint[] dataPoints = new DataPoint[timeRecord.size()];
        for(int i = 0; i < dataPoints.length; i++) {
            dataPoints[i] = new DataPoint(timeRecord.get(i), dataRecord.get(i));
        }

        graphView = (GraphView) findViewById(R.id.graph);
        graphView.setTitle("Verlauf");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        graphView.addSeries(series);

        // TODO Graph sollte zoombar sein

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
}
