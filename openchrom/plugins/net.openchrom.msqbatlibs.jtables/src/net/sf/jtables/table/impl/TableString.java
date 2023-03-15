/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 * Philip Wenig - refactorings
 *******************************************************************************/
package net.sf.jtables.table.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jtables.table.Column;
import net.sf.jtables.table.Row;
import net.sf.kerner.utils.collections.list.UtilList;

/**
 *
 * An {@link net.sf.jtables.table.TableMutableAnnotated AnnotatedMutableTable} with {@link java.lang.String String} elements.
 *
 *
 * @author <a href="mailto:alex.kerner.24@googlemail.com">Alexander Kerner</a>
 * @version 2010-12-25
 *
 */
public class TableString extends AnnotatedMutableTableImpl<String> {

	public TableString() {

		super();
	}

	public TableString(final List<Row<String>> rows) {

		super(rows);
	}

	public TableImpl<String> clone() throws CloneNotSupportedException {

		final List<Row<String>> rows = new ArrayList<Row<String>>();
		for(final Row<String> row : this.rows) {
			final Column<String> columns = new ColumnImpl<String>();
			for(final String element : row) {
				columns.add(element);
			}
			rows.add(columns);
		}
		return new TableString(rows);
	}

	public List<ColumnString> getColumns(final String idPattern) {

		final List<ColumnString> result = new ArrayList<ColumnString>();
		for(final Object value : getColumnIdentifier()) {
			if(value.toString().matches(idPattern)) {
				result.add((ColumnString)getColumn(value));
			}
		}
		return result;
	}

	public List<RowString> getRows(final String idPattern) {

		final List<RowString> result = new ArrayList<RowString>();
		for(final Object value : getRowIdentifier()) {
			if(value.toString().matches(idPattern)) {
				result.add((RowString)getRow(value));
			}
		}
		return result;
	}

	public TableString sortByColumnIds() {

		final List<String> sorted = new ArrayList<String>(UtilList.toStringList(getColumnIdentifier()));
		Collections.sort(sorted);
		final TableString result = new TableString();
		result.setColumnIdentifier(sorted);
		result.setRowIdentifier(getRowIdentifier());
		for(final String value : sorted) {
			result.addColumn(this.getColumn(value));
		}
		return result;
	}
}