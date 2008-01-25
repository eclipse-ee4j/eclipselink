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
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;

public final class MWXmlFragmentCollectionMapping extends MWAbstractXmlDirectCollectionMapping {

	// **************** Constructors ******************************************

	/**
	 * Default constructor, TopLink use only.
	 */
	private MWXmlFragmentCollectionMapping() {
		super();
	}
	
	MWXmlFragmentCollectionMapping(MWXmlDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}
	
	// **************** Morphing **********************************************
	
	public MWXmlFragmentCollectionMapping asMWXmlFragmentCollectionMapping() {
		return this;
	}

	@Override
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWXmlFragmentCollectionMapping(this);
	}

	// **************** TopLink methods ***************************************
	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlFragmentCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractXmlDirectCollectionMapping.class);
		
		return descriptor;	
	}
	
	@Override
	protected DatabaseMapping buildRuntimeMapping() {
		return new XMLFragmentCollectionMapping();
	}

}
