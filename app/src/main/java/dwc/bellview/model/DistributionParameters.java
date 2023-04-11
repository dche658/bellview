package dwc.bellview.model;


import org.apache.commons.math3.distribution.*;

/**
 * The distribution parameters produced from the Bhattacharya analysis.
 * @author Douglas Chesher
 */
public class DistributionParameters {
    private double mean = 0;
    private double variance = 0;
    
    /** Creates a new instance of DistributionParameters */
    public DistributionParameters() {
        
    }
    
    /**
     * Confidence interval. Set to 95 if a 95% confidence interval is required that is
     * equivalent to a 2.5th percentile lower limit.
     */
    public double getLowerLimit(double confidenceInterval) {
        double p = (confidenceInterval+(100-confidenceInterval)/2)/(100);
        NormalDistribution dist = new NormalDistribution(0d, 1d);
        double z = dist.inverseCumulativeProbability(p);
        return getMean()-(z*getStandardDeviation());
    }
    public double getUpperLimit(double confidenceInterval) {
        double p = (confidenceInterval+(100-confidenceInterval)/2)/(100);
        NormalDistribution dist = new NormalDistribution(0d, 1d);
        double z = dist.inverseCumulativeProbability(p);
        return getMean()+(z*getStandardDeviation());
    }
    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }
    
    public double getStandardDeviation() {
        return java.lang.Math.sqrt(getVariance());
    }
    
    
}
