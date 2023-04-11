package dwc.bellview.transform;


/**
 *
 * @author Douglas Chesher
 */
public interface DataTransform {
    
    public static final String NO_TRANSFORM = "No transform";
    public static final String LOG_TRANSFORM = "Log(e) transform";
    
    /**
     * Returns the data following the transform
     * @param value
     * @return transformed value
     */
    public Double transform(double value);

    /**
     * Returns the inverse of the transform
     * @param value
     * @return inverse transform value
     */
    public Double inverse(double value);
    
    /**
     * Return the name of the transform
     * @return name
     */
    public String getName();
}
