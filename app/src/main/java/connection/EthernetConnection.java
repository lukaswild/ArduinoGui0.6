package connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;


public class EthernetConnection implements IConnection {


    private final static String LOG_TAG = "EthernetConnection";
    private static String conName = ""; // Name der Connection
    private static String ipAddress = "";
    private static int conPort = 23; //Telnet
    private static EthernetConnection instance = null;


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

    @Override
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


    public static EthernetConnection initialiseConnection(String conName, String ipAddress) {
        if(instance == null) {
            instance = new EthernetConnection();
            EthernetConnection.conName = conName;
            EthernetConnection.ipAddress = ipAddress;
        }

        return instance;
    }


    @Override
    public void closeConnection() {
        instance = null;
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
