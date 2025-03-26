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
package net.openchrom.msd.identifier.supplier.cdk.formula;

import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.model.targets.UnknownTargetBuilder;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import net.openchrom.msd.identifier.supplier.cdk.settings.IFormulaSettings;

public class FormulaIdentifier {

	public static final String IDENTIFIER = "Formula Identifier";

	public void runIdentificationPeak(List<? extends IPeak> peaks, float limitMatchFactor, IFormulaSettings formulaSettings) {

		for(IPeak peak : peaks) {
			if(LimitSupport.doIdentify(peak.getTargets(), limitMatchFactor)) {
				if(peak instanceof IPeakMSD peakMSD) {
					setPeakTargetUnknown(peakMSD, formulaSettings);
				}
			}
		}
	}

	private void setPeakTargetUnknown(IPeakMSD peakMSD, IFormulaSettings formulaSettings) {

		IScanMSD unknown = peakMSD.getExtractedMassSpectrum();
		IMolecularFormulaSet molecularFormulaSet = MassToFormula.generateMolecularFormulaSet(unknown, formulaSettings);
		for(IMolecularFormula molecularFormula : molecularFormulaSet.molecularFormulas()) {
			ILibraryInformation libraryInformation = getLibraryInformationFormula(molecularFormula);
			IComparisonResult comparisonResult = UnknownTargetBuilder.getComparisonResultUnknown(formulaSettings.getMatchQuality());
			IIdentificationTarget peakTarget = new IdentificationTarget(libraryInformation, comparisonResult);
			peakTarget.setIdentifier(IDENTIFIER);
			peakMSD.getTargets().add(peakTarget);
		}
	}

	public void runIdentificationScan(List<? extends IScan> spectraList, float limitMatchFactor, IFormulaSettings formulaSettings) {

		for(IScan scan : spectraList) {
			if(LimitSupport.doIdentify(scan.getTargets(), limitMatchFactor)) {
				if(scan instanceof IScanMSD scanMSD) {
					setMassSpectrumTargetFormula(scanMSD, formulaSettings);
				}
			}
		}
	}

	private void setMassSpectrumTargetFormula(IScanMSD unknown, IFormulaSettings formulaSettings) {

		IMolecularFormulaSet molecularFormulaSet = MassToFormula.generateMolecularFormulaSet(unknown, formulaSettings);
		for(IMolecularFormula molecularFormula : molecularFormulaSet.molecularFormulas()) {
			ILibraryInformation libraryInformation = getLibraryInformationFormula(molecularFormula);
			IComparisonResult comparisonResult = UnknownTargetBuilder.getComparisonResultUnknown(formulaSettings.getMatchQuality());
			IIdentificationTarget massSpectrumTarget = new IdentificationTarget(libraryInformation, comparisonResult);
			massSpectrumTarget.setIdentifier(IDENTIFIER);
			unknown.getTargets().add(massSpectrumTarget);
		}
	}

	private static ILibraryInformation getLibraryInformationFormula(IMolecularFormula molecularFormula) {

		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setExactMass(MolecularFormulaManipulator.getMass(molecularFormula, MolecularFormulaManipulator.MolWeightIgnoreSpecified));
		libraryInformation.setMolWeight(MolecularFormulaManipulator.getMass(molecularFormula, MolecularFormulaManipulator.MolWeight));
		String formulaText = MolecularFormulaManipulator.getString(molecularFormula);
		libraryInformation.setFormula(formulaText);
		libraryInformation.setName(formulaText);
		return libraryInformation;
	}
}
