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
package net.openchrom.msd.identifier.supplier.cdk.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.AbstractIdentifierSettings;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import net.openchrom.msd.identifier.supplier.cdk.preferences.PreferenceSupplier;

public class MassSpectrumFormulaSettings extends AbstractIdentifierSettings implements IFormulaSettings, IMassSpectrumIdentifierSettings {

	@JsonProperty(value = "Match Quality", defaultValue = "80.0")
	@JsonPropertyDescription(value = "Use this fixed value as score.")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_FACTOR, maxValue = PreferenceSupplier.MAX_FACTOR)
	private float matchQuality = 80.0f;

	@JsonProperty(value = "Accuracy", defaultValue = "0.1")
	@JsonPropertyDescription(value = "The ± value range for masses around the molecular ion.")
	private float accuracy = 0.1f;

	@Override
	public float getMatchQuality() {

		return matchQuality;
	}

	@Override
	public void setMatchQuality(float matchQuality) {

		this.matchQuality = matchQuality;
	}

	@Override
	public float getAccuracy() {

		return accuracy;
	}

	@Override
	public void setAccuracy(float accuracy) {

		this.accuracy = accuracy;
	}
}
