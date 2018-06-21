/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
