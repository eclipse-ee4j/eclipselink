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
