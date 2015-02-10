package connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import main.MainActivity;


public class EthernetConnection implements IConnection {

    private int id;

    private String conNameDeclaration;
    private String conAddressDeclaration;
    private final static String LOG_TAG = "EthernetConnection";
    private static String conName = ""; // Name der Connection
    private static String ipAddress = "";
    private static int conPort = 23; //Telnet
    private static EthernetConnection instance = null;


    //getter, setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getConNameDeclaration() {
        return conNameDeclaration;
    }

    @Override
    public void setConNameDeclaration(String conNameDeclaration) {
        this.conNameDeclaration = conNameDeclaration;
    }

    @Override
    public String getConAddressDeclaration() {
        return conAddressDeclaration;
    }

    @Override
    public void setConAddressDeclaration(String conAddressDeclaration) {
        this.conAddressDeclaration = conAddressDeclaration;
    }

    //Konstruktoren
    private EthernetConnection() {}


    @Override
    public String getConName() {
        return conName;
    }

    @Override
    public void setConName(String conName) {
        EthernetConnection.conName = conName;
    }

    @Override
    public String getAddress() {
        return ipAddress;
    }

    @Override
    public void setAddress(String ipAddress) {
        EthernetConnection.ipAddress = ipAddress;
    }




    @Override
    public void sendData(String data) {
        // Network operation thread (AsyncTask)
        new SocketOperationSend().execute(data);
    }

//    @Override
    public String receiveData() {

        String message = "";

        try {
            Void v1 = null; // dummy data for parameter for AsyncTask
            message = new SocketOperationReceive().execute(v1).get();
            Log.d(LOG_TAG, "Empfangene Daten: " + message);
        } catch(InterruptedException  e) {
            Log.e(LOG_TAG, "Fehler: Datenübertragung wurde unterbrochen - Keine Daten empfangen");
        } catch (ExecutionException e) {
            Log.e(LOG_TAG, "Fehler beim Empfangen der Daten - Keine Daten empfangen");
        }
        return message;
    }


    public static EthernetConnection createAttributeCon(String conNameDeclaration, String conAddressDeclaration) {
        EthernetConnection conDeclaration = new EthernetConnection();
        conDeclaration.setConNameDeclaration(conNameDeclaration);
        conDeclaration.setConAddressDeclaration(conAddressDeclaration);

        return conDeclaration;
    }


    public static boolean initialiseConnection(String conName, String ipAddress) {
        if(instance == null) {
            instance = new EthernetConnection();
            EthernetConnection.conName = conName;
            EthernetConnection.ipAddress = ipAddress;
            MainActivity.setCurrentConnection(instance); // Diese Verbindung als aktuelle Verbindung setzen
            return true;
        }
        return false;
    }


    //    @Override
    public void closeConnection() {
        instance = null;
    }

    @Override
    public String toString() {
        return conName;
    }


    /**
     * AsyncTask thread to send data to the server.
     * @author Simon Bodner
     */
    private class SocketOperationSend extends AsyncTask<String, Void, Void> {


        @Override
        /**
         * Create a new socket, an OutputStreamWriter and send the data to the server.
         */
        protected Void doInBackground(String... params) {

            try {
                Socket socket = new Socket(ipAddress, conPort);
                OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
                writer.write(params[0]);
                Log.d("SENDING", "Data sent");
                writer.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error at sending data!!!");
            }

            return null;
        }
    }


    /**
     * AsyncTask thread to receive data from the server.
     * @author Simon Bodner
     */
    private class SocketOperationReceive extends AsyncTask<Void, Void, String> {

        @Override
        /**
         * Create a new socket, InputStreamReader, BufferedReader and read data from the server.
         */
        protected String doInBackground(Void... params) {

            String message = "";
            try {
                Socket socket = new Socket(ipAddress, conPort);
                InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);

                message = reader.readLine();
                System.out.println("New message: " + message);
                reader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error at receiving data!!!");
            }

            return message;
        }
    }
}
