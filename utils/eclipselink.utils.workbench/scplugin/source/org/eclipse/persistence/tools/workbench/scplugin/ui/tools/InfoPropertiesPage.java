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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page only shows a message on screen.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________
 * |                     |
 * |    <A message>      |
 * |                     |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class InfoPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Keeps a reference so we can set the text.
	 */
	private LabelArea infoLabel;

	/**
	 * Creates a new <code>InfoPropertiesPage</code>.
	 *
	 * @param messageKey The key used to retrieve the message to be displayed
	 * @param nodeHolder The holder of any node
	 */
	public InfoPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder, String messageKey)
	{
		super(nodeHolder, contextHolder);
		this.infoLabel.setText(resourceRepository().getString(messageKey));
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
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Message pane
		this.infoLabel = new LabelArea() {
			public boolean isFocusable() {
				return true;
			}
			public boolean isRequestFocusEnabled() {
				return true;
			}
		};

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(this.infoLabel, constraints);
      
		return panel;
	}
}
