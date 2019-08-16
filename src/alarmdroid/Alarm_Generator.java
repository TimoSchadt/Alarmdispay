package alarmdroid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import userInterface.Alarmdisplay;

public class Alarm_Generator {
	
	//Gesamte Alarmdepesche
	private String m_strInputString;
	//Header
	private String m_strHeader;
	//Einsatzort
	private String m_strEinsatzort;
	//Informationen
	private String m_strInformationen;
	
	//Bemerkung m_strBemerkung
	private String m_strBemerkung;
	//Stichwort m_strAlarmstichwort
	private String m_strAlarmstichwort;
	//Meldender m_strMeldender
	private String m_strMeldender;
	//Objekt m_strEinsatzobjekt
	private String m_strEinsatzobjekt;
	//Melder-Nr. m_strMeldernummer
	private String m_strMeldernummer;
	//Ort m_strOrt
	private String m_strOrt;
	//Ortsteil m_strOrtsteil
	private String m_strOrtsteil;
	//Bemerkung m_strOrtBemerkung
	private String m_strOrtBemerkung;
	//Stra�e m_strStreet
	private String m_strStreet;
	//Bemerkungen m_strZusatzinformationen
	private String m_strZusatzinformationen;
	//gedruckt am m_strAlarmierungszeit
	private String m_strAlarmierungszeit;
	//ist es Brand oder TH
	private boolean m_bBrand;
		
	private String m_strFilename;
	
	private Alarmdisplay m_pDisplay;
	
	ArrayList<String> m_aHome = new ArrayList<String>();
	

	private AAO m_AaoFireHome = new AAO("FireHome");
	private AAO m_AaoFireAway = new AAO("FireAway");
	private AAO m_AaoThHome = new AAO("ThHome");	
	private AAO m_AaoThAway = new AAO("ThAway");

	


	public Alarm_Generator(Alarmdisplay apDisplay){
		m_strInputString			= "";
		m_strHeader					= "";
		m_strEinsatzort				= "";
		m_strInformationen			= "";
		
		m_strBemerkung				= "";
		m_strAlarmstichwort			= "";
		m_strMeldender				= "";
		m_strEinsatzobjekt			= "";
		m_strMeldernummer			= "";
		m_strOrt					= "";
		m_strOrtsteil				= "";
		m_strStreet					= "";
		m_strOrtBemerkung 			= "";
		m_strZusatzinformationen	= "";
		m_strAlarmierungszeit		= "";
		m_strFilename				= "";
		m_bBrand					= false;

		m_pDisplay = apDisplay;

		
		m_aHome.add("Hainstadt");
		m_aHome.add("Wald-Amorbach");
		
		buildAAOs();
				
	}
	
	private void buildAAOs(){
		
		m_pDisplay.getEventLogger().logEvent("Build AAOs");

		readAAO(m_AaoFireHome,"Fire_Home.aao");
		readAAO(m_AaoFireAway,"Fire_Away.aao");
		readAAO(m_AaoThHome,"TH_Home.aao");
		readAAO(m_AaoThAway,"TH_Away.aao");
		
		m_pDisplay.getEventLogger().logEvent("Build AAOs: Done");

	}
	
	private void readAAO(AAO aAAO, String asFilename){
		
		m_pDisplay.getEventLogger().logEvent("Read AAO from File '" + asFilename +"'");
		
		FileReader fr;
		try {
			fr = new FileReader(asFilename);
		    BufferedReader br = new BufferedReader(fr);
			String actLine = br.readLine();		
		    while (!actLine.contains("EOF")){
		    	String strTruck = actLine.substring(0,actLine.indexOf('=')).trim();	
		    	String strTruckName = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();								
		    	aAAO.AddTruck(strTruck, strTruckName);
				actLine = br.readLine();	
		    }
		} catch (IOException e) {
			m_pDisplay.getEventLogger().logEvent(e.getMessage());
		}
		
		m_pDisplay.getEventLogger().logEvent("Read AAO from File '" + asFilename +"': Done");

	}
	
	public String getFileName(){
		return m_strFilename;
	}
	
	public void setFileName(String asFilename){
		m_strFilename = asFilename;
	}
	
	public String getInputString() {
		return m_strInputString;
	}

	public void setInputString(String inputString) {
		this.m_strInputString = inputString;
	}

	public String getAlarmstichwort(){
		return m_strAlarmstichwort;
	}
	
	public String getAlarmierungszeit(){
		return m_strAlarmierungszeit ;
	}
	
	public String getOrt(){
		return m_strOrt;
	}
	
	public String getOrtsteil(){
		if(m_strOrtsteil.isEmpty()){
			return m_strOrtsteil;
		}else{
			return " - " + m_strOrtsteil;
		}
	}
	
	public String getOrtBemerkung(){
		if(m_strOrtBemerkung.isEmpty()){
			return m_strOrtBemerkung;
		}else{
			return "\nBemerkung: " + m_strOrtBemerkung;
		}
	}
	
	public String getStreet(){
		if(m_strOrtsteil.isEmpty()){
			return m_strStreet;
		}else{
			return ", " + m_strStreet;
		}	
	}

	public String getAlarmDescription(){
		return m_strBemerkung;
	}
	
	
	public String getMeldender(){
		return m_strMeldender;
	}
	
	public String getEinsatzobjekt(){
		return m_strEinsatzobjekt;
	}
	
	public String getMeldernummer(){
		if(m_strMeldernummer.isEmpty()){
			return m_strMeldernummer;
		}else{
			return ", Meldernummer: " + m_strMeldernummer;
		}
	}

	public String getZusatzinformationen(){
		if(m_strZusatzinformationen.isEmpty()){
			return m_strZusatzinformationen;
		}else{
			return "Zusatzinformationen:\n " + m_strZusatzinformationen;
		}
	}
	
	public boolean isBrand(){
		return m_bBrand; 
	}
	
	public void checkAAO(){
		
		m_pDisplay.getEventLogger().logEvent("Check AAO");

		boolean bIsHome = false;
		
		AAO curAAO;
		
		for(int i = 0; i< m_aHome.size(); i++){
			if(m_aHome.get(i).compareTo(m_strOrtsteil) == 0){
				bIsHome = true;
			}
		}
				
		if(bIsHome){
			if(isBrand()){
				curAAO = m_AaoFireHome;
			}else{
				curAAO = m_AaoThHome;
			}
		}else{
			if(isBrand()){
				curAAO = m_AaoFireAway;
			}else{
				curAAO = m_AaoThAway;
			}
		}
		
		m_pDisplay.getEventLogger().logEvent("Check AAO: Chose AAO: " + curAAO.getName());
		
		for(int i = 0; i< curAAO.GetTruckNames().size(); i++){
			if(m_strInputString.contains(curAAO.GetTruckNames().get(i))){
				m_pDisplay.addTruck(curAAO.GetTrucks().get(i));
				m_pDisplay.getEventLogger().logEvent("Check AAO: Added Truck: " + curAAO.GetTrucks().get(i));
			}
		}
		
		m_pDisplay.getEventLogger().logEvent("Check AAO: Done ");
		
	}
	
	
	public boolean parse(){
		String strEinsatzort		= "Einsatzort";
		String strInformationen 	= "Informationen";
		
		String strStichwortLabel 	= "Stichwort ";
		String strBemerkung			= "Bemerkung ";
		String strMeldenderLabel 	= "Meldender ";
		String strObjektLabel		= "Objekt ";
		String strMelderNrLabel		= "Melder-Nr. ";
		String strOrtLabel 			= "Ort ";
		String strOrtsteilLabel		= "Ortsteil ";
		String strStreetLabel		= "Stra�e ";
		String strEMLabel			= "EM (St�rke/AGT)";
		String strZeitLabel			= "gedruckt am ";
		String strEOF				= "-1-";
		
		m_pDisplay.getEventLogger().logEvent("Parse Alarmmail");
		
		//Zuerst teile die Depesche in drei Teile
		int iEinsatzort = m_strInputString.indexOf(strEinsatzort,0);
		if(iEinsatzort<0){
			return false;
		}
		m_strHeader = m_strInputString.substring(0, iEinsatzort);
		try{
			m_strInformationen = m_strInputString.substring(m_strInputString.indexOf(strInformationen,0), m_strInputString.indexOf(strEMLabel,0));
			m_strInformationen = m_strInformationen.trim();
			m_strEinsatzort = m_strInputString.substring(m_strInputString.indexOf(strEinsatzort,0), m_strInputString.indexOf(strInformationen,0));

		}catch (Exception e){
			iEinsatzort = m_strInputString.indexOf(strEinsatzort,0);
			int iEM = m_strInputString.indexOf(strEMLabel,0);
			if(iEinsatzort<0 || iEM<0){
				return false;
			}
			m_strEinsatzort = m_strInputString.substring(iEinsatzort, iEM);
		}

		m_strEinsatzort= m_strEinsatzort.trim();

		/*
		 * Auswertung des Headers
		 */
		
		//Zusatzbemerkung zum Stichwort kann vorhanden sein
		try{
			m_strBemerkung = m_strHeader.substring(m_strHeader.indexOf(strBemerkung), m_strHeader.indexOf(strStichwortLabel, m_strHeader.indexOf(strBemerkung))).replace(strBemerkung, "");
			m_strBemerkung = m_strBemerkung.trim();
		}catch (Exception e) {
			//Bemerkung nicht vorhanden, kein Problem
		
		}	
		
		int iStichwortLabel = m_strHeader.indexOf(strStichwortLabel);
		int iMeldenderLabel = m_strHeader.indexOf(strMeldenderLabel, iStichwortLabel);
		if(iStichwortLabel<0 || iMeldenderLabel<0){
			return false;
		}
		//Alarmstichwort muss immer vorhanden sein
		m_strAlarmstichwort = m_strHeader.substring(iStichwortLabel, iMeldenderLabel).replace(strStichwortLabel, "");
		m_strAlarmstichwort = m_strAlarmstichwort.trim();
		
		if (m_strAlarmstichwort.startsWith("F")){
			m_bBrand = true;
		}else{
			m_bBrand = false;
		}
		
		//Meldender muss immer vorhanden sein
		m_strMeldender = m_strHeader.substring(iMeldenderLabel, m_strHeader.length()).replace(strMeldenderLabel, "");
		m_strMeldender = m_strMeldender.trim();
		

		/*
		 * Auswertung des Einsatzortes
		 */
		try{
			// Parse Objekt
			m_strEinsatzobjekt = m_strEinsatzort.substring(m_strEinsatzort.indexOf(strObjektLabel), m_strEinsatzort.indexOf(strMelderNrLabel, m_strEinsatzort.indexOf(strObjektLabel))).replace(strObjektLabel, "");
			m_strEinsatzobjekt = m_strEinsatzobjekt.trim();	
		}catch(Exception e){
			
		}
		
		try{
			// Parse Melder-Nr.
			m_strMeldernummer = m_strEinsatzort.substring(m_strEinsatzort.indexOf(strMelderNrLabel), m_strEinsatzort.indexOf(strOrtLabel, m_strEinsatzort.indexOf(strMelderNrLabel))).replace(strMelderNrLabel, "");
			m_strMeldernummer = m_strMeldernummer.trim();	
		}catch(Exception e){
			
		}

		int iOrtLabel = m_strEinsatzort.indexOf(strOrtLabel);
		int iOrtsteilLabel = m_strEinsatzort.indexOf(strOrtsteilLabel, iOrtLabel);
		if(iOrtLabel < 0 || iOrtsteilLabel < 0){
			return false;
		}
		// Parse Ort
		m_strOrt = m_strEinsatzort.substring(iOrtLabel,iOrtsteilLabel).replace(strOrtLabel, "");
		m_strOrt = m_strOrt.trim();
		// Parse Ortsteil
		int iStreetLabel = m_strEinsatzort.indexOf(strStreetLabel, iOrtsteilLabel);		
		if(iStreetLabel < 0){
			return false;
		}
		m_strOrtsteil = m_strEinsatzort.substring(iOrtsteilLabel, iStreetLabel).replace(strOrtsteilLabel, "");
		m_strOrtsteil = m_strOrtsteil.trim();
		try{
			//Parse OrtBemerkung 
			m_strOrtBemerkung = m_strEinsatzort.substring(m_strEinsatzort.indexOf(strBemerkung), m_strEinsatzort.length()).replace(strBemerkung, "");
			m_strOrtBemerkung = m_strOrtBemerkung.trim();
			// Parse Stra�e
			m_strStreet = m_strEinsatzort.substring(m_strEinsatzort.indexOf(strStreetLabel),  m_strEinsatzort.indexOf(strBemerkung, m_strEinsatzort.indexOf(strStreetLabel))).replace(strStreetLabel, "");
		}catch(Exception e){
			// Parse Stra�e
			m_strStreet = m_strEinsatzort.substring(m_strEinsatzort.indexOf(strStreetLabel), m_strEinsatzort.length()).replace(strStreetLabel, "");	
		}

		m_strStreet = m_strStreet.trim();

		/*
		 * Auswertung der Informationen
		 */
		// Parse street
		try{
			// Parse Melder-Nr.
			m_strZusatzinformationen = m_strInformationen.substring(m_strInformationen.indexOf(strBemerkung),  m_strInformationen.length()).replace(strBemerkung, "");
			m_strZusatzinformationen = m_strZusatzinformationen.trim();
		}catch(Exception e){
			
		}
		
		
		// Parse Alarmierungszeit
		m_strAlarmierungszeit = m_strInputString.substring(m_strInputString.indexOf(strZeitLabel),m_strInputString.indexOf(strEOF, m_strInputString.indexOf(strZeitLabel))).replace(strZeitLabel, "");
		m_strAlarmierungszeit = m_strAlarmierungszeit.trim();
	
		m_pDisplay.getEventLogger().logEvent("Parse Alarmmail: Done");
		return true;

	}

}
