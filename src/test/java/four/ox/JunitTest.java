package four.ox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JunitTest {
	@Test
	public void test() {
		
		int a = 2;
		int b = 2;
		assertEquals(a *2, b);
	}
}
