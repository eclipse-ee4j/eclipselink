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
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * tell the application to open a new window;
 * always enabled
 */
final class NewWindowAction
    extends AbstractFrameworkAction
{
    /** we need access to the application's internal api */
    private FrameworkApplication application;


    public NewWindowAction(WorkbenchContext context, FrameworkApplication application) {
        super(context);
        this.application = application;
    }

    protected void initialize() {
        super.initialize();
        this.setIcon(EMPTY_ICON);
        this.initializeTextAndMnemonic("window.newWindow");
        this.initializeToolTipText("window.newWindow.toolTipText");
        this.setEnabled(true);
    }

    protected void execute() {
        this.application.openNewWindow((WorkbenchWindow) currentWindow());
    }

}
