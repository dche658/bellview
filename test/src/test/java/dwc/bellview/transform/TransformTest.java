package dwc.bellview.transform;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransformTest {
	Double[] ri = new Double[2];
	
	@BeforeEach
	void setUp() throws Exception {
		ri[0] = 2.0245;
		ri[1] = 3.9762;
	}

	@Test
	void noneTransform() {
		DataTransform t = new NoneTransform();
		assertEquals(t.transform(ri[0]),ri[0]);
		assertEquals(t.transform(ri[1]),ri[1]);
	}
	
	@Test
	void logTransform() {
		DataTransform t = new LogTransform();
		Double low = Math.log(60);
		assertEquals(t.transform(60),low);
		assertEquals(t.inverse(low),60, 0.001, "Not within range");
	}

}
