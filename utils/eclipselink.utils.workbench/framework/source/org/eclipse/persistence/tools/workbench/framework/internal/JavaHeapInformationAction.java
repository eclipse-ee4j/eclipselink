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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * development-only action that displays the current Java heap information
 */
final class JavaHeapInformationAction
	extends AbstractFrameworkAction
{
	private JLabel totalMemoryLabel;
	private JLabel freeMemoryLabel;
	private JLabel maxMemoryLabel;
	private static final int MB = 1024 * 1024;

	JavaHeapInformationAction(WorkbenchContext context) {
		super(context);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction#initialize()
	 */
	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("JAVA_HEAP_INFO");
	}

	/**
	 * ignore the selected nodes
	 */
	protected void execute() {
		this.totalMemoryLabel = new JLabel();
		this.freeMemoryLabel = new JLabel();
		this.maxMemoryLabel = new JLabel();
		this.refreshLabels();
		JOptionPane.showMessageDialog(
			this.currentWindow(),
			this.buildPanel(),
			this.resourceRepository().getString("JAVA_HEAP_INFO"),
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	private Object buildPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(this.totalMemoryLabel);
		panel.add(this.freeMemoryLabel);
		panel.add(this.maxMemoryLabel);
		panel.add(this.buildGCButton());
		return panel;
	}

	private void refreshLabels() {
		Runtime rt = Runtime.getRuntime();
		NumberFormat nf = NumberFormat.getInstance();
		this.totalMemoryLabel.setText("Current Heap Size: " + nf.format(rt.totalMemory()/MB) + " MB");
		this.freeMemoryLabel.setText("Available Heap Space: " + nf.format(rt.freeMemory()/MB) + " MB");
		this.maxMemoryLabel.setText("Max Heap Size: " + nf.format(rt.maxMemory()/MB) + " MB");
	}

	private JButton buildGCButton() {
		return new JButton(this.buildGCAction());
	}

	private Action buildGCAction() {
		Action action = new AbstractAction("Collect Garbage") {
			public void actionPerformed(ActionEvent event) {
				JavaHeapInformationAction.this.gc();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void gc() {
		System.gc();
		this.refreshLabels();
	}

}
