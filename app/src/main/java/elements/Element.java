package elements;


import java.util.ArrayList;

public abstract class Element {

	protected String name;  // TODO notwendig?
	protected String identifier;
    private boolean isFirstInteraction = true;

    private ArrayList<Integer> timeRecord = new ArrayList<Integer>();
    private ArrayList<Integer> dataRecord = new ArrayList<Integer>();
    private long millisFirstInteraction;

    protected int resource;
	
	public void setName(String name) {
		this.name = name;
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

    public boolean isFirstInteraction() {
        return isFirstInteraction;
    }

    public void setFirstInteraction(boolean isFirstInteraction) {
        this.isFirstInteraction = isFirstInteraction;
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

    public long getMillisFirstInteraction() {
        return millisFirstInteraction;
    }

    public void setMillisFirstInteraction(long millisFirstInteraction) {
        this.millisFirstInteraction = millisFirstInteraction;
    }

}
