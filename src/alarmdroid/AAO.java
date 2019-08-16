package alarmdroid;

import java.util.ArrayList;

public class AAO {
	private ArrayList<String> m_strTrucks;
	
	private ArrayList<String> m_strTruckNames;
	
	private String m_strName;
	
	public AAO(String aStrName){	
		m_strName = aStrName;
		m_strTrucks = new ArrayList<String>();
		m_strTruckNames = new ArrayList<String>();
	}
	
	public void AddTruck(String aStrTruck, String aStrTruckName){
		m_strTrucks.add(aStrTruck);
		m_strTruckNames.add(aStrTruckName);
	}
	
	public ArrayList<String> GetTrucks(){
		return m_strTrucks;
	}

	public ArrayList<String> GetTruckNames(){
		return m_strTruckNames;
	}
	
	public String getName(){
		return m_strName;
	}

}
