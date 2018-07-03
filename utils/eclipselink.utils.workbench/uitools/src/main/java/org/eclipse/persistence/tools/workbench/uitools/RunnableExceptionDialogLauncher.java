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
package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JOptionPane;


/**
 * a Runnable that can be placed on the AWT Event Queue
 * and will open the specified exception dialog when it gets "dispatched"
 */
public final class RunnableExceptionDialogLauncher implements Runnable {
    private String message;
    private String title;
    private Component window;

    public RunnableExceptionDialogLauncher(Component window, String message, String title) {
        super();
        this.window = window;
        this.message = message;
        this.title = title;
    }

    public void run() {
        if ( ! EventQueue.isDispatchThread()) {
            throw new IllegalStateException("this method must be executed in the AWT event dispatcher thread");
        }
        LabelArea label = new LabelArea(this.message);
        JOptionPane.showMessageDialog(this.window, label, this.title, JOptionPane.ERROR_MESSAGE);
    }

}
