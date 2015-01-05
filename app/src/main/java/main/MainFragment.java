package main;

import android.app.Fragment;
import android.os.Bundle;

import observer.Project;

/**
 * Created by Lukas on 02.01.2015.
 */

//Die Aufgabe der Klasse ist sich beim drehen des Bildschirms das aktuelle Projekt zu merken, damit die Gui nicht neu gezeichnet wird.
public class MainFragment extends Fragment {


    private Project data;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public void setData(Project data) {
        this.data = data;
    }

    public Project getData() {
        return data;
    }

}
