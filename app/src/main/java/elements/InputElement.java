package elements;

import connection.IConnection;

public interface InputElement {

	public void sendDataToArduino(IConnection connection, String data);
}
