/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.ObjectStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public class ColumnStringHolder extends ObjectStringHolder {

	public ColumnStringHolder(MWColumn column, StringConverter stringConverter) {
		super(column, stringConverter);
	}

	public ColumnStringHolder(MWColumn column) {
		this(column, DEFAULT_STRING_CONVERTER);
	}

	public MWColumn getColumn() {
		return (MWColumn) this.object;
	}


	// ********** static methods **********

	public static ColumnStringHolder[] buildHolders(Iterator columns) {
		return buildHolders(CollectionTools.list(columns));
	}

	public static ColumnStringHolder[] buildHolders(Collection columns) {
		MWColumn[] columnArray = (MWColumn[]) columns.toArray(new MWColumn[columns.size()]);
		ColumnStringHolder[] holders = new ColumnStringHolder[columnArray.length];
		for (int i = columnArray.length; i-- > 0; ) {
			holders[i] = new ColumnStringHolder(columnArray[i]);
		}
		return holders;
	}


	// ********** constants **********

	public static final StringConverter DEFAULT_STRING_CONVERTER = new StringConverter() {
		public String convertToString(Object o) {
			return ((MWColumn) o).getName().toLowerCase();
		}
	};

}
