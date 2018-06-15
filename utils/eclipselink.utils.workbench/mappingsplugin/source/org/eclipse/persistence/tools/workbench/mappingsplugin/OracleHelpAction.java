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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * Describes a common help action that can be invoked across all MW
 * node types.
 */
public final class OracleHelpAction extends AbstractFrameworkAction {

    public OracleHelpAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        this.setIcon(EMPTY_ICON);
        this.initializeTextAndMnemonic("HELP");
        this.initializeToolTipText("HELP.tooltip");
    }

    protected void execute(ApplicationNode selectedNode) {
        this.helpManager().showTopic(selectedNode.helpTopicID());
    }
}
