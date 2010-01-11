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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;


public final class MapAsUnmappedAction
	extends ChangeMappingTypeAction
{

	public MapAsUnmappedAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeIcon("mapping.unmapped");
		this.initializeText("MAP_AS_UNMAPPED_ACTION");
		this.initializeMnemonic("MAP_AS_UNMAPPED_ACTION");
		this.initializeToolTipText("MAP_AS_UNMAPPED_ACTION.toolTipText");
	}

	protected MWMapping morphMapping(MWMapping mapping) {
		throw new UnsupportedOperationException("use morphNode(MappingNode) directly");
	}
	
	protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
		throw new UnsupportedOperationException("use morphNode(MappingNode) directly");
	}
	
	protected MappingNode morphNode(MappingNode mappingNode) {
		if (this.nodeIsMorphed(mappingNode)) {
			return mappingNode;
		}
		MappingDescriptorNode descriptorNode = mappingNode.getDescriptorNode();
		MWClassAttribute attribute = mappingNode.instanceVariable();
		descriptorNode.getMappingDescriptor().removeMapping(mappingNode.getMapping());
		for (Iterator stream = descriptorNode.children(); stream.hasNext(); ) {
			MappingNode next = (MappingNode) stream.next();
			if (next.instanceVariable() == attribute) {
				return next;
			}
		}
		throw new IllegalStateException("mapping node not found: " + attribute);
	}

	protected boolean nodeIsMorphed(MappingNode mappingNode) {
		return ! mappingNode.isMapped();
	}

	protected Class mappingClass() {
		throw new UnsupportedOperationException();
	}

}
