package dwc.bellview.model;

import dwc.bellview.SignificantFigures;

public class ReferenceInterval {

	private double lowerReferenceLimit;
	
	private double upperReferenceLimit;
	
	public ReferenceInterval() {
		
	}
	
	public ReferenceInterval(double lrl, double url) {
		this.lowerReferenceLimit = lrl;
		this.upperReferenceLimit = url;
	}

	public double getLowerReferenceLimit() {
		return lowerReferenceLimit;
	}

	public void setLowerReferenceLimit(double lowerReferenceLimit) {
		this.lowerReferenceLimit = lowerReferenceLimit;
	}

	public double getUpperReferenceLimit() {
		return upperReferenceLimit;
	}

	public void setUpperReferenceLimit(double upperReferenceLimit) {
		this.upperReferenceLimit = upperReferenceLimit;
	}
	
	public String toString(int significantFigures) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
        sb.append(SignificantFigures.format(this.lowerReferenceLimit, significantFigures));
        sb.append(" - ");
        sb.append(SignificantFigures.format(this.upperReferenceLimit, significantFigures));
        sb.append("]\n");
        return sb.toString();
	}
}
