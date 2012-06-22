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

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;


/**
 * Typical "About" dialog - albeit a bit lame....
 */
final class AboutDialog extends JDialog {

	/** tie the ESC key to the OK action */
	private Action okAction;	


	AboutDialog(WorkbenchContext context) {
		super((Frame) context.getCurrentWindow());
		this.initialize(context);
	}

	private void initialize(WorkbenchContext context) {
		this.setTitle(context.getApplicationContext().getResourceRepository().getString("about", context.getApplicationContext().getApplication().getProductName()));
		this.setName("About");

		this.setModal(true);
		this.setResizable(false);

		this.okAction = this.buildOKAction(context);		
		this.initializeDefaultKeyboardActions();
		this.initializeContentPane(context.getApplicationContext());

		//	TODO help topic id
		// this.setTopicID("Dialog.About");
	}

	protected AbstractFrameworkAction buildOKAction(WorkbenchContext context) {
		return new AbstractFrameworkAction(context) {
			protected void initialize() {
				this.initializeText("DIALOG.OK_BUTTON_TEXT");
			}
			protected void execute() {
				AboutDialog.this.dispose();
			}
		};
	}

	/**
	 * allow ESC to close the dialog
	 */
	protected void initializeDefaultKeyboardActions() {
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		this.getRootPane().getActionMap().put("cancel", this.okAction);
	}

	void initializeContentPane(ApplicationContext context) {
		this.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// main panel
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill				= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.NORTH;
		constraints.insets		= new Insets(10, 5, 0, 5);

		this.getContentPane().add(this.buildMainPanel(context), constraints);

		// separator
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;	
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill				= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.NORTH;
		constraints.insets		= new Insets(10, 5, 10, 5);

		this.getContentPane().add(new JSeparator(), constraints);

		// OK button
		JPanel buttonPanel = this.buildButtonPanel();
		buttonPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;	
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill				= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.NORTH;
		constraints.insets		= new Insets(0, 0, 0, 0);

		this.getContentPane().add(buttonPanel, constraints);
	}

	private JPanel buildMainPanel(ApplicationContext context) {
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel mainPanel = new JPanel(new GridBagLayout());

		// lame MW logo
		JLabel logoLabel = new JLabel(context.getResourceRepository().getIcon("mw.about"));
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 3;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.fill				= GridBagConstraints.HORIZONTAL;
		constraints.insets		= new Insets(20, 30, 0, 0);

		mainPanel.add(logoLabel, constraints);

		JLabel toplinkLabel = new JLabel(context.getApplication().getProductName());
		toplinkLabel.setHorizontalAlignment(SwingConstants.CENTER);

		Font font = toplinkLabel.getFont();
		toplinkLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 5));

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.fill				= GridBagConstraints.HORIZONTAL;
		constraints.insets		= new Insets(20, 10, 0, 50);

		mainPanel.add(toplinkLabel, constraints);

		// version
		JLabel versionLabel = new JLabel(context.getResourceRepository().getString("VERSION", context.getApplication().getVersionNumber()));
		versionLabel.setHorizontalAlignment(SwingConstants.CENTER);

		font = versionLabel.getFont();
		versionLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.fill				= GridBagConstraints.HORIZONTAL;
		constraints.insets		= new Insets(5, 10, 0, 50);

		mainPanel.add(versionLabel, constraints);

		// build
		JLabel buildLabel = new JLabel(context.getResourceRepository().getString("BUILD", context.getApplication().getBuildNumber()));
		buildLabel.setHorizontalAlignment(SwingConstants.CENTER);

		font = buildLabel.getFont();
		buildLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));

		constraints.gridx			= 1;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.fill				= GridBagConstraints.HORIZONTAL;
		constraints.insets		= new Insets(0, 10, 0, 50);

		mainPanel.add(buildLabel, constraints);

		// copyright
		LabelArea copyrightLabel = new LabelArea(context.getResourceRepository().getString("COPYRIGHT"));
		copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

		constraints.gridx			= 0;
		constraints.gridy			= 4;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.fill				= GridBagConstraints.HORIZONTAL;
		constraints.insets		= new Insets(30, 50, 0, 50);

		mainPanel.add(copyrightLabel, constraints);

		return mainPanel;
	}

	JPanel buildButtonPanel() {		
//		JPanel panel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel();

		JButton okButton = new JButton(this.okAction);
		panel.add(okButton);

		this.getRootPane().setDefaultButton(okButton);

		return panel;
	}

	/**
	 * This will show the dialog after first calling JDialog#pack()
	 * (which resizes the dialog to fit its contents) and then
	 * setting the location relative to the parent component.
	 */
	public void show() {
		this.pack();
		this.setLocationRelativeTo(this.getParent());
		super.show();
	}

}
