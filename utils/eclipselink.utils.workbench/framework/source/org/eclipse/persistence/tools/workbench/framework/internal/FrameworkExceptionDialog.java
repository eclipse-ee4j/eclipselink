/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Dialog;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.ExceptionDialog;


/**
 * A simple dialog for displaying a message, an exception, and
 * the exception's stack trace.
 */
final class FrameworkExceptionDialog extends ExceptionDialog {

	private static String buildTitle(WorkbenchContext context) {
		return context.getApplicationContext().getResourceRepository().getString("UNEXPECTED_ERROR");
	}

	FrameworkExceptionDialog(String message, Throwable exception, WorkbenchContext context) {
		super(message, exception, context, buildTitle(context));
	}

	FrameworkExceptionDialog(String message, Throwable exception, WorkbenchContext context, Dialog owner) {
		super(message, exception, context, owner, buildTitle(context));
	}


	// ********** AbstractDialog implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#helpTopicId()
	 */
	protected String helpTopicId() {
		return "dialog.unexpectedError";
	}

	/**
	 * No need for a Cancel button.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#cancelButtonIsVisible()
	 */
	protected boolean cancelButtonIsVisible() {
		return false;
	}

}
