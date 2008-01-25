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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class RelationalTransformationMappingNode
	extends MappingNode
{
	
	public RelationalTransformationMappingNode(MWRelationalTransformationMapping value, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
		super(value, mappingNodeTypePolicy, parent);
	}
	
	
	// ************** AbstractApplicationNode overrides *************
	
	protected String accessibleNameKey() {
		return "ACCESSIBLE_TRANSFORMATION_MAPPING_NODE";
	}

	
	// ************** ApplicationNode implementation *************
	
	public String helpTopicID() {
//		return this.getDescriptorNode().mappingHelpTopicPrefix() + ".transformation";
		return this.getDescriptorNode().mappingHelpTopicPrefix() + ".transformation.relational"; // For 10.1.3
	}

	protected String buildIconKey() {
		return getMapping().iconKey();	
	}


	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return RelationalTransformationMappingPropertiesPage.class;
	}

}
