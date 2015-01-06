
package connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BTConnection implements IConnection {

// TODO Initialisierung einer neuen Verbindung auch über einen Thread oder AsyncTask

    /**
     * UUID für Bluetooth-Kommunikation
     */

    private int id=0;
    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String LOG_TAG = "BTConnection";

    private String conNameDeclaration;
    private String conAddressDeclaration;
    private static String conName;
    private static BluetoothAdapter adapter = null;
    private static BluetoothSocket socket = null;
    private static OutputStream streamOut = null; // TODO BufferedOutputStream
    private static InputStream streamIn = null; // TODO BufferedInputStream


    private static boolean isConnected = false;
    private static String macAddress; // MAC Adresse des Bluetooth Adapters
    private static BTConnection instance = null; //Instanz für Singleton-Pattern

    //getter und setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private BTConnection() {}

    @Override
    public String getConName() {
        return conName;
    }

    @Override
    public void setConName(String conNname) {
        BTConnection.conName = conNname;
    }

    @Override
    public String getAddress() {
        return macAddress;
    }

    @Override
    public void setAddress(String macAdress) {
        BTConnection.macAddress = macAdress;
    }

    public boolean isConnected() {
        return isConnected;
    }


    public String getConNameDeclaration() {
        return conNameDeclaration;
    }

    public void setConNameDeclaration(String conNameDeclaration) {
        this.conNameDeclaration = conNameDeclaration;
    }

    public String getConAddressDeclaration() {
        return conAddressDeclaration;
    }

    public void setConAddressDeclaration(String conAddressDeclaration) {
        this.conAddressDeclaration = conAddressDeclaration;
    }


    @Override
    public void sendData(String data) {
        byte[] msgBuffer = data.getBytes();
        if (isConnected) {
            Log.d(LOG_TAG, "Sending data: " + data);
            try {
                streamOut.write(msgBuffer);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while sending data: " + e.toString());
            }
        } else {
            Log.d(LOG_TAG, "Cannot send data, not connected with Bluetooth");
        }
    }


    @Override
    public String receiveData() {

        byte[] buffer = new byte[1024]; // Puffer
        int laenge; // Anzahl empf. Bytes
        String data = "";
        try {
            if (streamIn.available() > 0) {
                laenge = streamIn.read(buffer);
                Log.d(LOG_TAG, "Number of received bytes: " + String.valueOf(laenge));

                // Message zusammensetzen:
                for (int i = 0; i < laenge; i++) {
                    data += (char) buffer[i];
                }

                Log.d(LOG_TAG, "Message: " + data);
            } else
                Log.d(LOG_TAG, "InputStream not available");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error receiving data: " + e.toString());
        }

        return data;

    }


    public static BTConnection createAttributeCon(String conNameDeclaration, String conAddressDeclaration) {
        BTConnection conDeclaration = new BTConnection();
        conDeclaration.setConNameDeclaration(conNameDeclaration);
        conDeclaration.setConAddressDeclaration(conAddressDeclaration);

        return conDeclaration;
    }


    /**
     * Aufbauen einer Bluetooth-Verbindung.
     * Verbindung kann nicht über einen Konstruktor aufgebaut werden, sondern nur über diese statische Methode.
     * Es kann nur eine Verbindung geben. Sollte bereits eine Verbindung vorhanden sein und
     * diese Methode ein zweites Mal aufgerufen werden, so wird das bereits vorhandene Objekt zurückgeliefert.
     * @param conName Vom Benutzer festgelegte Bezeichnung für die Verbindung
     * @param macAddress MAC-Adresse des Bluetooth-Devices am Arduino
     */
    public static boolean initialiseConnection(String conName, String macAddress) {

        Log.d(LOG_TAG, "Connecting with " + macAddress + "...");

        if(instance == null) {
            instance = new BTConnection();
            BTConnection.isConnected = true; // Auf true setzen, wenn etwas schief gehen sollte, wird dieser Wert auf false gesetzt
            BTConnection.adapter = BluetoothAdapter.getDefaultAdapter();
            BTConnection.macAddress = macAddress;
            BTConnection.conName = conName;
            BluetoothDevice remote_device = adapter.getRemoteDevice(BTConnection.macAddress);
            createConnectSocket(remote_device); // Socket erstellen und verbinden

            // Outputstream erstellen:
            createOutputStream();

            // Inputstream erstellen
            createInputStream();

            if (isConnected)
                Log.d(LOG_TAG, "Bluetooth-Verbindung erfolgreich initialisiert");
            else {
                Log.d(LOG_TAG, "Fehler beim Aufbauen der Bluetooth-Verbindung");
                return false;
            }

            return true;
        }

        return false;
    }


    private static void createInputStream() {
        try {
            streamIn = socket.getInputStream();
            Log.d(LOG_TAG, "InputStream created");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error creating InputStream: " + e.toString());
            isConnected = false;
        }
    }


    private static void createOutputStream() {
        try {
            streamOut = socket.getOutputStream();
            Log.d(LOG_TAG, "OutputStream created");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error creating OutputStream: " + e.toString());
            isConnected = false;
        }
    }


    private static void createConnectSocket(BluetoothDevice remote_device) {
        try {
            socket = remote_device.createInsecureRfcommSocketToServiceRecord(uuid);
            Log.d(LOG_TAG, "Socket erfolgreich erstellt");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Fehler beim Erstellen des Sockets: " + e.toString());
            isConnected = false;
            instance = null;
        }

        adapter.cancelDiscovery();

        // Socket verbinden
        try {
            socket.connect();
            Log.d(LOG_TAG, "Socket connected");
        } catch (IOException e) {
            isConnected = false;
            Log.e(LOG_TAG, "Error while trying to connect socket: " + e.toString());
        }

        // Socket beenden, falls nicht verbunden werden konnte
        if (!isConnected) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error while trying to close socket: " + e.toString());

            }
        }
    }

//    @Override
    public static void closeConnection() {

        if (isConnected && streamOut != null) {
            isConnected = false;
            Log.d(LOG_TAG, "Trennen: Beende Verbindung");
            try {
                streamOut.flush();
                socket.close();
                instance = null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Fehler beim Beenden der Verbindung: " + e.toString());
            }
        } else
            Log.d(LOG_TAG, "Trennen: Keine Verbindung zum Beenden");
    }
}

