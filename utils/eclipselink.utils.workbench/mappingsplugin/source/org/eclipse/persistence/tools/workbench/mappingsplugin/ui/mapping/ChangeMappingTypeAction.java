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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;


public abstract class ChangeMappingTypeAction extends AbstractToggleFrameworkAction {

	protected ChangeMappingTypeAction(WorkbenchContext context) {
		super(context);
	}

	protected void engageValueEnabled(AbstractApplicationNode node) {
		super.engageValueEnabled(node);
		((MappingNode) node).descriptor().addPropertyChangeListener(MWDescriptor.ACTIVE_PROPERTY, getEnabledStateListener());
	}

	protected void disengageValueEnabled(AbstractApplicationNode node) {
		super.disengageValueEnabled(node);
		((MappingNode) node).descriptor().removePropertyChangeListener(MWDescriptor.ACTIVE_PROPERTY, getEnabledStateListener());
	}

	
	protected void execute() {
		ApplicationNode[] nodes = this.selectedNodes();
		TreePath[] selectionPaths = new TreePath[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			ApplicationNode node = nodes[i];
			node = this.morphNode((MappingNode) node);
			selectionPaths[i] = new TreePath(node.path());
		}
		this.navigatorSelectionModel().setSelectionPaths(selectionPaths);
	}

	protected MappingNode morphNode(MappingNode mappingNode) {
		MWMapping mapping;
		if (mappingNode.isMapped()) {
			mapping = morphMapping(mappingNode.getMapping());
		} else {
			mapping = addMapping(mappingNode.descriptor(), mappingNode.instanceVariable());
		}
		return (MappingNode) mappingNode.getDescriptorNode().descendantNodeForValue(mapping);
	}

	/**
	 * morph the specified mapping and return the new mapping
	 */
	protected abstract MWMapping morphMapping(MWMapping mapping);
	
	/**
	 * add a mapping for the specified attribute
	 */
	protected abstract MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute);

	protected boolean shouldBeSelected(ApplicationNode selectedNode) {
		return nodeIsMorphed((MappingNode) selectedNode);
	}

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return ((MappingNode) selectedNode).descriptor().isActive();
	}
	
	/**
	 * return whether the specified node is already morphed
	 */
	protected boolean nodeIsMorphed(MappingNode mappingNode) {
		return mappingNode.isMapped() && (this.mappingClass().isAssignableFrom(mappingNode.getMapping().getClass()));
	}

	protected abstract Class mappingClass();

}
