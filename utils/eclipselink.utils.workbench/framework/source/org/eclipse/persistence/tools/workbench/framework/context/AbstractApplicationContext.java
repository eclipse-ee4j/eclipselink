/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
