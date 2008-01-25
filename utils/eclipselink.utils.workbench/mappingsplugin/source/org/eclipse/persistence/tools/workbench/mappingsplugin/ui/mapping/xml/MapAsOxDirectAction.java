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
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MapAsDirectAction;


final class MapAsOxDirectAction extends MapAsDirectAction
{
	MapAsOxDirectAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		this.initializeIcon("mapping.xmlDirect");
		this.initializeText("MAP_AS_XML_DIRECT_ACTION");
		this.initializeMnemonic("MAP_AS_XML_DIRECT_ACTION");
		this.initializeToolTipText("MAP_AS_XML_DIRECT_ACTION.toolTipText");
	}

}
