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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui;

import java.awt.Cursor;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;


final class NewDatabasePlatformRepositoryAction extends AbstractFrameworkAction {

	private PlatformsPlugin plugin;

	private NewNameDialog.Builder dialogBuilder;


	NewDatabasePlatformRepositoryAction(PlatformsPlugin plugin, WorkbenchContext contextHolder) {
		super(contextHolder);
		this.plugin = plugin;
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("NEW_DATABASE_PLATFORM_REPOSITORY");
		// no accelerator
		this.initializeIcon("NEW_DATABASE_PLATFORM_REPOSITORY");
		this.initializeToolTipText("NEW_DATABASE_PLATFORM_REPOSITORY.TOOL_TIP");
	}

	private NewNameDialog.Builder getDialogBuilder() {
		if (this.dialogBuilder == null) {
			this.dialogBuilder = this.buildDialogBuilder();
		}
		return this.dialogBuilder;
	}

	private NewNameDialog.Builder buildDialogBuilder() {
		NewNameDialog.Builder result = new NewNameDialog.Builder();
		result.setTitle(this.resourceRepository().getString("NEW_DATABASE_PLATFORM_REPOSITORY.DIALOG.TITLE"));
		result.setTextFieldDescription(this.resourceRepository().getString("NEW_DATABASE_PLATFORM_REPOSITORY.DIALOG.TEXT_FIELD_DESCRIPTION"));
		result.setOriginalName(this.resourceRepository().getString("NEW_DATABASE_PLATFORM_REPOSITORY.DIALOG.ORIGINAL_NAME"));
		result.setHelpTopicId("platforms.new.name");
		return result;
	}

	protected void execute() {
		NewNameDialog dialog = this.getDialogBuilder().buildDialog(this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasCanceled()) {
			return;
		}
		this.currentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.navigatorSelectionModel().pushExpansionState();

		ApplicationNode node = this.plugin.buildRepositoryNode(new DatabasePlatformRepository(dialog.getNewName()), this.getWorkbenchContext());
		this.nodeManager().addProjectNode(node);
		this.navigatorSelectionModel().setSelectedNode(node);

		this.navigatorSelectionModel().popAndRestoreExpansionState();
		this.currentWindow().setCursor(Cursor.getDefaultCursor());
	}


}
