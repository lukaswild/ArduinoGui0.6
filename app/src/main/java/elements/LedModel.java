package elements;


import com.example.arduinogui.R;

import connection.IConnection;

public class LedModel extends BoolElement implements OutputElement {

	
	public LedModel(String name, boolean statusHigh) {
		super.name = name;
		super.statusHigh = statusHigh;
        super.setRessource(R.drawable.lamp_off);
	}
	
	public LedModel() {} 
	
	@Override
	public String receiveData(IConnection connection) {
		// TODO Auto-generated method stub; überhaupt notwendig?
		System.out.println("Empfangen von Daten...");

//        return connection.receiveData();
        return ""; // TODO void
	}
	
}
