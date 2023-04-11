package dwc.bellview.transform;


/**
 * Base 10 logarithm transform
 * 
 * @author Douglas Chesher
 */
public class LogTransform implements DataTransform {
	
	private Double constant = 0.0;
	
	public LogTransform() {};
	
	public LogTransform(Double c) {
		constant = c;
	}
	
	public void setConstant(Double c) {
		constant = c;
	}
	
	public Double getConstant() {
		return constant;
	}

    @Override
    public Double transform(double value) {
        return Math.log(value + constant);
    }

    @Override
    public Double inverse(double value) {
        return Math.exp(value)-constant;
    }

    @Override
    public String getName() {
        return DataTransform.LOG_TRANSFORM;
    }
    
    
    
    
}
