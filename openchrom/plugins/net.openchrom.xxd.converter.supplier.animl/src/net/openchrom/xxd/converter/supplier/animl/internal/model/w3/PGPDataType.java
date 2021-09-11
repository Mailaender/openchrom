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
package net.openchrom.xxd.converter.supplier.animl.internal.model.w3;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PGPDataType", propOrder = {"content"})
public class PGPDataType {

	@XmlElementRefs({@XmlElementRef(name = "PGPKeyID", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = "PGPKeyPacket", namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false)})
	@XmlAnyElement(lax = true)
	protected List<Object> content;

	public List<Object> getContent() {

		if(content == null) {
			content = new ArrayList<Object>();
		}
		return this.content;
	}
}
