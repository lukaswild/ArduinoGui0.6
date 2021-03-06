package generic;

import android.util.Log;

/**
 * Generieren eines Codes für die übertragung von änderungen an den Arduino.
 * An erster Stelle steht entweder ein "B" für Boolean oder ein "P" für Pwm.
 * Der Wert, der gesetzt werden soll, steht an zweiter Stelle.
 * An dritter Stelle folgt der Identifier, über den ein Device am Arduino erkannt wird.
 * Getrennt werden die Werte jeweils durch einen Strichpunkt. 
 */
public class CodeGenerator {

    private final static String LOG_TAG = "CodeGenerator";

	public static String generateCodeToSend(boolean msg, String identifier) {
        Log.d(LOG_TAG, "Generiere Code...");
        String code = "";

		boolean msgBool = (boolean)msg;

		if(msgBool) // wenn msgBool true ist
			code = "B1";
		else 
			code = "B0";

		code += "I" + identifier;
        code.trim();
		return code; 
	}
	

	public static String generateCodeToSend(String msg, String identifier) {
        Log.d(LOG_TAG, "Generiere Code...");
		String code = "W" + msg + "I" + identifier;// W für PWM
		return code; 
	}

}

/*Code des Idenifyers:

für PWM:
P + message + I + identifyer; z.B. P12I2

für Bool:
B+ message + I + identifyer; z.B. B0IA2 -> A2 ist analog2 (identifyer)

*/

