package elements;

import com.example.arduinogui.R;

import connection.IConnection;

/**
 * Created by Lukas on 12.02.2015.
 */
public class PwmModel extends PwmElement implements OutputElement{

    public PwmModel(String name) {
        super.name = name;
        super.setResource(R.drawable.pwm_0);
        super.setCurrentPwm(0);
    }

    public PwmModel() {}



    @Override
    public String receiveData(IConnection connection) {
        return null;//TODO Daten receiven

    }

}
