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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlObjectReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.OXDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class XmlObjectReferenceMappingNode extends MappingNode {

	public XmlObjectReferenceMappingNode(MWXmlObjectReferenceMapping value, SelectionActionsPolicy mappingNodeTypePolicy, OXDescriptorNode parent) {
		super(value, mappingNodeTypePolicy, parent);
	}

	// **************** MappingNode contract **********************************
	
	@Override
	protected String buildIconKey() {
		return "mapping.objectReference";
	}

	
	// ************** AbstractApplicationNode overrides *************

	@Override
	protected String accessibleNameKey() {
		return "ACCESSIBLE_XML_OBJECT_REFERENCE_MAPPING_NODE";
	}

	
	// **************** ApplicationNode contract ******************************
	
	@Override
	public String helpTopicID() {
		return "mapping.xmlObjectReference"; 
	}
	
	
	// ********** MWApplicationNode overrides **********

	@Override
	protected Class propertiesPageClass() {
		return XmlObjectReferenceMappingPropertiesPage.class;
	}

}
