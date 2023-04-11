package dwc.bellview.model;

import java.util.List;

public class Filter {

	private String sex;

	private Double ageLow;

	private Double ageHigh;

	public Filter() {
		this.sex = null;
		this.ageLow = null;
		this.ageHigh = null;
	}
	
	public Filter(String sex, Double ageLow, Double ageHigh) {
		this.sex = sex;
		this.ageLow = ageLow;
		this.ageHigh = ageHigh;
	}

	public void reset() {
		this.sex = null;
		this.ageLow = null;
		this.ageHigh = null;
	}

	public List<DataElement> apply(List<DataElement> data) {
		List<DataElement> subset = new java.util.ArrayList<>();
		data.forEach(d -> {
			if (isEqualSex(d) && isGreaterThanEqualAgeLow(d) && isLessThanAgeHigh(d)) {
				subset.add(d);
			}
		});
		return subset;
	}

	private boolean isEqualSex(DataElement d) {
		if (this.sex == null) {
			return true;
		} else {
			return this.sex.equals(d.getSex());
		}
	}

	private boolean isGreaterThanEqualAgeLow(DataElement d) {
		if (this.ageLow == null) {
			return true;
		} else {
			return d.getAge() >= this.ageLow ? true : false;
		}
	}

	private boolean isLessThanAgeHigh(DataElement d) {
		if (this.ageHigh == null) {
			return true;
		} else if (d.getAge() == -1.0) {
			return false;
		} else {
			return d.getAge() < this.ageHigh ? true : false;
		}
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getSex() {
		return this.sex;
	}

	public void setAgeLow(Double ageLow) {
		this.ageLow = ageLow;
	}
	
	public Double getAgeLow() {
		return this.ageLow;
	}

	public void setAgeHigh(Double ageHigh) {
		this.ageHigh = ageHigh;
	}
	
	public Double getAgeHigh() {
		return this.ageHigh;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Filter: ");
		if (this.sex == null && this.ageLow == null && this.ageHigh==null) {
			sb.append("none");
		} else {
			if (sex != null) {
				sb.append("sex=").append(sex).append("; ");
			}
			if (ageLow != null) {
				sb.append("age>=").append(ageLow).append("; ");
			}
			if (ageHigh != null) {
				sb.append("age<").append(ageHigh).append(";");
			}
		}
		return sb.toString();
	}
}
