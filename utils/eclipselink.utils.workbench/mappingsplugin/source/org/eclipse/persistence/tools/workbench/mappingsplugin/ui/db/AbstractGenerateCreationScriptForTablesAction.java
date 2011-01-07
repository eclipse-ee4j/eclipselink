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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.util.Collection;

import javax.swing.JTextArea;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.TextAreaDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;



abstract class AbstractGenerateCreationScriptForTablesAction extends AbstractTableGenerationAction {

	AbstractGenerateCreationScriptForTablesAction(WorkbenchContext context) {
		super(context);
	}

	/**
	 * return the tables we need to work with
	 */
	protected abstract Collection buildTables();
		
	protected void execute() {
		try {
			this.checkDevelopmentLoginSpec("CREATE_ON_DATABASE_DIALOG.title");
		} catch (IllegalStateException ex) {
			return;
		}

		String ddl = this.database().ddlFor(this.buildTables());
		TextAreaDialog ddlDialog = 
			new TextAreaDialog(ddl, "dialog.sqlCreationScript", this.getWorkbenchContext()) {
				protected JTextArea buildTextArea() {
					JTextArea textArea = super.buildTextArea();
					textArea.setLineWrap(true);
					textArea.setWrapStyleWord(true);
					return textArea;
				}
			};
		ddlDialog.setTitle(this.resourceRepository().getString("SQL_CREATION_SCRIPT_DIALOG.title"));
		ddlDialog.show();
	}
	
	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return this.database().isConnected();
	}

	protected String[] enabledPropertyNames() {
		return new String[] {MWDatabase.CONNECTED_PROPERTY};
	}

}
