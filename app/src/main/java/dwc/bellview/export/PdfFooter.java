package dwc.bellview.export;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfFooter extends PdfPageEventHelper {
	private final Font footerFont;

    public PdfFooter() {
        this.footerFont = FontFactory.getFont(FontFactory.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
    }
    
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase footer = new Phrase("Page "+String.valueOf(writer.getPageNumber()), footerFont);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, 
                (document.right()-document.left())/2 + document.leftMargin(), 
                document.bottom() - 10, 0);
    }
}
