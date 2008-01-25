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

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.OXDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class XmlFragmentMappingNode extends MappingNode {

	public XmlFragmentMappingNode(MWXmlFragmentMapping value, SelectionActionsPolicy mappingNodeTypePolicy, OXDescriptorNode parent) {
		super(value, mappingNodeTypePolicy, parent);
	}
	
	// **************** MappingNode contract **********************************
	
	@Override
	protected String buildIconKey() {
		return "mapping.xmlFragment";
	}

	
	// ************** AbstractApplicationNode overrides *************

	@Override
	protected String accessibleNameKey() {
		return "ACCESSIBLE_XML_FRAGMENT_MAPPING_NODE";
	}

	
	// **************** ApplicationNode contract ******************************
	
	@Override
	public String helpTopicID() {
		return "mapping.xmlFragment"; 
	}
	
	
	// ********** MWApplicationNode overrides **********

	@Override
	protected Class propertiesPageClass() {
		return XmlFragmentMappingPropertiesPage.class;
	}	
}