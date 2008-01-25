/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningInsertFieldsPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class RelationalReturningInsertFieldsPanel extends ReturningInsertFieldsPanel {

	RelationalReturningInsertFieldsPanel( PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super( subjectHolder, contextHolder);
	}

	protected void addField() {
		Iterator selectedFields = this.returningPolicy().insertFields();

		DefaultListChooserDialog dialog =
			RelationalDescriptorComponentFactory.
				buildReturningPolicyFieldDialog(
					(MWRelationalClassDescriptor) returningPolicy().getParent(), 
					CollectionTools.collection(selectedFields), 
					getWorkbenchContext(),
					"descriptor.relational.returningPolicy"
				);

		dialog.show();
		
		if( dialog.wasConfirmed()) {
			MWColumn column = (MWColumn) dialog.selection();
			((MWRelationalReturningPolicy) this.returningPolicy()).addInsertFieldReadOnlyFlag(column);
		}
	}

}

