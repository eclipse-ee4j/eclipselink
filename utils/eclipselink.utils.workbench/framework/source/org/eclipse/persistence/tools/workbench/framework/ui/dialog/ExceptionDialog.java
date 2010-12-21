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
package org.eclipse.persistence.tools.workbench.framework.ui.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


/**
 * A simple dialog for displaying a message, an exception, and
 * the exception's stack trace.
 */
public class ExceptionDialog extends AbstractDialog {
	private String message;
	private Throwable exception;

	
	public ExceptionDialog(String message, Throwable exception, WorkbenchContext context, String title) {
		super(context, title);
		this.message = message;
		this.exception = exception;
	}

	public ExceptionDialog(String message, Throwable exception, WorkbenchContext context, Dialog owner, String title) {
		super(context, title, owner);
		this.message = message;
		this.exception = exception;
	}


	// ********** AbstractDialog implementation **********

	/**
	 * The main panel merely consists of a label or two.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildMainPanel()
	 */
	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 1));

		JLabel messageLabel = new JLabel();
		messageLabel.setText(this.message);
		messageLabel.setIcon(this.resourceRepository().getIcon("warning"));
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(messageLabel);

		JLabel exceptionLabel = new JLabel(this.exception.getClass().getName(), SwingConstants.CENTER);
		mainPanel.add(exceptionLabel);

		return mainPanel;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#helpTopicId()
	 */
	protected String helpTopicId() {
		return "dialog.unexpectedError";
	}

	/**
	 * Allow the user to see the stack trace.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildCustomActions()
	 */
	protected Iterator buildCustomActions() {
		Action stackTraceAction = this.buildStackTraceAction();
		return new SingleElementIterator(stackTraceAction);
	}

	private Action buildStackTraceAction() {
		return new AbstractAction(this.buildStackTraceText()) {
			public void actionPerformed(ActionEvent e) {
				ExceptionDialog.this.displayStackTrace();
			}
		};
	}

	protected String buildStackTraceText() {
		return this.resourceRepository().getString("STACK_TRACE");
	}

	/**
	 * display the stack trace with a text area dialog
	 */
	protected void displayStackTrace() {
		TextAreaDialog dialog =
			new TextAreaDialog(
				this.exception,
				this.helpTopicId(),
				this.getWorkbenchContext(),
				this
			);
		dialog.setTitle(this.buildStackTraceText());
		dialog.show();
	}

}
