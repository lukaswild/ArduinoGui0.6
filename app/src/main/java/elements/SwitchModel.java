package elements;

import android.util.Log;

import com.example.arduinogui.R;

import connection.IConnection;

public class SwitchModel extends BoolElement implements InputElement {

    private final String LOG_TAG = "SwitchModel";
	
	
    public SwitchModel(String name, boolean statusHigh) {
		super.name = name;
		super.statusHigh = statusHigh;
        super.setRessource(R.drawable.switch_off);
	}
	
	public SwitchModel() {} 
	

	@Override
	public void sendDataToArduino(IConnection connection, String data) {
        Log.d(LOG_TAG, "Daten an Arduino senden...");
        connection.sendData(data);
	}

}
