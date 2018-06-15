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

import java.awt.Cursor;
import java.awt.EventQueue;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * There should be one PreferencesAction per WorkbenchWindow; and
 * every instance share the same PreferencesView (built and held by the
 * application).
 */
final class PreferencesAction extends AbstractFrameworkAction {

    /** the application will give us the view when we actually need it */
    private FrameworkApplication application;


    /**
     * Construct an action that will open a PreferencesDialog displaying
     * the specified view. The supplied view is shared among all the
     * actions in the application.
     */
    PreferencesAction(WorkbenchContext context, FrameworkApplication application) {
        super(context);
        this.application = application;
    }

    /**
     * initialize stuff
     */
    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("PREFERENCES");
        this.initializeToolTipText("PREFERENCES.TOOL_TIP");
        this.initializeIcon("PREFERENCES");
    }

    /**
     * ignore the selected nodes
     */
    protected void execute() {
        EventQueue.invokeLater(new LaunchPreferencesDialog(getWorkbenchContext(), application));
    }

    private final class LaunchPreferencesDialog implements Runnable {
        private final WorkbenchContext context;
        private final FrameworkApplication application;

        LaunchPreferencesDialog(WorkbenchContext context, FrameworkApplication application) {
            super();
            this.context = context;
            this.application = application;
        }

        public void run() {
            getWorkbenchContext().getCurrentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            PreferencesDialog dialog = new PreferencesDialog(context, application.getPreferencesView());
            dialog.show();

            getWorkbenchContext().getCurrentWindow().setCursor(Cursor.getDefaultCursor());
        }
    }
}
