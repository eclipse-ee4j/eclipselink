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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;


public final class CreateNewClassAction extends AbstractEnablableFrameworkAction {

	CreateNewClassAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("CREATE_NEW_CLASS_ACTION");
		this.initializeAccelerator("CREATE_NEW_CLASS_ACTION.accelerator");
		this.initializeToolTipText("CREATE_NEW_CLASS_ACTION.toolTipText");
		this.initializeIcon("descriptor.new");
	}
	
	protected void execute() {
		this.promptToCreateNewClass(this.selectedNodes()[0]);
	}
	
	private void promptToCreateNewClass(ApplicationNode selectedNode) {
		MWProject project = (MWProject) selectedNode.getProjectRoot().getValue();

		NewClassNameDialog dialog = this.buildNewClassNameDialog(project, selectedNode);
		dialog.setTitle(this.resourceRepository().getString("ADD_CLASS_DIALOG_TITLE"));
		dialog.setVisible(true);
		
		if (dialog.wasCanceled()) {
			return;
		}

		String typeName = dialog.className();
		this.navigatorSelectionModel().pushExpansionState();
		MWClass type = project.getClassRepository().typeNamed(typeName);
		// this forces the class to not be a "stub" and makes things a bit more usable
		type.addZeroArgumentConstructor();
		MWDescriptor descriptor;

		try {
			descriptor = project.addDescriptorForType(type);
		} catch (InterfaceDescriptorCreationException ex) {
			throw new RuntimeException(ex);
		}

		((AbstractApplicationNode) selectedNode.getProjectRoot()).selectDescendantNodeForValue(descriptor, this.navigatorSelectionModel());
		this.navigatorSelectionModel().popAndRestoreExpansionState();
	}

	private NewClassNameDialog buildNewClassNameDialog(MWProject project, ApplicationNode selectedNode) {
		return new NewClassNameDialog(
			this.buildPackageNames(project),
			this.buildPackageName(selectedNode),
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

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return false;
	}

	protected void updateEnabledState() {
		this.setEnabled(this.selectedNodes().length == 1);
	}

}
