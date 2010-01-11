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
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepositoryWrapper;

/**
 * Wrap another context and expand its resource
 * repository with a resource repository wrapper.
 */
public class ExpandedResourceRepositoryApplicationContext extends ApplicationContextWrapper {

	private ResourceRepository expandedResourceRepository;


	// ********** constructor/initialization **********

	/**
	 * Construct a context with an expanded resource repository
	 * that adds the resources in the specified resource bundle and icon map
	 * to the original resource repository.
	 */
	public ExpandedResourceRepositoryApplicationContext(ApplicationContext delegate, Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		super(delegate);
		this.expandedResourceRepository = new ResourceRepositoryWrapper(this.delegateResourceRepository(), resourceBundleClass, iconResourceFileNameMap);
	}


	// ********** non-delegated behavior **********

	/**
	 * @see ApplicationContextWrapper#getResourceRepository()
	 */
	public ResourceRepository getResourceRepository() {
		return this.expandedResourceRepository;
	}


	// ********** additional behavior **********

	/**
	 * Return the original, unwrapped resource repository.
	 */
	public ResourceRepository delegateResourceRepository() {
		return this.getDelegate().getResourceRepository();
	}

}
