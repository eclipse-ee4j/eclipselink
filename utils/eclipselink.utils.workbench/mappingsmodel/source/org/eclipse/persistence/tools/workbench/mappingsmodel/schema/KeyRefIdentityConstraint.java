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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class KeyRefIdentityConstraint 
	extends IdentityConstraint 
{
	/** The key identity constraint used by this key ref identity constraint */
	private volatile String refer;
	
	/** Toplink use only */
	private KeyRefIdentityConstraint() {
		super();
	}
	
	KeyRefIdentityConstraint(MWModel parent) {
		super(parent);
	}
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(KeyRefIdentityConstraint.class);
		descriptor.getInheritancePolicy().setParentClass(IdentityConstraint.class);
		
		descriptor.addDirectMapping("refer", "refer/text()");
		
		return descriptor;
	}

	void reload(org.apache.xerces.impl.xs.identity.IdentityConstraint idConstraint) {
		super.reload(idConstraint);
		
		this.refer = idConstraint.getRefKey().getName();

	}
}
