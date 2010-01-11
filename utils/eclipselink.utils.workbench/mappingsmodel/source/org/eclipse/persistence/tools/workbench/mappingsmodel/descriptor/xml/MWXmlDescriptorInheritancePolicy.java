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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;

import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class MWXmlDescriptorInheritancePolicy
	extends MWDescriptorInheritancePolicy
	implements MWXmlNode
{
	// **************** Constructors ******************************************
	
	protected MWXmlDescriptorInheritancePolicy() {
		super();
	}
	
	protected MWXmlDescriptorInheritancePolicy(MWXmlDescriptor descriptor) {
		super(descriptor);
	}
	
	
	// **************** Class indicator field policy **************************
	
	protected MWClassIndicatorFieldPolicy buildClassIndicatorFieldPolicy() {
		return new MWXmlClassIndicatorFieldPolicy(this, getAllDescriptorsAvailableForIndicatorDictionary().iterator());
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		((MWXmlNode) this.getClassIndicatorPolicy()).resolveXpaths();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		((MWXmlNode) this.getClassIndicatorPolicy()).schemaChanged(change);
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {		
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.getInheritancePolicy().setParentClass(MWDescriptorInheritancePolicy.class);	
		descriptor.setJavaClass(MWXmlDescriptorInheritancePolicy.class);
		return descriptor;
	}
}
