package dwc.bellview.transform;

/**
 *
 * @author Douglas Chesher
 */
public class NoneTransform implements DataTransform {

    @Override
    public Double transform(double value) {
        return value;
    }

    @Override
    public Double inverse(double value) {
        return value;
    }

    @Override
    public String getName() {
        return DataTransform.NO_TRANSFORM;
    }
    
}
