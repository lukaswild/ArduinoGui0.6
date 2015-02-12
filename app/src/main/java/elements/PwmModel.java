package elements;

import com.example.arduinogui.R;

import connection.IConnection;

/**
 * Created by Lukas on 12.02.2015.
 */
public class PwmModel extends PwmElement implements OutputElement{

    public PwmModel(String name) {
        super.name = name;
        super.setRessource(R.drawable.pwm_0);
    }

    public PwmModel() {}

    public void refreshRes(){
        if (currentPwm>=0 && currentPwm <25){
            super.setRessource(R.drawable.pwm_0);
        }
        else if (currentPwm>=25 && currentPwm <51){
            super.setRessource(R.drawable.pwm_25_5);
        }
        else if (currentPwm>=51 && currentPwm <76){
            super.setRessource(R.drawable.pwm_51);
        }
        else if (currentPwm>=76 && currentPwm <102){
            super.setRessource(R.drawable.pwm_76_5);
        }
        else if (currentPwm>=102 && currentPwm <127){
            super.setRessource(R.drawable.pwm_102);
        }
        else if (currentPwm>=127 && currentPwm <153){
            super.setRessource(R.drawable.pwm_127_5);
        }
        else if (currentPwm>=153 && currentPwm <178){
            super.setRessource(R.drawable.pwm_153);
        }
        else if (currentPwm>=178 && currentPwm <204){
            super.setRessource(R.drawable.pwm_178_5);
        }
        else if (currentPwm>=204 && currentPwm <229){
            super.setRessource(R.drawable.pwm_204);
        }
        else if (currentPwm>=229 && currentPwm <255){
            super.setRessource((R.drawable.pwm_229_5));
        }
        else if (currentPwm>=255){
            super.setRessource((R.drawable.pwm_255));
        }
    }

    @Override
    public String receiveData(IConnection connection) {
        return null;
    }
}
