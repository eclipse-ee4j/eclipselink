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

import org.eclipse.persistence.tools.workbench.framework.resources.IconResourceFileNameMap;


public abstract class AbstractApplicationContext implements ApplicationContext {

	public AbstractApplicationContext() {
		super();
	}

	// ********** ApplicationContext implementation **********

	/**
	 * @see ApplicationContext#buildRedirectedPreferencesContext(String)
	 */
	public ApplicationContext buildRedirectedPreferencesContext(String path) {
		return new RedirectedPreferencesApplicationContext(this, path);
	}

	/**
	 * @see ApplicationContext#buildExpandedResourceRepositoryContext(Class, resources.IconResourceFileNameMap)
	 */
	public ApplicationContext buildExpandedResourceRepositoryContext(Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		return new ExpandedResourceRepositoryApplicationContext(this, resourceBundleClass, iconResourceFileNameMap);
	}

	/**
	 * @see ApplicationContext#buildExpandedResourceRepositoryContext(resources.IconResourceFileNameMap)
	 */
	public ApplicationContext buildExpandedResourceRepositoryContext(IconResourceFileNameMap iconResourceFileNameMap) {
		return this.buildExpandedResourceRepositoryContext(null, iconResourceFileNameMap);
	}

	/**
	 * @see ApplicationContext#buildExpandedResourceRepositoryContext(Class)
	 */
	public ApplicationContext buildExpandedResourceRepositoryContext(Class resourceBundleClass) {
		return this.buildExpandedResourceRepositoryContext(resourceBundleClass, null);
	}


}
