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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlCollectionReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.OXDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class XmlCollectionReferenceMappingNode extends MappingNode {

	public XmlCollectionReferenceMappingNode(MWXmlCollectionReferenceMapping value, SelectionActionsPolicy mappingNodeTypePolicy, OXDescriptorNode parent) {
		super(value, mappingNodeTypePolicy, parent);
	}

	// **************** MappingNode contract **********************************
	
	@Override
	protected String buildIconKey() {
		return "mapping.collectionReference";
	}

	
	// ************** AbstractApplicationNode overrides *************

	@Override
	protected String accessibleNameKey() {
		return "ACCESSIBLE_XML_COLLECTION_REFERENCE_MAPPING_NODE";
	}

	
	// **************** ApplicationNode contract ******************************
	
	@Override
	public String helpTopicID() {
		return "mapping.xmlCollectionReference"; 
	}
	
	
	// ********** MWApplicationNode overrides **********

	@Override
	protected Class propertiesPageClass() {
		return XmlCollectionReferenceMappingPropertiesPage.class;
	}

}
