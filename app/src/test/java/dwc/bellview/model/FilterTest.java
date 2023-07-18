package dwc.bellview.model;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilterTest {
	
	private List<DataElement> data = new ArrayList<>();;

	@BeforeEach
	public void setUp() throws Exception {
		DataElement d0 = new DataElement(1.0,1.0, "M", 5);
		DataElement d1 = new DataElement(1.0,1.0, "F", 5);
		DataElement d2 = new DataElement(1.0,1.0, "M", 10);
		DataElement d3 = new DataElement(1.0,1.0, "F", 10);
		DataElement d4 = new DataElement(1.0,1.0, "M", 15);
		DataElement d5 = new DataElement(1.0,1.0, "F", 15);
		DataElement d6 = new DataElement(1.0,1.0, "M", 25);
		DataElement d7 = new DataElement(1.0,1.0, "F", 25);
		DataElement d8 = new DataElement(1.0,1.0, "M", 35);
		DataElement d9 = new DataElement(1.0,1.0, "F", 35);
		DataElement d10 = new DataElement(1.0,1.0, DataElement.SEX_UNKNOWN, 10);
		DataElement d11 = new DataElement(1.0,1.0, DataElement.SEX_UNKNOWN, 10);
		DataElement d12 = new DataElement(1.0,1.0, "M", DataElement.AGE_UNKNOWN);
		DataElement d13 = new DataElement(1.0,1.0, "M", DataElement.AGE_UNKNOWN);
		DataElement d14 = new DataElement(1.0,1.0, DataElement.SEX_UNKNOWN, DataElement.AGE_UNKNOWN);
		data.add(d0);
		data.add(d1);
		data.add(d2);
		data.add(d3);
		data.add(d4);
		data.add(d5);
		data.add(d6);
		data.add(d7);
		data.add(d8);
		data.add(d9);
		data.add(d10);
		data.add(d11);
		data.add(d12);
		data.add(d13);
		data.add(d14);
	}

	@Test
	public void test1() {
		Filter filter = new Filter("M", 10.0, 25.0);
		List<DataElement> subset = filter.apply(data);
		assertEquals(2, subset.size());
	}
	
	@Test
	public void test2() {
		Filter filter = new Filter("M", null, 25.0);
		List<DataElement> subset = filter.apply(data);
		assertEquals(3, subset.size());
	}
	
	@Test
	public void test3() {
		Filter filter = new Filter("M", null, null);
		List<DataElement> subset = filter.apply(data);
		assertEquals(7, subset.size());
	}
	
	@Test
	public void test4() {
		Filter filter = new Filter(null, 10.0, 25.0);
		List<DataElement> subset = filter.apply(data);
		assertEquals(6, subset.size());
	}
	
	@Test
	public void test5() {
		Filter filter = new Filter(null, null, null);
		List<DataElement> subset = filter.apply(data);
		assertEquals(15, subset.size());
	}
}
