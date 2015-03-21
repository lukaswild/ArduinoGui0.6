package elements;

import com.example.arduinogui.R;

/**
 * Created by Lukas on 12.02.2015.
 */
public class PwmModel extends PwmElement implements OutputElement{

    public PwmModel(String name) {
        super.name = name;
        super.setResource(R.drawable.pwm_0);
        super.setCurrentPwm(0);
        super.setKind("ADC-Anzeige");
    }

    public PwmModel() {
        super.setKind("ADC-Anzeige");

    }

}
