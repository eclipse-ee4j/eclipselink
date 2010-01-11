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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.SimpleWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsPluginFactory;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository.DatabasePlatformRepositoryNode;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * 
 */
public abstract class AbstractPropertiesPageTest {
	private ApplicationNode[] nodes;
	private int max;
	private int index;
	private Component page;
	private boolean pageIsCleared;
	private JPanel mainPanel;


	protected AbstractPropertiesPageTest() {
		super();
	}

	protected void exec(String[] args) throws Exception {
		File platformsFile;
		if ((args == null) || (args.length == 0)) {
			platformsFile = (File) ClassTools.invokeStaticMethod(DatabasePlatformRepository.class, "buildDefaultFile");
		} else {
			platformsFile = new File(args[0]);
		}
		WorkbenchContext wbContext = this.buildWorkbenchContext();
		Plugin plugin = PlatformsPluginFactory.instance().createPlugin(wbContext.getApplicationContext());
		DatabasePlatformRepositoryNode reposNode = (DatabasePlatformRepositoryNode) plugin.open(platformsFile, wbContext);
		this.nodes = this.buildNodes(reposNode);
		this.max = this.nodes.length - 1;
		this.index = 0;
		this.page = this.nodes[this.index].propertiesPage(null);
		this.pageIsCleared = false;
		this.openWindow();
	}

	private WorkbenchContext buildWorkbenchContext() {
		return new SimpleWorkbenchContext(null, null, ResourceRepository.NULL_INSTANCE, null, null, null, null, null, null);
	}

	private ApplicationNode[] buildNodes(DatabasePlatformRepositoryNode reposNode) {
		Collection result = new ArrayList();
		ListValueModel childrenModel = this.nodesModel(reposNode);
		for (Iterator stream = (Iterator) childrenModel.getValue(); stream.hasNext(); ) {
			result.add(stream.next());
		}
		return (ApplicationNode[]) result.toArray(new ApplicationNode[result.size()]);
	}

	protected abstract ListValueModel nodesModel(DatabasePlatformRepositoryNode reposNode);

	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setSize(600, 500);
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
		this.mainPanel = new JPanel(new BorderLayout());
		this.mainPanel.add(this.page, BorderLayout.CENTER);
		this.mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return this.mainPanel;
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(1, 0));
		controlPanel.add(this.buildPreviousButton());
		controlPanel.add(this.buildNextButton());
		controlPanel.add(this.buildClearButton());
		controlPanel.add(this.buildPrintButton());
		return controlPanel;
	}

	private JButton buildNextButton() {
		return new JButton(this.buildNextAction());
	}

	private Action buildNextAction() {
		Action action = new AbstractAction("next") {
			public void actionPerformed(ActionEvent event) {
				AbstractPropertiesPageTest.this.next();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void next() {
		if (this.index < this.max) {
			int oldIndex = this.index;
			this.index++;
			this.installNewPage(oldIndex, this.index);
		}
	}

	private void installNewPage(int oldIndex, int newIndex) {
		// remove old page...
		this.mainPanel.remove(this.page);
		this.nodes[oldIndex].releasePropertiesPage(this.page);

		// ...add new page
		this.page = this.nodes[newIndex].propertiesPage(null);
		this.mainPanel.add(this.page, BorderLayout.CENTER);

		// since the panel is already displayed, we need to refresh it
		this.mainPanel.revalidate();
		this.mainPanel.repaint();
	}

	private JButton buildPreviousButton() {
		return new JButton(this.buildPreviousAction());
	}

	private Action buildPreviousAction() {
		Action action = new AbstractAction("previous") {
			public void actionPerformed(ActionEvent event) {
				AbstractPropertiesPageTest.this.previous();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void previous() {
		if (this.index > 0) {
			int oldIndex = this.index;
			this.index--;
			this.installNewPage(oldIndex, this.index);
		}
	}

	private JButton buildClearButton() {
		return new JButton(this.buildClearAction());
	}

	private Action buildClearAction() {
		Action action = new AbstractAction("clear") {
			public void actionPerformed(ActionEvent event) {
				AbstractPropertiesPageTest.this.clear();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void clear() {
		if (this.pageIsCleared) {
			this.nodes[this.index].propertiesPage(null);		// ignore what is returned
			this.pageIsCleared = false;
		} else {
			this.nodes[this.index].releasePropertiesPage(this.page);
			this.pageIsCleared = true;
		}
	}

	private JButton buildPrintButton() {
		return new JButton(this.buildPrintAction());
	}

	private Action buildPrintAction() {
		Action action = new AbstractAction("print") {
			public void actionPerformed(ActionEvent event) {
				AbstractPropertiesPageTest.this.print();
			}
		};
		action.setEnabled(true);
		return action;
	}

	protected Object currentValue() {
		return this.nodes[this.index].getValue();
	}

	protected abstract void print();
}
