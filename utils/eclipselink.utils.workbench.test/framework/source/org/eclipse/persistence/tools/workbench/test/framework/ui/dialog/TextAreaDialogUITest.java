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
package org.eclipse.persistence.tools.workbench.test.framework.ui.dialog;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import org.eclipse.persistence.tools.workbench.framework.ui.dialog.TextAreaDialog;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class TextAreaDialogUITest {
	private JFrame window;

	public static void main(String[] args) throws Exception {
		new TextAreaDialogUITest().exec(args);
	}

	public TextAreaDialogUITest() {
		super();
	}

	private void exec(String[] args) throws Exception {
		this.window = new JFrame(ClassTools.shortClassNameForObject(this));
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.window.getContentPane().setLayout(new GridLayout(1, 0));
		this.window.getContentPane().add(this.buildTextButton());
		this.window.getContentPane().add(this.buildExceptionButton());
		this.window.setLocation(200, 200);
		this.window.setSize(300, 100);
		this.window.setVisible(true);
	}

	private JButton buildTextButton() {
		return new JButton(this.buildTextAction());
	}

	private Action buildTextAction() {
		return new AbstractAction("text") {
			public void actionPerformed(ActionEvent event) {
				TextAreaDialogUITest.this.testText();
			}
		};
	}

	private JButton buildExceptionButton() {
		return new JButton(this.buildExceptionAction());
	}

	private Action buildExceptionAction() {
		return new AbstractAction("exception") {
			public void actionPerformed(ActionEvent event) {
				TextAreaDialogUITest.this.testException();
			}
		};
	}

	private void testText() {
		TestWorkbenchContext context = new TestWorkbenchContext();
		context.setCurrentWindow(this.window);
		TextAreaDialog dialog = new TextAreaDialog("The quick brown fox jumps over the lazy dog.", "foo.help", context);
		dialog.setTitle("test");
		dialog.show();
	}

	private void testException() {
		TestWorkbenchContext context = new TestWorkbenchContext();
		context.setCurrentWindow(this.window);
		TextAreaDialog dialog = new TextAreaDialog(new RuntimeException(new IOException()), "foo.help", context);
		dialog.setTitle("test");
		dialog.show();
	}

}
