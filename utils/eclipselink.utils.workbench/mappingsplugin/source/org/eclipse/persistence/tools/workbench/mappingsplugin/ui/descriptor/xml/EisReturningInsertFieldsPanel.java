/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ReturningInsertFieldsPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooserDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisReturningInsertFieldsPanel
	extends ReturningInsertFieldsPanel 
{
	EisReturningInsertFieldsPanel( PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super( subjectHolder, contextHolder);
	}
	
	protected void addField() {
		MWEisReturningPolicy returningPolicy = (MWEisReturningPolicy) this.returningPolicy();
		MWEisReturningPolicyInsertFieldReturnOnlyFlag insertField = 
			returningPolicy.buildEmptyInsertFieldReadOnlyFlag();
		
		XpathChooserDialog.promptToSelectXpath(
			insertField.getXmlField(),
			this.getWorkbenchContext()
		);
		
		if (insertField.getXmlField().isSpecified()) {
			returningPolicy.addInsertFieldReadOnlyFlag(insertField);
		}
	}

	private MWEisDescriptor eisDescriptor() {
		return (MWEisDescriptor) returningPolicy().getOwningDescriptor();
	}
}

