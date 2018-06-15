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


final class AboutAction extends AbstractFrameworkAction
{
    AboutAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        setText(resourceRepository().getString("ABOUT"));
        initializeMnemonic("ABOUT");
//        initializeIcon("about");
    }

    protected void execute() {
        getAboutDialog().show();
    }

    private AboutDialog getAboutDialog() {
        //Do not cache the AboutDialog. If this is necessary,
        //a WorkbenchContextHolder must be given to the AboutDialog
        return new AboutDialog(getWorkbenchContext());
    }

}
