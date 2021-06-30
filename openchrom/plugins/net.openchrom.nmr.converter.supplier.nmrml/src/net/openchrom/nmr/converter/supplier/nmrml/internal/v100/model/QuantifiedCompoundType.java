/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.nmr.converter.supplier.nmrml.internal.v100.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantifiedCompoundType", propOrder = {"concentration", "clusterList", "peakList"})
public class QuantifiedCompoundType extends ChemicalCompoundType {

	@XmlElement(required = true)
	protected ValueWithUnitType concentration;
	protected ClusterListType clusterList;
	protected PeakListType peakList;

	public ValueWithUnitType getConcentration() {

		return concentration;
	}

	public void setConcentration(ValueWithUnitType value) {

		this.concentration = value;
	}

	public ClusterListType getClusterList() {

		return clusterList;
	}

	public void setClusterList(ClusterListType value) {

		this.clusterList = value;
	}

	public PeakListType getPeakList() {

		return peakList;
	}

	public void setPeakList(PeakListType value) {

		this.peakList = value;
	}
}
