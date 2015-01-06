package connection;

/**
 * Generieren eines Codes für die übertragung von änderungen an den Arduino.
 * An erster Stelle steht entweder ein "B" für Boolean oder ein "P" für Pwm.
 * Der Wert, der gesetzt werden soll, steht an zweiter Stelle.
 * An dritter Stelle folgt der Identifier, über den ein Device am Arduino erkannt wird.
 * Getrennt werden die Werte jeweils durch einen Strichpunkt. 
 */
public class CodeGenerator {

	public static String generateCodeToSend(boolean msg, String identifier) {
		String code = "";

		boolean msgBool = (boolean)msg;

		if(msgBool) // wenn msgBool true ist
			code = "B1";
		else 
			code = "B0";

		code += "I" + identifier;
		return code; 
	}
	

	public static String generateCodeToSend(String msg, String identifier) { 
		String code = "P" + msg + "I" + identifier;// P für PWM
		return code; 
	}

}

/*Code des Idenifyers:

für PWM:
P + message + I + identifyer; z.B. P12I2

für Bool:
B+ message + I + identifyer; z.B. B0IA2 -> A2 ist analog2 (identifyer)

*/

