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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * prompt the user to open a file;
 * always enabled
 */
final class OpenAction
    extends AbstractFrameworkAction
{
    private static final String MOST_RECENT_OPEN_LOCATION_PREFERENCE = "recent open location";
        private static final String MOST_RECENT_OPEN_LOCATION_PREFERENCE_DEFAULT = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();


    /** we need access to the node manager's internal api */
    private FrameworkNodeManager nodeManager;


    OpenAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
        super(context);
        this.nodeManager = nodeManager;
    }

    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("file.open");
        this.initializeIcon("file.open");
        this.initializeToolTipText("file.open.toolTipText");
        this.initializeAccelerator("file.open.ACCELERATOR");
    }

    /**
     * prompt the user for a file, then ask the node manager to open it;
     * the node manager will figure out whether the file is already open
     */
    protected void execute() {
        File file = this.promptUserForFile();
        if (file != null) {
            preferences().node(FrameworkApplication.GENERAL_PREFERENCES_NODE).put(MOST_RECENT_OPEN_LOCATION_PREFERENCE, file.getAbsolutePath());
            nodeManager.open(file, this.getWorkbenchContext());
        }
    }

    /**
     * Prompt the user for a file. Return the selected file.
     * Return null if the user presses Cancel or there are any problems.
     */
    private File promptUserForFile() {
        String mostRecentOpenFile = preferences().node(FrameworkApplication.GENERAL_PREFERENCES_NODE).get(MOST_RECENT_OPEN_LOCATION_PREFERENCE, MOST_RECENT_OPEN_LOCATION_PREFERENCE_DEFAULT);

        JFileChooser fileChooser = new JFileChooser(new File(mostRecentOpenFile));
        int status = fileChooser.showOpenDialog(this.currentWindow());
        if (status == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

}
