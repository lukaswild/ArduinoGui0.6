package elements;

import android.util.Log;

import com.example.arduinogui.R;

import connection.IConnection;

/**
 * Created by Simon on 12.02.2015.
 */
public class PushButtonModel extends BoolElement implements InputElement {

    private static final String LOG_TAG = "PushButtonModel";
    private int resourceButtonOff = R.drawable.button_off;
    private int resourceButtonOn = R.drawable.button_on;

    public PushButtonModel(String name) {
        super.name = name;
        super.setStatusHigh(false);
        super.setKind("Button");
        setResource(false);
    }

    public int getResourceButtonOff() {
        return resourceButtonOff;
    }

    public void setResourceButtonOff(int resourceButtonOff) {
        this.resourceButtonOff = resourceButtonOff;
    }

    public int getResourceButtonOn() {
        return resourceButtonOn;
    }

    public void setResourceButtonOn(int resourceButtonOn) {
        this.resourceButtonOn = resourceButtonOn;
    }

    @Override
    public void setResource(boolean statusHigh) {
        if(statusHigh)
            super.setResource(resourceButtonOn);
        else
            super.setResource(resourceButtonOff);
    }

    @Override
    public void sendDataToArduino(IConnection connection, String data, int status) {
        Log.d(LOG_TAG, "PushButtonModel sendet Daten an Arduino...");
        connection.sendData(data);
    }
}
