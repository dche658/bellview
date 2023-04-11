package dwc.bellview.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
/**
 * This is the model for the histogram.
 * <p>
 * In order to perform a Bhattacharya analysis after instantiating the Histogram
 * set the minimum histogram value, maximum histogram value, and the bin width.
 * Then call analyzeDeltaLogY() method.</p>
 * @author Douglas Chesher
 */
public class Histogram {
    private Double minValue = 0.0;
    private Double maxValue = 0.0;
    private Double binWidth = 0.0;
    private int dataCount = 0;
    private static final Logger logger = LoggerFactory.getLogger(Histogram.class);
    
    private List<HistogramBin> elements = new ArrayList<>();
    private HistogramBin lowerElement = new HistogramBin();
    private HistogramBin upperElement = new HistogramBin();
    /** Creates a new instance of Histogram */
    public Histogram() {
    }
    
    public static Histogram defaultHistogram() {
    	Histogram h = new Histogram();
    	h.setMinValue(1.0);
    	h.setMaxValue(10.0);
    	h.setBinWidth(1.0);
    	return h;
    }
    
    public void sortByBinMidPoint() {
        Collections.sort(elements, new BinMidPointComparator());
    }
    
    public void sortByLogOnePlusHdivX() {
        Collections.sort(elements, new LogOnePlusHdivXComparator());
    }
    
    public void resetCounts() {
        for (HistogramBin e: elements) {
            e.setCount(0);
            e.setDeltaLogY(0.0);
        }
       
    }
    
    public HistogramBin getBinContaining(Double val) {
        HistogramBin element = null;
        for (int i = 0; i < getElements().size(); i++) {
            
            if (val >= getElements().get(i).getBinValue() && 
                    (val < (getElements().get(i).getBinValue() + getElements().get(i).getBinWidth()))) {
                element = getElements().get(i);
                break;
            }
        }
        return element;
    }
    
    public void clear() {
        setMinValue(0.0);
        setMaxValue(0.0);
        setBinWidth(0.0);
        dataCount = 0;
        getElements().clear();
    }
    
    /** Returns the total number of data points in this histogram
     * @return N
     */
    public int getDataCount() {
        return dataCount;
    }
    
    private void initialiseBins() {
        elements.clear();
        dataCount = 0;
        double n = Math.abs((getMaxValue()-getMinValue())/getBinWidth());
        int numberOfBins = (int) n;
        if (n > numberOfBins) {
            numberOfBins +=1;
        }
        logger.debug("Number of bins "+numberOfBins);
        for (int i=0;i<numberOfBins;i++) {
            HistogramBin bin = new HistogramBin();
            bin.setBinValue(getMinValue()+(i*getBinWidth()));
            bin.setBinWidth(getBinWidth());
            elements.add(bin);
        }
        getLowerElement().setBinValue(getMinValue()-getBinWidth());
        getUpperElement().setBinValue(getMinValue()+(getBinWidth()*numberOfBins));
        getLowerElement().setCount(0);
        getUpperElement().setCount(0);
    }
    /** Increments the count for the relevant bin
     *
     * @param value
     */
    public void addData(double value) {
        int index = (int) ((value - getMinValue())/getBinWidth());
        if (index < 0) {
            getLowerElement().incrementCount();
        }
        else if (index < elements.size()) {
            elements.get(index).incrementCount();
        }
        else {
            getUpperElement().incrementCount();
        }
        dataCount +=1;
    }
    /**
     * Call this method to analyze the histogram data.
     */
    public void analyzeDeltaLogY() {
        double deltaLogY;
        for (int i=0;i<elements.size()-1;i++) {
            deltaLogY = calcDeltaLogY(elements.get(i+1).getCount(),elements.get(i).getCount());
            elements.get(i).setDeltaLogY(deltaLogY);
        }
        if (elements.size()>0) {
            deltaLogY = calcDeltaLogY(elements.get(0).getCount(),getLowerElement().getCount());
            getLowerElement().setDeltaLogY(deltaLogY);
            deltaLogY = calcDeltaLogY(getUpperElement().getCount(),elements.get(elements.size()-1).getCount());
            elements.get(elements.size()-1).setDeltaLogY(deltaLogY);
        }
    }
    
    private double calcDeltaLogY(double y2, double y1) {
        if (y1==0 || y2==0) {
            return 0;
        }
        else {
            //logger.debug("f(x+h)="+y2+", f(x)="+y1+", dly="+(Math.log(y2)-Math.log(y1)));
            return (Math.log(y2)-Math.log(y1));
        }
    }
    /** Calculates the default bin width assuming 15 bins
     * @return 
     */
    public Double getDefaultBinWidth() {
        double defaultValue = Math.abs((getMaxValue() - getMinValue())/15);
        logger.debug("Default bin width "+defaultValue);
        return defaultValue;
    }
    
    public Double getMinValue() {
        return minValue;
    }
    
    public void setMinValue(Double minValue) {
        this.minValue = minValue;
        initialiseBins();
    }
    
    public Double getMaxValue() {
        return maxValue;
    }
    
    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
        initialiseBins();
    }
    
    public Double getBinWidth() {
        return binWidth;
    }
    
    /**
     * Setting the bin width also resets the histogram bin counts to zero
     * @param binWidth
     */
    public void setBinWidth(Double binWidth) {
        logger.debug("Setting bin width to "+binWidth);
        this.binWidth = binWidth;
        this.initialiseBins();
    }
    
    public List<HistogramBin> getElements() {
        return elements;
    }
    
    public void setElements(List<HistogramBin> elements) {
        this.elements = elements;
    }
    @Override
    public String toString() {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        StringBuilder txt = new StringBuilder();
        txt.append("Min value = ").append(nf.format(getMinValue())).append("\n");
        txt.append("Max value = ").append(nf.format(getMaxValue())).append("\n");
        txt.append("Bin width = ").append(nf.format(getBinWidth())).append("\n");
        for (int i=0;i<getElements().size();i++) {
            HistogramBin e = getElements().get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(i).append("\t");
            sb.append(nf.format(e.getBinValue())).append("\t");
            sb.append(nf.format(getBinWidth())).append("\t");
            sb.append(e.getCount()).append("\t");
            sb.append(nf.format(e.getDeltaLogY()));
            sb.append("\n");
            txt.append(sb);
        }
        return txt.toString();
    }
    
    public HistogramBin getLowerElement() {
        return lowerElement;
    }
    
    public void setLowerElement(HistogramBin lowerElement) {
        this.lowerElement = lowerElement;
    }
    
    public HistogramBin getUpperElement() {
        return upperElement;
    }
    
    public void setUpperElement(HistogramBin upperElement) {
        this.upperElement = upperElement;
    }
}

class BinMidPointComparator implements Comparator<HistogramBin>, java.io.Serializable {

    private static final long serialVersionUID = 1L;

	@Override
    public int compare(HistogramBin t, HistogramBin t1) {
        if (t.getBinMidPoint() < t1.getBinMidPoint()) {
            return -1;
        } else if (t.getBinMidPoint() == t1.getBinMidPoint()) {
            return 0;
        } else {
            return 1;
        }
    }
    
}

class LogOnePlusHdivXComparator implements Comparator<HistogramBin>, java.io.Serializable {

    private static final long serialVersionUID = 3512767430127189696L;

	@Override
    public int compare(HistogramBin t, HistogramBin t1) {
        if (t.getLogOnePlusHdivX() < t1.getLogOnePlusHdivX()) {
            return -1;
        } else if (t.getLogOnePlusHdivX() == t1.getLogOnePlusHdivX()) {
            return 0;
        } else {
            return 1;
        }
    }
    
}


