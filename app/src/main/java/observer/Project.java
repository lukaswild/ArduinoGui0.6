package observer;

import android.util.Log;
import android.view.View;
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
import elements.PwmElement;
import elements.PwmInputModel;
import elements.PwmModel;
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
    private DateFormat dateFormatFormatted = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private Gui gui;
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
            return mapAllViewModels.get(key).getResource();
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

    public Project(Gui gui, String name, ImageAdapter imageAdapter) {

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

    public String getDateStringFormatted(Calendar date) {
        return dateFormatFormatted.format(date.getTime());
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

            if(model.getIdentifier() != null) {
                boolean bStatus = !newStatus;
                int statusInt = 0;
                String code = CodeGenerator.generateCodeToSend(bStatus, model.getIdentifier()); //////////////////////////
                Log.d(LOG_TAG, "Identifier: " + model.getIdentifier());

                // Element, welches Event ausgel�st hat, sollte im Normalfall ein InputElement sein
                if (model instanceof InputElement) { // sollte true sein - als Absicherung trotzdem abfragen
                    Log.d(LOG_TAG, "Model ist ein InputElement");
                    Log.d(LOG_TAG, "Senden an Arduino...");
                    if(bStatus)
                        statusInt = 1;
                    else
                        statusInt = 0;
                    ((InputElement) model).sendDataToArduino(currentConnection, code, statusInt); // Daten an Arduino senden
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

                                if (codeSuccessStr.contains("100")) {
                                    // �nderung des Status im Model
                                    if(model instanceof BoolElement && currentElement instanceof BoolElement) {
                                        ((BoolElement) model).setStatusHigh(newStatus);
                                        ((BoolElement)currentElement).setStatusHigh(newStatus);
                                        ((BoolElement)model).setResource(newStatus);
                                        ((BoolElement)currentElement).setResource(newStatus);
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
        }
        else if (model instanceof PwmElement){

            if (model.getIdentifier() !=null){

                //Die Idee ist, dass der PWM Wert für den Arduino immer gleich ausschaut
                //wird jetzt eimal der Wert 34, und einmal der Wert 128 gesendet, dann unterscheiden sich
                //die Werte anhand der Stellen. Das macht die Überprüfung am Arduino schwieriger.
                //Besser ist 034, oder z.B.: 009, so ist der PWM Wert immer 3 Stellen lang.

                String pwm ="" ;
                if (ziffernrekursiv(((PwmElement) model).getCurrentPwm())==1){
                    pwm= "00"+Integer.toString(((PwmElement) model).getCurrentPwm());
                }
                else if (ziffernrekursiv(((PwmElement) model).getCurrentPwm())==2){
                    pwm="0"+Integer.toString(((PwmElement) model).getCurrentPwm());
                }
                else{
                    pwm=Integer.toString(((PwmElement) model).getCurrentPwm());
                }

                String code = CodeGenerator.generateCodeToSend(pwm,model.getIdentifier());
                Log.d(LOG_TAG, "Identifier: " + model.getIdentifier());

                if (model instanceof InputElement){
                    Log.d(LOG_TAG, "Model ist ein InputElement");
                    Log.d(LOG_TAG, "Senden an Arduino...");
                    ((InputElement) model).sendDataToArduino(currentConnection, code, Integer.parseInt(pwm));
                    Log.d(LOG_TAG, "Gesendet");

                    // Überprüfung, ob Erfolgscode 100 von Arduino ankommt. Wenn ja --> Gui aktualisieren
                    String codeSuccessStr =  BTConnection.receiveData();
                    Log.d(LOG_TAG, codeSuccessStr);
                    Log.d(LOG_TAG, BTConnection.receiveData());
                    Log.d(LOG_TAG, BTConnection.receiveData());

                    Iterator iterator2 = mapAllViewModels.entrySet().iterator();
                    while (iterator2.hasNext()) {
                        Log.d(LOG_TAG, "Größe der Map: " + String.valueOf(mapAllViewModels.size()));
                        Map.Entry entry = (Map.Entry) iterator2.next();
                        Element currentElement = (Element) entry.getValue();

                        if (currentElement instanceof OutputElement) {
                            String identifierCurEl = currentElement.getIdentifier();
                            if(model.getIdentifier().equals(identifierCurEl)) {
                                // Dazugehöriges OutputElement gefunden
                                Log.d(LOG_TAG, "Verknüpftes Outputelement gefunden: " + currentElement.getName() + " Identifier: " + currentElement.getIdentifier());
                                Log.d(LOG_TAG, "Position des OutputElements: " + entry.getKey());

                                codeSuccessStr.trim();
                                Log.d(LOG_TAG, "codeSuccessStr: " + codeSuccessStr);

                                if (codeSuccessStr.contains("W")) {
                                    // �nderung des Status im Model
                                    String receive ="";
                                    int receiveInt=0;

                                     if (codeSuccessStr.charAt(0)=='1'){

                                         receive +=codeSuccessStr.charAt(4);
                                         receive +=codeSuccessStr.charAt(5);
                                         receive +=codeSuccessStr.charAt(6);
                                     }
                                    else if(codeSuccessStr.charAt(0)=='W'){

                                         String s="";
                                         for (int i =1;i<codeSuccessStr.length();i++){
                                             s+=codeSuccessStr.charAt(i);
                                             //receive +=Integer.parseInt((String)codeSuccessStr.charAt(i));

                                         }
                                         receiveInt=Integer.parseInt(s);

                                    }
                                    //receiveInt=Integer.parseInt(receive);
                                    Log.d(LOG_TAG, "receiveInt " + receiveInt);


                                    if(model instanceof PwmElement && currentElement instanceof PwmElement) {

                                        ((PwmElement)currentElement).setCurrentPwm(receiveInt);
                                        ((PwmElement)currentElement).refreshRes();
                                        ((PwmElement)model).setCurrentPwm(receiveInt);
                                        ((PwmElement)model).refreshRes();
                                        Log.d(LOG_TAG, "im instanceof");
                                        notify(this, currentElement, position, (Integer) entry.getKey());
                                        imageAdapter.notifyDataSetChanged();

                                    }
                                }
                            }
                        }
                    }


                }




            }

        }
        Log.d(LOG_TAG, "Kein BoolElement");

    }


    public void addModelToMap(int position, Element model) {
        mapAllViewModels.put(position, model);
        setLastModifiedDate(Calendar.getInstance());
        Log.d(LOG_TAG, "Model wurde Map hinzugefügt");
        Log.d(LOG_TAG, "Größe der Map: " + mapAllViewModels.size());
    }


    private void setMap(){
        for (int i=0;i<40;i++){
            mapAllViewModels.put(i,new EmptyElement());
        }
    }
    public static int ziffernrekursiv(int zahl) {
        return (zahl>0)? 1+ziffernrekursiv(zahl/10) : 0;
    }

}