package connection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Simon on 16.02.2015.
 * Quelle: http://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
 * Mithilfe dieser Klasse kann überprüft werden, ob eine gültige IP-Adresse eingegeben wurde.
 *
 */
public class IPAddressValidator{

    private Pattern pattern;
    private Matcher matcher;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public IPAddressValidator(){
        pattern = Pattern.compile(IPADDRESS_PATTERN);
    }

    /**
     * Validate ip address with regular expression
     * @param ip ip address for validation
     * @return true valid ip address, false invalid ip address
     */
    public boolean isValid(final String ip){
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}