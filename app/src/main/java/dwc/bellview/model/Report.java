package dwc.bellview.model;

import java.util.ArrayList;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dwc.bellview.BellviewUtils;
import dwc.bellview.SignificantFigures;
import dwc.bellview.file.ImportError;
import dwc.bellview.transform.DataTransform;

/**
 * Main data model for this application
 * @author Douglas Chesher
 */
public class Report {

	private static final Logger LOG = LoggerFactory.getLogger(Report.class);
	
    private double confidenceLevel = 0;
    private int defaultImageHeight = 300;
    private int defaultImageWidth = 400;
    private int significantFigures = 3;
    private double z = 1.96;
    private final Analyte analyte = new Analyte();
    private final java.util.List<ImportError> importErrors = new ArrayList<>();
    //private DataTransform dataTransform = new NoneTransform();
    //private ApplicationContext application = null;
    private BellviewModel model = null;
    private SimpleRegression reg = null;
    private DistributionParameters dist = null;
    
    public Report() {
    }

    /** Creates a new instance of Report
     * @param histogram
     * @param model */
    public Report(BellviewModel model) {
        this.model = model;
    }
    
    public void setModel(BellviewModel model) {
		this.model = model;
	}


	public void setRegression(SimpleRegression reg) {
		this.reg = reg;
	}
	
	public SimpleRegression getRegression() {
		return this.reg;
	}
	
	public void setDistributionParameters(DistributionParameters parameters) {
		this.dist = parameters;
	}
	
	public DistributionParameters getDistributionParameters() {
		return this.dist;
	}

    /**
     * Produce a string representation of the report data.
     * @return Report values
     */
    @Override
    public String toString() {
    	LOG.debug("Report data transform: {}",model.getDataTransform().getName());
    	if (reg == null) throw new RuntimeException("Regression object in Report has not been set");
        StringBuilder rpt = new StringBuilder();
        ReferenceInterval ri = getReferenceInterval();
        rpt.append("REGRESSION\n");
        rpt.append(BellviewUtils.getMessage("report.label.slope")).append(":\t\t\t").append(SignificantFigures.format(reg.getSlope(), significantFigures)).append("\n");
        rpt.append(BellviewUtils.getMessage("report.label.intercept")).append(":\t\t\t").append(SignificantFigures.format(reg.getIntercept(), significantFigures)).append("\n");
        rpt.append(BellviewUtils.getMessage("report.label.slope.stderror")).append(":\t").append(SignificantFigures.format(reg.getSlopeStdErr(), significantFigures)).append("\n");
        rpt.append(BellviewUtils.getMessage("report.label.intercept.stderror")).append(":\t").append(SignificantFigures.format(reg.getInterceptStdErr(), significantFigures)).append("\n");
        rpt.append("\n");
        rpt.append("COUNTS\n");
        rpt.append(model.getFilter().toString()).append("\n");
        rpt.append(BellviewUtils.getMessage("report.label.recordcount")).append(":\t\t\t").append(SignificantFigures.format(model.getSubset().size(), significantFigures)).append("\n");
        rpt.append(BellviewUtils.getMessage("report.label.recordcount.below")).append(":\t").append(SignificantFigures.format(getResultCountBelowLowerReferenceLimit(ri), significantFigures)).append("\n");
        rpt.append(BellviewUtils.getMessage("report.label.recordcount.above")).append(":\t").append(SignificantFigures.format(getResultCountAboveUpperReferenceLimit(ri), significantFigures)).append("\n");
        rpt.append("\n");
        rpt.append("DISTRIBUTION\n");
        rpt.append(BellviewUtils.getMessage("report.label.mean")).append(":\t\t\t").append(SignificantFigures.format(dist.getMean(), significantFigures)).append("\n");
        rpt.append(BellviewUtils.getMessage("report.label.stddev")).append(":\t").append(SignificantFigures.format(dist.getStandardDeviation(), significantFigures)).append("\n");
        if (model.getDataTransform().getName().equals(DataTransform.LOG_TRANSFORM)) {
        	rpt.append("Reference interval following inverse transform\n");
        }
        rpt.append(BellviewUtils.getMessage("report.label.interval")).append(":\t").append(ri.toString(significantFigures));
        rpt.append("\n");
        return rpt.toString();
    }
    
    public ReferenceInterval getReferenceInterval() {
    	if (reg==null) throw new RuntimeException("Regression object in Report has not been set");
    	if (dist==null) throw new RuntimeException("Distribution parameters in report have not been set");
    	ReferenceInterval ri = new ReferenceInterval();
    	Double lowerLimit = dist.getMean()-(z*dist.getStandardDeviation());
    	Double upperLimit = dist.getMean()+(z*dist.getStandardDeviation());
    	LOG.debug("Reference interval {} to {}", lowerLimit, upperLimit);
    	if (model.getDataTransform().getName().equals(DataTransform.NO_TRANSFORM)) {
    		ri.setLowerReferenceLimit(lowerLimit);
    		ri.setUpperReferenceLimit(upperLimit);
    	} else {
    		ri.setLowerReferenceLimit(model.getDataTransform().inverse(lowerLimit));
    		ri.setUpperReferenceLimit(model.getDataTransform().inverse(upperLimit));
    	}
    	LOG.debug("Transform: {}",model.getDataTransform().getName());
    	LOG.debug("Interval after transform {} to {}",ri.getLowerReferenceLimit(), ri.getUpperReferenceLimit());
    	return ri;
    }

     /**
     * Returns the estimated mean of the population after analyzeRegression
     * has been called.
     * @return mean of the population.
     */
    public double getMean() {
        return dist.getMean();
    }

    /**
     * Returns the SD of the population after analyzeRegression has been
     * called.
     * @return SD of the population.
     */
    public double getSd() {
        return dist.getStandardDeviation();
    }



    /**
     * Returns the analyte for this report. That is, the analyte for
     * which this analysis has been performed.
     * @return the analyte.
     */
    public Analyte getAnalyte() {
        return analyte;
    }


    /**
     * Returns the number of significant figures used for rounding. Only
     * applies to the decimal part. Integer part is not rounded.
     * @return number of significant figures.
     */
    public int getSignificantFigures() {
        return significantFigures;
    }

    /**
     * Sets the number of significant figures used for rounding. Only
     * applies to the decimal part. Integer part is not rounded.
     * @param significantFigures number of significant figures.
     */
    public void setSignificantFigures(int significantFigures) {
        this.significantFigures = significantFigures;
    }

    /**
     * Returns the confidence interval for determining the reference
     * interval.
     * @return the level of confidence.
     */
    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    /**
     * Set the level of confidence used to determine the population
     * reference interval. Generally set to 95.
     * @param confidenceLevel level of confidence.
     */
    public void setConfidenceLevel(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public long getResultCountBelowLowerReferenceLimit(ReferenceInterval ri) {
    	long c = 0;
    	for (DataElement de: model.getSubset()) {
    		if (de.getRawValue()< ri.getLowerReferenceLimit()) c+=1;
    	}
    	return c;
    }

    public long getResultCountAboveUpperReferenceLimit(ReferenceInterval ri) {
        long c = 0;
        for (DataElement de: model.getSubset()) {
        	if (de.getRawValue() > ri.getUpperReferenceLimit()) c+=1;
        }
        return c;
    }
    
    public java.util.List<ImportError> getImportErrors() {
        return importErrors;
    }

    public int getDefaultImageHeight() {
        return defaultImageHeight;
    }

    public void setDefaultImageHeight(int defaultImageHeight) {
        this.defaultImageHeight = defaultImageHeight;
    }

    public int getDefaultImageWidth() {
        return defaultImageWidth;
    }

    public void setDefaultImageWidth(int defaultImageWidth) {
        this.defaultImageWidth = defaultImageWidth;
    }
    
    /**
     * Analyte for which the analysis is being undertaken.
     * @author Douglas Chesher
     */
    public class Analyte {
        private String name = "";
        private String units = "";
        /** Creates a new instance of Analyte */
        public Analyte() {
        }

        /**
         * @return Name of the analyte.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the analyte.
         * @param name Name of the analyte.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Returns the units of measure for this analyte.
         * @return Units of measure.
         */
        public String getUnits() {
            return units;
        }

        /**
         * Sets the units of measure for this analyte.
         * @param units Units of measure.
         */
        public void setUnits(String units) {
            this.units = units;
        }
        
    }
}



