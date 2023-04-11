package dwc.bellview.export;

import dwc.bellview.BellviewException;

/**
*
* @author Douglas Chesher
*/
public interface ReportWriter {
   
   public void writeReport(java.io.File file) throws BellviewException;
}