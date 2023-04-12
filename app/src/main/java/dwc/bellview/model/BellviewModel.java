package dwc.bellview.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dwc.bellview.BellviewException;
import dwc.bellview.file.ImportError;
import dwc.bellview.transform.DataTransform;
import dwc.bellview.transform.NoneTransform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BellviewModel {
	
	private static Logger logger = LoggerFactory.getLogger(BellviewModel.class);
	
	/* 
	 * Data transformation algorithm. Default is none.
	 */
	private DataTransform dataTransform = new NoneTransform();

	/*
	 * Histogram
	 */
	private Histogram histogram = new Histogram();
	
	/* 
	 * Descriptive statistics for the data
	 */
	private DescriptiveStatistics statistics = new DescriptiveStatistics();
	
	/*
	 * Linear regression model
	 */
	private SimpleRegression regression;
	
	/*
	 * Start index for regression model
	 */
	private IntegerProperty startIndex = new SimpleIntegerProperty(this,"startIndex",0);
	
	/*
	 * End index for regression model
	 */
	private IntegerProperty endIndex = new SimpleIntegerProperty(this,"endIndex",0);
	
	/*
	 * Data Filter for sex and age
	 */
	private Filter filter = new Filter();
	

	private List<DataElement> data = new ArrayList<>();
	
	private List<DataElement> subset = new ArrayList<>();
	
	private List<String> genderList = new ArrayList<>();
	
	private List<ImportError> importErrors;
	
	public BellviewModel() {
		
	}

	public ObservableList<Map<String, Object>> getHistogramTableData() {
		ObservableList<Map<String, Object>> dataList = FXCollections.<Map<String, Object>>observableArrayList();
		for (int i=0;i<histogram.getElements().size();i++) {
			Map<String,Object> m = new HashMap<>();
			HistogramBin e = histogram.getElements().get(i);
			m.put("index", i);
			m.put("bin", e.getBinValue());
			m.put("count", e.getCount());
			m.put("dly", e.getDeltaLogY());
			dataList.add(m);
		}
		return dataList;
	}
	public DataTransform getDataTransform() {
		return dataTransform;
	}

	public void setDataTransform(DataTransform dataTransform) {
		this.dataTransform = dataTransform;
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
	}
	
	@SuppressWarnings("exports")
	public DescriptiveStatistics getStatistics() {
		return statistics;
	}

	@SuppressWarnings("exports")
	public void setStatistics(DescriptiveStatistics statistics) {
		this.statistics = statistics;
	}
	
	@SuppressWarnings("exports")
	public DescriptiveStatistics buildStatistics() {
		statistics.clear();
		subset.forEach(d -> {
			statistics.addValue(dataTransform.transform(d.getRawValue()));
		});
		logger.debug("Mean: {}; SD: {}; N: {}", statistics.getMean(), statistics.getStandardDeviation(), statistics.getN());
		return statistics;
	}

	public void addDataToHistogram() {
		subset.forEach(d -> {
			histogram.addData(dataTransform.transform(d.getRawValue()));
		});
		histogram.analyzeDeltaLogY();
	}
	
	public void applyTransform() {
		subset.forEach(d -> {
			d.setTransformedValue(dataTransform.transform(d.getRawValue()));
		});
	}
	
	@SuppressWarnings("exports")
	public SimpleRegression calculateRegression() throws BellviewException {
        this.regression = new SimpleRegression();
        if (endIndex.get() >= histogram.getElements().size()) {
            StringBuilder msg = new StringBuilder("Last index of ");
            msg.append(endIndex).append(" is greater than or equal to the number of bins=");
            msg.append(histogram.getElements().size());
            throw new BellviewException(msg.toString());
        }
        for (int i = startIndex.get(); i <= endIndex.get(); i++) {
            HistogramBin e = histogram.getElements().get(i);
            regression.addData(e.getBinValue() + e.getBinWidth() / 2, e.getDeltaLogY());
        }
        return regression;
    }
	
	@SuppressWarnings("exports")
	public DistributionParameters getDistributionParameters(SimpleRegression reg) {
        DistributionParameters parameters = new DistributionParameters();
        double xIntercept = (0 - reg.getIntercept()) / reg.getSlope();
        parameters.setMean(xIntercept + (histogram.getBinWidth() / 2));
        parameters.setVariance((-1 * histogram.getBinWidth() / reg.getSlope()) - (histogram.getBinWidth() * histogram.getBinWidth() / 12));
        return parameters;
    }
	
	public IntegerProperty getStartIndex() {
		return startIndex;
	}
	
	public void setStartIndexValue(Integer startIndex) {
		this.startIndex.set(startIndex);
	}

	public IntegerProperty getEndIndex() {
		return endIndex;
	}
	
	public void setEndIndexValue(Integer endIndex) {
		this.endIndex.set(endIndex);
	}

	public List<DataElement> getData() {
		return data;
	}

	public List<DataElement> getSubset() {
		return subset;
	}
	
	public void setSubset(List<DataElement> data) {
		this.subset = data;
	}

	public void setData(List<DataElement> data) {
		this.data = data;
		this.subset = data;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public List<String> getGenderList() {
		return genderList;
	}

	public void setGenderList(List<String> genderList) {
		this.genderList = genderList;
	}

	public List<ImportError> getImportErrors() {
		return importErrors;
	}

	public void setImportErrors(List<ImportError> importErrors) {
		this.importErrors = importErrors;
	}
	
}
