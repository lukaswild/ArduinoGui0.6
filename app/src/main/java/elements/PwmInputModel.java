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

    //Die Idee ist, dass der PWM Wert für den Arduino immer gleich ausschaut
    //wird jetzt eimal der Wert 34, und einmal der Wert 128 gesendet, dann unterscheiden sich
    //die Werte anhand der Stellen. Das macht die Überprüfung am Arduino schwieriger.
    //Besser ist 034, oder z.B.: 009, so ist der PWM Wert immer 3 Stellen lang.

    @Override
    public void sendDataToArduino(IConnection connection,String data) {

        String pwm ="" ;
        if (ziffernrekursiv(currentPwm)==1){
            pwm= "00"+Integer.toString(currentPwm);
        }
        else if (ziffernrekursiv(currentPwm)==2){
            pwm="0"+Integer.toString(currentPwm);
        }
        else{
            pwm=Integer.toString(currentPwm);
        }

        connection.sendData(pwm);
    }

    public static int ziffernrekursiv(int zahl) {
        return (zahl>0)? 1+ziffernrekursiv(zahl/10) : 0;
    }
}
