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

/**
 * Wrap another context and redirect its application context
 * to another application context. Useful for nodes.
 */
public class ApplicationContextWorkbenchContext
    extends WorkbenchContextWrapper
{
    private ApplicationContext applicationContext;


    // ********** constructor/initialization **********

    /**
     * Construct a context with an expanded resource repository
     * that adds the resources in the specified resource bundle and icon map
     * to the original resource repository.
     */
    public ApplicationContextWorkbenchContext(WorkbenchContext delegate, ApplicationContext applicationContext) {
        super(delegate);
        this.applicationContext = applicationContext;
    }


    // ********** non-delegated behavior **********

    /**
     * @see WorkbenchContext#getApplicationContext()
     */
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }


    // ********** additional behavior **********

    /**
     * Return the original application context.
     */
    public ApplicationContext delegateApplicationContext() {
        return this.getDelegate().getApplicationContext();
    }

}
