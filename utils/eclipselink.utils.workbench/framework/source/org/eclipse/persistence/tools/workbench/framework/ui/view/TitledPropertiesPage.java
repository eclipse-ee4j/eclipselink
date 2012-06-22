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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * This properties page displays a title at the top. The icon and label
 * displayed in the title are determined by the application node
 * passed in on construction.
 * 
 * Do not add a TitledPropertiesPage to a TabbedPropertiesPage.
 * 
 * Subclasses must implement #buildPage(). The component returned by
 * that method will be added to a scroll pane which is added to the panel
 * below the title.
 * 
 * Here is the layout:
 * <pre>
 * _______________________________________
 * | icon  displayString                 |
 * |¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯|
 * |                                     |
 * |                                     |
 * |                                     |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 * 
 * @see DisplayableTitlePanel
 * 
 */
public abstract class TitledPropertiesPage extends AbstractPropertiesPage {

	protected TitledPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Add the title pane
		this.add(this.buildTitlePanel(), BorderLayout.PAGE_START);

		// Add the container under the title pane
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));
		this.add(container, BorderLayout.CENTER);

		// Add the page
		JScrollPane scrollPane = new JScrollPane(this.buildPage());
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(3, 3, 3, 3);

		container.add(scrollPane, constraints);

		// Add the tabbed pane in order to get the border
		JTabbedPane pane = new JTabbedPane(SwingConstants.TOP);
		pane.setFocusable(false);
		pane.setRequestFocusEnabled(false);

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		container.add(pane, constraints);
	}

	/**
	 * Subclasses should return a component that will be placed "under"
	 * the title panel built above.
	 */
	protected abstract Component buildPage();

}
