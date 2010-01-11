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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Frame;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;


/**
 * import tables from the database, adding or refreshing as appropriate
 */
final class AddOrRefreshTablesAction 
	extends AbstractEnablableFrameworkAction 
{

	AddOrRefreshTablesAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		// TODO this mnemonic should be set with an index ~kfm
		this.initializeIcon("table.addFromDatabase");
		this.initializeTextAndMnemonic("ADD_OR_REFRESH_TABLES_ACTION");
		this.initializeToolTipText("ADD_OR_REFRESH_TABLES_ACTION.toolTipText");
	}

	protected void execute() {
		ApplicationNode[] projectNodes = this.selectedProjectNodes();
		for (int i = 0; i < projectNodes.length; i++) {
			this.execute((ProjectNode) projectNodes[i]);
		}
	}

	protected void execute(ProjectNode projectNode) {
		MWDatabase database = projectNode.getProject().getDatabase();
		TableImporterDialog importDialog = new TableImporterDialog(this.getWorkbenchContext(), database);
		importDialog.show();
		if (importDialog.wasCanceled()) {
			return;
		}
		this.startTableImporter(database, importDialog.importsTablesFullyQualified(), importDialog.selectedTables());
	}

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return ((DatabaseNode) selectedNode).getDatabase().isConnected();
	}

	protected String[] enabledPropertyNames() {
		return new String[] {MWDatabase.CONNECTED_PROPERTY};
	}

	/**
	 * Start a thread that will import tables from the database
	 */
	private void startTableImporter(MWDatabase database, boolean importsTablesFullyQualified, Collection selectedTables) {
		Thread thread = new Thread(new TableImporter(database, importsTablesFullyQualified, selectedTables), "Database Table Importer");
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}


	// ********** inner class **********

	/**
	 * display a "wait" dialog while the tables are being imported
	 */
	private class TableImporter implements Runnable {
		private MWDatabase database;
		private boolean importsTablesFullyQualified;
		private Collection selectedTables;

		TableImporter(MWDatabase database, boolean importsTablesFullyQualified, Collection selectedTables) {
			super();
			this.database = database;
			this.importsTablesFullyQualified = importsTablesFullyQualified;
			this.selectedTables = selectedTables;
		}
		
		public void run() {
			WaitDialog waitDialog = this.buildWaitDialog();
			launchLater(waitDialog);

			this.database.getValidator().pause();
			try {
				if (this.importsTablesFullyQualified) {
					this.database.importQualifiedTablesFor(this.selectedTables);
				} else {
					this.database.importUnqualifiedTablesFor(this.selectedTables);
				}
			} finally {
				// if we don't resume the validator, things will be really whacked...
				this.database.getValidator().resume();
				waitDialog.dispose();
			}
		}

		private WaitDialog buildWaitDialog() {
			return new WaitDialog(
				(Frame) this.workbenchContext().getCurrentWindow(),
				this.resourceRepository().getIcon("database.large"),
				this.resourceRepository().getString("TABLE_IMPORTATION_DIALOG.TITLE"),
				this.resourceRepository().getString("TABLE_IMPORTATION_MESSAGE")
			);
		}

		private WorkbenchContext workbenchContext() {
			return AddOrRefreshTablesAction.this.getWorkbenchContext();
		}

		private ResourceRepository resourceRepository() {
			return AddOrRefreshTablesAction.this.resourceRepository();
		}

	}

}
