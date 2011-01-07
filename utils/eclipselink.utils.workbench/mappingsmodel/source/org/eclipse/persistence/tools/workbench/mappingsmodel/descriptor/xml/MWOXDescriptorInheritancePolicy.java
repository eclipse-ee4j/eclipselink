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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * Inheritance policy used in OX projects
 */
public final class MWOXDescriptorInheritancePolicy
	extends MWXmlDescriptorInheritancePolicy 
{
	// **************** Constructors ******************************************
	
	/** For TopLink use only */
	private MWOXDescriptorInheritancePolicy() {
		super();
	}
	
	MWOXDescriptorInheritancePolicy(MWOXDescriptor descriptor) {
		super(descriptor);
	}	
	
	
	// **************** Problem handling **************************************
	
	protected String descendantDescriptorTypeMismatchProblemString() {
		throw new UnsupportedOperationException("Should not have this problem, currently only one type of OX descriptor");
	}
	
	protected boolean checkDescendantsForDescriptorTypeMismatch() {
		return false;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.getInheritancePolicy().setParentClass(MWXmlDescriptorInheritancePolicy.class);
		descriptor.setJavaClass(MWOXDescriptorInheritancePolicy.class);
		return descriptor;
	}
}
