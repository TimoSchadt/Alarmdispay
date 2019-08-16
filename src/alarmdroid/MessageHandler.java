package alarmdroid;


import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageHandler {
private Message[] inbox;
private volatile Message Alarmmail;
private volatile String FileName;
private boolean ready;
private String m_strSenders;

public MessageHandler(Message[] newInbox){
		inbox = newInbox;
	   ready= false;
	   Alarmmail = null;
	   FileName = null;
	   m_strSenders = "";
}

public MessageHandler(String aStrSenders){
	inbox = null;
	ready= false;
	   Alarmmail = null;
	   FileName = null;
	   m_strSenders = aStrSenders;
}

public void reset(){
	inbox = null;
	   ready= false;
	   Alarmmail = null;
	   FileName = null;
}

public void setInbox(Message[] msg){
	inbox = msg;
}

public Message getAlarmmail() {
	return Alarmmail;
}
public String getFileName() {
	return FileName;
}
public boolean isReady(){
	return ready;
}

public void extractAlarmmails(){
	if(inbox != null){
			for (int i=0; i<inbox.length; i++){
				try {		
					if (isValidSender(inbox[i].getFrom()[0].toString())){
						Alarmmail = inbox[i];
						break;
					}
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} }
		ready = true;
	}else{
		Alarmmail = null;
		ready = true;
	}
		
}

private boolean isValidSender(String aStrSender){
	int iActSeperator= m_strSenders.indexOf(",", 0);
	String actSender = m_strSenders.substring(0, iActSeperator);
	
	while(!actSender.isEmpty()){
			if(aStrSender.contains(actSender.trim())){
				return true;
			}
			int iNextSeperator = m_strSenders.indexOf(",", iActSeperator+1);
			if(iNextSeperator < 0){
				iNextSeperator = m_strSenders.length();
			}
			actSender = m_strSenders.substring(iActSeperator+1,iNextSeperator);
			iActSeperator = iNextSeperator;
	}

	return false;
}
}
