package Views;

import android.content.Context;
import android.widget.TextView;

import com.example.arduinogui.R;

/**
 * Created by Lukas on 12.02.2015.
 */
public class PwmView extends MyImageView {

    //Die Textview soll den Wert des PWM-Elementes anzeigen
    public TextView txtview = (TextView) findViewById(R.id.textPWM);

    public PwmView(Context context) {
        super(context);
    }
}
