/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package net.openchrom.nmr.processing.supplier.base.fragment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

import net.openchrom.nmr.processing.supplier.base.core.ZeroFillingProcessor;

public class ZeroFillingTest {

	@Test
	public void testFilling() {

		assertEquals(2l, ZeroFillingProcessor.fill(new Complex[1]).length);
		assertEquals(2l, ZeroFillingProcessor.fill(new Complex[2]).length);
		Complex[] fill = ZeroFillingProcessor.fill(new Complex[3]);
		assertEquals(4l, fill.length);
		// test that only the last item is filled with zero
		assertNull(fill[0]);
		assertNull(fill[1]);
		assertNull(fill[2]);
		assertEquals(Complex.ZERO, fill[3]);
		assertEquals(4l, ZeroFillingProcessor.fill(new Complex[4]).length);
		assertEquals(32l, ZeroFillingProcessor.fill(new Complex[17]).length);
		assertEquals(16384l, ZeroFillingProcessor.fill(new Complex[16365]).length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNull() {

		ZeroFillingProcessor.fill(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmpty() {

		ZeroFillingProcessor.fill(new Complex[0]);
	}
}
