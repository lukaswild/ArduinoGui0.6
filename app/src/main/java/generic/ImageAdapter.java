package generic;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
import java.util.logging.Logger;

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
    private final String LOG_TAG = "ImageAdapter";
    private final String ELEMENT_NAME =  "element";
    private Activity activity;
    private int numberOfElements;

    public void update(int name, int id) {

        imgRes.put(id,name);

    }
    //das gleiche wie update
    public void updateTextRes(String element, int key){
        textRes.put(key, element);}

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
    public ImageAdapter(Context c, Activity activity, int numberOfElements) {
        mContext = c;
        this.activity = activity;
        this.numberOfElements = numberOfElements;

        //sollte verhindern, dass Buttons nach dem Drehen, sprich wenn der Standardkonstruktor wieder aufgerufen wird, wieder neu gezeichnet werden
        //wenn etwas vorhanden, dass soll das gezeichnet werden -> Intialisierung soll nur einmal passieren.
        for (int i =0;i<numberOfElements;i++){
            imgRes.put(i,R.drawable.add1); // TODO in die gui !!
        }
    }

    public void  setRessource(ArrayList<Element> elements){


    }

    public void copyTXT(int oldpos, int newpos){
        Log.d(LOG_TAG,"Im CopyTXT:"+oldpos+"/"+newpos);
        textRes.put(newpos,textRes.get(oldpos));

    }


    public int getCount() {
        return imgRes.size();

    }

    public Object getItem(int position) {

        return  imgRes.get(position);

    }

    @Override
    public long getItemId(int position) {
        return imgRes.get(position);
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getItemInt(int position){
            return imgRes.get(position);
        }



    // Hier wird eine neue View erzeugt
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final int positionFinal = position;

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
        if (imgRes.get(position)==R.drawable.button_off||imgRes.get(position)==R.drawable.button_on) {
           imgView.setImageResource(0);
        }
        setImgVisibility(txtView, position);

        imgView.setTag(getItemInt(position));


        if((Integer)imgView.getTag() == R.drawable.button_off) {
            imgView.setBackgroundResource(R.drawable.selector_btn_default);
        }
        else
            imgView.setBackgroundResource(0);

        Log.d(LOG_TAG,"txtres:"+getTextRes(position));
        Log.d(LOG_TAG,"pos:"+position);

        if (txtView.getVisibility()==View.VISIBLE){
            txtView.setText(getTextRes(position));
        }



        return view;
    }


    private void setImgVisibility(TextView txtView, int position){

        if (getItemInt(position)==R.drawable.button_off){
            txtView.setVisibility(View.INVISIBLE);
        }
        else if (getItemInt(position)==R.drawable.add1){
            txtView.setVisibility(View.INVISIBLE);
        }

        else if (getItemInt(position)==R.drawable.button_on){
            txtView.setVisibility(View.INVISIBLE);
        }

        else if (getItemInt(position)==R.drawable.switch_on){
            txtView.setVisibility(View.INVISIBLE);
        }
        else if (getItemInt(position)==R.drawable.switch_off){
            txtView.setVisibility(View.INVISIBLE);
        }
        else if (getItemInt(position)==R.drawable.lamp_off){
            txtView.setVisibility(View.INVISIBLE);
        }
        else if (getItemInt(position)==R.drawable.lamp_on){
            txtView.setVisibility(View.INVISIBLE);
        }
        else {
            txtView.setVisibility(View.VISIBLE);
        }

    }



    private class RunnableNotify implements Runnable {

        @Override
        public void run() {
            notifyDataSetChanged();

//            notifyDataSetInvalidated();
            Log.d("AAAAAAAAAAA", "In Thread");
        }
    }

    private class TaskNotify extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
//            notifyDataSetChanged();
            Log.d("AAAAAAAAAAA", "In AsyncTask");
            return  "";
        }
    }

    public HashMap<Integer, Integer> getImgRes() {
        return imgRes;
    }

}
