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

/**
 * show Help for the Problems View
 */
final class ProblemsViewHelpAction extends AbstractFrameworkAction {

    public ProblemsViewHelpAction(WorkbenchContext workbenchContext) {
        super(workbenchContext);
    }

    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("CSH_HELP");
    }

    protected void execute() {
        this.helpManager().showTopic("problemsPane");
    }

}
