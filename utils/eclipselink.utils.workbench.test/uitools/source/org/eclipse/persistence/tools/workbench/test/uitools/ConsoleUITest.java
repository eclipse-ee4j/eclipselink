/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.eclipse.persistence.tools.workbench.uitools.Console;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class ConsoleUITest {
	private PrintStream out;
	private PrintStream err;
	private Console console;

	public static void main(String[] args) throws Exception {
		new ConsoleUITest().exec(args);
	}

	public ConsoleUITest() {
		super();
	}

	private void exec(String[] args) throws Exception {
		JFrame window = new JFrame(ClassTools.shortClassNameForObject(this));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new GridLayout(1, 0));
		window.getContentPane().add(this.buildOpenButton());
		window.getContentPane().add(this.buildOutButton());
		window.getContentPane().add(this.buildErrButton());
		window.getContentPane().add(this.buildCloseButton());
		window.setLocation(200, 200);
		window.setSize(300, 100);
		window.setVisible(true);
	}

	private JButton buildOpenButton() {
		return new JButton(this.buildOpenAction());
	}

	private Action buildOpenAction() {
		return new AbstractAction("open") {
			public void actionPerformed(ActionEvent event) {
				ConsoleUITest.this.open();
			}
		};
	}

	private JButton buildOutButton() {
		return new JButton(this.buildOutAction());
	}

	private Action buildOutAction() {
		return new AbstractAction("out") {
			public void actionPerformed(ActionEvent event) {
				ConsoleUITest.this.testOut();
			}
		};
	}

	private JButton buildErrButton() {
		return new JButton(this.buildErrAction());
	}

	private Action buildErrAction() {
		return new AbstractAction("err") {
			public void actionPerformed(ActionEvent event) {
				ConsoleUITest.this.testErr();
			}
		};
	}

	private JButton buildCloseButton() {
		return new JButton(this.buildCloseAction());
	}

	private Action buildCloseAction() {
		return new AbstractAction("close") {
			public void actionPerformed(ActionEvent event) {
				ConsoleUITest.this.close();
			}
		};
	}


	// ********** behavior **********

	void open() {
		if (this.console == null) {
			// save the original streams, so we can restore them
			this.out = System.out;
			this.err = System.err;
			this.console = Console.buildSystemConsole();
		}
		this.console.open();
	}

	void testOut() {
		System.out.println("1. The quick brown fox jumps over the lazy dog.");
		System.out.println("2. The quick brown fox jumps over the lazy dog.");
		System.out.println("3. The quick brown fox jumps over the lazy dog.");
	}

	void testErr() {
		new RuntimeException(new IOException()).printStackTrace();
	}

	void close() {
		if (this.console != null) {
			System.setOut(this.out);
			System.setErr(this.err);
			this.console.close();
			this.console = null;
		}
	}

}
