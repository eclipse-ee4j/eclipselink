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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.util.Iterator;

import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;


public class RenamePackageAction extends AbstractFrameworkAction {

	public RenamePackageAction(WorkbenchContext workbenchContext) {
		super(workbenchContext);
	}

	protected void initialize() {
		super.initialize();
		this.setIcon(EMPTY_ICON);
		this.initializeTextAndMnemonic("RENAME_PACKAGE_ACTION");
		this.initializeToolTipText("RENAME_PACKAGE_ACTION.tooltip");
	}
	
	protected void execute(ApplicationNode selectedNode) {
		String packageName = this.promptForPackageName(selectedNode);
		if (packageName == null) {
			return;
		}
		this.navigatorSelectionModel().pushExpansionState();
		for (Iterator stream = CollectionTools.collection(((DescriptorPackageNode) selectedNode).descriptorNodes()).iterator(); stream.hasNext(); ) {
			this.navigatorSelectionModel().pushExpansionState();
			DescriptorNode descriptorNode = (DescriptorNode) stream.next();
			MWDescriptor descriptor = descriptorNode.getDescriptor();
			MWClass mwClass = descriptor.getMWClass();				
			mwClass.setName(packageName + "." + mwClass.shortName());
			descriptor.setName(packageName + "." + mwClass.shortName());
			this.navigatorSelectionModel().popAndRestoreExpansionState(descriptorNode, ((ProjectNode) selectedNode.getProjectRoot()).descriptorPackageNodeNamed(packageName).descendantNodeForValue(descriptor));
		}
		this.navigatorSelectionModel().popAndRestoreExpansionState(selectedNode, ((ProjectNode) selectedNode.getProjectRoot()).descriptorPackageNodeNamed(packageName));
		this.navigatorSelectionModel().setSelectedNode(((ProjectNode) selectedNode.getProjectRoot()).descriptorPackageNodeNamed(packageName));
	}
	
	private String promptForPackageName(ApplicationNode node) {
		LocalNewNameDialog.LocalBuilder packageNameDialogBuilder = new LocalNewNameDialog.LocalBuilder();
		packageNameDialogBuilder.addExistingNames(this.packageNames(node));
		packageNameDialogBuilder.setTitle(this.resourceRepository().getString("RENAME_PACKAGE_ACTION_DIALOG.title"));
		packageNameDialogBuilder.setTextFieldDescription(this.resourceRepository().getString("RENAME_PACKAGE_ACTION_DIALOG.textField"));
		packageNameDialogBuilder.setOriginalName(node.displayString());
		LocalNewNameDialog dialog = (LocalNewNameDialog) packageNameDialogBuilder.buildDialog(this.getWorkbenchContext());

		dialog.show();
		
		return dialog.wasConfirmed() ? dialog.getNewName() : null;
	}

	private Iterator packageNames(ApplicationNode selectedNode) {
		return ((MWProject) selectedNode.getProjectRoot().getValue()).packageNames();
	}


	// ********** member class **********

	private static class LocalNewNameDialog extends NewNameDialog {

		LocalNewNameDialog(WorkbenchContext context, Builder builder) {
			super(context, builder);
		}

		// check each package name component for a reserved word
		protected boolean nameIsIllegal(String name) {
			String[] segments = name.split("\\.");
			for (int i = 0; i < segments.length; i++) {
				if (NameTools.javaReservedWordsContains(segments[i])) {
					return true;
				}
			}
			return false;
		}

		private static class LocalBuilder extends Builder {
			LocalBuilder() {
				super();
			}
			protected DocumentFactory buildDefaultDocumentFactory() {
				return new NewNameDialog.DocumentFactory() {
					public Document buildDocument() {
						return RegexpDocument.buildDocument(RegexpDocument.RE_PACKAGE);
					}
				};
			}
			protected NewNameDialog buildDialog(WorkbenchContext context, Builder clone) {
				return new LocalNewNameDialog(context, clone);
			}
		}

	}

}
