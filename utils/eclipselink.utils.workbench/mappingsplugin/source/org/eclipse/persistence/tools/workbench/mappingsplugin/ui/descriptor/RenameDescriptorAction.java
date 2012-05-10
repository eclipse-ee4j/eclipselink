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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.NewClassNameDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;


final class RenameDescriptorAction extends AbstractFrameworkAction {

	RenameDescriptorAction(WorkbenchContext context) {
		super(context.buildExpandedResourceRepositoryContext(UiDescriptorBundle.class));
	}
	
	protected void initialize() {
		super.initialize();
		this.setIcon(EMPTY_ICON);
		this.initializeTextAndMnemonic("RENAME_DESCRIPTOR_ACTION");
		this.initializeToolTipText("RENAME_DESCRIPTOR_ACTION.toolTipText");
	}

	protected void execute(final ApplicationNode selectedNode) {
		// if the descriptor's package changes, the node's path will change;
		// so hold the descriptor and search for its node after the rename is complete
		MWDescriptor descriptor = (MWDescriptor) selectedNode.getValue();
		ApplicationNode projectNode = selectedNode.getProjectRoot();
		this.navigatorSelectionModel().pushExpansionState();
		this.promptToRenameDescriptor(selectedNode);
		this.navigatorSelectionModel().popAndRestoreExpansionState();
		this.navigatorSelectionModel().setSelectedNode(projectNode.descendantNodeForValue(descriptor));
	}

	private void promptToRenameDescriptor(ApplicationNode selectedNode) {
		MWDescriptor descriptor = (MWDescriptor) selectedNode.getValue();
		MWProject project = descriptor.getProject();

		NewClassNameDialog dialog = this.buildNewClassNameDialog(project, selectedNode, descriptor.getName());
		dialog.setTitle(this.resourceRepository().getString("RENAME_CLASS_DIALOG_TITLE"));
		dialog.setAllowExistingType(false);
		dialog.setVisible(true);
		
		if (dialog.wasCanceled()) {
			return;
		}

		String typeName = dialog.className();
		descriptor.getMWClass().setName(typeName);
		descriptor.setName(typeName);
	}

	private NewClassNameDialog buildNewClassNameDialog(MWProject project, ApplicationNode selectedNode, String typeName) {
		return new NewClassNameDialog(
			this.buildPackageNames(project),
			this.buildPackageName(selectedNode),
			ClassTools.shortNameForClassNamed(typeName),
			project,
			this.getWorkbenchContext()
		);
	}

	private Collection buildPackageNames(MWProject project) {
		Iterator packageNames = new FilteringIterator(project.packageNames()) {
			protected boolean accept(Object packageName) {
				return ((String) packageName).length() > 0;
			}
		};
		return CollectionTools.collection(packageNames);
	}

	private String buildPackageName(ApplicationNode selectedNode) {
		return ((MappingsApplicationNode) selectedNode).candidatePackageName();
	}

}
