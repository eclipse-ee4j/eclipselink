/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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


public class TableStringHolder extends ObjectStringHolder {

	public TableStringHolder(MWTable table, StringConverter stringConverter) {
		super(table, stringConverter);
	}

	public TableStringHolder(MWTable table) {
		this(table, DEFAULT_STRING_CONVERTER);
	}

	public MWTable getTable() {
		return (MWTable) this.object;
	}


	// ********** static methods **********

	public static TableStringHolder[] buildHolders(Iterator tables) {
		return buildHolders(CollectionTools.list(tables));
	}

	public static TableStringHolder[] buildHolders(Collection tables) {
		MWTable[] tableArray = (MWTable[]) tables.toArray(new MWTable[tables.size()]);
		TableStringHolder[] holders = new TableStringHolder[tableArray.length];
		for (int i = tableArray.length; i-- > 0; ) {
			holders[i] = new TableStringHolder(tableArray[i]);
		}
		return holders;
	}


	// ********** constants **********

	public static final StringConverter DEFAULT_STRING_CONVERTER = new StringConverter() {
		public String convertToString(Object o) {
			return ((MWTable) o).getShortName().toLowerCase();
		}
	};

}
