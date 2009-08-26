/*
 * Copyright (c) 2006, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.Dialog;

/**
 * This <code>PostExecution</code> is used to post execute a portion of code
 * once a dialog, that was launched into a different UI thread, has been
 * disposed.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public interface PostExecution<T extends Dialog>
{
	/**
	 * Notifies this post exection the dialog that was launched into a different
	 * UI thread has been disposed.
	 *
	 * @param dialog The dialog that was launched into a different thread
	 */
	public void execute(T dialog);
}