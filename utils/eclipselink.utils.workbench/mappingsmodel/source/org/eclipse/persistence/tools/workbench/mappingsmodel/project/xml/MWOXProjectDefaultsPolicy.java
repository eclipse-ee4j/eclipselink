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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * This class describes the project defaults for an OX project.
 * 
 * @version 10.1.3
 */
public final class MWOXProjectDefaultsPolicy extends MWProjectDefaultsPolicy
{
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWOXProjectDefaultsPolicy.class);
		descriptor.getInheritancePolicy().setParentClass(MWProjectDefaultsPolicy.class);
		
		return descriptor;
	}
	
	private MWOXProjectDefaultsPolicy()
	{
		super();
	}
	
	MWOXProjectDefaultsPolicy(MWOXProject parent)
	{
		super(parent);
	}
}
