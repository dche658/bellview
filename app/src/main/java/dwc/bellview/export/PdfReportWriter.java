package dwc.bellview.export;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import com.itextpdf.text.BadElementException;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import dwc.bellview.BellviewException;
import dwc.bellview.BellviewUtils;
import dwc.bellview.SignificantFigures;
import dwc.bellview.model.BellviewModel;
import dwc.bellview.model.HistogramBin;
import dwc.bellview.model.ReferenceInterval;
import dwc.bellview.model.Report;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

public class PdfReportWriter implements ReportWriter {
	
//	private static final Logger LOG = LoggerFactory.getLogger(PdfReportWriter.class);
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat(BellviewUtils.getMessage("report.date.format"));
	private Report report;
	private BellviewModel model;
	private WritableImage histogramImage;
	private WritableImage bhattacharyaImage;
	private WritableImage residualsImage;
	

	
	public PdfReportWriter(Report rpt, BellviewModel model, WritableImage histogramImage, WritableImage bhattacharyaImage, WritableImage residualsImage) {
		this.report = rpt;
		this.model = model;
		this.histogramImage = histogramImage;
		this.bhattacharyaImage = bhattacharyaImage;
		this.residualsImage = residualsImage;
	}

	@Override
	public void writeReport(File file) throws BellviewException {
		BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            Document doc = new Document(PageSize.A4,50,50,50,30);
            try {
                PdfWriter writer = PdfWriter.getInstance(doc,out);
                writer.setPageEvent(new PdfFooter());
                doc.addCreationDate();
                doc.addSubject(BellviewUtils.getMessage("report.title"));
                doc.addAuthor(System.getProperty("user.name"));
                doc.addCreationDate();
                doc.open();
                buildDocument(doc);
            } finally {
                doc.close();
            }
        } catch (DocumentException | IOException ex) {
        	throw new BellviewException(BellviewUtils.getMessage("error.analysis.pdf"),ex); 
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ioe) {
                throw new BellviewException("Error while closing the PDF file",ioe);
            }
        }
	}
	
	private void buildDocument(Document doc) throws DocumentException,IOException {
		ReferenceInterval ri = report.getReferenceInterval();
		
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA,14,Font.BOLD,BaseColor.BLACK);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA,12,Font.BOLD,BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA,12,Font.NORMAL,BaseColor.BLACK);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA,9,Font.BOLD,BaseColor.BLACK);
        Font tableFont = FontFactory.getFont(FontFactory.HELVETICA,9,Font.NORMAL,BaseColor.BLACK);

        doc.add(new Paragraph(BellviewUtils.getMessage("report.title"),titleFont));
        Paragraph phrase0 = new Paragraph();
        phrase0.add(new Chunk(BellviewUtils.getMessage("report.label.analyte.name"),labelFont));
        phrase0.add(" ");
        phrase0.add(new Chunk(report.getAnalyte().getName(),normalFont));
        doc.add(phrase0);
        
        Paragraph phrase1 = new Paragraph();
        phrase1.add(new Chunk(BellviewUtils.getMessage("report.label.analyte.units"),labelFont));
        phrase1.add(" ");
        phrase1.add(new Chunk(report.getAnalyte().getUnits(),normalFont));
        doc.add(phrase1);
        Paragraph phraseDate = new Paragraph();
        phraseDate.add(new Chunk(BellviewUtils.getMessage("label.date")+" ",labelFont));
        phraseDate.add(new Chunk(dateFormat.format(new java.util.Date()),normalFont));
        doc.add(phraseDate);
        doc.add(new Paragraph(" "));
        
        PdfPTable table1 = new PdfPTable(4);
        addPropertyToTable(BellviewUtils.getMessage("report.label.slope"),SignificantFigures.format(report.getRegression().getSlope(),report.getSignificantFigures()),table1,tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.intercept"),SignificantFigures.format(report.getRegression().getIntercept(),report.getSignificantFigures()),table1,tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.slope.stderror"),SignificantFigures.format(report.getRegression().getSlopeStdErr(),report.getSignificantFigures()),table1,tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.intercept.stderror"),SignificantFigures.format(report.getRegression().getInterceptStdErr(),report.getSignificantFigures()),table1,tableFont);
        //addPropertyToTable(BellviewUtils.getMessage("report.label.confidence"),SignificantFigures.format(reportModel.getConfidenceLevel(),reportModel.getSignificantFigures()),table1,tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.recordcount"), String.valueOf(model.getData().size()), table1, tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.recordcount.below"), String.valueOf(report.getResultCountBelowLowerReferenceLimit(ri)), table1, tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.recordcount.above"), String.valueOf(report.getResultCountAboveUpperReferenceLimit(ri)), table1, tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.mean"),SignificantFigures.format(report.getDistributionParameters().getMean(),report.getSignificantFigures()),table1,tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.stddev"),SignificantFigures.format(report.getDistributionParameters().getStandardDeviation(),report.getSignificantFigures()),table1,tableFont);

        addPropertyToTable(BellviewUtils.getMessage("report.label.interval"),ri.toString(report.getSignificantFigures()),table1,tableFont);
        addPropertyToTable("","",table1,tableFont);
        doc.add(table1);
        //doc.add(new Paragraph(" "));
        
        PdfPTable table = new PdfPTable(4);
        PdfPCell tblHeader0 = new PdfPCell(new Phrase(BellviewUtils.getMessage("report.table.header.0"),tableHeaderFont));
        PdfPCell tblHeader1 = new PdfPCell(new Phrase(BellviewUtils.getMessage("report.table.header.1"),tableHeaderFont));
        PdfPCell tblHeader2 = new PdfPCell(new Phrase(BellviewUtils.getMessage("report.table.header.2"),tableHeaderFont));
        PdfPCell tblHeader3 = new PdfPCell(new Phrase(BellviewUtils.getMessage("report.table.header.3"),tableHeaderFont));
        tblHeader0.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        tblHeader1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        tblHeader2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        tblHeader3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        tblHeader0.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        tblHeader1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        tblHeader2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        tblHeader3.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        tblHeader0.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tblHeader1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tblHeader2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tblHeader3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(tblHeader0);
        table.addCell(tblHeader1);
        table.addCell(tblHeader2);
        table.addCell(tblHeader3);
        for (int r=0;r<model.getHistogram().getElements().size();r++) {
            HistogramBin e = model.getHistogram().getElements().get(r);
            PdfPCell cell0 = new PdfPCell(new Phrase(String.valueOf(r),tableFont));
            PdfPCell cell1 = new PdfPCell(new Phrase(SignificantFigures.format(e.getBinMidPoint(),report.getSignificantFigures()),tableFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(SignificantFigures.format(e.getCount(),report.getSignificantFigures()),tableFont));
            PdfPCell cell3 = new PdfPCell(new Phrase(SignificantFigures.format(e.getDeltaLogY(),report.getSignificantFigures()),tableFont));
            cell0.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell0.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell3.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell0);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
        }
        doc.add(new Paragraph(" "));
        doc.add(table);
        doc.add(Chunk.NEXTPAGE);
        com.itextpdf.text.Image histogramImg = getPdfImage(histogramImage);
        histogramImg.setAlignment(Rectangle.ALIGN_CENTER);
        doc.add(histogramImg);
        com.itextpdf.text.Image logDiffImg = getPdfImage(bhattacharyaImage);
        logDiffImg.setAlignment(Rectangle.ALIGN_CENTER);
        doc.add(logDiffImg);
        com.itextpdf.text.Image residualsImg = getPdfImage(residualsImage);
        residualsImg.setAlignment(Rectangle.ALIGN_CENTER);
        doc.add(residualsImg);
    }
	
	private Image getPdfImage(WritableImage image) throws IOException, BadElementException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", baos);
	    Image graphImg = Image.getInstance(baos.toByteArray());
		return graphImg;
	}
	
	private void addPropertyToTable(String propertyName, String propertyValue, PdfPTable table, Font tableFont) {
        PdfPCell cell0 = new PdfPCell(new Phrase(propertyName, tableFont));
        PdfPCell cell1 = new PdfPCell(new Phrase(propertyValue, tableFont));
        cell0.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell1.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell0.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell0);
        table.addCell(cell1);
    }

}
