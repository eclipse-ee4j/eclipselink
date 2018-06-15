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
package org.eclipse.persistence.tools.workbench.framework.help;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * There should be one HelpTopicIDWindowAction per WorkbenchWindow.
 * This command should ONLY be used in "development" mode.
 */
final class HelpTopicIDWindowAction extends AbstractFrameworkAction {

    /**
     * Construct an action that will open the Help Topic ID window.
     * There is only one window per application.
     */
    HelpTopicIDWindowAction(WorkbenchContext context) {
        super(context);
    }

    /**
     * initialize stuff
     */
    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("HELP_TOPIC_ID_WINDOW");
        this.initializeToolTipText("HELP_TOPIC_ID_WINDOW.TOOL_TIP");
    }

    /**
     * ignore the selected nodes
     */
    protected void execute() {
        // no need for localization - this should only occur in development
        JOptionPane.showMessageDialog(this.currentWindow(), "Invalid Help Manager: ");
    }

}
