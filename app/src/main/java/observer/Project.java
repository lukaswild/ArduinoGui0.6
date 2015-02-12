package observer;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.example.arduinogui.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import connection.BTConnection;
import connection.IConnection;
import elements.BoolElement;
import elements.Element;
import elements.EmptyElement;
import elements.InputElement;
import elements.LedModel;
import elements.OutputElement;
import elements.SwitchModel;
import generic.CodeGenerator;
import generic.ImageAdapter;

public class Project extends Observable {
    private int id = 0;//ID für eindeutige Identifizierung und später für DB, wird im Konstruktor vergeben
    private static int count = 0;

    private String name; // Name sollte vom Benutzer im Nachhinein vergeben werden
    private int numberOfRows; // Anzahl von Elementen in einer Reihe
    private int numberOfLines; // Anzahl von Elementen untereinander (Anzahl von Zeilen)

    private Calendar creationDate = Calendar.getInstance();
    private Calendar lastModifiedDate;

    private Calendar lastOpenedDate;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");

    private Gui gui;
    private Db dbConnection; // TODO DB-Programmierung
    private final String LOG_TAG = "Project";

    private SwitchModel elementSwitch;
    private LedModel elementLed;
    private ImageAdapter imageAdapter;
    private HashMap<Integer, Element> mapAllViewModels; // Speichern aller Modelelemente mit ihrer Position als Key


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

    public int getRessourceFromMap(int key){
        //Die ID der Elemnte ist eine aufsteigende Nummer. Deshalb ist die Size gleich der höchsten nummer
        if (mapAllViewModels.containsKey(key)){
            return mapAllViewModels.get(key).getRessource();
        }
        else {
            return R.drawable.add1;
        }
    }
    public Element getElementFromMap(int key){
        //Die ID der Elemnte ist eine aufsteigende Nummer. Deshalb ist die Size gleich der höchsten nummer
        if (mapAllViewModels.containsKey(key)){
            return mapAllViewModels.get(key);
        }
        else {
           return null;
        }
    }
    public Calendar getCreationDate() {
        return creationDate;
    }

    public Calendar getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Calendar lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Calendar getLastOpenedDate() {
        return lastOpenedDate;
    }

    public void setLastOpenedDate(Calendar lastOpenedDate) {
        this.lastOpenedDate = lastOpenedDate;
    }

    public void setGui(Gui gui) {
        this.gui =gui;
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
        setMap();
        lastModifiedDate = creationDate;
        lastOpenedDate = creationDate;
        id = ++count;
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
        setMap();

        lastModifiedDate = creationDate;
        lastOpenedDate = creationDate;
        id = ++count;
    }

    public Project(Gui gui, String name, ImageAdapter imageAdapter) { // TODO neuen Konstruktor schreiben, mit allen Elementen übergeben wie in DB

//        allElementModels = new ArrayList<Element>();
        numberOfRows = 2;
        numberOfLines = 3;
        this.elementLed = new LedModel();
        this.elementSwitch = new SwitchModel();
        GridView view;
        this.gui = gui;
        this.name =name;
        this.id=id;
        this.mapAllViewModels = new HashMap<Integer, Element>();
        this.imageAdapter = imageAdapter;
        addToObservers(gui); // Gui zur Liste der Observers hinzufügen - damit werden Updates an die Gui gesendet
        setMap();
        lastModifiedDate = creationDate;
        lastOpenedDate = creationDate;
        id = ++count;
    }

    public Project(Gui gui, int internalId, String name, Calendar creationDate, Calendar lastModifiedDate, Calendar lastOpenedDate, HashMap<Integer, Element> mapAllViewModels) {
        this.id = internalId;
        this.name = name;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
        this.lastOpenedDate = lastOpenedDate;
        this.mapAllViewModels = mapAllViewModels;
        this.imageAdapter = imageAdapter;
        this.gui = gui;
        addToObservers(gui); // Gui zur Liste der Observers hinzufügen - damit werden Updates an die Gui gesendet
        numberOfRows = 2;
        numberOfLines = 3;
    }




    public void addElement(int key, Element element) {
        mapAllViewModels.put(key, element);
        setLastModifiedDate(Calendar.getInstance());
    }

    public boolean removeElement(int key) {
        if(mapAllViewModels.containsKey(key)) {
            mapAllViewModels.remove(key);
            setLastModifiedDate(Calendar.getInstance());
            return true;
        }
        return false;
    }



    public Element getElementByName(String name) {
        Iterator iterator = mapAllViewModels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Element e = (Element) entry.getValue();
            if (e.getName() != null) {
                if (e.getName().equals(name)) {
                    return e;
                }
            }
        }
        return null;
    }


    public String getDateString(Calendar date) {
        return dateFormat.format(date.getTime());
    }

    /**
     * Wenn eine View durch ihren Listener ein Event liefert, so wird diese Methode ausgef�hrt.
     * Als erstes wird durch die im Parameter mitgelieferte View das ausl�sende Element identifiziert.
     * Im Anschluss wird �ber Bluetooth oder Ethernet die Nachricht an den Arduino gesendet.
     * Ebenso wird mithilfe des Observer-Patterns die Gui aktualisiert, also z.B. das Feedbackelement Led auf High gesetzt.
     * @param v - View des das Event ausl�senden Elements
     */
    public void sendDataUpdateGui(View v, IConnection currentConnection, int position, boolean newStatus) {
        Element model = mapAllViewModels.get(position);
        Log.d(LOG_TAG, "Position: " + position);

        if(model != null)
            Log.d(LOG_TAG, "Modelname : " + model.getName());
        else
            Log.e(LOG_TAG, "Model ist  NULL");

        if(model instanceof BoolElement) {
            Log.d(LOG_TAG, "Model ist ein BoolElement");

//            boolean curStatus = ((BoolElement) model).isStatusHigh();
//            boolean newStatus = !curStatus; // TODO wenn Schalter betätigt wird, bevor Elemente verknüpft sind, so wird der falsche Status gesendet, da der Status einfach immer negiert wird

            if(model.getIdentifier() != null) {
                String code = CodeGenerator.generateCodeToSend(!newStatus, model.getIdentifier()); //////////////////////////
                Log.d(LOG_TAG, "Identifier: " + model.getIdentifier());

                // Element, welches Event ausgel�st hat, sollte im Normalfall ein InputElement sein
                if (model instanceof InputElement) { // sollte true sein - als Absicherung trotzdem abfragen
                    Log.d(LOG_TAG, "Model ist ein InputElement");
                    Log.d(LOG_TAG, "Senden an Arduino...");
                    ((InputElement) model).sendDataToArduino(currentConnection, code); // Daten an Arduino senden
                    Log.d(LOG_TAG, "Gesendet");

                    // Überprüfung, ob Erfolgscode 100 von Arduino ankommt. Wenn ja --> Gui aktualisieren
                    String codeSuccessStr =  BTConnection.receiveData();
                    Log.d(LOG_TAG, codeSuccessStr);
                    Log.d(LOG_TAG, BTConnection.receiveData());
                    Log.d(LOG_TAG, BTConnection.receiveData());

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
                                Log.d(LOG_TAG, "Position des OutputElements: " + entry.getKey());

                                codeSuccessStr.trim();
                                Log.d(LOG_TAG, "codeSuccessStr: " + codeSuccessStr);

                                if (codeSuccessStr.equals("100")) {
                                    // �nderung des Status im Model
                                    if(model instanceof BoolElement && currentElement instanceof BoolElement) {
                                        ((BoolElement) model).setStatusHigh(newStatus);
                                        ((BoolElement)currentElement).setStatusHigh(newStatus);
                                        model.setRessource(newStatus);
                                        currentElement.setRessource(newStatus);
                                        notify(this, currentElement, position, (Integer) entry.getKey());
                                    }
                                }
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
        setLastModifiedDate(Calendar.getInstance());
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

    //TODO Fehler
    private class ElementListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            //	sendDataUpdateGui(v); Geht nicht mehr weil keine Connection Instanz
        }
    }

    private void setMap(){
        for (int i=0;i<40;i++){
            mapAllViewModels.put(i,new EmptyElement());
        }
    }


}