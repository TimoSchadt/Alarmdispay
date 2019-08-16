package alarmdroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import com.itextpdf.text.pdf.PdfReader;

import userInterface.Alarmdisplay;;

public class Alarmdroid {

    private User Nutzer;
    private GMailReader gMail;
    private Message[] msg;
    private MessageHandler MH;
    private Message Alarmmail;
    private PdfReader reader_Ref;
    
    private String m_sMailadress="";
    private String m_sPassword="";
    private String m_strSenders="";
    
    private Alarmdisplay m_pDisplay;

    
    
    public Alarmdroid(Alarmdisplay apDisplay, String asMailadress, String asPassword, String asSenders){
    	m_sMailadress = asMailadress;
    	m_sPassword = asPassword;
    	m_strSenders = asSenders;
    	m_pDisplay = apDisplay;
    	
    	Nutzer  = new User(m_sMailadress,m_sPassword,true);

    }
    
    private String saveAttachment(Message msg){
    	
    	m_pDisplay.getEventLogger().logEvent("Save Attachment");
    	
		String FileName = null;
		try {	
			String contentType = msg.getContentType();
			if (contentType.contains("multipart")) {
				MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
				mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
				mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
				mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
				mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
				mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
				CommandMap.setDefaultCommandMap(mc);
				
				Multipart multiPart;
				multiPart = (Multipart) msg.getContent();		
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(1);
				String destFilePath = part.getFileName();
				InputStream input = part.getInputStream();
				
				File file = new File(destFilePath);  
				FileOutputStream output = new FileOutputStream(file);
				byte[] buffer = new byte[4096];	 
				int byteRead;	 
				while ((byteRead = input.read(buffer)) != -1) {
				    output.write(buffer, 0, byteRead);
				}
				
				FileName = part.getFileName();
				output.close();	
			}
		} catch (IOException e) {
			m_pDisplay.getEventLogger().logEvent(e.getMessage());
		} catch (MessagingException e) {
			m_pDisplay.getEventLogger().logEvent(e.getMessage());

		}
		
    	m_pDisplay.getEventLogger().logEvent("Save Attachment: Done");

		return FileName;
	}
    
public Alarm_Generator getAlarm(){
	
	m_pDisplay.getEventLogger().logEvent("Get Alarm");
	
	gMail = new GMailReader(Nutzer.getAdress(),Nutzer.getPassword());
    MH = new MessageHandler(m_strSenders);
    
    gMail.openConnection();

    try {
		msg = gMail.readMail();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    MH.reset();	
	MH.setInbox(msg);
	MH.extractAlarmmails();
	
	Alarmmail = MH.getAlarmmail();
	if (Alarmmail == null){
		gMail.closeConnection();
		m_pDisplay.getEventLogger().logEvent("Get Alarm: Done, No Alarmmail found");
		return null;
	}
	String FN = saveAttachment(Alarmmail);
	PDF_Reader reader = new PDF_Reader();
	FileInputStream is;
	Alarm_Generator generator = null;
	try {
		is = new FileInputStream(FN);
		reader_Ref = new PdfReader(is);
		reader = new PDF_Reader();
		reader.setReader(reader_Ref);
		reader.readPDF();	
		generator = new Alarm_Generator(m_pDisplay);	
		generator.setInputString(reader.getText());
		generator.setFileName(FN);
		if(!generator.parse()){
			m_pDisplay.getEventLogger().logEvent("Get Alarm: Done, failed parsing Alarmmail");
			generator = null;
		};
	} catch (FileNotFoundException e) {
		m_pDisplay.getEventLogger().logEvent(e.getMessage());
		 generator = null;
	} catch (IOException e) {
		m_pDisplay.getEventLogger().logEvent(e.getMessage());
		generator = null;

	}
	gMail.closeConnection();
	
	m_pDisplay.getEventLogger().logEvent("Get Alarm: Done, found Alarmmail");

	return generator;	
	}

public Alarm_Generator getAlarm(String FN){
	
	m_pDisplay.getEventLogger().logEvent("Get Alarm");
	
	if (!(new File(FN).exists())) {
		return null;
	}
	
	Alarm_Generator generator = null;
	try {
		BufferedReader br = new BufferedReader(new FileReader(FN));
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
	        sb.append(line);
	        sb.append(System.lineSeparator());
	        line = br.readLine();
	    }
		generator = new Alarm_Generator(m_pDisplay);	
		generator.setInputString(sb.toString());
		generator.setFileName(FN);
		if(!generator.parse()){
			m_pDisplay.getEventLogger().logEvent("Get Alarm: Done, failed parsing Alarmmail");
			generator = null;
		};
	    br.close();
	    new File(FN).delete();
	

	} catch (FileNotFoundException e) {
		m_pDisplay.getEventLogger().logEvent(e.getMessage());
		 generator = null;
	} catch (IOException e) {
		m_pDisplay.getEventLogger().logEvent(e.getMessage());
		generator = null;

	}
	
	m_pDisplay.getEventLogger().logEvent("Get Alarm: Done, found Alarmmail");

	return generator;	
	}

}
