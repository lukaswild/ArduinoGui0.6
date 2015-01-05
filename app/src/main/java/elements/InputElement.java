package elements;

import connection.IConnection;

public interface InputElement {

    //TODO sollte die Methode nicht SendData heißen ?
	public void sendDataToArduino(IConnection connection, String data);
}
