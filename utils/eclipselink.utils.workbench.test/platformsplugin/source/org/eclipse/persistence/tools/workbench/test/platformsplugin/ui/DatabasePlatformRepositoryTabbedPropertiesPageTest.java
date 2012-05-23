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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsPluginFactory;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * 
 */
public class DatabasePlatformRepositoryTabbedPropertiesPageTest {
	private ApplicationNode node;
	private Component page;
	private boolean pageIsCleared;


	public static void main(String[] args) throws Exception {
		new DatabasePlatformRepositoryTabbedPropertiesPageTest().exec(args);
	}

	public DatabasePlatformRepositoryTabbedPropertiesPageTest() {
		super();
	}

	private void exec(String[] args) throws Exception {
		String platformsFileName;
		if ((args == null) || (args.length == 0)) {
			platformsFileName = "/platforms.xml";
		} else {
			platformsFileName = args[0];
		}
		WorkbenchContext wbContext = this.buildWorkbenchContext();
		File platformsFile = FileTools.resourceFile(platformsFileName);
		Plugin plugin = PlatformsPluginFactory.instance().createPlugin(wbContext.getApplicationContext());
		node = plugin.open(platformsFile, wbContext);
		page = node.propertiesPage(null);
		pageIsCleared = false;
		this.openWindow();
	}

	private WorkbenchContext buildWorkbenchContext() {
		return new TestWorkbenchContext();
	}

	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setSize(500, 400);
		window.setLocation(300, 300);
		window.setVisible(true);
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.exit(0);
			}
		};
	}

	private Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(page, BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(1, 0));
		controlPanel.add(this.buildClearButton());
		controlPanel.add(this.buildPrintButton());
		return controlPanel;
	}

	private JButton buildClearButton() {
		return new JButton(this.buildClearAction());
	}

	private Action buildClearAction() {
		Action action = new AbstractAction("clear") {
			public void actionPerformed(ActionEvent event) {
				DatabasePlatformRepositoryTabbedPropertiesPageTest.this.clear();
			}
		};
		action.setEnabled(true);
		return action;
	}

	// toggle in and out the repository
	private void clear() {
		if (pageIsCleared) {
			node.propertiesPage(null);		// ignore what is returned
			pageIsCleared = false;
		} else {
			node.releasePropertiesPage(page);
			pageIsCleared = true;
		}
	}

	private JButton buildPrintButton() {
		return new JButton(this.buildPrintAction());
	}

	private Action buildPrintAction() {
		Action action = new AbstractAction("print") {
			public void actionPerformed(ActionEvent event) {
				DatabasePlatformRepositoryTabbedPropertiesPageTest.this.print();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void print() {
		DatabasePlatformRepository repository = this.repository();
		System.out.println("repository: " + repository);
		System.out.println("\tfile: " + repository.getFile());
		System.out.println("\tdefault platform: " + repository.getDefaultPlatform());
		System.out.println("\tcomment: " + repository.getComment());
	}

	private DatabasePlatformRepository repository() {
		return (DatabasePlatformRepository) node.getValue();
	}

}
