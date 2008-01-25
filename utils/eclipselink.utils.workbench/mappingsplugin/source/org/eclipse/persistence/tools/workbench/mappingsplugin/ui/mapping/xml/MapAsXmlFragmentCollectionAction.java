/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ChangeMappingTypeAction;


public final class MapAsXmlFragmentCollectionAction extends
		ChangeMappingTypeAction {

	public MapAsXmlFragmentCollectionAction(WorkbenchContext context) {
		super(context);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.initializeIcon("mapping.xmlFragmentCollection");
		this.initializeText("MAP_AS_XML_FRAGMENT_COLLECTION_ACTION");
		this.initializeMnemonic("MAP_AS_XML_FRAGMENT_COLLECTION_ACTION");
		this.initializeToolTipText("MAP_AS_XML_FRAGMENT_COLLECTION_ACTION.toolTipText");	
	}

	// ************ ChangeMappingTypeAction implementation ***********

	@Override
	protected MWMapping morphMapping(MWMapping mapping) {
		return mapping.asMWXmlFragmentCollectionMapping();
	}

	@Override
	protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
		return ((MWOXDescriptor) descriptor).addXmlFragmentCollectionMapping(attribute);
	}

	@Override
	protected Class mappingClass() {
		return MWXmlFragmentCollectionMapping.class;
	}

}
