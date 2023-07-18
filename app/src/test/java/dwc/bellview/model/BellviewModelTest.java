package dwc.bellview.model;

import java.io.File;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dwc.bellview.BellviewException;
import dwc.bellview.file.CSVFileImporter;

public class BellviewModelTest {
	private BellviewModel model;

	public BellviewModelTest() {
		model = new BellviewModel();
	}

	@BeforeEach
	public void loadData() {
		try {
			File file = new File("src/test/resources/testdata.csv");
			CSVFileImporter importer = new CSVFileImporter();
			importer.setFile(file);
			importer.setColumnIndex(3);
			importer.setFirstRowHeadings(true);

			importer.run();
			model.getData().clear();
			model.setData(importer.getDataItems());
			System.out.println("Record count "+model.getData().size());
		} catch (BellviewException ex) {
			Assertions.fail(ex);
		}
	}

	@Test
	public void buildHistogram() {
		DescriptiveStatistics statistics = model.buildStatistics();
		Assertions.assertTrue(statistics.getN() > 5000);
		model.getHistogram().setMinValue(statistics.getMin());
		model.getHistogram().setMaxValue(statistics.getMax());
		model.getHistogram().setBinWidth(model.getHistogram().getDefaultBinWidth());
		model.addDataToHistogram();
		System.out.println(model.getHistogram().toString());
		model.getHistogram().clear();
		model.getHistogram().setMinValue(42d);
		model.getHistogram().setMaxValue(82d);
		model.getHistogram().setBinWidth(1.0);
		model.addDataToHistogram();
		System.out.println(model.getHistogram().toString());

		try {
			model.setStartIndexValue(13);
			model.setEndIndexValue(38);
			SimpleRegression regression = model.calculateRegression();
			System.out.println("N: "+regression.getN());
			System.out.println("Slope: "+regression.getSlope());
			System.out.println("Slope CI: ["+
			(regression.getSlope()-regression.getSlopeConfidenceInterval())+
			" - "+
			(regression.getSlope()+regression.getSlopeConfidenceInterval())+"]");
			System.out.println("Intercept: "+regression.getIntercept());
			System.out.println("MSE: "+regression.getMeanSquareError());
			System.out.println("Rsq: "+regression.getRSquare());
		} catch (BellviewException e) {
			Assertions.fail(e);
		}

	}

}
