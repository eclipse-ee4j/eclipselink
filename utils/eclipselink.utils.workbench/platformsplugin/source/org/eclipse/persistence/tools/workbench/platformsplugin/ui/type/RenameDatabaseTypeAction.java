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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.type;

import java.util.Iterator;

import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * prompt the user for a new name for each of the selected nodes
 */
final class RenameDatabaseTypeAction extends AbstractFrameworkAction {

	public RenameDatabaseTypeAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("RENAME_DATABASE_TYPE");
		// no accelerator
		this.initializeIcon("RENAME_DATABASE_TYPE");
		this.initializeToolTipText("RENAME_DATABASE_TYPE.TOOL_TIP");
	}

	protected void execute() {
		// save the selection state, so we can restore it when we are done
		TreePath[] paths = this.navigatorSelectionModel().getSelectionPaths();

		NewNameDialog.Builder builder = this.buildNewNameDialogBuilder();
		ApplicationNode[] nodes = this.selectedNodes();
		for (int i = nodes.length; i-- > 0; ) {
			this.execute(((DatabaseTypeNode) nodes[i]).getDatabaseType(), builder);
		}

		// restore the selection state
		this.navigatorSelectionModel().setSelectionPaths(paths);
	}

	private NewNameDialog.Builder buildNewNameDialogBuilder() {
		// set the properties that stay the same for every node
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setTitle(this.resourceRepository().getString("RENAME_DATABASE_TYPE_DIALOG_TITLE"));
		builder.setTextFieldDescription(this.resourceRepository().getString("RENAME_DATABASE_TYPE_DIALOG_DESCRIPTION"));
		builder.setHelpTopicId("dialog.dbTypeRename");
		return builder;
	}

	protected void execute(DatabaseType dbType, NewNameDialog.Builder builder) {
		// the list of existing names can change with each type
		builder.setExistingNames(this.existingTypeNames(dbType.getPlatform()));
		builder.setOriginalName(dbType.getName());
		NewNameDialog dialog = builder.buildDialog(this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed()) {
			dbType.setName(dialog.getNewName());
		}
	}

	private Iterator existingTypeNames(DatabasePlatform platform) {
		return new TransformationIterator(platform.databaseTypes()) {
			protected Object transform(Object next) {
				return ((DatabaseType) next).getName();
			}
		};
	}

}
