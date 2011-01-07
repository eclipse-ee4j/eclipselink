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
package org.eclipse.persistence.tools.workbench.framework.ui.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


/**
 * Simple dialog for displaying uneditable text.
 */
public class TextAreaDialog extends AbstractDialog {
	private String text;
	private String helpTopicID;
	private JTextArea textArea;


	// ********** static methods **********

	private static String buildStackTrace(Throwable exception) {
		Writer stream = new StringWriter(5000);
		exception.printStackTrace(new PrintWriter(stream));
		return stream.toString();
	}


	// ********** constructors **********

	public TextAreaDialog(String text, String title, String helpTopicID, WorkbenchContext context) {
		super(context, title);
		this.text = text;
		this.helpTopicID = helpTopicID;
	}

	public TextAreaDialog(String text, String helpTopicID, WorkbenchContext context) {
		super(context);
		this.text = text;
		this.helpTopicID = helpTopicID;
	}

	public TextAreaDialog(String text, String helpTopicID, WorkbenchContext context, Dialog owner) {
		super(context, owner);
		this.text = text;
		this.helpTopicID = helpTopicID;
	}

	public TextAreaDialog(Throwable exception, String helpTopicID, WorkbenchContext context) {
		this(buildStackTrace(exception), helpTopicID, context);
	}

	public TextAreaDialog(Throwable exception, String helpTopicID, WorkbenchContext context, Dialog owner) {
		this(buildStackTrace(exception), helpTopicID, context, owner);
	}


	// ********** AbstractDialog implementation **********

	/**
	 * @see AbstractDialog#helpTopicId()
	 */
	protected String helpTopicId() {
		return this.helpTopicID;
	}

	/**
	 * @see AbstractDialog#buildMainPanel()
	 */
	protected Component buildMainPanel() {
		this.setSize(500, 400);
		this.textArea = buildTextArea();
		return new JScrollPane(this.textArea);
	}
	
	protected JTextArea buildTextArea() {
		JTextArea result = new JTextArea(this.text);
		result.setFont(UIManager.getFont("Label.font"));
		result.setEditable(false);		
		return result;
	}

	/**
	 * @see AbstractDialog#cancelButtonIsVisible()
	 */
	protected boolean cancelButtonIsVisible() {
		return false;
	}

	/**
	 * @see AbstractDialog#buildCustomActions()
	 */
	protected Iterator buildCustomActions() {
		Action copyAction = this.buildCopyAction();
		return new SingleElementIterator(copyAction);
	}

	private Action buildCopyAction() {
		return new AbstractAction(this.buildCopyText()) {
			public void actionPerformed(ActionEvent e) {
				TextAreaDialog.this.copyPressed();
			}
		};
	}	

	protected String buildCopyText() {
		return this.resourceRepository().getString("TEXT_AREA_DIALOG.COPY_BUTTON_TEXT");
	}

	protected void copyPressed() {
		int pos = this.textArea.getCaretPosition();
		this.textArea.selectAll();
		this.textArea.copy();
		this.textArea.setCaretPosition(pos);
	}


	/**
	 * Override this method because we set an explicit size
	 * and don't want to "pack" the dialog.
	 * @see AbstractDialog#prepareToShow()
	 */
	protected void prepareToShow() {
		this.setLocationRelativeTo(this.getParent());
	}


	// ********** public API **********

	/**
	 * Allow clients to directly manipulate the text area.
	 */
	public JTextArea getTextArea() {
		return this.textArea;
	}

}
