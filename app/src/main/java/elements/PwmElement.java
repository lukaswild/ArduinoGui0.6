package elements;

import com.example.arduinogui.R;

public abstract class PwmElement extends Element {

	protected int lowestPwm = 0;
	protected int highestPwm = 255;
	protected int currentPwm;


	public int getCurrentPwm() {
		return currentPwm;
	}
	
	public void setCurrentPwm(int currentPwm) {
		this.currentPwm = currentPwm;
	}

    public void refreshRes() {
        if (currentPwm>=0 && currentPwm <25){
            super.setResource(R.drawable.pwm_0);
        }
        else if (currentPwm>=25 && currentPwm <51){
            super.setResource(R.drawable.pwm_25_5);
        }
        else if (currentPwm>=51 && currentPwm <76){
            super.setResource(R.drawable.pwm_51);
        }
        else if (currentPwm>=76 && currentPwm <102){
            super.setResource(R.drawable.pwm_76_5);
        }
        else if (currentPwm>=102 && currentPwm <127){
            super.setResource(R.drawable.pwm_102);
        }
        else if (currentPwm>=127 && currentPwm <153){
            super.setResource(R.drawable.pwm_127_5);
        }
        else if (currentPwm>=153 && currentPwm <178){
            super.setResource(R.drawable.pwm_153);
        }
        else if (currentPwm>=178 && currentPwm <204){
            super.setResource(R.drawable.pwm_178_5);
        }
        else if (currentPwm>=204 && currentPwm <229){
            super.setResource(R.drawable.pwm_204);
        }
        else if (currentPwm>=229 && currentPwm <255){
            super.setResource((R.drawable.pwm_229_5));
        }
        else if (currentPwm>=255) {
            super.setResource((R.drawable.pwm_255));
        }
    }
	
}
