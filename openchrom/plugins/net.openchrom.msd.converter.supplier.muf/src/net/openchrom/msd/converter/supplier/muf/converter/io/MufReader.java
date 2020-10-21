/*******************************************************************************
 * Copyright (c) 2020 Matthias Mailänder.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.msd.converter.supplier.muf.converter.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

import net.openchrom.msd.converter.supplier.muf.converter.model.MufMassSpectra;

import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.MatFile;
import us.hebi.matlab.mat.types.Matrix;
import us.hebi.matlab.mat.types.Struct;

public class MufReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(MufReader.class);

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws IOException {

		MatFile matFile = Mat5.readFromFile(file); // 7.3 HDF is not supported https://github.com/HebiRobotics/MFL/issues/46
		Struct spec = matFile.getStruct("spec");
		MufMassSpectra muf = new MufMassSpectra();
		for(int col = 0; col < spec.getNumCols(); col++) {
			IScanMSD massSpectrum = new ScanMSD();
			Matrix pik = spec.get("pik", 0, col);
			for(int n = 0; n < pik.getNumCols(); n++) {
				double mz = pik.getDouble(0, n);
				float intensity = pik.getFloat(1, n);
				try {
					massSpectrum.addIon(new Ion(mz, intensity));
				} catch(AbundanceLimitExceededException
						| IonLimitExceededException e) {
					logger.warn(e);
				}
			}
			muf.addMassSpectrum(massSpectrum);
		}
		matFile.close();
		return muf;
	}
}
