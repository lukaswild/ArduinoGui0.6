package connection;

public interface IConnection {
	
	public void sendData(String data);
	
//	public String receiveData();
	
//	public void closeConnection();

    public void setConName(String name);

    public String getConName();

    public String getConNameDeclaration();

    public String getConAddressDeclaration();

    public void setAddress(String address);

    public String getAddress();
	
	
}
