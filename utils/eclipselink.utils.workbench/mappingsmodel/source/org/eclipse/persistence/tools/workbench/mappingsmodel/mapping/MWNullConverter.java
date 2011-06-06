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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;

public final class MWNullConverter 
	extends MWConverter
{
	// **************** Constructors ****************
	
	/** Default constructor - for TopLink use only */
	private MWNullConverter() {
		super();
	}

	public MWNullConverter(MWConverterMapping parent) {
		super(parent);
	}
	
	
	// ************** MWConverter implementation *************
	
	/** Should ONLY be used in one place - the UI */
	public String accessibleNameKey() {
		return "ACCESSIBLE_DIRECT_TO_FIELD_MAPPING_NODE";
	}

	public String getType() {
		return NO_CONVERTER;
	}
	
	public String iconKey() {
		return "mapping.directToField";
	}


	// **************** Runtime Conversion ****************
	
	public Converter runtimeConverter(DatabaseMapping mapping) {
		return null;
	}
	

	// **************** TopLink Methods ***************************************
	
	public /* package-protected */ MWConverter getValueForTopLink() {
		return null;
	}
	
}
