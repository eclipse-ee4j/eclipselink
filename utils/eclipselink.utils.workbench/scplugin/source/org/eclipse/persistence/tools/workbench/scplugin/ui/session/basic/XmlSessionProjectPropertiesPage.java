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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the Project information for XML Session.
 * <p>
 * Here the layout of this page:<br>
 * <pre>
 * ____________________________
 * |                          |
 * |  {@link XmlSessionProjectPane}   |
 * |                          |
 * ----------------------------</pre>
 *
 * @see DatabaseSessionAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class XmlSessionProjectPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>SessionGeneralPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link SessionNode}
	 */
	public XmlSessionProjectPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
		addHelpTopicId(this, "session.general.project");
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Project sub-panel
		XmlSessionProjectPane generalPane = new XmlSessionProjectPane(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(generalPane, constraints);

		return container;
	}
}
