package elements;


public class Element {

	protected String name;  // TODO notwendig?
	protected String identifier;


    protected int ressource;
	
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

    public int getRessource() {
        return ressource;
    }

    public void setRessource(int ressource) {
        this.ressource = ressource;
    }

}
