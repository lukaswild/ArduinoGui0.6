package elements;

import connection.IConnection;

public class SwitchModel extends BoolElement implements InputElement {

	
	
	public SwitchModel(String name, boolean statusHigh) {
		super.name = name;
		super.statusHigh = statusHigh;
	}
	
	public SwitchModel() {} 
	

	@Override
	public void sendDataToArduino(IConnection connection, String data) {
		connection.sendData(data);
	}

}
