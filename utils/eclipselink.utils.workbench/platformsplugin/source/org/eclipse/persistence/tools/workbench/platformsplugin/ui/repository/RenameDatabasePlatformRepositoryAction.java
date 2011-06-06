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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository;

import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;


/**
 * prompt the user for a new name for each of the selected nodes
 */
final class RenameDatabasePlatformRepositoryAction extends AbstractFrameworkAction {

	public RenameDatabasePlatformRepositoryAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("RENAME_DATABASE_PLATFORM_REPOSITORY");
		this.initializeIcon("RENAME_DATABASE_PLATFORM_REPOSITORY");
		// no accelerator
		this.initializeToolTipText("RENAME_DATABASE_PLATFORM_REPOSITORY.TOOL_TIP");
	}

	protected void execute() {
		// save the selection state, so we can restore it when we are done
		TreePath[] paths = this.navigatorSelectionModel().getSelectionPaths();

		NewNameDialog.Builder builder = this.buildNewNameDialogBuilder();
		ApplicationNode[] nodes = this.selectedNodes();
		for (int i = nodes.length; i-- > 0; ) {
			this.execute(((DatabasePlatformRepositoryNode) nodes[i]).getDatabasePlatformRepository(), builder);
		}

		// restore the selection state
		this.navigatorSelectionModel().setSelectionPaths(paths);
	}

	private NewNameDialog.Builder buildNewNameDialogBuilder() {
		// set the properties that stay the same for every node
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setTitle(this.resourceRepository().getString("RENAME_DATABASE_PLATFORM_REPOSITORY_DIALOG_TITLE"));
		builder.setTextFieldDescription(this.resourceRepository().getString("RENAME_DATABASE_PLATFORM_REPOSITORY_DIALOG_DESCRIPTION"));
		builder.setHelpTopicId("dialog.dbPlatformRepositoryRename");
		return builder;
	}

	protected void execute(DatabasePlatformRepository repository, NewNameDialog.Builder builder) {
//		builder.setExistingNames(this.existingPlatformNames(dbPlatform.getRepository()));
		builder.setOriginalName(repository.getName());
		NewNameDialog dialog = builder.buildDialog(this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed()) {
			repository.setName(dialog.getNewName());
		}
	}

}
