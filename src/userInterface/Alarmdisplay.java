package userInterface;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapKit.DefaultProviders;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import alarmdroid.AAO;
import alarmdroid.Alarm_Generator;
import alarmdroid.Alarmdroid;

import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Alarmdisplay extends JFrame{

    private boolean m_bDarkenScreen = false;
    private boolean m_bIsFullScreen = false;

    private int m_iResetTime = 0;
    private int m_iPrevX,m_iPrevY,m_iPrevWidth,m_iPrevHeight;
    private int m_iResX = getToolkit().getScreenSize().width;
    private int m_iResY = getToolkit().getScreenSize().height;

	private static final long serialVersionUID = 1L;

    private String m_sMailadress="";
    private String m_sPassword="";
    private String m_sPrintername="";
    private String m_strSenders="";
    private String m_strHomeAdress;
    private String m_strHomeName;
    
    private Date m_dLastAlert; 
    
    private final LongLatService m_tDirectionService = new LongLatService();
    private final JXMapKit m_mapKit = new JXMapKit();

	private JPanel m_contentPane = new JPanel();
	
    private final Box m_InformationVerticalBox = Box.createVerticalBox();
    private final Box m_AlarmObjectVerticalBox = Box.createVerticalBox();
    private final Box m_TrucksHorizontalBox = Box.createHorizontalBox();
    private final Box m_ControlsHorizontalBox = Box.createHorizontalBox();
 
    private final JTextArea m_lblObjectAddress = new JTextArea();
    private final JTextArea m_lblObjectInformation = new JTextArea();
    private final JTextArea m_lblObjectName = new JTextArea();
    
    private final JLabel m_lblAlarmTime = new JLabel("");
    private final JLabel m_lblAlarmKeyword = new JLabel("");
    private final JLabel lblAlarmDescription = new JLabel("");
    private final JLabel m_lblEmpty = new JLabel(" ");
    
    private final JButton m_btnOpenEDP = new JButton("EDP \u00D6ffnen");
    private final JButton m_btnRestore = new JButton("Zur\u00FCcksetzen");
    
    private ArrayList<JLabel> m_lTruckLabels = new ArrayList<JLabel>();
    
    private Alarm_Generator m_pAlarmGenerator;
	private Alarmdroid m_pAlarmDroid;
	private EventLogger m_pEventLogger;
    
    
    
    public static void main(String[] args) {
    	Alarmdisplay frame = new Alarmdisplay();
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
         frame.setVisible(true);
    }
    
	public Alarmdisplay(){
		// Call base constructor
	    super("Alarmdisplay");
	    
	    m_pEventLogger = new EventLogger("Alarmdisplay.log");
	    m_pEventLogger.logEvent("EventLog started");
	    // Read the config from .cfg file
	    readConfig();
	    
	    // Initalize the Alarmdroid 
	    m_pAlarmDroid = new  Alarmdroid(this,m_sMailadress, m_sPassword, m_strSenders);
	    
	    // Draw the GUI
	    initGUI();
	    
	    // Reset the GUI
	    reset();
	    
	    // If wanted set the screen to black
		if(m_bDarkenScreen){
			setBlack();
		}
		
		
 		// Enter the inf loop
 		while(true){
 			// Get actual date and time
			Date dActDate = new Date();
			
			// Ask the Alarmdroid if there is any alarm
        	m_pAlarmGenerator = m_pAlarmDroid.getAlarm();
        	
        	// if there is a alarm show it
    		if (m_pAlarmGenerator != null){
    			showAlarm(m_pAlarmGenerator);
    		}else{
    			// if not check if there is a deprecated alarm on the screen and reset if so         			
    			if((m_iResetTime>0) && (m_dLastAlert != null) && ((dActDate.getTime() - m_dLastAlert.getTime()) > 1000*60*m_iResetTime)){
    				m_dLastAlert = null;
	    			reset();
	        		if(m_bDarkenScreen){
	            		setBlack();
	        		}     		
        		}
    		}
    		try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				m_pEventLogger.logEvent(e.getMessage());
			}
    	}
	    
	}


    

  private void initGUI(){
	  //Initialize the basic Frame
	  
	  m_pEventLogger.logEvent("Initialize GUI");

	  setTitle("Alarmdisplay");
	  setBackground(Color.BLACK);
	  m_contentPane.setBackground(Color.GRAY);
	  m_contentPane.setBounds(0, 0, m_iResX, m_iResY);  
	  setContentPane(m_contentPane);
      m_contentPane.setLayout(new BorderLayout(25, 25));

	  // Ensure the Frame is FullScreen
	  if(m_bIsFullScreen == false){	 
	      m_iPrevX = getX();
		  m_iPrevY = getY();
		  m_iPrevWidth = getWidth();
		  m_iPrevHeight = getHeight();
		  dispose(); 
		  setUndecorated(true);
	      setBounds(0,0,getToolkit().getScreenSize().width,getToolkit().getScreenSize().height);
	      setVisible(true);
	      m_bIsFullScreen = true;
      }else{
            setVisible(true);
            setBounds(m_iPrevX, m_iPrevY, m_iPrevWidth, m_iPrevHeight);
            dispose();
            setUndecorated(false);
			setVisible(true);
            m_bIsFullScreen = false;
       }
	  
      // Initialize the OpenStreetMap
	  m_mapKit.setBorder(new LineBorder(new Color(0, 0, 0), 4));
      m_mapKit.setName("mapKit");
      m_contentPane.add(m_mapKit, BorderLayout.CENTER);
      m_mapKit.setDefaultProvider(DefaultProviders.OpenStreetMaps);
      m_mapKit.setZoom(1);
      
      //Add the vertical box fpr the alarm information
      m_contentPane.add(m_InformationVerticalBox, BorderLayout.NORTH);
      
      // Add the label to display the alarm date and time
      m_lblAlarmTime.setHorizontalAlignment(SwingConstants.LEFT);
      m_lblAlarmTime.setForeground(Color.WHITE);
      m_lblAlarmTime.setFont(new Font("Dialog", Font.BOLD, 40));
      m_lblAlarmTime.setBackground(Color.RED);
      m_InformationVerticalBox.add(m_lblAlarmTime);

      // Add the label to display the alarm keyword
      m_lblAlarmKeyword.setForeground(Color.white);
      m_lblAlarmKeyword.setFont(new Font("Dialog", Font.BOLD, 80));
      m_lblAlarmKeyword.setText("Feuerwehr Breuberg-Hainstadt");
      m_lblAlarmKeyword.setBackground(Color.green);
      m_InformationVerticalBox.add(m_lblAlarmKeyword);

      // Add the label to display the alarm description
      lblAlarmDescription.setForeground(Color.white);
	  lblAlarmDescription.setFont(new Font("Dialog", Font.BOLD, 40));
	  lblAlarmDescription.setBackground(Color.red);
	  m_InformationVerticalBox.add(lblAlarmDescription);

	  // Add the box for the controls
	  m_ControlsHorizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
	  m_InformationVerticalBox.add(m_ControlsHorizontalBox);
	  
	  // Add place holder
	  m_ControlsHorizontalBox.add(m_lblEmpty);
	  
	  // Add open EDP button
	  m_ControlsHorizontalBox.add(m_btnOpenEDP);
	  m_btnOpenEDP.setAlignmentX(Component.CENTER_ALIGNMENT);
	  m_btnOpenEDP.setHorizontalAlignment(SwingConstants.RIGHT);
	  m_btnOpenEDP.setFont(new Font("Tahoma", Font.BOLD, 25));
	  m_btnOpenEDP.addMouseListener(new MouseAdapter() {
		  @Override
		  public void mouseClicked(MouseEvent arg0) {
			  try {
				  Desktop.getDesktop().browse(new URI("https://kats.odenwaldkreis.de"));
					  } catch (URISyntaxException | IOException e) {
				  e.printStackTrace();
			  } 
		  }
	  	});
	  // Add place holder
	  m_ControlsHorizontalBox.add(m_lblEmpty);
	  
	  // Add restore button
	  m_ControlsHorizontalBox.add(m_btnRestore);
	  m_btnRestore.setAlignmentX(Component.CENTER_ALIGNMENT);
  	  m_btnRestore.setFont(new Font("Tahoma", Font.BOLD, 25));
	  m_btnRestore.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			reset();
    			if(m_bDarkenScreen){
    				setBlack();
    			}
    		}
    	});

	  // Add Box for alarm object
	  m_contentPane.add(m_AlarmObjectVerticalBox, BorderLayout.SOUTH);
      
	  // Add label to display object name
	  m_lblObjectName.setBackground(Color.GRAY);
      m_lblObjectName.setForeground(Color.WHITE);
      m_lblObjectName.setFont(new Font("Dialog", Font.BOLD, 40));
      m_lblObjectName.setAlignmentX(0.0f);
      m_AlarmObjectVerticalBox.add(m_lblObjectName);
      
      // Add label to display object address
      m_lblObjectAddress.setBackground(Color.GRAY);
      m_lblObjectAddress.setAlignmentX(Component.LEFT_ALIGNMENT);
      m_lblObjectAddress.setFont(new Font("Dialog", Font.BOLD, 40));
      m_lblObjectAddress.setForeground(Color.WHITE);
      m_AlarmObjectVerticalBox.add(m_lblObjectAddress);
      
      // Add label to display object information
      m_lblObjectInformation.setBackground(Color.GRAY);
      m_lblObjectInformation.setForeground(Color.WHITE);
      m_lblObjectInformation.setFont(new Font("Dialog", Font.BOLD, 25));
      m_lblObjectInformation.setAlignmentX(0.0f); 
      m_AlarmObjectVerticalBox.add(m_lblObjectInformation);
      
      // Add the box to display the requested trucks
      m_TrucksHorizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
      m_AlarmObjectVerticalBox.add(m_TrucksHorizontalBox);
      
	  m_pEventLogger.logEvent("Initialize GUI: Done");

  }
  
 
  public void addTruck(String asName){
	  // Add a new truck to the label list
	  
	  m_pEventLogger.logEvent("Adding truck '" + asName +"' to alarm");

	  JLabel lTruckLabel = new JLabel(" " + asName + " ");
  
	  lTruckLabel.setHorizontalAlignment(SwingConstants.LEFT);
	  lTruckLabel.setFont(new Font("Dialog", Font.BOLD, 80));
	  lTruckLabel.setForeground(Color.BLACK);
	  lTruckLabel.setBackground(Color.GREEN);
	  lTruckLabel.setOpaque(true);
	  lTruckLabel.setBorder(new LineBorder(new Color(0, 0, 0), 4));
      
	  m_lTruckLabels.add(lTruckLabel);
	  
	  m_pEventLogger.logEvent("Adding truck '" + asName +"' to alarm: Done");
	  	  
  }
  
  public void clearTruckList(){
	  // Clear the Truck List
	  
	  m_pEventLogger.logEvent("Clear truck list");

	  while(m_lTruckLabels.size() != 0){
		  m_lTruckLabels.remove(0);
	  }
	  m_TrucksHorizontalBox.removeAll();
	  m_pEventLogger.logEvent("Clear truck list: Done");
  }
  

        
    
    private void reset(){
    	// Reset GUI
    	
        m_pEventLogger.logEvent("Reset GUI");

		m_lblAlarmKeyword.setText(m_strHomeName);
		m_lblAlarmKeyword.setBackground(Color.GRAY);
		
		m_lblAlarmTime.setText("");
		m_lblAlarmTime.setBackground(Color.GRAY);

		m_lblObjectAddress.setText("");
		m_lblObjectAddress.setBackground(Color.GRAY);

		m_lblObjectInformation.setText("");
		m_lblObjectInformation.setBackground(Color.GRAY);

		m_lblObjectName.setText("");
	    m_lblObjectName.setBackground(Color.GRAY);
				
		m_contentPane.setBackground(Color.GRAY);
    	lblAlarmDescription.setVisible(false);
    	m_lblObjectAddress.setVisible(false);
    	m_lblObjectInformation.setVisible(false);
    	m_lblObjectName.setVisible(false);
    	m_lblObjectAddress.setVisible(false);
    	
    	m_tDirectionService.getLongitudeLatitude(m_strHomeAdress);
        m_mapKit.setCenterPosition(new GeoPosition(m_tDirectionService.getLat(), m_tDirectionService.getLong()));
                
		clearTruckList();
		
        m_pEventLogger.logEvent("Reset GUI: Done");
    }
    
    
    private void setBlack(){
    	// Set background black and hide everything else so the screen is dark
    	
		m_pEventLogger.logEvent("Setting GUI black");

    	m_lblAlarmKeyword.setVisible(false);
    	lblAlarmDescription.setVisible(false);
    	m_lblObjectAddress.setVisible(false);
    	m_lblObjectInformation.setVisible(false);
    	m_lblObjectName.setVisible(false);
    	m_lblObjectAddress.setVisible(false);
    	m_lblAlarmTime.setVisible(false);
		m_mapKit.setVisible(false);
		m_btnRestore.setVisible(false);
		m_btnOpenEDP.setVisible(false);
		m_contentPane.setBackground(Color.BLACK);
		
		m_pEventLogger.logEvent("Setting GUI black: Done");
    }
    
    private void wakeOn(Alarm_Generator arAlarmGenerator){
    	// Bring back the content and set background color depending on kind of alarm
		m_pEventLogger.logEvent("Wake on GUI");
		
    	m_lblAlarmKeyword.setVisible(true);
    	lblAlarmDescription.setVisible(true);
    	m_lblObjectAddress.setVisible(true);
    	m_lblObjectInformation.setVisible(true);
    	m_lblObjectName.setVisible(true);
    	m_lblObjectAddress.setVisible(true);
    	m_lblAlarmTime.setVisible(true);
		m_mapKit.setVisible(true);
		m_btnRestore.setVisible(true);
		m_btnOpenEDP.setVisible(true);
		
		if (arAlarmGenerator.isBrand()){
			m_contentPane.setBackground(new Color(255, 60, 60));
		}else{
			m_contentPane.setBackground(new Color(0, 180, 255));
		}
		m_pEventLogger.logEvent("Done");
		
    }



	
	private void showAlarm(Alarm_Generator arAlarmGenerator){
		
		m_pEventLogger.logEvent("Show alarm");

		// Store the system time of the alert
		m_dLastAlert = new Date();
	    
		// reset the GUI
		reset();
		
		arAlarmGenerator.checkAAO();
		
		// wake on the screen
		wakeOn(arAlarmGenerator);

		// Draw the map and set the landmark
        try {
			m_tDirectionService.getLongitudeLatitude(arAlarmGenerator.getOrt() + ", " + arAlarmGenerator.getStreet());
			m_mapKit.setCenterPosition(new GeoPosition(m_tDirectionService.getLat(), m_tDirectionService.getLong()));
			Set<Waypoint> waypoints = new HashSet<Waypoint>();
			waypoints.add(new Waypoint(m_tDirectionService.getLat(), m_tDirectionService.getLong()));
			WaypointPainter painter = new WaypointPainter();
			painter.setWaypoints(waypoints);
			m_mapKit.getMainMap().setOverlayPainter(painter);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			m_pEventLogger.logEvent("Location not found on map!");
	    	m_tDirectionService.getLongitudeLatitude(m_strHomeAdress);
	        m_mapKit.setCenterPosition(new GeoPosition(m_tDirectionService.getLat(), m_tDirectionService.getLong()));
		}
        
        // Fill the contet into the labels
		m_lblAlarmKeyword.setText(arAlarmGenerator.getAlarmstichwort());
		
		if(arAlarmGenerator.getAlarmDescription().isEmpty()){
			lblAlarmDescription.setVisible(false);
		}else{
			lblAlarmDescription.setVisible(true);
			lblAlarmDescription.setText(arAlarmGenerator.getAlarmDescription());
		}
		
		m_lblAlarmTime.setText(arAlarmGenerator.getAlarmierungszeit());
		
		if(arAlarmGenerator.getEinsatzobjekt().isEmpty() && arAlarmGenerator.getMeldernummer().isEmpty()){
			m_lblObjectName.setVisible(false);
		}else{
			m_lblObjectName.setVisible(true);
			m_lblObjectName.setText(arAlarmGenerator.getEinsatzobjekt() +  arAlarmGenerator.getMeldernummer());
		}
		
		m_lblObjectAddress.setText(arAlarmGenerator.getOrt() + arAlarmGenerator.getOrtsteil() + arAlarmGenerator.getStreet()  + arAlarmGenerator.getOrtBemerkung() + "\n");
		
		if(arAlarmGenerator.getZusatzinformationen().isEmpty()){
			m_lblObjectInformation.setVisible(false);
		}else{
			m_lblObjectInformation.setVisible(true);
			m_lblObjectInformation.setText(arAlarmGenerator.getZusatzinformationen());
		}
		
		// set the label colors depending on the kind of alarm
		if (arAlarmGenerator.isBrand()){
			m_contentPane.setBackground(new Color(255, 60, 60));
	    	m_lblAlarmKeyword.setBackground(new Color(255, 60, 60));
	    	lblAlarmDescription.setBackground(new Color(255, 60, 60));
	    	m_lblObjectAddress.setBackground(new Color(255, 60, 60));
	    	m_lblObjectInformation.setBackground(new Color(255, 60, 60));
	    	m_lblObjectName.setBackground(new Color(255, 60, 60));
		}else{
			m_contentPane.setBackground(new Color(0, 180, 255));
	    	m_lblAlarmKeyword.setBackground(new Color(0, 180, 255));
	    	lblAlarmDescription.setBackground(new Color(0, 180, 255));
	    	m_lblObjectAddress.setBackground(new Color(0, 180, 255));
	    	m_lblObjectInformation.setBackground(new Color(0, 180, 255));
	    	m_lblObjectName.setBackground(new Color(0, 180, 255));

		}
		
		// Add the truck labels			
		for(int i = 0; i< m_lTruckLabels.size(); i++){
			m_TrucksHorizontalBox.add(m_lTruckLabels.get(i));
		}
		
		// If there is a printer defined send hin the command
		if(!m_sPrintername.contains("NONE")){
	        String s = null;
	
	        try {	            
	        	Process p = Runtime.getRuntime().exec("lpr -P " + m_sPrintername + " " + arAlarmGenerator.getFileName());
	          
	        	BufferedReader stdInput = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));
	
	            BufferedReader stdError = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	
	            while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
	            
	            while ((s = stdError.readLine()) != null) {
	                System.out.println(s);
	            }
	            
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
		m_pEventLogger.logEvent("Show alarm: Done");

    }
	
	void readConfig(){
		// Read the configuration from Display.cfg 
		
		m_pEventLogger.logEvent("Read config");

	    FileReader fr;
		try {
			fr = new FileReader("Display.cfg");
		    BufferedReader br = new BufferedReader(fr);
			String actLine = br.readLine();		
		    while (!actLine.contains("EOF")){
				if(actLine.contains("DarkenScreen")){
					String value = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    if(Integer.parseInt(value) == 1){
				    	m_bDarkenScreen = true;
				    }else{
				    	m_bDarkenScreen = false;
				    }
				    m_pEventLogger.logEvent("Read Parameter 'DarkenScreen' from config. Value is '" + value + "'");
	
				}
				
				if(actLine.contains("ResetTime")){
					String value = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    m_iResetTime =Integer.parseInt(value);
				    m_pEventLogger.logEvent("Read Parameter 'ResetTime' from config. Value is '" + value + "'");
				}
								
				
				if(actLine.contains("Mailadress")){
					m_sMailadress = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    m_pEventLogger.logEvent("Read Parameter 'Mailadress' from config. Value is '" + m_sMailadress + "'");
				}
				
				if(actLine.contains("Password")){
					m_sPassword = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    m_pEventLogger.logEvent("Read Parameter 'Password' from config. Value is '" + m_sPassword + "'");
				}
				
				if(actLine.contains("Printername")){
					m_sPrintername = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    m_pEventLogger.logEvent("Read Parameter 'Printername' from config. Value is '" + m_sPrintername + "'");
				}
				
				if(actLine.contains("Senders")){
					m_strSenders = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    m_pEventLogger.logEvent("Read Parameter 'Senders' from config. Value is '" + m_strSenders + "'");
				}
				
				if(actLine.contains("HomeName")){
					m_strHomeName = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    m_pEventLogger.logEvent("Read Parameter 'HomeName' from config. Value is '" + m_strHomeName + "'");
				}
				
				if(actLine.contains("HomeAddress")){
					m_strHomeAdress = actLine.substring(actLine.indexOf('=')+1, actLine.indexOf(';')).trim();
				    m_pEventLogger.logEvent("Read Parameter 'HomeAddress' from config. Value is '" + m_strHomeAdress + "'");
				}
								
				actLine = br.readLine();	
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			m_pEventLogger.logEvent(e.getMessage());
		}
		
		m_pEventLogger.logEvent("Read config:Done");		
	}
	
	public EventLogger getEventLogger(){
		return m_pEventLogger;
	}
			    
}