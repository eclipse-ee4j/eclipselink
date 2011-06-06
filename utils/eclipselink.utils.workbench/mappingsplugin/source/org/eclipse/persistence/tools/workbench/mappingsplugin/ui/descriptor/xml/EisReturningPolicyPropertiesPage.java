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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;


import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class EisReturningPolicyPropertiesPage extends ReturningPolicyPropertiesPage
{
	public static final int EDITOR_WEIGHT = 12;
	
	/**
	 * Creates a new <code>ReturningPolicyPropertiesPage</code>.
	 */
	EisReturningPolicyPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super( nodeHolder, contextHolder);
		addHelpTopicId( this, "descriptor.relational.returningPolicy");
	}


	protected AbstractPanel insertFieldsPanel(PropertyValueModel returningPolicyHolder) {
		return new EisReturningInsertFieldsPanel(returningPolicyHolder, getWorkbenchContextHolder());
	}
	
	protected AbstractPanel updateFieldsPanel(PropertyValueModel returningPolicyHolder) {
		return new EisReturningUpdateFieldsPanel(returningPolicyHolder, getWorkbenchContextHolder());
	}
	
}
