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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAdvancedPropertyAdditionException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;

import org.eclipse.persistence.oxm.XMLDescriptor;


public final class MWCompositeEisDescriptor 
	extends MWEisDescriptor 
{

	// ********** static methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWCompositeEisDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWXmlDescriptor.class);
		

		return descriptor;
	}	
	

	// **************** Constructors ******************************************
	
	private MWCompositeEisDescriptor() {
		super();
	}

	public MWCompositeEisDescriptor(MWEisProject project, MWClass type, String name) {
		super(project, type, name);
	}

	
	public MWCompositeEisDescriptor asCompositeEisDescriptor() {
		return this;
	}

	public void applyAdvancedPolicyDefaults(MWProjectDefaultsPolicy defaultsPolicy) {
		defaultsPolicy.applyAdvancedPolicyDefaults(this);
	}
	
	
	public void addLockingPolicy() throws MWAdvancedPropertyAdditionException {
		//do nothing, locking not supported
	}
	
	public boolean isRootDescriptor() {
		return false;
	}
	
	@Override
	public MWXmlPrimaryKeyPolicy primaryKeyPolicy() {
		throw new UnsupportedOperationException("primary keys not supported on composite descriptors");
	}
}
