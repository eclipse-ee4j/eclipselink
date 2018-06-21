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
package org.eclipse.persistence.tools.workbench.framework.internal;

import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * Straightforward implementation of an action repository.
 */
final class FrameworkActionRepository implements ActionRepository {

    private FrameworkNodeManager frameworkNodeManager;
    private WorkbenchContext workbenchContext;

    // ********** constructor/initialization **********

    FrameworkActionRepository(WorkbenchWindow workbenchWindow) {
        super();
        this.initialize(workbenchWindow);
    }

    private void initialize(WorkbenchWindow workbenchWindow) {
        this.workbenchContext = workbenchWindow.getContext();
        this.frameworkNodeManager = workbenchWindow.nodeManager();
    }


    // ********** ActionRepository implementation **********

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.action.ActionRepository#getSaveAction()
     */
    public FrameworkAction getSaveAction() {
        return new SaveAction(this.workbenchContext, this.frameworkNodeManager);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.action.ActionRepository#getSaveAsAction()
     */
    public FrameworkAction getSaveAsAction() {
        return new SaveAsAction(this.workbenchContext, this.frameworkNodeManager);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.action.ActionRepository#getCloseAction()
     */
    public FrameworkAction getCloseAction() {
        return new CloseAction(this.workbenchContext, this.frameworkNodeManager);
    }


    // ********** internal methods **********

    FrameworkAction getOpenAction() {
        return new OpenAction(this.workbenchContext, this.frameworkNodeManager);
    }

    FrameworkAction getCloseAllAction() {
        return new CloseAllAction(this.workbenchContext, this.frameworkNodeManager);
    }

    FrameworkAction getSaveAllAction() {
        return new SaveAllAction(this.workbenchContext, this.frameworkNodeManager);
    }

    FrameworkAction getExitAction() {
        return new ExitAction(this.workbenchContext, this.frameworkNodeManager);
    }

}
