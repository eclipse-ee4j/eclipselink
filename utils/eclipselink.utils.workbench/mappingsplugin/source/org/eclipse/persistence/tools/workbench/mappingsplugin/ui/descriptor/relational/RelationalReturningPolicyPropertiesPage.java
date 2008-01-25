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


import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.RelationalReturningInsertFieldsPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class RelationalReturningPolicyPropertiesPage extends ReturningPolicyPropertiesPage
{
	public static final int EDITOR_WEIGHT = 12;
	
	/**
	 * Creates a new <code>ReturningPolicyPropertiesPage</code>.
	 */
	RelationalReturningPolicyPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super( nodeHolder, contextHolder);
		addHelpTopicId( this, "descriptor.relational.returningPolicy");
	}


	protected AbstractPanel insertFieldsPanel(PropertyValueModel returningPolicyHolder) {
		return new RelationalReturningInsertFieldsPanel(returningPolicyHolder, getWorkbenchContextHolder());
	}
	
	protected AbstractPanel updateFieldsPanel(PropertyValueModel returningPolicyHolder) {
		return new RelationalReturningUpdateFieldsPanel(returningPolicyHolder, getWorkbenchContextHolder());
	}
	
}