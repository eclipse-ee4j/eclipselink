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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.XmlDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class OxDirectMappingNode 
	extends MappingNode
{
	// **************** Constructors ******************************************
	
	public OxDirectMappingNode(MWXmlDirectMapping mapping, SelectionActionsPolicy mappingNodeTypePolicy, XmlDescriptorNode parent) {
		super(mapping, mappingNodeTypePolicy, parent);
	}
	
	
	// ************** AbstractApplicationNode overrides *************
	
	protected String accessibleNameKey() {
		return "ACCESSIBLE_XML_DIRECT_MAPPING_NODE";
	}
	
	
	// **************** MappingNode contract **********************************
	
	protected String buildIconKey() {
		return "mapping.xmlDirect";
	}

	
	// **************** ApplicationNode contract ******************************
	
	public String helpTopicID() {
//		return getDescriptorNode().mappingHelpTopicPrefix() + ".direct";
		return getDescriptorNode().mappingHelpTopicPrefix() + "Direct"; // For 10.1.3
	}
	
	
	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return OxDirectMappingPropertiesPage.class;
	}

}
