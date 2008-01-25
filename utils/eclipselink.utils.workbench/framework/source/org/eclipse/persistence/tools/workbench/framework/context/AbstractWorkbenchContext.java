/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.context;

import org.eclipse.persistence.tools.workbench.framework.resources.IconResourceFileNameMap;

/**
 * Provide an implementation of the #build__() methods
 * that can be used by concrete subclasses.
 */
public abstract class AbstractWorkbenchContext
	implements WorkbenchContext
{

	// ********** constructor **********

	protected AbstractWorkbenchContext() {
		super();
	}


	// ********** WorkbenchContext implementation **********
	
	/**
	 * @see WorkbenchContext#buildExpandedApplicationContextWorkbenchContext(ApplicationContext)
	 */
	public WorkbenchContext buildExpandedApplicationContextWorkbenchContext(ApplicationContext appContext) {
		return new ApplicationContextWorkbenchContext(this, appContext);
	}

	public WorkbenchContext buildExpandedResourceRepositoryContext(Class resourceBundleClass) {
		return buildExpandedResourceRepositoryContext(resourceBundleClass, null);
	}
	
	/**
	 * @see WorkbenchContext#buildExpandedResourceRepositoryContext(Class, resources.IconResourceFileNameMap)
	 */
	public WorkbenchContext buildExpandedResourceRepositoryContext(Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		return buildExpandedApplicationContextWorkbenchContext(getApplicationContext().buildExpandedResourceRepositoryContext(resourceBundleClass, iconResourceFileNameMap));
	}

	/**
	 * @see ApplicationContext#buildExpandedResourceRepositoryContext(resources.IconResourceFileNameMap)
	 */
	public WorkbenchContext buildExpandedResourceRepositoryContext(IconResourceFileNameMap iconResourceFileNameMap) {
		return this.buildExpandedResourceRepositoryContext(null, iconResourceFileNameMap);
	}
	
	
}
