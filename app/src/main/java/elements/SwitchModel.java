package elements;

import android.util.Log;

import com.example.arduinogui.R;

import connection.IConnection;

public class SwitchModel extends BoolElement implements InputElement {

    private final String LOG_TAG = "SwitchModel";
    int resourceSwitchOff = R.drawable.switch_on;
    int resourceSwitchOn = R.drawable.switch_off;


    public SwitchModel(String name, boolean statusHigh) {
        super.name = name;
        super.statusHigh = statusHigh;
        if(statusHigh)
            super.setResource(resourceSwitchOn);
        else
            super.setResource(resourceSwitchOff);
    }

    public SwitchModel() {}

    @Override
    public void setResource(boolean status) {
        if(status)
            super.setResource(resourceSwitchOn);
        else
            super.setResource(resourceSwitchOff);
    }

    public int getResourceSwitchOff() {
        return resourceSwitchOff;
    }

    public void setResourceSwitchOff(int resourceSwitchOff) {
        this.resourceSwitchOff = resourceSwitchOff;
    }

    public int getResourceSwitchOn() {
        return resourceSwitchOn;
    }

    public void setResourceSwitchOn(int resourceSwitchOn) {
        this.resourceSwitchOn = resourceSwitchOn;
    }

    @Override
    public void sendDataToArduino(IConnection connection, String data, int status) {
        Log.d(LOG_TAG, "Daten an Arduino senden...");
        connection.sendData(data);
        if(isFirstInteraction()) {
            setMillisFirstInteraction(System.currentTimeMillis());
            setFirstInteraction(false);
        }

        long timeDifference = (System.currentTimeMillis() - getMillisFirstInteraction()) / 1000;
        /*
        Der Graph wird so gezeichnet, dass die jeweilen DataPoints mit einer Geraden verbunden werden.
        Um schöne Sprünge von 0 auf 1 zu haben, muss deshalb der jeweils vorherige Eintrag mit der aktuellen Zeit
        nochmals in die Liste eingetragen werden
         */
        if(!getTimeRecord().isEmpty() && !getDataRecord().isEmpty()) {
            getTimeRecord().add((int) timeDifference);
            getDataRecord().add(getDataRecord().get(getDataRecord().size() - 1));
        }

        getTimeRecord().add((int) timeDifference); // TODO Sollen diese Listen auch in der DB gespeichert werden? eher nicht
        getDataRecord().add(status);
        Log.d(LOG_TAG, "Neuer Status aufgezeichnet");
    }
}
