package observer;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import connection.IConnection;
import elements.Element;
import elements.LedModel;
import elements.SwitchModel;
import generic.ComObject;
import generic.ImageAdapter;


/* 
 * Bei extends View k�nnte Klasse Gui in Activity sofort als contentView gesetzt und somit angezeigt werden. 
 */
public class Gui extends View implements IObserver {

    private final String LOG_TAG = "Gui";
    private GridView gridView;
    public static HashMap<Integer, String> elementIdentifier = new HashMap<Integer, String>();
    private final String ELEMENT_NAME = "element";


    /**
     * ctor
     * @param context
     * @param numberOfRows - Anzahl Elemente in der Breite
     */
    public Gui(Context context, int numberOfRows,GridView gridView) {
        super(context);

        if(numberOfRows <= 0)
            numberOfRows = 3;

        this.gridView = gridView;
    }

    //getter für gridview
    public GridView getGridView() {
        return gridView;
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
//			Project project = (Project) senderClass;
////			ArrayList<Element> allModelElements = project.getAllElements(); // Hole alle Modelelemente
//            HashMap<Integer, Element> allViewModels = project.getMapAllViewModels();
//			String newStatusString = comObject.getStatus();
//            //TODO Anpassung wegen neuer HashMap
//            //int modelIndex =0;
//			int modelIndex = allModelElements.indexOf(comObject.getView());
//
//			Element modelToUpdate = allModelElements.get(modelIndex); // InputElement, welches das Event ausgelöst hat (z.B. Switch)
//
//
//			// TODO vorerst nur f�r BoolElement - PwmElement sp�ter
//
//			boolean newStatus = false;
//			if(newStatusString.equals("1"))
//				newStatus = true;
//
//			// Setzen der neuen Status im Model
//			((BoolElement)modelToUpdate).setStatusHigh(newStatus);


        } else
            Log.e(LOG_TAG, "Allgemeiner Fehler - Keine Instanz von Project hat Update ausgel�st");

    }

    // @Override
    public void update(Observable senderClass, Element modelToUpdate, int position) {
        Log.d(LOG_TAG, "Updaten der Gui...");
        View child = gridView.getChildAt(position);
        //  if(model instanceof LedModel) {
        ImageAdapter imageAdapter =  (ImageAdapter) gridView.getAdapter();
        Integer o = (Integer) imageAdapter.getItem(position);
        Log.d(LOG_TAG, o.getClass().toString());

        if(modelToUpdate instanceof LedModel) {
            if((Integer)imageAdapter.getItem(position) == R.drawable.lamp_off)
                imageAdapter.Update(R.drawable.lamp_on, position);
            else
                imageAdapter.Update(R.drawable.lamp_off, position);
        }

        imageAdapter.notifyDataSetChanged();



        //    }

    }




    /* TODO Bei Hinzufügen eines neuen OutputElements bzw. Identifiers Stellung des Schalters überprüfen, da wenn Schalter bereits ein ist das Element auch sofort ein sein soll
     *  --> Einfach Status des realen Elements am Arduino abfragen und unsere Elemene auf diese Status setzen
     */
    public void initializeUI(final Project project, final ImageAdapter imgadapt, final IConnection currentConnection) {

        //Löscht zuerst einmal den Inhalt von Gridview
        project.getGui().getGridView().clearAnimation();
        project.getGui().getGridView().setAdapter(imgadapt);
        project.getGui().getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                switch (imgadapt.getItemInt(position)) {
                    case R.drawable.switch_off:
                        imgadapt.Update(R.drawable.switch_on,position);
                        imgadapt.notifyDataSetInvalidated();
                        Log.d(LOG_TAG, "Switch off: Senden an Arduino und Updaten der Gui...");
                        project.sendDataUpdateGui(v, currentConnection, position);
                        break;

                    case R.drawable.switch_on:
                        imgadapt.Update(R.drawable.switch_off,position);
                        imgadapt.notifyDataSetInvalidated();
                        Log.d(LOG_TAG, "Switch on: Senden an Arduino und Updaten der Gui...");
                        project.sendDataUpdateGui(v, currentConnection, position);
                        break;

                    //Button funktioniert zurzeit gleich wie ein switch, -> schlecht, besser alle click im onTouchListener realisieren,
                    //es kann auf drücken, bzw. loslassen der views geschaut werden, funktioniert derzeit noch nicht
                    //github comment
//                    case R.drawable.button_off:
//                        imgadapt.Update(R.drawable.button_on,position);
//                        imgadapt.notifyDataSetInvalidated();
//                        project.sendDataUpdateGui(v,CurrentConnection, position);
//                        break;
//                        TODO ToggleButton
//                    case R.drawable.button_on:
//                        imgadapt.Update(R.drawable.button_off,position);
//                        imgadapt.notifyDataSetInvalidated();
//
//                        //Update an GUI funktioniert noch nicht
//                        project.sendDataUpdateGui(v,CurrentConnection, position);
//                        break;


                    case R.drawable.lamp_off:

                        Toast.makeText(getContext(), "Element " + position + " ist kein Eingabelement!", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        // Toast.makeText(getBaseContext(), "Fehler bei pos/" + position, Toast.LENGTH_SHORT).show();
//                        for(Element e : CurrentProject.getAllElements()) {
//                            Toast.makeText(getBaseContext(), e.getName(), Toast.LENGTH_SHORT).show();
//                        }

//                        Iterator iterator = CurrentProject.getMapAllViewModels().entrySet().iterator();
                        Iterator iterator = project.getMapAllViewModels().entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            Toast.makeText(getContext(), ((Element)(entry.getValue())).getName(), Toast.LENGTH_SHORT).show();
                            iterator.remove();
                        }
                        break;
                }
            }
        });

  /*      gui.getGridView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                MyImageView view = (MyImageView) v;

                if ((event.getAction() == MotionEvent.ACTION_DOWN) && (int) v.getId() == (int) R.drawable.switch_off) {
                    imgadapt.Update(R.drawable.switch_on,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }
                if ((event.getAction() == MotionEvent.ACTION_DOWN) && (int) v.getId() == (int) R.drawable.switch_on) {
                    imgadapt.Update(R.drawable.switch_off,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }
                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
                    imgadapt.Update(R.drawable.switch_on,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }
                if ((event.getAction() == MotionEvent.ACTION_UP) && (int) v.getId() == (int) R.drawable.button_off) {
                    imgadapt.Update(R.drawable.button_off,(int) v.getId());
                    imgadapt.notifyDataSetInvalidated();
                    return true;
                }


                Toast.makeText(getBaseContext(),"id:"+view.getName(),Toast.LENGTH_SHORT).show();
                return  false;
            }
        });*/

        // gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        project.getGui().getGridView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View v,final int position, long id) {
                final Context context = getContext();

                if ((int) imgadapt.getItemInt(position) == R.drawable.add1) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.menu_popup);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.AddButton: // PushButton adden
                                    imgadapt.Update(R.drawable.button_off, position);
                                    imgadapt.notifyDataSetChanged();
                                    // TODO Model für PushButton der Liste im Project adden
                                    return true;

                                case R.id.AddLED:
                                    //es wird das Bild geändert, und dem imageadapt wir gesagt, dass sich etwas geändert hat -> er soll neu zeichnen
                                    Log.d(LOG_TAG, "Hinzufügen einer neuen LED");
                                    imgadapt.Update(R.drawable.lamp_off, position);
                                    imgadapt.notifyDataSetChanged();
                                    // Hinzufügen eines neuen ModelElements in die Liste aller Models im Project
                                    project.addModelToMap(position, new LedModel(ELEMENT_NAME + Integer.toString(position), false));
                                    //     project.addElement(new LedModel(ELEMENT_NAME + Integer.toString(position), false));
                                    return true;

                                case R.id.AddSwitch:
                                    Log.d(LOG_TAG, "Hinzufügen eines neuen Switchs");
                                    imgadapt.Update(R.drawable.switch_off, position);
                                    imgadapt.notifyDataSetChanged();
                                    project.addModelToMap(position, new SwitchModel(ELEMENT_NAME + Integer.toString(position), false));
                                    //     project.addElement(new SwitchModel(ELEMENT_NAME + Integer.toString(position), false));
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    return false;
                } else {

                    final PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.menu_popup_clickoptions);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem item) {

                            int itemId = item.getItemId();
                            switch (itemId) {

                                case R.id.identifyer:
                                    popupMenu.dismiss();
                                    PopupMenu popupMenu2 = new PopupMenu(getContext(), v);
                                    popupMenu2.inflate(R.menu.menu_popup_identifyer);
                                    popupMenu2.show();
                                    popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


                                        @Override
                                        public boolean onMenuItemClick(MenuItem item2) {

                                            createHashmap();
                                            //auf die Eingabe reagierne, und dementsprechend den Identifyer des Elements setzen

                                            //Toast.makeText(getBaseContext(),"id:gfh",Toast.LENGTH_SHORT).show();
                                            switch (item2.getItemId()){
                                                case R.id.p1:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P1");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p2:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P2");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p3:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P3");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p4:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P4");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p5:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P5");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p6:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P6");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p7:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P7");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p8:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P8");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p9:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P9");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p10:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P10");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p11:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P11");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p12:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P12");
                                                        return true;
                                                    }
                                                    return false;

                                                case R.id.p13:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P13");
                                                        return true;
                                                    }
                                                    return false;
                                                case R.id.p14:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P14");
                                                        return true;
                                                    }
                                                    return false;
                                                case R.id.p15:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("P15");
                                                        return true;
                                                    }
                                                    return false;
                                                case R.id.a1:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("A1");
                                                        return true;
                                                    }
                                                    return false;
                                                case R.id.a2:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("A2");
                                                        return true;
                                                    }
                                                    return false;
                                                case R.id.a3:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("A3");
                                                        return true;
                                                    }
                                                    return false;
                                                case R.id.a4:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("A4");
                                                        return true;
                                                    }
                                                    return false;
                                                case R.id.a5:
                                                    if (project.getElementByName("element" + position)!= null){
                                                        project.getElementByName("element" + position).setIdentifier("A5");
                                                        return true;
                                                    }
                                                    return false;

                                                default:
                                                    return false;
                                            }
                                            // project.getElementByName("element" + position).setIdentifier(elementIdentifier.get(item2.getItemId()));
                                            // Toast.makeText(getBaseContext(),"identifyer:"+project.getElementByName("element" + position).getIdentifier(),Toast.LENGTH_SHORT).show();
                                            //return true;
                                        }
                                    });

                                    return false;

                                case R.id.delete:
                                    project.getMapAllViewModels().remove(position); // Model aus der HashMap entfernen
                                    imgadapt.Update(R.drawable.add1, position);
                                    imgadapt.notifyDataSetChanged();
                                    return true;

                                default:
                                   /* for (Element var : CurrentProject.getAllElements()) {
                                        if (var.getName() == ("element" + position)) {
                                            var.setIdentifier(Integer.toString(item.getItemId()));


                                        Toast.makeText(getBaseContext(),"ID" + var.getIdentifier(),Toast.LENGTH_SHORT);
                                            return  true;
                                     }
                                    }*/
                                    return false;
                            }
                            //return false;
                        }
                    });
                    return false;
                }
            }
        });
    }


    public void createHashmap(){
        elementIdentifier.put(R.id.p1, "P1");
        elementIdentifier.put(R.id.p2, "P2");
        elementIdentifier.put(R.id.p3, "P3");
        elementIdentifier.put(R.id.p4, "P4");
        elementIdentifier.put(R.id.p5, "P5");
        elementIdentifier.put(R.id.p6, "P6");
        elementIdentifier.put(R.id.p7, "P7");
        elementIdentifier.put(R.id.p8, "P8");
        elementIdentifier.put(R.id.p9, "P9");
        elementIdentifier.put(R.id.p10, "P10");
        elementIdentifier.put(R.id.p11, "P11");
        elementIdentifier.put(R.id.p12, "P12");
        elementIdentifier.put(R.id.p13, "P13");
        elementIdentifier.put(R.id.p14, "P14");
        elementIdentifier.put(R.id.p15, "P15");
        elementIdentifier.put(R.id.a1, "A1");
        elementIdentifier.put(R.id.a2, "A2");
        elementIdentifier.put(R.id.a3, "A3");
        elementIdentifier.put(R.id.a4, "A4");
        elementIdentifier.put(R.id.a5, "A5");
    }

}
