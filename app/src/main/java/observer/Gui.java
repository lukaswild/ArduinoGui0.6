package observer;

import java.util.ArrayList;


import elements.BoolElement;
import elements.ComObject;
import elements.Element;
import elements.OutputElement;
import elements.SwitchModel;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewDebug.IntToString;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.arduinogui.R;


/* 
 * Bei extends View k�nnte Klasse Gui in Activity sofort als contentView gesetzt und somit angezeigt werden. 
 */
public class Gui extends View implements IObserver {


	private final String LOG_TAG = "Gui";
	GridLayout gridLayout; 
	ArrayList<View> allViews = new ArrayList<View>();



    private GridView mgridView;


	/**
	 * ctor
	 * @param context
	 * @param numberOfRows - Anzahl Elemente in der Breite 
	 */
	public Gui(Context context, int numberOfRows,GridView gridview) {
		super(context);


		if(numberOfRows <= 0)
			numberOfRows = 3;

        mgridView=gridview;

	}

    //getter für gridview
    public GridView getGridView() {
        return mgridView;
    }

	public ArrayList<View> getAllViews() {
		return allViews;
	}


	public void setAllViews(ArrayList<View> allViews) {
		this.allViews = allViews;
	}



	/**
	 * Herausfiltern des zu aktualisierenden Elements aus der Liste mit allen Elementen und �ndern des Status 
	 * 
	 * Es m�ssen insgesamt 2 Elemente aktualisiert werden: das, welches das Event ausgel�st hat (Schalterstellung aktualisieren)
	 * und das, welches das ausl�sende anzeigt (Led). --> Ausl�sende Element ist bereits bekannt durch ComObject, anzeigende 
	 * Element muss entweder gesucht oder auch mithilfe von ComObject der Methode �bergeben werden. 
	 */
	@Override
	public void update(Observable senderClass, ComObject comObject) {

		if(senderClass instanceof Project) { // senderClass sollte von Klasse Project sein 
			Project project = (Project) senderClass;
			ArrayList<Element> allModelElements = project.getAllElements(); // Hole alle Modelelemente
			String newStatusString = comObject.getStatus();
            //TODO Fehler
            //int modelIndex =0;
			int modelIndex = allModelElements.indexOf(comObject.getView());

			Element modelToUpdate = allModelElements.get(modelIndex); // InputElement, welches das Event ausgelöst hat (z.B. Switch)
			
			

			// TODO vorerst nur f�r BoolElement - PwmElement sp�ter

			boolean newStatus = false;
			if(newStatusString.equals("1"))
				newStatus = true;

			// Setzen der neuen Status im Model
			((BoolElement)modelToUpdate).setStatusHigh(newStatus);

			
		} else
			Log.e(LOG_TAG, "Allgemeiner Fehler - Keine Instanz von Project hat Update ausgel�st"); 

	}
	



}
