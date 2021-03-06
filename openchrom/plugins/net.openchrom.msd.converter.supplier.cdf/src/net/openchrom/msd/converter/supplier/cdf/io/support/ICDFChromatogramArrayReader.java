/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.msd.converter.supplier.cdf.io.support;

import net.openchrom.msd.converter.supplier.cdf.exceptions.NoSuchScanStored;
import net.openchrom.msd.converter.supplier.cdf.model.VendorScan;

public interface ICDFChromatogramArrayReader extends IAbstractCDFChromatogramArrayReader {

	/**
	 * Returns a valid mass spectrum of the given scan.
	 * 
	 * @param scan
	 * @param precision
	 * @return CDFMassSpectrum
	 * @throws NoSuchScanStored
	 */
	public VendorScan getMassSpectrum(int scan, int precision) throws NoSuchScanStored;
}
