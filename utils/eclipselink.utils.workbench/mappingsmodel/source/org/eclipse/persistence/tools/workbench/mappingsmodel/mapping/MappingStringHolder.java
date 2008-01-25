/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.ObjectStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public class MappingStringHolder extends ObjectStringHolder {

	public MappingStringHolder(MWMapping mapping, StringConverter stringConverter) {
		super(mapping, stringConverter);
	}

	public MappingStringHolder(MWMapping mapping) {
		this(mapping, DEFAULT_STRING_CONVERTER);
	}

	public MWMapping getMapping() {
		return (MWMapping) this.object;
	}


	// ********** static methods **********

	public static MappingStringHolder[] buildHolders(Iterator mappings) {
		return buildHolders(CollectionTools.list(mappings));
	}

	public static MappingStringHolder[] buildHolders(Collection mappings) {
		MWMapping[] mappingArray = (MWMapping[]) mappings.toArray(new MWMapping[mappings.size()]);
		MappingStringHolder[] holders = new MappingStringHolder[mappingArray.length];
		for (int i = mappingArray.length; i-- > 0; ) {
			holders[i] = new MappingStringHolder(mappingArray[i]);
		}
		return holders;
	}


	// ********** constants **********

	public static final StringConverter DEFAULT_STRING_CONVERTER = new StringConverter() {
		public String convertToString(Object o) {
			return ((MWMapping) o).getName().toLowerCase();
		}
	};

}
