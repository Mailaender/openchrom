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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonCombinedComparator;
import org.eclipse.chemclipse.msd.model.core.comparator.IonComparatorMode;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.formula.MolecularFormulaGenerator;
import org.openscience.cdk.formula.MolecularFormulaRange;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import net.openchrom.msd.identifier.supplier.cdk.settings.IFormulaSettings;

public class MassToFormula {

	private static final Logger logger = Logger.getLogger(MassToFormula.class);

	public static IMolecularFormulaSet generateMolecularFormulaSet(IScanMSD unknown, IFormulaSettings formulaSettings) {

		IIon molPeak = estimateMolecularIon(unknown);
		IIon mPlusOne = unknown.getIons().get(unknown.getIons().indexOf(molPeak));

		MolecularFormulaRange range = new MolecularFormulaRange();
		Isotopes isotopes;
		try {
			isotopes = Isotopes.getInstance();

			int carbons = (int)Math.round((mPlusOne.getIon() / molPeak.getIon()) * (1 / 0.011));
			range.addIsotope(isotopes.getMajorIsotope("C"), carbons - 1, carbons + 1);

			range.addIsotope(isotopes.getMajorIsotope("H"), 0, 2 * carbons + 2); // cover at least alkanes

			double massCarbon = 12.011;

			double massOxygen = 15.999;
			range.addIsotope(isotopes.getMajorIsotope("O"), 0, (int)Math.round((molPeak.getIon() - massCarbon * carbons) / massOxygen));

			double massNitrogen = 14.0067;
			range.addIsotope(isotopes.getMajorIsotope("N"), 0, (int)Math.round((molPeak.getIon() - massCarbon * carbons) / massNitrogen));
		} catch(IOException e) {
			logger.error(e);
		}

		float min = (float)(molPeak.getIon() - formulaSettings.getAccuracy());
		float max = (float)(molPeak.getIon() + formulaSettings.getAccuracy());

		MolecularFormulaGenerator generator = new MolecularFormulaGenerator(SilentChemObjectBuilder.getInstance(), min, max, range);
		return generator.getAllFormulas();
	}

	// TODO: This is not always correct.
	private static IIon estimateMolecularIon(IScanMSD scanMSD) {

		try {
			List<IIon> ionList = new ArrayList<>(scanMSD.makeDeepCopy().getIons());
			ionList.sort(new IonCombinedComparator(IonComparatorMode.ABUNDANCE_FIRST, SortOrder.DESC));
			List<IIon> highestIons = new ArrayList<>(ionList.stream().limit(10).toList()); // TODO arbitrary pick
			highestIons.sort(new IonCombinedComparator(IonComparatorMode.MZ_FIRST, SortOrder.DESC));
			return highestIons.getFirst();
		} catch(CloneNotSupportedException e) {
			logger.error(e);
		}
		return null;
	}
}
