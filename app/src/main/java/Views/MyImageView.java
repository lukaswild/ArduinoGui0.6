package Views;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Lukas on 26.12.2014.
 */
public class MyImageView extends ImageView {
    private String name;



    private int id;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, String name) {
        super(context);
        this.name =  name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }
}