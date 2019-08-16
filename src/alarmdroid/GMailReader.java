package alarmdroid;


import java.util.Properties;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class GMailReader extends javax.mail.Authenticator { 
    private String mailhost = "imap.gmail.com"; 
    private String user; 
    private String password; 
    private Session session; 
    private static Store store ;
    private static Folder folder;
    private boolean ready;
    
    public GMailReader(String user, String password) { 
        this.user = user; 
        this.password = password; 
        ready = false;
        Properties props = new Properties(); 
        props.setProperty("mail.store.protocol", "imaps"); 
        props.setProperty("mail.imaps.host", mailhost); 
        props.put("mail.imaps.auth", "true"); 
        props.put("mail.imaps.port", "993"); 
        props.put("mail.imaps.socketFactory.port", "993"); 
        props.put("mail.imaps.socketFactory.class", 
                  "javax.net.ssl.SSLSocketFactory"); 
        props.put("mail.imaps.socketFactory.fallback", "false"); 
        props.setProperty("mail.imaps.quitwait", "false"); 
        session = Session.getDefaultInstance(props, this); 
    } 
    
    public boolean openConnection(){
    	try {
			store = session.getStore("imaps");   
			store.connect("imap.gmail.com", user, password);
            folder = store.getFolder("INBOX"); 
            folder.open(Folder.READ_WRITE); 
            return true;
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
    }
    public void closeConnection()
    {
    	try {
			store.close();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public synchronized Message[] readMail() throws Exception { 
        ready = false;
    	try { 

         // search for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            if (folder.getMessageCount()  == 0){
                ready = true;
                return null; 
            }
    
            Message msgs[] = folder.search(unseenFlagTerm);
            FetchProfile fp = new FetchProfile(); 
            fp.add(FetchProfile.Item.ENVELOPE); 
            folder.fetch(msgs, fp); 
            ready = true;
            return msgs; 
        } catch (Exception e) { 
            System.out.println("readMail: " + e.getMessage()); 
            ready = true;
            return null; 
        } 
    }
	public boolean isReady() {
		return ready;
	}
}