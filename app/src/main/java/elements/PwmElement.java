package elements;

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
	
	
	
}
