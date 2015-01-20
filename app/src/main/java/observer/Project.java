package observer;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import connection.IConnection;
import elements.BoolElement;
import elements.Element;
import elements.InputElement;
import elements.LedModel;
import elements.OutputElement;
import elements.SwitchModel;
import generic.CodeGenerator;
import generic.ImageAdapter;

public class Project extends Observable {


    private static int id=0;//ID für eindeutige Identifizierung und später für DB, wird im Konstruktor vergeben



    private String name; // Name sollte vom Benutzer im Nachhinein vergeben werden
    private int numberOfRows; // Anzahl von Elementen in einer Reihe
    private int numberOfLines; // Anzahl von Elementen untereinander (Anzahl von Zeilen)
    /*
    private ArrayList<IConnection> listAllCons;
    private IConnection currentConnection; // Je nach dem welchen Verbindungstyp Benutzer wählt BT oder Ethernet
    */
//    private ArrayList<Element> allElementModels;
    private Gui gui;
    private Db dbConnection; // TODO DB-Programmierung
    private final String LOG_TAG = "Project";

    private SwitchModel elementSwitch;
    private LedModel elementLed;
    private ImageAdapter imageAdapter;

    private HashMap<Integer, Element> mapAllViewModels; // Speichern aller Modelelemente mit ihrer Position als Key

    //  private SwitchView viewSwitch;

    //	private LedView viewLed;

    //Getter und Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name =name;
    }

    public Gui getGui() {
        return gui;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Integer, Element> getMapAllViewModels() {
        return mapAllViewModels;
    }

    public void setMapAllViewModels(HashMap<Integer, Element> mapAllViewModels) {
        this.mapAllViewModels = mapAllViewModels;
    }


    //Dinge wie Views, Imageadapter, usw. sollten nicht gespeichert werden, sondern neu erzeugt werden, da
    //sie auf dem aktuellen Context beruhen

    /*public ImageAdapter getImageAdapter() {
        return imageAdapter;
    }

    public void setImageAdapter(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }
*/
    public Project(Gui gui) {

//        allElementModels = new ArrayList<Element>();
        numberOfRows = 2;
        numberOfLines = 3;
        this.elementLed = new LedModel();
        this.elementSwitch = new SwitchModel();
        GridView view;
        this.gui = gui;
        this.mapAllViewModels = new HashMap<Integer, Element>();
        addToObservers(gui);
    }

    public Project(Gui gui, String name) {

//        allElementModels = new ArrayList<Element>();
        numberOfRows = 2;
        numberOfLines = 3;
        this.elementLed = new LedModel();
        this.elementSwitch = new SwitchModel();
        GridView view;
        this.gui =gui;
        this.name =name;
        this.mapAllViewModels = new HashMap<Integer, Element>();
        addToObservers(gui);
    }

    public Project(Gui gui, String name, int id, ImageAdapter imageAdapter) {

//        allElementModels = new ArrayList<Element>();
        numberOfRows = 2;
        numberOfLines = 3;
        this.elementLed = new LedModel();
        this.elementSwitch = new SwitchModel();
        GridView view;
        this.gui =gui;
        this.name =name;
        this.id=id;
        this.mapAllViewModels = new HashMap<Integer, Element>();
        this.imageAdapter = imageAdapter;
        addToObservers(gui); // Gui zur Liste der Observers hinzufügen - damit werden Updates an die Gui gesendet
    }

    public void setGui(Gui gui) {
        this.gui =gui;
    }

    public void addElement(int key, Element element) {
        mapAllViewModels.put(key, element);
    }

    public boolean removeElement(int key) {
        if(mapAllViewModels.containsKey(key)) {
            mapAllViewModels.remove(key);
            return true;
        }
        return false;
    }


    public Element getElementByName(String name) {
        Iterator iterator = mapAllViewModels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Element e = (Element) entry.getValue();
            if(e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }


    /**
     * Wenn eine View durch ihren Listener ein Event liefert, so wird diese Methode ausgef�hrt.
     * Als erstes wird durch die im Parameter mitgelieferte View das ausl�sende Element identifiziert.
     * Im Anschluss wird �ber Bluetooth oder Ethernet die Nachricht an den Arduino gesendet.
     * Ebenso wird mithilfe des Observer-Patterns die Gui aktualisiert, also z.B. das Feedbackelement Led auf High gesetzt.
     * @param v - View des das Event ausl�senden Elements
     */
    public void sendDataUpdateGui(View v, IConnection currentConnection, int position) {
        Element model = mapAllViewModels.get(position);
        Log.d(LOG_TAG, "Position: " + position);

        if(model != null)
            Log.d(LOG_TAG, "Modelname : " + model.getName());
        else
            Log.e(LOG_TAG, "Model ist  NULL");

        if(model instanceof BoolElement) {
            Log.d(LOG_TAG, "Model ist ein BoolElement");
            boolean curStatus = ((BoolElement) model).isStatusHigh();
            boolean newStatus = !curStatus;

            if(model.getIdentifier() != null) {
                String code = CodeGenerator.generateCodeToSend(newStatus, model.getIdentifier());
                Log.d(LOG_TAG, "Identifier: " + model.getIdentifier());

                // Element, welches Event ausgel�st hat, sollte im Normalfall ein InputElement sein
                if (model instanceof InputElement) { // sollte true sein - als Absicherung trotzdem abfragen
                    Log.d(LOG_TAG, "Model ist ein InputElement");
                    Log.d(LOG_TAG, "Senden an Arduino...");
                    ((InputElement) model).sendDataToArduino(currentConnection, code); // Daten an Arduino senden
                    Log.d(LOG_TAG, "Gesendet");


				/* TODO Status nur ändern, wenn Übertragung auch wirklich funktioniert hat:
				 * - entweder zuerst Status des Arduino-Elements abfragen,
				 * - oder über Arduino-Library sofort nach Aktualisieren true zur�cksenden,
				 *   über Connection dies empfangen und zur�ckgeben.
				 */
                    // �nderung des Status im Model
                    ((BoolElement) model).setStatusHigh(newStatus);

                    Iterator iterator = mapAllViewModels.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Log.d(LOG_TAG, "Größe der Map: " + String.valueOf(mapAllViewModels.size()));
                        Map.Entry entry = (Map.Entry) iterator.next();
                        Element currentElement = (Element) entry.getValue();

                        if (currentElement instanceof OutputElement) {
                            String identifierCurEl = currentElement.getIdentifier();
                            if(model.getIdentifier().equals(identifierCurEl)) {
                                // Dazugehöriges OutputElement gefunden
                                Log.d(LOG_TAG, "Verknüpftes Outputelement gefunden: " + currentElement.getName() + " Identifier: " + currentElement.getIdentifier());

                                Log.d(LOG_TAG, "Position des OutputElements: " + (Integer)entry.getKey());
                                notify(this, currentElement, (Integer) entry.getKey());
                            }
                        }
                    }
                } else
                    Log.e(LOG_TAG, "Error - Kein InputElement");
            } else
                Log.e(LOG_TAG, "Error - Kein Identifier gesetzt");
        } else //TODO else if (model instanceof PwmElement)
            Log.d(LOG_TAG, "Kein BoolElement");
    }


    public void addModelToMap(int position, Element model) {
        mapAllViewModels.put(position, model);
        Log.d(LOG_TAG, "Model wurde Map hinzugefügt");
        Log.d(LOG_TAG, "Größe der Map: " + mapAllViewModels.size());
    }



    /**
     * Verbindet ein Element mit der zugeh�rigen Zeichenkette, welche das Arduino-Programm verwendet
     * @param e Android-Element
     * @param identifier Identifikationsstring des Arduino-Elements
     */
    public void connectWith(Element e, String identifier) {
        // TODO Momentan wird Identifier in Element definiert --> Methode w�re unn�tig
    }

    /**
     * Listener-Klasse f�r die einzelnen Views.
     * Dieser Listener verweist auf die Methode sendDataUpdateGui.
     * @author Simon
     */
    //TODO Fehler
    private class ElementListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            //	sendDataUpdateGui(v); Geht nicht mehr weil keine Connection Instanz
        }
    }
}