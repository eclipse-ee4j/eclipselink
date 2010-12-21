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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;

public final class MWXmlFragmentMapping extends MWAbstractXmlDirectMapping {

	// **************** Constructors ******************************************

	/**
	 * Default constructor, TopLink use only.
	 */
	public MWXmlFragmentMapping() {
		super();
	}

	public MWXmlFragmentMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

	// **************** Morphing **********************************************
	
	public MWXmlFragmentMapping asMWXmlFragmentMapping() {
		return this;
	}

	@Override
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWXmlFragmentMapping(this);
	}

	// **************** TopLink methods ***************************************
	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlFragmentMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractXmlDirectMapping.class);
		
		return descriptor;	
	}
	
	@Override
	public DatabaseMapping buildRuntimeMapping() {
		return new XMLFragmentMapping();
	}

}
