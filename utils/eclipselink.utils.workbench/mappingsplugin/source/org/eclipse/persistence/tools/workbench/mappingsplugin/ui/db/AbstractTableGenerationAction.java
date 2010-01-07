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

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



abstract class AbstractTableGenerationAction extends AbstractEnablableFrameworkAction {

	protected AbstractTableGenerationAction(WorkbenchContext context) {
		super(context);
	}

	protected DatabaseNode databaseNode() {
		return (DatabaseNode) this.selectedNodes()[0].getParent();
	}

	protected MWDatabase database() {
		return this.databaseNode().getDatabase();
	}

	protected Collection selectedMWTables() {
		ApplicationNode[] nodes = this.selectedNodes();
		int len = nodes.length;
		Collection selectedTables = new ArrayList(len);
		for (int i = 0; i < len; i++) {
			selectedTables.add(((TableNode) nodes[i]).getTable());
		}
		return selectedTables;
	}

	protected Collection allMWTables() {
		return CollectionTools.collection(this.database().tables());
	}

	protected void checkDevelopmentLoginSpec(String dialogTitleKey) {
		if (this.database().getDevelopmentLoginSpec() == null) {
			JOptionPane.showMessageDialog(
				this.getWorkbenchContext().getCurrentWindow(),
				this.resourceRepository().getString("NO_LOGIN_DEFINED_DIALOG.message"),
				this.resourceRepository().getString(dialogTitleKey),
				JOptionPane.WARNING_MESSAGE
			);
			throw new IllegalStateException();
		}
	}

}
