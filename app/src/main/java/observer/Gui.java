package observer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.arduinogui.R;

import java.util.HashMap;

import connection.IConnection;
import elements.BoolElement;
import elements.Element;
import elements.EmptyElement;
import elements.LedModel;
import elements.PushButtonModel;
import elements.PwmElement;
import elements.PwmInputModel;
import elements.PwmModel;
import elements.SwitchModel;
import generic.ComObjectStd;
import generic.ImageAdapter;
import main.DiagramActivity;


/* 
 * Bei extends View k�nnte Klasse Gui in Activity sofort als contentView gesetzt und somit angezeigt werden. 
 */
public class Gui extends View implements IObserver {

    private final String LOG_TAG = "Gui";
    private GridView gridView;
    public static HashMap<Integer, String> elementIdentifier = new HashMap<Integer, String>();
    private final String ELEMENT_NAME = "element";
    private int imgLedOn = R.drawable.lamp_on;
    private int imgLedOff = R.drawable.lamp_off;
    private int imgSwitchOff = R.drawable.switch_off;
    private int imgSwitchOn = R.drawable.switch_on;
    private int imgButtonOff = R.drawable.button_off;
    private int imgButtonOn = R.drawable.button_on;


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


    @Override
    public void update(Observable senderClass, Object msg) {

        if(msg instanceof ComObjectStd) {
            ComObjectStd comObj = (ComObjectStd) msg;
            Element modelOutput = comObj.getModelOutput();
            int inputElementPosition = comObj.getInputElementPosition();
            int outputElementPosition = comObj.getOutputElementPosition();
            int actionNr = comObj.getActionNr();


            if (actionNr == DatabaseHandler.ACTION_UPDATE_ELEMENT_BOTH || actionNr == DatabaseHandler.ACTION_NOTHING || actionNr == DatabaseHandler.ACTION_UPDATE_SINGLE_ELEMENT) {
                Log.d(LOG_TAG, "Updaten der Gui...");
                Log.d(LOG_TAG, "outputElementPosition: " + outputElementPosition);
                View child = gridView.getChildAt(outputElementPosition);
                //  if(model instanceof LedModel) {
                ImageAdapter imageAdapter = (ImageAdapter) gridView.getAdapter();
                Integer o = (Integer) imageAdapter.getItem(outputElementPosition);
                Log.d(LOG_TAG, o.getClass().toString());
                if (modelOutput.isFirstInteraction()) {
                    modelOutput.setMillisFirstInteraction(System.currentTimeMillis());
                    modelOutput.setFirstInteraction(false);
                }

                long timeDifference = (System.currentTimeMillis() - modelOutput.getMillisFirstInteraction()) / 1000;
        /*
        Der Graph wird so gezeichnet, dass die jeweilen DataPoints mit einer Geraden verbunden werden.
        Um schöne Sprünge von 0 auf 1 zu haben, muss deshalb der jeweils vorherige Eintrag mit der aktuellen Zeit
        nochmals in die Liste eingetragen werden
         */
                if (!modelOutput.getTimeRecord().isEmpty() && !modelOutput.getDataRecord().isEmpty()) {
                    modelOutput.getTimeRecord().add((int) timeDifference);
                    modelOutput.getDataRecord().add(modelOutput.getDataRecord().get(modelOutput.getDataRecord().size() - 1));
                } else {
                    modelOutput.getTimeRecord().add((int) timeDifference);
                    modelOutput.getDataRecord().add(0);
                }
                modelOutput.getTimeRecord().add((int) timeDifference);
                Log.d("EEEEE", "hinzugefügt");
                int statusToAdd = 0;

                if (modelOutput instanceof BoolElement) {
                    if (!((BoolElement) modelOutput).isStatusHigh())
                        statusToAdd = 1;
                    modelOutput.getDataRecord().add(statusToAdd);
                    Log.d(LOG_TAG, "Neuer Status aufgezeichnet");

                    if (modelOutput instanceof LedModel) {
                        if ((Integer) imageAdapter.getItem(inputElementPosition) == imgSwitchOff || (Integer) imageAdapter.getItem(inputElementPosition) == imgButtonOff)
                            updateLedStatus(imageAdapter, outputElementPosition, true);
                        else
                            updateLedStatus(imageAdapter, outputElementPosition, false);
                    }
                } else if (modelOutput instanceof PwmElement) {
                    Log.d(LOG_TAG, "PWM der säule:" + ((PwmElement) modelOutput).getCurrentPwm());
                    ((PwmElement) modelOutput).refreshRes();
                    String pwm = String.valueOf(((PwmElement) modelOutput).getCurrentPwm());
                    imageAdapter.update(modelOutput.getResource(), outputElementPosition);
                    modelOutput.getDataRecord().add(((PwmElement)modelOutput).getCurrentPwm());
                    // imageAdapter.updateTextRes(pwm, outputElementPosition);
                }

                Log.d(LOG_TAG, "Gui aktualisiert");
            }
        }
    }


    /**
     * Ändern des Status einer Led und dem ImageAdapter die Änderungen mitteilen
     * @param imageAdapter - ImageAdapter welcher die Elemente beinhaltet
     * @param position - Position an der sich die Led befindet
     * @param high - Status auf welchen die Led gesetzt werden soll
     */
    private void updateLedStatus(ImageAdapter imageAdapter, int position, boolean high) {
        if(high)
            setLedStatus(imageAdapter, position, imgLedOn);
        else
            setLedStatus(imageAdapter, position, imgLedOff);
    }

    private void setLedStatus(ImageAdapter imageAdapter, int position, int resource) {
        imageAdapter.update(resource, position);
        View view = gridView.getChildAt(position);
        final ImageView imgView = (ImageView) view.findViewById(R.id.imageview);
        imgView.setImageResource(resource);
    }



    /* TODO Bei Hinzufügen eines neuen OutputElements bzw. Identifiers Stellung des Schalters überprüfen, da wenn Schalter bereits ein ist das Element auch sofort ein sein soll
     *  --> Einfach Status des realen Elements am Arduino abfragen und unsere Elemene auf diese Status setzen
     */

    public void initializeUI(final Project project, final ImageAdapter imgadapt, final IConnection currentConnection, boolean editmode) {
        //Löscht zuerst einmal den Inhalt von Gridview
        project.getGui().getGridView().clearAnimation();
        project.getGui().getGridView().setAdapter(imgadapt);
        setOnClickListener(project, imgadapt, currentConnection, editmode);
    }

    private void setOnClickListener(Project project, ImageAdapter imgadapt, IConnection currentConnection, boolean editmode) {
        ////Edit Modus ausgeschaltet, Benutzer will Schalter einschalten usw.
        if (!editmode)
            setGridViewItemClickListenerNonEditMode(project, imgadapt, currentConnection);
        else if (editmode)
            setGridViewItemClickListenerEditMode(project, imgadapt, currentConnection);
    }

    private void setGridViewItemClickListenerEditMode(final Project project, final ImageAdapter imgadapt, final IConnection currentConnection) {
        project.getGui().getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View v, final int position, long id) {

                //Ist das elemtn ein plus, wenn ja popup mit neuen elementen zeigen
                if (imgadapt.getItemInt(position) == R.drawable.add1) {
                    Context wrapper = new ContextThemeWrapper(getContext(),R.style.MyAwesomeBackground_PopupStyle);
                    final PopupMenu popupMenu = new PopupMenu(wrapper, v);
                    popupMenu.inflate(R.menu.menu_popup);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            return addButtonPressed(item, imgadapt, position, project, currentConnection, isInEditMode());
                        }
                    });
                }

                //wenn nicht popup mit identifyer, delete zeigen
                else {

                    final Context context = getContext();
                    Context wrapper = new ContextThemeWrapper(context,R.style.MyAwesomeBackground_PopupStyle);
                    final PopupMenu popupMenu = new PopupMenu(wrapper, v);
                    popupMenu.inflate(R.menu.menu_popup_clickoptions);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem item) {

                            int itemId = item.getItemId();
                            switch (itemId) {

                                case R.id.identifyer:
                                    popupMenu.dismiss();
                                    Context wrapper = new ContextThemeWrapper(context,R.style.MyAwesomeBackground_PopupStyle);
                                    final PopupMenu popupMenu2 = new PopupMenu(wrapper, v);
                                    popupMenu2.inflate(R.menu.menu_popup_identifyer);
                                    popupMenu2.show();
                                    popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


                                        @Override
                                        public boolean onMenuItemClick(MenuItem item2) {

                                            //createHashmap(); //TODO switch case durch hashmap ersetzen
                                            //auf die Eingabe reagierne, und dementsprechend den Identifyer des Elements setzen

                                            switch (item2.getItemId()) {
                                                case R.id.p1:
                                                    setIdentifier(project, position, "P1");
                                                    break;

                                                case R.id.p2:
                                                    setIdentifier(project, position, "P2");
                                                    break;

                                                case R.id.p3:
                                                    setIdentifier(project, position, "P3");
                                                    break;

                                                case R.id.p4:
                                                    setIdentifier(project, position, "P4");
                                                    break;

                                                case R.id.p5:
                                                    setIdentifier(project, position, "P5");
                                                    break;

                                                case R.id.p6:
                                                    setIdentifier(project, position, "P6");
                                                    break;

                                                case R.id.p7:
                                                    setIdentifier(project, position, "P7");
                                                    break;

                                                case R.id.p8:
                                                    setIdentifier(project, position, "P8");
                                                    break;

                                                case R.id.p9:
                                                    setIdentifier(project, position, "P9");
                                                    break;

                                                case R.id.p10:
                                                    setIdentifier(project, position, "P10");
                                                    break;

                                                case R.id.p11:
                                                    setIdentifier(project, position, "P11");
                                                    break;

                                                case R.id.p12:
                                                    setIdentifier(project, position, "P12");
                                                    break;

                                                case R.id.p13:
                                                    setIdentifier(project, position, "P13");
                                                    break;

                                                case R.id.p14:
                                                    setIdentifier(project, position, "P14");
                                                    break;

                                                case R.id.p15:
                                                    setIdentifier(project, position, "P15");
                                                    break;

                                                case R.id.a1:
                                                    setIdentifier(project, position, "A1");
                                                    break;
                                                case R.id.a2:
                                                    setIdentifier(project, position, "A2");
                                                    break;

                                                case R.id.a3:
                                                    setIdentifier(project, position, "A3");
                                                    break;

                                                case R.id.a4:
                                                    setIdentifier(project, position, "A4");
                                                    break;

                                                case R.id.a5:
                                                    setIdentifier(project, position, "A5");
                                                    break;

                                                default:
                                                    return false;
                                            }
                                            imgadapt.notifyDataSetChanged();
                                            ComObjectStd comObj = new ComObjectStd(null, project.getElementFromMap(position), -1, position, project.getId(), DatabaseHandler.ACTION_UPDATE_IDENTIFIER);
                                            project.notify(null, comObj);
//                                            project.notify(null, null, project.getElementFromMap(position), -1, position, project.getId(), DatabaseHandler.ACTION_UPDATE_IDENTIFIER);
                                            return true;
                                        }
                                    });

                                    return false;

                                case R.id.delete:
                                    EmptyElement emptyElement = new EmptyElement();
                                    ComObjectStd comObj = new ComObjectStd(null, emptyElement, -1, position, project.getId(), DatabaseHandler.ACTION_UPDATE_ELEMENT_TYPE);
                                    project.notify(null, comObj);
//                                    project.notify(null, null, emptyElement, -1, position, project.getId(), DatabaseHandler.ACTION_UPDATE_ELEMENT_TYPE);
                                    project.getMapAllViewModels().remove(position); // Model aus der HashMap entfernen
                                    project.getMapAllViewModels().put(position, emptyElement);
                                    imgadapt.update(R.drawable.add1, position);
                                    imgadapt.notifyDataSetChanged();
                                    return true;

                                case R.id.element_tools: // Diagramm anzeigen
                                    startActivityDiagram(project, position);
                                    return true;
                            }

                            return false;
                        }
                    });

                }

            }});
    }

    private void setIdentifier(Project project, int position, String identifier) {
        if (project.getElementFromMap(position) != null) {
            project.getElementFromMap(position).setIdentifier(identifier);
        }
    }

    private void setGridViewItemClickListenerNonEditMode(final Project project, final ImageAdapter imgadapt, final IConnection currentConnection) {
        for(int i = 0; i < imgadapt.getCount(); i++) {
            final int iFinal = i;
            if(imgadapt.getItemInt(i) == imgButtonOff) {
                Log.d(LOG_TAG, "ID: " + imgadapt.getItemInt(i) + ", Position: " + i);

                gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            boolean once = false;
                            @Override
                            public void onGlobalLayout() {
                                if (!once) {
                                    once = true;
                                    View vAtPosition = gridView.getChildAt(iFinal);
                                    pushBtnSetTouchListener(imgadapt, currentConnection, project, iFinal, vAtPosition);
                                }
                            }
                        });

//                pushBtnSetTouchListener(imgadapt, currentConnection, project, i, vAtPosition);
                Log.d(LOG_TAG, "TouchListener für PushButton gesetzt");
            }
        }


        project.getGui().getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View v, final int position, long id) {
                switch (imgadapt.getItemInt(position)) {

                    case R.drawable.add1:
                        Context wrapper = new ContextThemeWrapper(getContext(),R.style.MyAwesomeBackground_PopupStyle);
                        final PopupMenu popupMenu = new PopupMenu(wrapper, v);
                        popupMenu.inflate(R.menu.menu_popup);
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                return addButtonPressed(item, imgadapt, position, project, currentConnection, isInEditMode());
                            }
                        });

                        break;

                    case R.drawable.switch_off:
                        Log.d(LOG_TAG, "Switch off: Senden an Arduino und Updaten der Gui...");
                        try {
                            project.sendDataUpdateGui(v, currentConnection, position, false);
                            imgadapt.update(R.drawable.switch_on, position);
                            imgadapt.notifyDataSetInvalidated();
                        }catch (NullPointerException e) {
                            makeToastNoConnection();
                        }
                        break;

                    case R.drawable.switch_on:
                        Log.d(LOG_TAG, "Switch on: Senden an Arduino und Updaten der Gui...");
                        try {
                            project.sendDataUpdateGui(v, currentConnection, position, true);
                            imgadapt.update(R.drawable.switch_off, position);
                            imgadapt.notifyDataSetInvalidated();
                        } catch (NullPointerException e) {
                            makeToastNoConnection();
                        }
                        break;

                    case R.drawable.pwm_slider:
                        //wenn es ein pwm_slider ist, dann muss das elemnt ein PWmInput sein.
                        final PwmInputModel pwm = (PwmInputModel)project.getElementFromMap(position);


                        final Dialog popDialog = new Dialog(getContext());
                        popDialog.setCancelable(true);
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        final View viewlayout = inflater.inflate(R.layout.pwm_seekbar,(ViewGroup)findViewById(R.id.seekBarPWM));

                        popDialog.setContentView(viewlayout);
                        popDialog.setTitle("ADC Wert");
                        popDialog.show();

                        final SeekBar seek1 = (SeekBar)viewlayout.findViewById(R.id.seekBarPWM);
                        seek1.setProgress(pwm.getCurrentPwm());
                        seek1.setDrawingCacheBackgroundColor(Color.DKGRAY);

                        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                             @Override
                                                             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                                                 pwm.setCurrentPwm(progress);
                                                                 imgadapt.updateTextRes(Integer.toString(progress),position);
                                                                 imgadapt.notifyDataSetChanged();

                                                             }

                                                             @Override
                                                             public void onStartTrackingTouch(SeekBar seekBar) {

                                                             }

                                                             @Override
                                                             public void onStopTrackingTouch(SeekBar seekBar) {

                                                             }
                                                         }

                        );
                        final Button button = (Button)viewlayout.findViewById(R.id.buttonOKPWM);

                        button.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popDialog.cancel();
                                //Wert des bild setzen
                                imgadapt.notifyDataSetChanged();
                                if (null==currentConnection) {
                                    makeToastNoConnection();
                                }
                                else{

                                    project.sendDataUpdateGui(v, currentConnection, position, false);
                                }
                            }
                        });
                        break;

                    case R.drawable.pwm_102:
                    case R.drawable.pwm_0:
                    case R.drawable.pwm_127_5:
                    case R.drawable.pwm_153:
                    case R.drawable.pwm_178_5:
                    case R.drawable.pwm_204:
                    case R.drawable.pwm_229_5:
                    case R.drawable.pwm_255:
                    case R.drawable.pwm_25_5:
                    case R.drawable.pwm_51:
                    case R.drawable.pwm_76_5:
                        makeToastNoInputElement(position);
                        break;

                    case R.drawable.lamp_off:
                        makeToastNoInputElement(position);
                        break;

////////// sollte aktiviert bleiben, solange angeklickt ////////////////
                    case R.drawable.button_off:
//                        Toast.makeText(getContext(), "button", Toast.LENGTH_SHORT).show();
                        break;

                    case R.drawable.button_on:
//                            Toast.makeText(getContext(), "Button aus", Toast.LENGTH_SHORT).show();
//                            imgadapt.update(R.drawable.button_off, position);
//                            imgadapt.notifyDataSetChanged();
                        break;
//////////


                }


            }

        });



    }

    private void makeToastNoInputElement(int position) {
        Toast.makeText(getContext(), "Element ist kein Eingabelement!", Toast.LENGTH_SHORT).show();
    }


    private void startActivityDiagram(Project project, int position) {
        Element elementClicked = project.getMapAllViewModels().get(position);
        Intent intentDiagram = new Intent(getContext(), DiagramActivity.class);
        intentDiagram.putExtra("timeRecord", elementClicked.getTimeRecord());
        intentDiagram.putExtra("dataRecord", elementClicked.getDataRecord());
        intentDiagram.putExtra("elementClass", elementClicked.getClass().toString());
        intentDiagram.putExtra("elementIdentifier", elementClicked.getIdentifier());
        getContext().startActivity(intentDiagram);
    }


    private void makeToastNoConnection() {
        Toast.makeText(getContext(), "Bitte zuerst eine Verbindung auswählen", Toast.LENGTH_SHORT).show();
    }


    private boolean addButtonPressed(MenuItem item, final ImageAdapter imgadapt, final int position, Project project, final IConnection currentConnection, boolean editMode) {
        final Project projectFinal = project;

        switch (item.getItemId()) {

            case R.id.AddPushButton: // PushButton adden

                final int positionFinal = position;

                imgadapt.update(R.drawable.button_off, position);
                imgadapt.notifyDataSetChanged();
                PushButtonModel newPushBtn = new PushButtonModel("Button");
                addToMapAndNotifyDb(position, project, newPushBtn);

                View vAtPosition = project.getGui().getGridView().getChildAt(position);
                pushBtnSetTouchListener(imgadapt, currentConnection, projectFinal, positionFinal, vAtPosition);



                return true;

            case R.id.AddLED:
                //es wird das Bild geändert, und dem imageadapt wir gesagt, dass sich etwas geändert hat -> er soll neu zeichnen
                Log.d(LOG_TAG, "Hinzufügen einer neuen LED");
                imgadapt.update(R.drawable.lamp_off, position);
                imgadapt.notifyDataSetChanged();
//                imgadapt.notifyDataSetInvalidated();
                // Hinzufügen eines neuen ModelElements in die Liste aller Models im Project
                LedModel newLedModel = new LedModel(ELEMENT_NAME + Integer.toString(position), false);
                addToMapAndNotifyDb(position, project, newLedModel);

                return true;

            case R.id.AddSwitch:
                Log.d(LOG_TAG, "Hinzufügen eines neuen Switchs");
                imgadapt.update(R.drawable.switch_off, position);
                imgadapt.notifyDataSetChanged();
                SwitchModel newSwitchModel = new SwitchModel(ELEMENT_NAME + Integer.toString(position), false);
                addToMapAndNotifyDb(position, project, newSwitchModel);
                //     project.addElement(new SwitchModel(ELEMENT_NAME + Integer.toString(position), false));
                return true;

            case R.id.AddPWMCol:
                Log.d(LOG_TAG, "Hinzufügen einer PWM Säule");
                imgadapt.update(R.drawable.pwm_0, position);
                imgadapt.notifyDataSetChanged();
                PwmModel newPwmModel = new PwmModel(ELEMENT_NAME + Integer.toString(position));
//                project.addModelToMap(position, new PwmModel(ELEMENT_NAME + Integer.toString(position)));
                imgadapt.updateTextRes("0", position);
                imgadapt.notifyDataSetChanged();
                addToMapAndNotifyDb(position, project, newPwmModel);
                return true;

            case R.id.AddPWMView:
                Log.d(LOG_TAG, "Hinzufügen eines PWM Reglers");
                imgadapt.update(R.drawable.pwm_slider, position);
                imgadapt.notifyDataSetChanged();
                PwmInputModel newPwmInputModel = new PwmInputModel(ELEMENT_NAME + Integer.toString(position));
//                project.addModelToMap(position, new PwmInputModel(ELEMENT_NAME + Integer.toString(position)));
                // TextView txtView2 = (TextView) gridView.findViewById(R.id.textView5);
                imgadapt.updateTextRes("0",position);
                imgadapt.notifyDataSetChanged();
                addToMapAndNotifyDb(position, project, newPwmInputModel);
                return true;


            default:
                return false;
        }
    }

    private void pushBtnSetTouchListener(final ImageAdapter imgadapt, final IConnection currentConnection, final Project projectFinal, final int positionFinal, View vAtPosition) {
        vAtPosition.setTag(positionFinal);
        vAtPosition.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()/* & MotionEvent.ACTION_MASK*/) {

                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        try {
                            projectFinal.sendDataUpdateGui(v, currentConnection, positionFinal, false);
//                            imgadapt.update(R.drawable.button_on, positionFinal);


//                            RunnableSendDataBtn runSend = new RunnableSendDataBtn(projectFinal, v, currentConnection, positionFinal);
//                            runSend.setStatus(false);
//                            Thread threadSend = new Thread(runSend);
                            RunnableUpdate runUpdate = new RunnableUpdate(imgadapt);
                            runUpdate.setResourcePosition(R.drawable.button_on, positionFinal);
                            Thread threadUpdate = new Thread(runUpdate);
//                            threadSend.start(); // mit diesem Thread: eine Thread Exception wird geworfen (nur originaler Thread hat Zugriff auf Views)
                            threadUpdate.start();


                        } catch (NullPointerException e) {
                            makeToastNoConnection();
                        }
                        Log.d(LOG_TAG, "Button down");
                        break;

                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        try {
                            projectFinal.sendDataUpdateGui(v, currentConnection, positionFinal, true);
//                            imgadapt.update(R.drawable.button_off, positionFinal);

//                            RunnableSendDataBtn runSend = new RunnableSendDataBtn(projectFinal, v, currentConnection, positionFinal);
//                            runSend.setStatus(true);
//                            Thread threadSend = new Thread(runSend);
                            RunnableUpdate runUpdate = new RunnableUpdate(imgadapt);
                            runUpdate.setResourcePosition(R.drawable.button_off, positionFinal);
                            Thread threadUpdate = new Thread(runUpdate);
//                            threadSend.start(); // mit diesem Thread: eine Thread Exception wird geworfen (nur originaler Thread hat Zugriff auf Views)
                            threadUpdate.start();



                        } catch (NullPointerException e) {
                            makeToastNoConnection();
                        }
                        Log.d(LOG_TAG, "Button up");
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });
    }

    public void addToMapAndNotifyDb(int position, Project project, Element element) {
        project.addModelToMap(position, element);
        ComObjectStd comObj = new ComObjectStd(null, element, -1, position, project.getId(), DatabaseHandler.ACTION_UPDATE_ELEMENT_TYPE);
        project.notify(null, comObj);
//        project.notify(null, null, element, -1, position, project.getId(), DatabaseHandler.ACTION_UPDATE_ELEMENT_TYPE);
    }



    private class RunnableSendDataBtn implements Runnable {
        private Project project;
        private View v;
        private IConnection connection;
        private int position;
        private boolean status;

        public RunnableSendDataBtn(Project project, View v, IConnection connection, int position) {
            this.project = project;
            this.v = v;
            this.connection = connection;
            this.position = position;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        @Override
        public void run() {
            sendDataUpdateGuiBtn(v, connection, position, status);
        }

        private synchronized void sendDataUpdateGuiBtn(View v, IConnection connection, int position, boolean status) {
            project.sendDataUpdateGui(v, connection, position, status);
        }
    }


    private class RunnableUpdate implements Runnable {
        private ImageAdapter imgadapt;
        private int resource;
        private int position;

        public RunnableUpdate(ImageAdapter imgadapt) {
            this.imgadapt = imgadapt;
        }

        public void setResourcePosition(int resource, int position) {
            this.resource = resource;
            this.position = position;
        }

        @Override
        public void run() {
            updateGui(imgadapt, resource, position);
        }

        private synchronized void updateGui(ImageAdapter imgadapt, int resource, int position) {
            imgadapt.update(resource, position);
        }
    }

    public void updatePWm(Element currentElement,ImageAdapter imageAdapter,int pos){
        Log.d(LOG_TAG,"in der updatePWM");
        try {
            Log.d(LOG_TAG,"name:"+currentElement.getName());

            char[] tex = currentElement.getName().toCharArray();
            String newtex = "";
            int newpos = 0;
            try {
                newtex += tex[7];
                newtex += tex[8];

            } catch (IndexOutOfBoundsException e) {
                newtex="";
                newtex += tex[7];
            }
            newpos = Integer.parseInt(newtex);
            imageAdapter.copyTXT(pos,newpos);
            imageAdapter.notifyDataSetChanged();
        }
        catch(NullPointerException e){
            Log.d(LOG_TAG,"name ist null");
        }

    }
}

