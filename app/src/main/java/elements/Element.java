package elements;


public abstract class Element {

	protected String name;  // TODO notwendig?
	protected String identifier;


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

}
