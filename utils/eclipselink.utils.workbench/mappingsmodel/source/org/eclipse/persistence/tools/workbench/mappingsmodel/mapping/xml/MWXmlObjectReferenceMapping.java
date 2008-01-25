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

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;

public final class MWXmlObjectReferenceMapping extends MWAbstractXmlReferenceMapping {

	// **************** Constructors ******************************************

	/**
	 * Default constructor, TopLink use only.
	 */
	private MWXmlObjectReferenceMapping() {
		
	}
	
	public MWXmlObjectReferenceMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlObjectReferenceMapping.class);
		descriptor.descriptorIsAggregate();
		descriptor.getInheritancePolicy().setParentClass(MWAbstractXmlReferenceMapping.class);
		
		return descriptor;
	}
	
	// **************** Morphing **********************************************
	
	public MWXmlObjectReferenceMapping asMWXmlObjectReferenceMapping() {
		return this;
	}

	@Override
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWXmlObjectReferenceMapping(this);
	}

	// **************** Runtime Conversion ************************************
	
	@Override
	public DatabaseMapping buildRuntimeMapping() {
		return new XMLObjectReferenceMapping();
	}

}
