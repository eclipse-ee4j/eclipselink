/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverterMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWXmlTypeConversionConverter extends MWTypeConversionConverter {


	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWXmlTypeConversionConverter.class);
		descriptor.getInheritancePolicy().setParentClass(MWTypeConversionConverter.class);
		return descriptor;
	}	
	

	// **************** Constructors ******************
	
	/** Default constructor - for TopLink use only */
	private MWXmlTypeConversionConverter() {
		super();
	}

	public MWXmlTypeConversionConverter(MWConverterMapping parent) {
		super(parent);
	}

}
