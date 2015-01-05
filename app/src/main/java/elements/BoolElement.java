package elements;


public abstract class BoolElement extends Element {

	protected boolean statusHigh;

	public boolean isStatusHigh() {
		return statusHigh;
	}

	public void setStatusHigh(boolean statusHigh) {
		this.statusHigh = statusHigh;
	} 
}
