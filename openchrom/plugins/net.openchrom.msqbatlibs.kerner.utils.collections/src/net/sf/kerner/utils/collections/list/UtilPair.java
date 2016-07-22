/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package net.sf.kerner.utils.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.kerner.utils.pair.Pair;

public class UtilPair {

	public UtilPair() {
	}

	public static <T> List<T> getAllFirst(Collection<? extends Pair<T, ?>> collection) {

		List<T> result = new ArrayList<>();
		for(Pair<T, ?> c : collection) {
			if(c != null)
				result.add(c.getFirst());
		}
		return result;
	}

	public static <T> List<T> getAllSecond(Collection<? extends Pair<?, T>> collection) {

		List<T> result = new ArrayList<>();
		for(Pair<?, T> c : collection) {
			if(c != null)
				result.add(c.getSecond());
		}
		return result;
	}
}
