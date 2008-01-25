/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.context;

import java.awt.Window;

/**
 * Wrap another context and redirect its window
 * to another window. Useful for dialogs.
 */
public class WindowWorkbenchContext
	extends WorkbenchContextWrapper
{
	private Window window;


	// ********** constructor/initialization **********

	/**
	 * Construct a context for a new window or dialog.
	 */
	public WindowWorkbenchContext(WorkbenchContext delegate, Window window) {
		super(delegate);
		this.window = window;
	}


	// ********** non-delegated behavior **********

	/**
	 * @see WorkbenchContextWrapper#getCurrentWindow()
	 */
	public Window getCurrentWindow() {
		return this.window;
	}


	// ********** additional behavior **********

	/**
	 * Return the original window.
	 */
	public Window delegateCurrentWindow() {
		return this.getDelegate().getCurrentWindow();
	}

}
