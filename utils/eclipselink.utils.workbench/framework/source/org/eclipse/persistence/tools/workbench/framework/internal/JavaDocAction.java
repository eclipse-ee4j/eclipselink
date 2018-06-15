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


final class JavaDocAction extends AbstractFrameworkAction
{

    JavaDocAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        this.initializeTextAndMnemonic("API");
    }

    protected void execute() {
        this.helpManager().showTopic("eclipselink_api");
    }

}
