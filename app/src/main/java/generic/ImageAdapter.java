package generic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arduinogui.R;

import java.util.ArrayList;
import java.util.HashMap;

import Views.MyImageView;
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
    private static HashMap<Integer, String> textRes = new HashMap<Integer, String>();
    private Context mContext;

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

    //das gleiche wie update
    public void updateTextRes(String element, int key){
        textRes.put(key, element);
    }
    public String getTextRes(int key){
        return  textRes.get(key);
    }


    public int getLength() {
        return length;
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
                imgRes.put(i,elements.get(i).getResource());
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

        final ImageView imgView = (ImageView) view.findViewById(R.id.imageview);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(length, length));
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView.setPadding(10, 10, 10, 3);
        TextView txtView = (TextView)view.findViewById(R.id.textView5);

        imgView.setImageResource(getItemInt(position));

        setImgVisability(txtView,position);


//        imgView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch(event.getAction()) {
//
//                    case MotionEvent.ACTION_DOWN:
//
//                        return true;
//
//                    case MotionEvent.ACTION_UP:
//
//                        return true;
//                }
//                return false;
//            }
//        });

        return view;


        ///ALTER IMAGEADAPTER///
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

}
private void setImgVisability(TextView txtView, int position){

    if (getItemInt(position)==R.drawable.pwm_0){
        txtView.setText(getTextRes(position));


        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_25_5){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_51){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_76_5){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_102){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_127_5){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_153){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_178_5){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_204){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }

    else if (getItemInt(position)==R.drawable.pwm_229_5){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_255){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }
    else if (getItemInt(position)==R.drawable.pwm_slider){

        txtView.setText(getTextRes(position));
        txtView.setVisibility(View.VISIBLE);
    }

    else {
        txtView.setVisibility(View.INVISIBLE);
    }
}


}
