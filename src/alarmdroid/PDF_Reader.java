package alarmdroid;


import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;



public class PDF_Reader {
	private PdfReader reader;
	private String Text;
	
	public void readPDF() {
        StringBuffer buff = new StringBuffer();    
        try {
        	
            int numberOfPages = reader.getNumberOfPages();
            String s;
            for (int i = 1; i <= numberOfPages; i++) {
                s  = PdfTextExtractor.getTextFromPage(reader, i);
                buff.append(s + "\n");
            }    
        } catch (IOException e) {
            e.printStackTrace();
        }
        Text = buff.toString();
    }

	public String getText() {
		return Text;
	}

	public PdfReader getReader() {
		return reader;
	}

	public void setReader(PdfReader reader) {
		this.reader = reader;
	}
}
