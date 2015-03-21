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
        super.kind = "Schalter";
        if(statusHigh)
            super.setResource(resourceSwitchOn);
        else
            super.setResource(resourceSwitchOff);
    }

    public SwitchModel() {
        super.kind = "Schalter";
    }

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
        registerTimeRecord(status);
    }
}
