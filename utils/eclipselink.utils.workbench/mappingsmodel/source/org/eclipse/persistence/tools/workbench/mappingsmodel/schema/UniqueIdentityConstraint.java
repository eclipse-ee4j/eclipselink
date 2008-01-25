/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class UniqueIdentityConstraint 
	extends IdentityConstraint 
{
	/** Toplink use only */
	private UniqueIdentityConstraint() {
		super();
	}
	UniqueIdentityConstraint(MWModel parent) {
		super(parent);
	}
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(UniqueIdentityConstraint.class);
		descriptor.getInheritancePolicy().setParentClass(IdentityConstraint.class);
		
		return descriptor;
	}

}
