/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

public class WritePoolTabbedPropertiesPage extends PoolTabbedPropertiesPage {

	public WritePoolTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		this.addTab( this.buildGeneralPropertiesPage(), this.buildGeneralPropertiesPageTitle());
	}
}