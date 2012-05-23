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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningUpdateFieldsPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



final class RelationalReturningUpdateFieldsPanel extends ReturningUpdateFieldsPanel {

	/**
	 * Creates a new <code>ReturningUpdateFieldsPanel</code>.
	 *
	 */
	RelationalReturningUpdateFieldsPanel( PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super( subjectHolder, contextHolder);
	}	
	
	protected String addButtonKey() {
		return "UPDATE_ADD_BUTTON";
	}

	protected String removeButtonKey() {
		return "UPDATE_REMOVE_BUTTON";
	}

	protected void addField() {
		Iterator selectedFields = this.returningPolicy().updateFields();
		
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
			((MWRelationalReturningPolicy) this.returningPolicy()).addUpdateField(column);
		}
	}

}
