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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.XmlDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class CompositeCollectionMappingNode
	extends MappingNode
{
	// **************** Constructors ******************************************
	
	public CompositeCollectionMappingNode(MWCompositeCollectionMapping mapping, SelectionActionsPolicy mappingNodeTypePolicy, XmlDescriptorNode parent) {
		super(mapping, mappingNodeTypePolicy, parent);
	}
	
	
	// **************** MappingNode contract **********************************
	
	protected String buildIconKey() {
		return "mapping.compositeCollection";
	}

	
	// ************** AbstractApplicationNode overrides *************

	protected String accessibleNameKey() {
		return "ACCESSIBLE_COMPOSITE_COLLECTION_MAPPING_NODE";
	}


	// **************** ApplicationNode contract ******************************
	
	public String helpTopicID() {
//		return this.getDescriptorNode().mappingHelpTopicPrefix() + ".compositeCollection";

		//	TODO: For now!!! It's dirty but we can't use mappingHelpTopicPrefix()
		// for both EIS and OX and we can't return "mapping" only, will break all
		// the other mappind nodes!!!
		if (this.getDescriptorNode().mappingHelpTopicPrefix().endsWith(".xml")) {
			return "mapping.compositeCollection";
		} else {
			return "mapping.eis.compositeCollection";
		}
	}
	
	
	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		XmlDescriptorNode parentNode = (XmlDescriptorNode) getDescriptorNode();
		return parentNode.propertiesPageClassForCompositeCollectionMapping();
	}
}
