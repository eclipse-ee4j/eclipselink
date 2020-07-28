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

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;

/**
 * toggle the creation of new projects with "synchronous" validators;
 * always enabled
 */
final class SynchronousProblemsAction
    extends AbstractFrameworkAction
{
    /** we need access to the workbench window's internal api */
    private WorkbenchWindow workbenchWindow;


    public SynchronousProblemsAction(WorkbenchWindow workbenchWindow) {
        super(workbenchWindow.getContext());
        this.workbenchWindow = workbenchWindow;
    }

    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("tools.synchronousProblems");
        this.initializeToolTipText("tools.synchronousProblems.toolTipText");
    }

    protected void execute() {
        this.workbenchWindow.toggleSynchronousProblems();
    }

}
