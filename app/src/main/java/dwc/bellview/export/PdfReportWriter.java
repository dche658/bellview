package dwc.bellview.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;

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
	
	private static final Logger LOG = LoggerFactory.getLogger(PdfReportWriter.class);
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat(BellviewUtils.getMessage("report.date.format"));
	private Report report;
	private BellviewModel model;
	private WritableImage histogramImage;
	private WritableImage bhattacharyaImage;
	private WritableImage residualsImage;
	private PdfFont BOLD;
	private PdfFont NORMAL;
	

	
	public PdfReportWriter(Report rpt, BellviewModel model, WritableImage histogramImage, WritableImage bhattacharyaImage, WritableImage residualsImage) {
		this.report = rpt;
		this.model = model;
		this.histogramImage = histogramImage;
		this.bhattacharyaImage = bhattacharyaImage;
		this.residualsImage = residualsImage;
	}

	@Override
	public void writeReport(File file) throws BellviewException {
		Document document = null;
		LOG.debug("Begin write PDF report");
		try {                
			PdfWriter writer = new PdfWriter(file);
			LOG.debug("Created PDF writer");
			PdfDocument pdf = new PdfDocument(writer);
			LOG.debug("Created PDF document");
			document = new Document(pdf, PageSize.A4);
			LOG.debug("Created report document");
			pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new TextFooterEventHandler(document));
			LOG.debug("Begin build document");
            buildDocument(document);
            document.close();
            LOG.debug("PDF report closed");
            document = null;
        } catch (IOException ex) {
        	LOG.error("Exception writing PDF", ex);
        	throw new BellviewException(BellviewUtils.getMessage("error.analysis.pdf"),ex); 
        } finally {
        	if (document != null) {
                document.close();
                LOG.debug("PDF document close");
            }
        }
	}
	
	private void buildDocument(Document doc) throws IOException {
		BOLD = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		NORMAL = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		
		
		
//        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA,14,Font.BOLD,BaseColor.BLACK);
//        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA,12,Font.BOLD,BaseColor.BLACK);
//        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA,12,Font.NORMAL,BaseColor.BLACK);
//        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA,9,Font.BOLD,BaseColor.BLACK);
//        Font tableFont = FontFactory.getFont(FontFactory.HELVETICA,9,Font.NORMAL,BaseColor.BLACK);

        Paragraph title = new Paragraph(BellviewUtils.getMessage("report.title"));
        title.setFontSize(12);
        title.setFont(BOLD);
        doc.add(title);
        doc.setFont(NORMAL);
        doc.setFontSize(10);
        Paragraph phrase0 = new Paragraph();
        Text lblAnalyteName = new Text(BellviewUtils.getMessage("report.label.analyte.name"));
        lblAnalyteName.setFont(BOLD);
        phrase0.add(lblAnalyteName);
        phrase0.add(" ");
        phrase0.add(report.getAnalyte().getName());
        doc.add(phrase0);
        LOG.debug("Add Paragraph 0");
        
        Paragraph phrase1 = new Paragraph();
        Text lblAnalyteUnits = new Text(BellviewUtils.getMessage("report.label.analyte.units"));
        lblAnalyteUnits.setFont(BOLD);
        phrase1.add(lblAnalyteUnits);
        phrase1.add(" ");
        phrase1.add(report.getAnalyte().getUnits());
        doc.add(phrase1);
        LOG.debug("Add Paragraph 1");
        
        Paragraph phraseDate = new Paragraph();
        Text lblDate = new Text(BellviewUtils.getMessage("label.date")+" ");
        lblDate.setFont(BOLD);
        phraseDate.add(lblDate);
        phraseDate.add(dateFormat.format(new java.util.Date()));
        doc.add(phraseDate);
        LOG.debug("Add Paragraph 3");
        
        doc.add(new Paragraph(" "));
        
        doc.add(getTable1());
        LOG.debug("Add Table 1");
        
        doc.add(new Paragraph(" "));
        doc.add(getTable2());
        LOG.debug("Add Table 2");
        
        doc.add(new AreaBreak());
        Image histogramImg = getPdfImage(histogramImage);
        histogramImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
        doc.add(histogramImg);
        LOG.debug("Add Chart 1");
        
        Image logDiffImg = getPdfImage(bhattacharyaImage);
        logDiffImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
        doc.add(logDiffImg);
        LOG.debug("Add Chart 2");
        
        Image residualsImg = getPdfImage(residualsImage);
        residualsImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
        doc.add(residualsImg);
        LOG.debug("Add Chart 3");
    }
	
	private Image getPdfImage(WritableImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", baos);
		ImageData id = ImageDataFactory.create(baos.toByteArray());
		Image graphImg = new Image(id);
		graphImg.setAutoScale(false);
		double width = image.getWidth();
		float scalingFactor = (float) (300f/width);
		graphImg.scale(scalingFactor, scalingFactor);
		return graphImg;
	}
	
	private Table getTable1() {
		ReferenceInterval ri = report.getReferenceInterval();
		Table table = new Table(4);
		addPropertyToTable(BellviewUtils.getMessage("report.label.slope"),SignificantFigures.format(report.getRegression().getSlope(),report.getSignificantFigures()),table);
        addPropertyToTable(BellviewUtils.getMessage("report.label.intercept"),SignificantFigures.format(report.getRegression().getIntercept(),report.getSignificantFigures()),table);
        addPropertyToTable(BellviewUtils.getMessage("report.label.slope.stderror"),SignificantFigures.format(report.getRegression().getSlopeStdErr(),report.getSignificantFigures()),table);
        addPropertyToTable(BellviewUtils.getMessage("report.label.intercept.stderror"),SignificantFigures.format(report.getRegression().getInterceptStdErr(),report.getSignificantFigures()),table);
        //addPropertyToTable(BellviewUtils.getMessage("report.label.confidence"),SignificantFigures.format(reportModel.getConfidenceLevel(),reportModel.getSignificantFigures()),table1,tableFont);
        addPropertyToTable(BellviewUtils.getMessage("report.label.recordcount"), String.valueOf(model.getData().size()), table);
        addPropertyToTable(BellviewUtils.getMessage("report.label.recordcount.below"), String.valueOf(report.getResultCountBelowLowerReferenceLimit(ri)), table);
        addPropertyToTable(BellviewUtils.getMessage("report.label.recordcount.above"), String.valueOf(report.getResultCountAboveUpperReferenceLimit(ri)), table);
        addPropertyToTable(BellviewUtils.getMessage("report.label.mean"),SignificantFigures.format(report.getDistributionParameters().getMean(),report.getSignificantFigures()),table);
        addPropertyToTable(BellviewUtils.getMessage("report.label.stddev"),SignificantFigures.format(report.getDistributionParameters().getStandardDeviation(),report.getSignificantFigures()),table);

        addPropertyToTable(BellviewUtils.getMessage("report.label.interval"),ri.toString(report.getSignificantFigures()),table);
        //addPropertyToTable("","",table);
		return table;
	}
	
	private void addPropertyToTable(String propertyName, String propertyValue, Table table) {
        Cell cell0 = new Cell();
        cell0.add(new Paragraph(propertyName));
        Cell cell1 = new Cell();
        cell1.add(new Paragraph(propertyValue));
        cell0.setHorizontalAlignment(HorizontalAlignment.LEFT);
        cell1.setHorizontalAlignment(HorizontalAlignment.LEFT);
        cell0.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell1.setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(cell0);
        table.addCell(cell1);
    }
	
	private Table getTable2() {
		Color lightGray = new DeviceRgb(211, 211, 211);
		Table table = new Table(4);
        Cell tblHeader0 = new Cell();
        tblHeader0.add(new Paragraph(BellviewUtils.getMessage("report.table.header.0")));
        tblHeader0.setFont(BOLD);
        tblHeader0.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tblHeader0.setVerticalAlignment(VerticalAlignment.MIDDLE);
        tblHeader0.setBackgroundColor(lightGray);
        
        Cell tblHeader1 = new Cell();
        tblHeader1.add(new Paragraph(BellviewUtils.getMessage("report.table.header.1")));
        tblHeader1.setFont(BOLD);
        tblHeader1.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tblHeader1.setVerticalAlignment(VerticalAlignment.MIDDLE);
        tblHeader1.setBackgroundColor(lightGray);
        
        Cell tblHeader2 = new Cell();
        tblHeader2.add(new Paragraph(BellviewUtils.getMessage("report.table.header.2")));
        tblHeader2.setFont(BOLD);
        tblHeader2.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tblHeader2.setVerticalAlignment(VerticalAlignment.MIDDLE);
        tblHeader2.setBackgroundColor(lightGray);
        
        Cell tblHeader3 = new Cell();
        tblHeader3.add(new Paragraph(BellviewUtils.getMessage("report.table.header.3")));
        tblHeader3.setFont(BOLD);
        tblHeader3.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tblHeader3.setVerticalAlignment(VerticalAlignment.MIDDLE);
        tblHeader3.setBackgroundColor(lightGray);

        table.addCell(tblHeader0);
        table.addCell(tblHeader1);
        table.addCell(tblHeader2);
        table.addCell(tblHeader3);
        for (int r=0;r<model.getHistogram().getElements().size();r++) {
            HistogramBin e = model.getHistogram().getElements().get(r);
            Cell cell0 = new Cell();
            cell0.add(new Paragraph(String.valueOf(r)));
            cell0.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cell0.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            Cell cell1 = new Cell();
            cell1.add(new Paragraph(SignificantFigures.format(e.getBinMidPoint(),report.getSignificantFigures())));
            cell1.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cell1.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            Cell cell2 = new Cell();
            cell2.add(new Paragraph(SignificantFigures.format(e.getCount(),report.getSignificantFigures())));
            cell2.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cell2.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            Cell cell3 = new Cell();
            cell3.add(new Paragraph(SignificantFigures.format(e.getDeltaLogY(),report.getSignificantFigures())));
            cell3.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cell3.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            table.addCell(cell0);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
        }
        return table;
	}
	

	private static class TextFooterEventHandler implements IEventHandler {
        protected Document doc;

        public TextFooterEventHandler(Document doc) {
            this.doc = doc;
        }

        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            Rectangle pageSize = docEvent.getPage().getPageSize();
            PdfFont font = null;
            try {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            } catch (IOException e) {
                // Such an exception isn't expected to occur,
                // because helvetica is one of standard fonts
                LOG.error(e.getMessage());
            }

            LOG.debug("Writing document footer");
            float coordX = pageSize.getRight() - doc.getRightMargin();
            //float headerY = pageSize.getTop() - doc.getTopMargin() + 10;
            float footerY = doc.getBottomMargin();
            StringBuilder pageNumbers = new StringBuilder("Page ");
            pageNumbers.append(doc.getPdfDocument().getPageNumber(docEvent.getPage()));
            Canvas canvas = new Canvas(docEvent.getPage(), pageSize);
            canvas

                    // If the exception has been thrown, the font variable is not initialized.
                    // Therefore null will be set and iText will use the default font - Helvetica
                    .setFont(font)
                    .setFontSize(8)
                    .showTextAligned(pageNumbers.toString(), coordX, footerY, TextAlignment.RIGHT)
                    .close();
        }
    }
}
