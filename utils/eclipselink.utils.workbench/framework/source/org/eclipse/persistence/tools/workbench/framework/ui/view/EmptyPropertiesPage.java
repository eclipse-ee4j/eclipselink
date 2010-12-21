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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * NB: Don't make this panel a Singleton or place it in a static field,
 * because you will probably need a separate instance for each open
 * window or view that needs an empty properties page.
 */
public class EmptyPropertiesPage extends AbstractPropertiesPage {

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[0];

	
	public EmptyPropertiesPage(WorkbenchContext context) {
		super(context);
	}
	
	protected void initializeLayout() {
		// do nothing
	}
	
}
