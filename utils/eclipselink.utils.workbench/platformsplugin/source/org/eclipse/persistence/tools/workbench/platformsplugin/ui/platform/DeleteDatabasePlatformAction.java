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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;


/**
 * delete all the selected nodes, after user confirmation
 */
final class DeleteDatabasePlatformAction extends AbstractFrameworkAction {

	public DeleteDatabasePlatformAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("DELETE_DATABASE_PLATFORM");
		// no accelerator
		this.initializeIcon("DELETE_DATABASE_PLATFORM");
		this.initializeToolTipText("DELETE_DATABASE_PLATFORM.TOOL_TIP");
	}

	protected void execute() {
		int response = JOptionPane.showConfirmDialog(
						this.currentWindow(),
						this.confirmMessage(),
						this.confirmTitle(),
						JOptionPane.YES_NO_OPTION
		);
		if (response == JOptionPane.YES_OPTION) {
			super.execute();
		}
	}

	protected void execute(ApplicationNode selectedNode) {
		DatabasePlatform platform = ((DatabasePlatformNode) selectedNode).getDatabasePlatform();
		platform.getRepository().removePlatform(platform);
	}

	private String confirmMessage() {
		return this.resourceRepository().getString("DELETE_DATABASE_PLATFORM_DIALOG_MESSAGE");
	}

	private String confirmTitle() {
		return this.resourceRepository().getString("DELETE_DATABASE_PLATFORM_DIALOG_TITLE");
	}

}
