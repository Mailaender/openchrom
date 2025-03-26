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
package net.openchrom.msd.identifier.supplier.cdk.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

import net.openchrom.msd.identifier.supplier.cdk.Activator;
import net.openchrom.msd.identifier.supplier.cdk.settings.MassSpectrumFormulaSettings;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final float MIN_FACTOR = 0.0f;
	public static final float MAX_FACTOR = 100.0f;

	public static final String P_LIMIT_MATCH_FACTOR_FORMULA = "limitMatchFactorFormula";
	public static final float DEF_LIMIT_MATCH_FACTOR_FORMULA = 80.0f;

	public static final String P_MATCH_QUALITY_FORMULA = "matchFactorFormula";
	public static final float DEF_MATCH_QUALITY_FORMULA = 80.0f;

	public static final String P_MASS_ACCURACY_FORMULA = "massAccuracyFormula";
	public static final float DEF_MASS_ACCURACY_FORMULA = 0.1f;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

	}

	public static MassSpectrumFormulaSettings getMassSpectrumFormulaSettings() {

		MassSpectrumFormulaSettings settings = new MassSpectrumFormulaSettings();

		settings.setLimitMatchFactor(INSTANCE().getFloat(P_LIMIT_MATCH_FACTOR_FORMULA, DEF_LIMIT_MATCH_FACTOR_FORMULA));
		settings.setMatchQuality(INSTANCE().getFloat(P_MATCH_QUALITY_FORMULA, DEF_MATCH_QUALITY_FORMULA));
		settings.setAccuracy(INSTANCE().getFloat(P_MASS_ACCURACY_FORMULA, DEF_MASS_ACCURACY_FORMULA));

		return settings;
	}
}