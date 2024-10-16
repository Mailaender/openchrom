/*******************************************************************************
 * Copyright (c) 2013, 2022 Marwin Wollschläger, Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marwin Wollschläger - initial API and implementation
 * Dr. Philip Wenig - additional API and implementation
 *******************************************************************************/
package net.openchrom.xxd.identifier.supplier.cdk.converter;

import org.openscience.cdk.interfaces.IAtomContainer;

import net.openchrom.xxd.identifier.supplier.cdk.preferences.PreferenceSupplier;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.NameToStructureConfig;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

public class OPSINIupacToMoleculeConverter implements IStructureConverter {

	private CDKSmilesToMoleculeConverter smilesToIMolecule = new CDKSmilesToMoleculeConverter();

	@Override
	public IAtomContainer generate(String input) {

		IAtomContainer molecule = null;
		if(input != null) {
			NameToStructure nameStructure = NameToStructure.getInstance();
			NameToStructureConfig nameStructureConfig = new NameToStructureConfig();
			nameStructureConfig.setAllowRadicals(PreferenceSupplier.isAllowRadicals());
			nameStructureConfig.setDetailedFailureAnalysis(PreferenceSupplier.isDetailedFailureAnalysis());
			nameStructureConfig.setInterpretAcidsWithoutTheWordAcid(PreferenceSupplier.isInterpretAcidsWithoutTheWordAcid());
			nameStructureConfig.setOutputRadicalsAsWildCardAtoms(PreferenceSupplier.isOutputRadicalsAsWildCardAtoms());
			nameStructureConfig.setWarnRatherThanFailOnUninterpretableStereochemistry(PreferenceSupplier.isWarnRatherThanFail());
			OpsinResult result = nameStructure.parseChemicalName(input, nameStructureConfig);
			molecule = smilesToIMolecule.generate(result.getSmiles());
		}
		return molecule;
	}
}
