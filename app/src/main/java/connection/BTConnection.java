
package connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import main.MainActivity;

public class BTConnection implements IConnection {

    private int id = 0;
    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String LOG_TAG = "BTConnection";

    private String conNameDeclaration;
    private String conAddressDeclaration;
    private static String conName;

    private static BluetoothAdapter adapter = null;
    private static BluetoothSocket socket = null;
//    private static BufferedOutputStream streamOut = null; // TODO BufferedOutputStream - Übertragung geht nicht
//    private static BufferedInputStream streamIn = null; // TODO BufferedInputStream - Übertragung geht nicht
    private static InputStream streamIn = null;
    private static OutputStream streamOut = null;

    private static boolean isConnected = false;
    private static String macAddress; // MAC Adresse des Bluetooth Adapters
    private static BTConnection instance = null; //Instanz für Singleton-Pattern


    //getter und setter


    public static BluetoothAdapter getAdapter() {
        return adapter;
    }

    public static void setAdapter(BluetoothAdapter adapter) {
        BTConnection.adapter = adapter;
    }


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
    public void setConName(String conName) {
        BTConnection.conName = conName;
    }

    @Override
    public String getAddress() {
        return macAddress;
    }

    @Override
    public void setAddress(String macAddress) {
        BTConnection.macAddress = macAddress;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static BTConnection getInstance() {
        return instance;
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
            Log.d(LOG_TAG, "Senden der Daten: " + data);
            try {
                streamOut.write(msgBuffer);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Fehler beim Senden von Daten: " + e.toString());
            }
        } else {
            Log.e(LOG_TAG, "Fehler beim Senden der Daten: Keine Verbindung");
        }
    }


//    @Override
    public static String receiveData() {
        SystemClock.sleep(400); // Warten bis Bestätigungsdaten von Arduino am Smartphone angelangt sind: 200 ms sind zu wenig, 300 passen --> zur Sicherheit 400

        byte[] buffer = new byte[1024]; // Puffer
        int laenge; // Anzahl empf. Bytes
        String data = "";
        try {
            Log.d(LOG_TAG, "StreamIn: " + streamIn.available());
            if (streamIn.available() > 0) {
                laenge = streamIn.read(buffer);
                Log.d(LOG_TAG, "Anzahl empfangener Bytes : " + String.valueOf(laenge));

                // Message zusammensetzen:
                for (int i = 0; i < laenge; i++) {
                    data += (char) buffer[i];
                }

                Log.d(LOG_TAG, "Nachricht : " + data);
            } else
                Log.e(LOG_TAG, "InputStream nicht verfügbar ");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Fehler beim Empfangen der Daten : " + e.toString());
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
        // TODO BT Berechtigungsanfrage automatisch, wenn BT disabled

        Log.d(LOG_TAG, "Verbinden mit " + macAddress + "...");

        if(instance == null) {
            instance = new BTConnection();
            BTConnection.isConnected = true; // Auf true setzen, wenn etwas schief gehen sollte, wird dieser Wert auf false gesetzt
            BTConnection.adapter = BluetoothAdapter.getDefaultAdapter();
            if(BTConnection.adapter == null) {
                Log.e(LOG_TAG, "Bluetooth nicht unterstützt");
                return false;
            }



            BTConnection.macAddress = macAddress;
            BTConnection.conName = conName;
            BluetoothDevice remote_device = null;

                remote_device = adapter.getRemoteDevice(BTConnection.macAddress);
                createConnectSocket(remote_device); // Socket erstellen und verbinden

                // TODO Toast ausgeben, wenn MAC ungültig und ToggleButton nicht verbinden (kann nur über Activity gemacht werden)

            // Outputstream erstellen:
            createOutputStream();

            // Inputstream erstellen
            createInputStream();

            if (isConnected) {
                // Aufgebaute Verbindung als aktuell verwendete in der MainActivity setzen (currentConnection)
                MainActivity.setCurrentConnection(instance);
                Log.d(LOG_TAG, "Bluetooth-Verbindung erfolgreich initialisiert");
                return true;
            }
            else
                Log.e(LOG_TAG, "Fehler beim Aufbauen der Bluetooth-Verbindung");
        }
        return false;
    }


    private static void createInputStream() {
        try {
            streamIn = socket.getInputStream();
            Log.d(LOG_TAG, "InputStream erzeugt");
            Log.d(LOG_TAG, "" + streamIn.available());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fehler beim Erzeugen des InputStreams: " + e.toString());
            isConnected = false;
        }
    }


    private static void createOutputStream() {
        try {
            streamOut = socket.getOutputStream();
            Log.d(LOG_TAG, "OutputStream erzeugt");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Fehler beim Erzeugen des OutputStreams: " + e.toString());
            isConnected = false;
        }
    }


    private static void createConnectSocket(BluetoothDevice remote_device) {
        try {
            socket = remote_device.createInsecureRfcommSocketToServiceRecord(uuid);
            Log.d(LOG_TAG, "Socket erfolgreich erzeugt");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Fehler beim Erzeugen des Sockets: " + e.toString());
            isConnected = false;
            instance = null;
        }

        adapter.cancelDiscovery();

        // Socket verbinden
        try {
            socket.connect();
            Log.d(LOG_TAG, "Socket verbunden");
        } catch (IOException e) {
            isConnected = false;
            instance = null;
            Log.e(LOG_TAG, "Fehler beim Verbinden des Sockets : " + e.toString());
        }

        // Socket beenden, falls nicht verbunden werden konnte
        if (!isConnected) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Fehler beim Schließen des Sockets: " + e.toString());

            }
        }
    }

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
            Log.e(LOG_TAG, "Trennen: Keine Verbindung zum Beenden");
    }
}