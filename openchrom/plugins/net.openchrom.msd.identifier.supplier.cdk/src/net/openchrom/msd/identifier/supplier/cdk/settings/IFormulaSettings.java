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

import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;

public interface IFormulaSettings extends IIdentifierSettings {

	float getMatchQuality();

	void setMatchQuality(float matchQuality);

	float getAccuracy();

	void setAccuracy(float accuracy);
}
