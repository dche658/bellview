package dwc.bellview.model;


/**
 *
 * @author Douglas Chesher
 */

public class DataElement {
	
	public static final String SEX_UNKNOWN = "U";
	
	public static final double AGE_UNKNOWN = -1.0;
    
    private Long id;
    
    private double rawValue;
    
    private double transformedValue;
    
    private double age;
    
    private String sex;

    public DataElement() {
        this(0.0, 0.0, SEX_UNKNOWN, AGE_UNKNOWN);
    }
    
    public DataElement(double rawValue, double transformedValue) {
        this(rawValue, transformedValue, SEX_UNKNOWN, AGE_UNKNOWN);
    }
    
    public DataElement(double rawValue, double transformedValue, String sex, double age) {
    	this.rawValue = rawValue;
        this.transformedValue = transformedValue;
        this.sex = sex;
        this.age = age;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DataElement)) {
            return false;
        }
        DataElement other = (DataElement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(DataElement.class.getName());
        sb.append("[id=").append(id).append(", rawValue=").append(rawValue)
                .append(", transformedValue").append(transformedValue).append("]");
        return sb.toString();
    }

    /**
     * @return the rawValue
     */
    public double getRawValue() {
        return rawValue;
    }

    /**
     * @param rawValue the rawValue to set
     */
    public void setRawValue(double rawValue) {
        this.rawValue = rawValue;
    }

    /**
     * @return the transformedValue
     */
    public double getTransformedValue() {
        return transformedValue;
    }

    /**
     * @param transformedValue the transformedValue to set
     */
    public void setTransformedValue(double transformedValue) {
        this.transformedValue = transformedValue;
    }

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
    
}
