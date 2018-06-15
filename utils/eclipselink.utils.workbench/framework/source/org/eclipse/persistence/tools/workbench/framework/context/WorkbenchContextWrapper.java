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

import java.awt.Component;
import java.awt.Window;

import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;


/**
 * This class wraps another context and delegates most of
 * the behavior to it. (The #build__() methods are NOT delegated -
 * they must return a wrapped version of THIS object, NOT the delegate.)
 * Subclasses can simply override the method(s) that should not be delegated.
 */
public abstract class WorkbenchContextWrapper extends AbstractWorkbenchContext {
    private WorkbenchContext delegate;


    // ********** constructor/initialization **********

    /**
     * Construct an wrapper for the specified context.
     */
    public WorkbenchContextWrapper(WorkbenchContext delegate) {
        super();
        if (delegate == null) {
            throw new NullPointerException();
        }
        this.delegate = delegate;
    }


    // ********** WorkbenchContext implementation **********


    /**
     * @see WorkbenchContext#getApplicationContext()
     */
    public ApplicationContext getApplicationContext() {
        return this.delegate.getApplicationContext();
    }

    /**
     * @see WorkbenchContext#getCurrentWindow()
     */
    public Window getCurrentWindow() {
        return this.delegate.getCurrentWindow();
    }

    /**
     * @see WorkbenchContext#getNavigatorSelectionModel()
     */
    public NavigatorSelectionModel getNavigatorSelectionModel() {
        return this.delegate.getNavigatorSelectionModel();
    }

    /**
     * @see WorkbenchContext#getActionRepository()
     */
    public ActionRepository getActionRepository() {
        return this.delegate.getActionRepository();
    }

    /**
     * @see WorkbenchContext#getPropertiesPage()
     */
    public Component getPropertiesPage() {
        return this.delegate.getPropertiesPage();
    }

    // ********** additional behavior **********

    /**
     * Allow access to the delegate.
     */
    public WorkbenchContext getDelegate() {
        return this.delegate;
    }

}
