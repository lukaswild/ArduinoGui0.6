package elements;


import java.util.ArrayList;

public abstract class Element {

	protected String name;  // TODO notwendig?
    protected String kind;
	protected String identifier;
    private static boolean isFirstInteraction = true;

    private ArrayList<Integer> timeRecord = new ArrayList<Integer>();
    private ArrayList<Integer> dataRecord = new ArrayList<Integer>();
    private static long millisFirstInteraction;

    protected int resource;
	
	protected void setName(String name) {
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

}
