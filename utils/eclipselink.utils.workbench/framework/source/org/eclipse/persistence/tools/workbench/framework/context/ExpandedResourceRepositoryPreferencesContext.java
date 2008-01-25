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
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepositoryWrapper;

/**
 * Wrap another context and expand its resource
 * repository with a resource repository wrapper.
 */
public class ExpandedResourceRepositoryPreferencesContext
	extends PreferencesContextWrapper
{
	private ResourceRepository expandedResourceRepository;


	// ********** constructor/initialization **********

	/**
	 * Construct a context with an expanded resource repository
	 * that adds the resources in the specified resource bundle and icon map
	 * to the original resource repository.
	 */
	public ExpandedResourceRepositoryPreferencesContext(PreferencesContext delegate, Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		super(delegate);
		this.initialize(resourceBundleClass, iconResourceFileNameMap);
	}

	protected void initialize(Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		this.expandedResourceRepository = this.buildExpandedResourceRepository(resourceBundleClass, iconResourceFileNameMap);
	}

	protected ResourceRepository buildExpandedResourceRepository(Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		return new ResourceRepositoryWrapper(this.delegateResourceRepository(), resourceBundleClass, iconResourceFileNameMap);
	}


	// ********** non-delegated behavior **********

	/**
	 * @see PreferencesContextWrapper#getResourceRepository()
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
