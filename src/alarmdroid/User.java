package alarmdroid;


public class User {
	private String adress;
	private String Password;
	private Boolean ELW;
	
	public User(String adress, String password, Boolean eLW){
		this.adress = adress;
		Password = password;
		ELW = eLW;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public Boolean isELW() {
		return ELW;
	}
	public void setELW(Boolean eLW) {
		ELW = eLW;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
}
