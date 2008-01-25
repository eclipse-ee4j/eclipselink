/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class RelationalDirectMapMappingNode extends MappingNode {

	
	public RelationalDirectMapMappingNode(MWRelationalDirectMapMapping value, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
		super(value, mappingNodeTypePolicy, parent);
	}


	// ************** AbstractApplicationNode overrides *************

	protected String accessibleNameKey() {
		return "ACCESSIBLE_DIRECT_MAP_MAPPING_NODE";
	}


	// ************** ApplicationNode implementation *************
	
	public String helpTopicID() {
		return this.getDescriptorNode().mappingHelpTopicPrefix() + ".directMap";
	}

	protected String buildIconKey() {
		return ((MWRelationalDirectMapMapping) getMapping()).iconKey();		
	}


	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return RelationalDirectMapMappingTabbedPropertiesPage.class;
	}

}
