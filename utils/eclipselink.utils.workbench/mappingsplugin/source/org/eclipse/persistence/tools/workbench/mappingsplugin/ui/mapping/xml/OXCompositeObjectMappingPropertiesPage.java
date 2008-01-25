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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;


public final class OXCompositeObjectMappingPropertiesPage extends
		CompositeObjectMappingPropertiesPage {

	public OXCompositeObjectMappingPropertiesPage(WorkbenchContext context) {
		super(context);
	}
	
	protected AbstractXmlFieldPanel buildXmlFieldPanel() {
		return new ElementTypeableXmlFieldPanel(getSelectionHolder(), this.buildXmlFieldHolder(), this.getWorkbenchContextHolder(), MWCompositeObjectMapping.ELEMENT_TYPE_PROPERTY);
	}

}
