package generic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import Views.MyImageView;
import Views.PwmView;
import elements.Element;

//import com.example.test.R;

/**
 * Created by Lukas on 17.12.2014.
 */
public class ImageAdapter extends BaseAdapter {

    public static class ViewHolderItem {
        MyImageView imageview;
    }
    //Felder
    private static HashMap<Integer, Integer> imgRes = new HashMap<Integer, Integer>();
    private Context mContext;


    public int getLength() {
        return length;
    }

    //vorher, war langth und width angegeben, da die symbole quadratisc sind, ist das nicht gut
    //jetzt wird nurmehr die länge lenght angegeben, und diese dann für breite und länge verwendet
    //length kann über den slider in den Einstellungen gesetzt werden
    private int length=125;
    private int init=0;
    private final String LOG_TAG = "ImageAdapter";
    private final String ELEMENT_NAME =  "element";
    private static int elementCount = 0;

    public void update(int name, int id) {

        imgRes.put(id,name);


    }

    public void setMap(HashMap<Integer, Integer> imgRes) {
        this.imgRes = imgRes;
    }

    public void remove(int key) {
        imgRes.remove(key);
    }

    public void setLength(int length) {
        this.length = length;
    }


    //Die Methoden com.example.test.ImageAdapter, getCount, getItem, getItemId müssen überschrieben werden
    public ImageAdapter(Context c) {
        mContext = c;

        //sollte verhindern, dass buttons nach dem Drehen, sprich wenn der standardkonstruktor wieder aufgerufen wird, wieder neu gezeichnet werden
        //wenn etwas vorhanden, dass soll das gezeichnet werden -> intialisierung soll nur einmal passieren.
        for (int i =0;i<40;i++){
            imgRes.put(i,R.drawable.add1); // TODO in die gui !!
        }
    }

    public void  setRessource(ArrayList<Element> elements){
        for (int i =0;i<elements.size();i++){

            if (elements.get(i)!=null){
                imgRes.put(i,elements.get(i).getRessource());
            }
        }

    }
    public int getCount() {
        return imgRes.size();

    }

    public Object getItem(int position) {

        return  imgRes.get(position);

    }


    public int getItemInt(int position){
        return imgRes.get(position);
    }


    public long getItemId(int position) {
        return 0;
    }


    // Hier wird eine neue View erzeugt
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.image, null);
        }

        else {

            view = convertView;
        }

        ImageView imgView = (ImageView) view.findViewById(R.id.imageview);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(length, length));
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView.setPadding(10, 10, 10, 3);
        TextView albNameView = (TextView) view.findViewById(R.id.textView5);

        /*if (imgRes.size() > 0) {

            for (int i = 0; i < imgRes.size(); i++) {

                //imgView.setBackgroundResource(getItemInt(i));
                imgView.setImageResource(getItemInt(i));
                albNameView.setText("0");


            }

        }*/
        imgView.setImageResource(getItemInt(position));
        if (getItemInt(position)==R.drawable.pwm_0){
            albNameView.setVisibility(View.VISIBLE);
            albNameView.setText("0");
        }

        return view;


        /*
        // ImageView imageView;
        MyImageView imageView;
        //ViewHolderItem viewHolder;
        if (convertView == null) {
            // wenn die view leer ist -> Daten sollen hineingeschrieben werden
            imageView = new MyImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(length, length));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(10, 10, 10, 10);
            imageView.setName(ELEMENT_NAME + position);

        }
        else {
            imageView = (MyImageView) convertView;//auskommentieren ?
        }

        imageView.setImageResource((getItemInt(position)));
        imageView.setId(position);

        if (getItemInt(position)==R.drawable.button_off){
            imageView.setName("button");
        }

        return imageView;

    }
*/

}}
