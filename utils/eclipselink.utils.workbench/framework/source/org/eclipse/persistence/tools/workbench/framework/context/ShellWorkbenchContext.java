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
package org.eclipse.persistence.tools.workbench.framework.context;

import java.awt.Component;
import java.awt.Window;

import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class is used when we only have an application repository.
 * Any call that needs access to "dynamic" state (e.g. the current
 * window) will trigger a runtime exception.
 */
public class ShellWorkbenchContext extends AbstractWorkbenchContext {
	private ApplicationContext applicationContext;
	private static final String CR = StringTools.CR;
	
	public ShellWorkbenchContext(ApplicationContext applicationContext) {
		super();
		this.applicationContext = applicationContext;
	}
	
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	public ActionRepository getActionRepository() {
		throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
				"It does not have access to an action repository.");
	}

	public Window getCurrentWindow() {
		throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
				"It does not have access to a current window.");
	}

	public NavigatorSelectionModel getNavigatorSelectionModel() {
		throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
				"It does not have access to a navigator.");
	}
    
    public Component getPropertiesPage() {
        throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
        "It does not have access to a properties page.");
    }

}
