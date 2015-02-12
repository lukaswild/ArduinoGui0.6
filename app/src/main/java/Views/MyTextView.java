package Views;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by Lukas on 12.02.2015.
 */
public class MyTextView extends TextView {

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    private  int id;



    public MyTextView(Context context) {
        super(context);
    }
}
