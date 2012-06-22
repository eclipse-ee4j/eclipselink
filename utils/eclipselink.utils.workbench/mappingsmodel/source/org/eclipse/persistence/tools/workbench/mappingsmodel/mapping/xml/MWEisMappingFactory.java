/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
