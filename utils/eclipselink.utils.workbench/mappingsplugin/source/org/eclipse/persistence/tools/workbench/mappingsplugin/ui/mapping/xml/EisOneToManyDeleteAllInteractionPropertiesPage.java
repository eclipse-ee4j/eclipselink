/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml.InteractionPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisOneToManyDeleteAllInteractionPropertiesPage extends ScrollablePropertiesPage
{
	EisOneToManyDeleteAllInteractionPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
		addHelpTopicId(this, "mappings.eis.deleteAllInteraction");
	}

	private PropertyValueModel buildInterationHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWEisOneToManyMapping) subject).getDeleteAllInteraction();
			}
		};
	}

	protected Component buildPage(){
		return new InteractionPanel(getApplicationContext(), buildInterationHolder(), "mappings.eis.deleteAllInteraction");
	}


}