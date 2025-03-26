/*******************************************************************************
 * Copyright (c) 2025 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.msd.identifier.supplier.cdk.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

import net.openchrom.msd.identifier.supplier.cdk.formula.FormulaIdentifier;
import net.openchrom.msd.identifier.supplier.cdk.preferences.PreferenceSupplier;
import net.openchrom.msd.identifier.supplier.cdk.settings.MassSpectrumFormulaSettings;

public class MassSpectrumIdentifierFormula extends AbstractMassSpectrumIdentifier {

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();

		MassSpectrumFormulaSettings formulaSettings;
		if(identifierSettings instanceof MassSpectrumFormulaSettings settings) {
			formulaSettings = settings;
		} else {
			formulaSettings = PreferenceSupplier.getMassSpectrumFormulaSettings();
		}

		float limitMatchFactor = formulaSettings.getLimitMatchFactor();
		FormulaIdentifier formulaIdentifier = new FormulaIdentifier();
		formulaIdentifier.runIdentificationScan(massSpectraList, limitMatchFactor, formulaSettings);
		processingInfo.addInfoMessage(FormulaIdentifier.IDENTIFIER, "Mass spectra have been identified.");
		return processingInfo;
	}
}
