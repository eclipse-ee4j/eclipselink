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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.Console;


/**
 * An implementation of Console that externalizes the strings for public consumption
 * so that it can be used at runtime to display strings written to System.out and 
 * System.err so that the Application can display such messages to the user.
 */
public class FrameworkConsole extends Console {
	private ApplicationContext appContext;


	public FrameworkConsole(ApplicationContext context) {
		super();
		this.initialize(context);
	}

	protected void initialize() {
		// wait until application context is instantiated
		if (this.appContext != null) {
			super.initialize();
		}
	}

	private void initialize(ApplicationContext context) {
		this.appContext = context;
		// call #initialize() again
		this.initialize();
	}

	protected JPanel buildMainPanel() {
		JPanel mainPanel = super.buildMainPanel();
		mainPanel.add(this.buildErrorLabel(), BorderLayout.PAGE_START);
		return mainPanel;
	}

	private Component buildErrorLabel() {
		JLabel errorLabel = new JLabel(this.resourceRepository().getString("UNEXPECTED_OUTPUT_ERROR_MESSAGE"));
		errorLabel.setIcon(this.resourceRepository().getIcon("error.large"));
		return errorLabel;
	}

	protected Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new BorderLayout());
		JPanel controlPanel2 = new JPanel(new BorderLayout());
		controlPanel2.add(this.buildHelpButton(), BorderLayout.LINE_START);
		controlPanel2.add(this.buildOKButton(), BorderLayout.LINE_END);
		controlPanel2.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
		controlPanel.add(controlPanel2, BorderLayout.CENTER);
		return controlPanel;
	}

	private Component buildHelpButton() {
		return new JButton(this.buildHelpAction());
	}

	private Action buildHelpAction() {
		Action action = new AbstractAction(resourceRepository().getString("DIALOG.HELP_BUTTON_TEXT")) {
			public void actionPerformed(ActionEvent event) {
				FrameworkConsole.this.helpManager().showTopic(FrameworkConsole.this.helpTopicId());
			}
		};
		action.setEnabled(true);
		return action;
	}

	private Component buildOKButton() {
		return new JButton(this.buildOKAction());
	}

	private Action buildOKAction() {
		Action action = new AbstractAction(resourceRepository().getString("DIALOG.OK_BUTTON_TEXT")) {
			public void actionPerformed(ActionEvent event) {
				FrameworkConsole.this.clear();
				FrameworkConsole.this.hide();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private ResourceRepository resourceRepository() {
		return this.appContext.getResourceRepository();
	}

	HelpManager helpManager() {
		return this.appContext.getHelpManager();
	}

	String helpTopicId() {
		return "dialog.unexpected.output";
	}

	protected String title() {
		return this.resourceRepository().getString("UNEXPECTED_OUTPUT_LOG_TITLE");
	}

}
