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
package org.eclipse.persistence.tools.workbench.framework.internal;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.ThreadBrowser;


/**
 * development-only action that displays the current Java threads
 */
final class ThreadBrowserAction
    extends AbstractFrameworkAction
{

    ThreadBrowserAction(WorkbenchContext context) {
        super(context);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction#initialize()
     */
    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("THREAD_BROWSER");
    }

    /**
     * ignore the selected nodes
     */
    protected void execute() {
        new ThreadBrowser().open();
    }

}
