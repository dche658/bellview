package dwc.bellview.model;

/** Represents a single bin within a histogram. The bin value set and retrieved
 * by <code>setBin</code> and <code>getBin</code> respectively is lower value
 * of the bin. The upper limit of the bin is given by bin + binWidth.
 *
 * @author Douglas Chesher
 */
public class HistogramBin {
	private double binValue = -1;
    private double binWidth = 0;
    private int count = 0;
    private double deltaLogY=0;
    
    /** Creates a new instance of HistogramElement */
    public HistogramBin() {
    }
    
    public HistogramBin(Double binValue, Double binWidth, Integer count) {
        this.binValue = binValue;
        this.binWidth = binWidth;
        this.count = count;
    }
    
    public HistogramBin(Double binValue, Double binWidth, Integer count, Double deltaLogY) {
        this(binValue,binWidth,count);
        this.deltaLogY = deltaLogY;
    }

    public double getBinValue() {
        return binValue;
    }

    public void setBinValue(double binValue) {
        this.binValue = binValue;
    }

    public double getBinWidth() {
        return binWidth;
    }

    public void setBinWidth(double binWidth) {
        this.binWidth = binWidth;
    }
    
    public double getBinMidPoint() {
        return binValue + (binWidth/2);
    }
    
    /**
     * Return ln(1 + (binWidth / binMidPoint))
     * @return calculated value
     */
    public double getLogOnePlusHdivX() {
        return Math.log(1 + (binWidth/getBinMidPoint()));
    }
    
    public double getGofLambdaRX(Double R, Double lambda) {
        return (Math.pow(binWidth,3.0)/12)*(((R-1)*lambda/Math.pow(getBinMidPoint(),2))-((R-1)*(R-2)/Math.pow(getBinMidPoint(), 3)));
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrementCount() {
        setCount(getCount()+1);
    }
    
    public double getDeltaLogY() {
        return deltaLogY;
    }

    public void setDeltaLogY(double deltaLogY) {
        this.deltaLogY = deltaLogY;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bin=").append(getBinValue());
        sb.append(", Width=").append(getBinWidth());
        sb.append(", BinMidPt=").append(getBinMidPoint());
        sb.append(", Count").append(getCount());
        sb.append(", DLY=").append(getDeltaLogY());
        return sb.toString();
    }
}
