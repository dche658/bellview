package dwc.bellview;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class MathPrecision {

	@Test
	void test() {
		List<Double> cases = new ArrayList<>();
		cases.add(1.23456);
		cases.add(45.6789);
		cases.add(876.345);
		cases.add(3832.98);

		assertEquals("1.23",BellviewUtils.numericToString(cases.get(0), 3));
		assertEquals("45.7",BellviewUtils.numericToString(cases.get(1), 3));
		assertEquals("876",BellviewUtils.numericToString(cases.get(2), 3));
		assertEquals("3830",BellviewUtils.numericToString(cases.get(3), 3));
	}

}
