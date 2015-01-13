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
import elements.SwitchModel;
import generic.CodeGenerator;
import generic.ComObject;
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
    private Db dbConnection; // TODO DB-Programmierung erfolgt später
    private final String LOG_TAG = "Project";

    private SwitchModel elementSwitch;
    private LedModel elementLed;
    private ImageAdapter imageAdapter;

    private HashMap<Integer, Element> mapAllViewModels;

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

    //Dineg wie Views, Imageadapter, usw. sollten nicht gespeichert werden, sondenr neu erzeugt werden, da
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
    }
    public Project(Gui gui, String name, int id) {

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
    }

    public void setGui(Gui gui) {
        this.gui =gui;
    }

    /*
    public ArrayList<IConnection> getListAllCons() {
        return listAllCons;
    }

    public void setListAllCons(ArrayList<IConnection> listAllCons) {
        this.listAllCons = listAllCons;
    }

    public IConnection getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(IConnection currentConnection) {
        this.currentConnection = currentConnection;
    }
    */

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


//    public  void  addElement(Element element) {
//        allElementModels.add(element);
//    }

//    public  boolean removeElement(Element element){
//        if (allElementModels.equals(element)) {
//            allElementModels.remove(element);
//            return true;
//        }
//        else return false;
//    }

//    public ArrayList<Element> getAllElements() {
//        return allElementModels;
//    }

//    public Element getElementByName(String Name){
//        for (Element z : allElementModels){
//            if (z.getName().equals(Name)){
//                return z;
//            }
//        }
//        return  null;
//    }


    public Element getElementByName(String name) {
        Iterator iterator = mapAllViewModels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Element e = (Element) entry.getValue();
            if(e.getName().equals(name)) {
                return e;
            }
            iterator.remove();
        }
        return null;
    }


//    public void setAllElements(ArrayList<Element> allElements) {
//        this.allElementModels = allElements;
//    }

    /**
     * Wenn eine View durch ihren Listener ein Event liefert, so wird diese Methode ausgef�hrt.
     * Als erstes wird durch die im Parameter mitgelieferte View das ausl�sende Element identifiziert.
     * Im Anschluss wird �ber Bluetooth oder Ethernet die Nachricht an den Arduino gesendet.
     * Ebenso wird mithilfe des Observer-Patterns die Gui aktualisiert, also z.B. das Feedbackelement Led auf High gesetzt.
     * @param v - View des das Event ausl�senden Elements
     */
    public void sendDataUpdateGui(View v, IConnection currentConnection, int position) {
        Log.d(LOG_TAG, "In Methode sendDataUpdateGui");
//        Element model = (Element) v.getTag(); // zugeh�rige Modelklasse holen, kann nur ein Element sein
        Element model = mapAllViewModels.get(position);

        if(model == null)
            Log.e(LOG_TAG, "Model ist NULL");

        if(model instanceof BoolElement) {
            Log.d(LOG_TAG, "Model ist ein BoolElement");
            boolean curStatus = ((BoolElement) model).isStatusHigh();
            boolean newStatus = !curStatus;

            String code = CodeGenerator.generateCodeToSend(newStatus, model.getIdentifier());
            Log.d(LOG_TAG, "Identifier: " + model.getIdentifier());

            // Element, welches Event ausgel�st hat, sollte im Normalfall ein InputElement sein
            if(model instanceof InputElement) { // sollte true sein - als Absicherung
                Log.d(LOG_TAG, "Model ist ein InputElement");
                Log.d(LOG_TAG, "Senden an Arduino...");
                ((InputElement)model).sendDataToArduino(currentConnection, code);
                Log.d(LOG_TAG, "Gesendet");

				/* TODO Status nur �ndern, wenn �bertragung auch wirklich funktioniert hat:
				 * - entweder zuerst Status des Arduino-Elements abfragen,
				 * - oder über Arduino-Library sofort nach Aktualisieren true zur�cksenden,
				 *   über Connection dies empfangen und zur�ckgeben.
				 */

                // �nderung des Status im Model
                ((BoolElement) model).setStatusHigh(newStatus);

                // änderung des Status des InputElements in Klasse Gui
                Log.d(LOG_TAG, "Erzeugen eines ComObjects");
                ComObject comObject = new ComObject(v, newStatus);
                Log.d(LOG_TAG, "Gui benachrichtigen und aktualisieren...");
                notify(comObject);
                Log.d(LOG_TAG, "Gui aktualisiert");


                ///////// TODO Ersetzen durch HashMap - Iterator unten
                // änderung aller dazugehörigen OutputElemente
//                for(Element e : allElementModels) {
//                    if(e instanceof OutputElement) {
//                        if(e.getIdentifier().equals(model.getIdentifier())) {
//                            // Zugeh�riges OutputElement, z.B. Led, wurde gefunden
//
//                            ArrayList<View> allViews = gui.getAllViews();
//                            for(View view : allViews) {
//                                //if(((SuperView)view).getName().equals(model.getName())) {
//
//
//                                // Zugeh�rige View gefunden
//                                // View updaten
//                                //	comObject = new ComObject(view, newStatus);
//                                //	notify(comObject);
//                            }
//                        }
//                    }
//                }

                Iterator iterator = mapAllViewModels.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Log.d(LOG_TAG, entry.getKey() + " = " + ((Element)(entry.getValue())).getName());
                    iterator.remove();
                }


            } else
                Log.e(LOG_TAG, "Error - Kein InputElement");

        } else {
            Log.e(LOG_TAG, "Error - Kein BoolElement!");
        }
    } //TODO else if (model instanceof PwmElement)




    /**
     * Verbindet ein Element mit der zugeh�rigen Zeichenkette, welche das Arduino-Programm verwendet
     * @param e Android-Element
     * @param identifier Identifikationsstring des Arduino-Elements
     */
    public void connectWith(Element e, String identifier) {
        // TODO Momentan wird Identifier in Element definiert --> Methode w�re unn�tig
    }

    public void addModelToMap(int position, Element model) {
        mapAllViewModels.put(position, model);
    }

    /**
     * Listener-Klasse f�r die einzelnen Views.
     * Dieser Listener verweist auf die Methode sendDataUpdateGui.
     * @author Simon
     *
     */
    //TODO Fehler
    private class ElementListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            //	sendDataUpdateGui(v); Geht nicht mehr weil keine Connection Instanz
        }
    }


}
