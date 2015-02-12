package elements;

import com.example.arduinogui.R;

import connection.IConnection;

/**
 * Created by Lukas on 12.02.2015.
 */
public class PwmInputModel extends PwmElement implements InputElement {


    public PwmInputModel(String name) {
        super.name = name;
        super.setResource(R.drawable.pwm_slider);
        super.setCurrentPwm(0);
    }

    @Override
    public void sendDataToArduino(IConnection connection, String data) {
        connection.sendData(Integer.toString(currentPwm));
    }
}
