package generic;

import android.view.View;
import elements.Element;

/**
 * Klasse enthält 2 Instanzvariablen: ein Element und einen Status.
 * Diese Klasse wird zur Statusaktualisierung der Gui verwendet.
 * @author Simon Bodner
 * @version 2014 - 1
 *
 */
// TODO Klasse momentan nicht notwendig
public class ComObject {

    private Element model;



    private int position;

	private View view;
	private String status; // Ist ein String - bei boolean enthält er 0 oder 1


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Element getModel() {
        return model;
    }

    public void setModel(Element model) {
        this.model = model;
    }
	
	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	public ComObject(View view, String statusStr) {
		this.view = view;
		this.status = statusStr;
	}
	
	public ComObject(View view, boolean statusBool) {
		this.view = view;
		
		getStatusString(statusBool);
	}


    public ComObject(Element model, int position) {
        this.model = model;
        this.position = position;
    }
	
//	public ComObject(String viewName, boolean statusBool) {
//		
//		getStatusString(statusBool);
//	}
	
	private void getStatusString(boolean statusBool) {
		if(statusBool)
			this.status = "1";
		else
			this.status = "0";
	}
}
