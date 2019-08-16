package userInterface;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class EventLogger {
	
	private String m_strFileName;
	private Date m_dActdate;
	private FileWriter m_fwFileWriter; 
    private BufferedWriter m_bwBufferedWriter; 
    
    public EventLogger(String aStrFileName){
    	m_strFileName = aStrFileName;
    	try {
			m_fwFileWriter= new FileWriter(m_strFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	m_bwBufferedWriter= new BufferedWriter(m_fwFileWriter);
    }
   
    public void logEvent(String aEvtText){
    	m_dActdate = new Date();
    	try {
			m_bwBufferedWriter.append(m_dActdate.toString() + ":\t" + aEvtText + "\n");
			m_bwBufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
