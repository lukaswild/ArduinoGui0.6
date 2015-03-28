package connection;

public interface IConnection {
	
	public void sendData(String data);

    public void setConName(String name);

    public String getConName();

    public String getConNameDeclaration();

    public String getConAddressDeclaration();

    public void setConNameDeclaration(String name);

    public void setConAddressDeclaration(String address);

    public void setAddress(String address);

    public String getAddress();
	
	
}
