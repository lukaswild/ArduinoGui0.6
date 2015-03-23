package elements;


import android.util.Log;

import java.util.ArrayList;

public abstract class Element {

	protected String name;
    protected String kind;
	protected String identifier;
    private static boolean isFirstInteraction = true;

    private ArrayList<Integer> timeRecord = new ArrayList<Integer>();
    private ArrayList<Integer> dataRecord = new ArrayList<Integer>();
    private static long millisFirstInteraction;
    private static String LOG_TAG = "Element";

    protected int resource;
	
	public void setName(String name) {
		this.name = name;
	}


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }


    public void Element(){

    }

	public String getName() {
		return name; 
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier; 
	}

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

//    public abstract void setResource(boolean status);

    public static boolean isFirstInteraction() {
        return isFirstInteraction;
    }

    public static void setFirstInteraction(boolean isFirstInteraction) {
        Element.isFirstInteraction = isFirstInteraction;
    }

    public ArrayList<Integer> getTimeRecord() {
        return timeRecord;
    }

    public void setTimeRecord(ArrayList<Integer> timeRecord) {
        this.timeRecord = timeRecord;
    }

    public ArrayList<Integer> getDataRecord() {
        return dataRecord;
    }

    public void setDataRecord(ArrayList<Integer> dataRecord) {
        this.dataRecord = dataRecord;
    }

    public static long getMillisFirstInteraction() {
        return millisFirstInteraction;
    }

    public static void setMillisFirstInteraction(long millisFirstInteraction) {
        Element.millisFirstInteraction = millisFirstInteraction;
    }

    public void registerTimeRecord(int status) {

        if(isFirstInteraction()) {
            setMillisFirstInteraction(System.currentTimeMillis());
            setFirstInteraction(false);
        }

        long timeDifference = (System.currentTimeMillis() - getMillisFirstInteraction()) / 1000;
        /*
        Der Graph wird so gezeichnet, dass die jeweilen DataPoints mit einer Geraden verbunden werden.
        Um schöne Sprünge von 0 auf 1 zu haben, muss deshalb der jeweils vorherige Eintrag mit der aktuellen Zeit
        nochmals in die Liste eingetragen werden
         */
        if(!getTimeRecord().isEmpty() && !getDataRecord().isEmpty()) {
            getTimeRecord().add((int) timeDifference);
            getDataRecord().add(getDataRecord().get(getDataRecord().size() - 1));
        } else {
            getTimeRecord().add((int) timeDifference);
            getDataRecord().add(0);
        }

        getTimeRecord().add((int) timeDifference);
        getDataRecord().add(status);
        Log.d(LOG_TAG, "Neuer Status aufgezeichnet");
    }

}
