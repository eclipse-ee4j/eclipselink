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

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

public final class MWEisMappingFactory extends MWXmlMappingFactory
{
	private static MWEisMappingFactory INSTANCE;

	//	****************** static methods **************
	
	public static synchronized MWEisMappingFactory instance() {
		if (INSTANCE == null) {
			INSTANCE = new MWEisMappingFactory();
		}
		return INSTANCE;
	}

	// **************** Factory methods ***************************************

	public MWEisOneToManyMapping createEisOneToManyMapping(MWEisDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWEisOneToManyMapping(descriptor, attribute, name);
	}

	public MWEisOneToOneMapping createEisOneToOneMapping(MWEisDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWEisOneToOneMapping(descriptor, attribute, name);
	}
}