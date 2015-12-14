/*******************************************************************************
 *  Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package net.sf.kerner.utils.collections.trasformer;

import net.sf.kerner.utils.collections.list.AbstractTransformingListFactory;
import net.sf.kerner.utils.pair.Pair;

public class TransformerObjectPairToSecond<S> extends AbstractTransformingListFactory<Pair<?, S>, S> {

	public S transform(final Pair<?, S> element) {

		return element.getSecond();
	}
}
