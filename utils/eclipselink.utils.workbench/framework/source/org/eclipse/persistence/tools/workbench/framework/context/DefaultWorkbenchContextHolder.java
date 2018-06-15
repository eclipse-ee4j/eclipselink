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
 * This workbench context holder can either hold a normal workbench context
 * or a "shell" workbench context that only has an application context.
 * See the constructor comments.
 */
public class DefaultWorkbenchContextHolder
    implements WorkbenchContextHolder
{
    private WorkbenchContext workbenchContext;


    /**
     * Construct a workbench context holder for the specified
     * workbench context. Nothing special here.
     */
    public DefaultWorkbenchContextHolder(WorkbenchContext workbenchContext) {
        super();
        this.workbenchContext = workbenchContext;
    }

    /**
     * Construct a workbench context holder that will *only* return the specified
     * application context. It will not return any window-specific state (e.g.
     * the current window, the action repository, the navigator selection model).
     * This holder can be passed to objects that need access to the relatively
     * "static" state contained by the application context during instantiation and
     * initialization but will later need access to the "dynamic" state of the workbench
     * window (e.g. properties pages, which are shared among workbench windows).
     * Typically, this workbench context will be replaced by a "legitimate" one once
     * we are associated with a workbench window.
     */
    public DefaultWorkbenchContextHolder(ApplicationContext applicationContext) {
        this(new ShellWorkbenchContext(applicationContext));
    }

    /**
     * @see WorkbenchContextHolder#getWorkbenchContext()
     */
    public WorkbenchContext getWorkbenchContext() {
        return this.workbenchContext;
    }

    /**
     * users of this implementation can replace the workbench context
     */
    public void setWorkbenchContext(WorkbenchContext workbenchContext) {
        this.workbenchContext = workbenchContext;
    }

}
