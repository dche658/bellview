package dwc.bellview.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class HistogramBinTest {
	
	@Test
	public void binTest() {
		HistogramBin bin = new HistogramBin(50d,10d,0);
		
		for(int i=0;i<100;i++) {
			bin.incrementCount();
		}
		assertEquals(100,bin.getCount());
		
	}

}
