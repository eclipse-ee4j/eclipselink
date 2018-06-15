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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

// JDK
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;

// Mapping Workbench

/**
 * This action checks to see if the {@link SessionBrokerAdapter} is empty, ie
 * with no managed sessions prior to delete it.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class DeleteBrokerAction extends DeleteSessionAction
{
    /**
     * Creates a new <code>DeleteBrokerAction</code>.
     *
     * @param context
     */
    public DeleteBrokerAction(WorkbenchContext context)
    {
        super(context);
    }

    protected void execute(ApplicationNode selectedNode)
    {
        SessionBrokerAdapter broker = (SessionBrokerAdapter) selectedNode.getValue();

        if (broker.sessionsSize() > 0)
            showWarning(broker);
        else
            super.execute(selectedNode);
    }

    private void showWarning(SessionBrokerAdapter broker)
    {
        JOptionPane.showMessageDialog
        (
            getWorkbenchContext().getCurrentWindow(),
            resourceRepository().getString("DELETE_BROKER_MESSAGE", broker.displayString()),
            resourceRepository().getString("DELETE_BROKER_TITLE"),
            JOptionPane.WARNING_MESSAGE
        );
    }
}
