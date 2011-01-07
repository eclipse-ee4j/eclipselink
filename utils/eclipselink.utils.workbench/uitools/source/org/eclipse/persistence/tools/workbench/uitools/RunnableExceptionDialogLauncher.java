/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
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
