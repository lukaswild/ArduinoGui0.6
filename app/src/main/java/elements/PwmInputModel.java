package elements;

import com.example.arduinogui.R;

import connection.IConnection;

/**
 * Created by Lukas on 12.02.2015.
 */
public class PwmInputModel extends PwmElement implements InputElement {

    private final static String LOG_TAG = "PwmInputModel";

    public PwmInputModel(String name) {
        super.name = name;
        super.setResource(R.drawable.pwm_slider);
        super.setCurrentPwm(0);
        super.kind = "Adc-Input";
    }

    public PwmInputModel() {
        super.kind = "Adc-Input";
    }

    @Override
    public void sendDataToArduino(IConnection connection, String dataCode, int status) {
        connection.sendData(dataCode);

    }
}
