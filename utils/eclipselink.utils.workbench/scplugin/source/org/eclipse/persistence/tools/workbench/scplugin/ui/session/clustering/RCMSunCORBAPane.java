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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JCheckBox;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

// Mapping Workbench

/**
 * This page shows the information about the {@link SunCORBATransportManagerAdapter}.
 * <p>
 * Here the layout of this pane:</pre>
 * ________________________________________________
 * |                                              |
 * |  x Remove Connection on Error                |
 * |                                              |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯<pre>
 *
 * Known container of this pane:<br>
 * - {@link RemoteCommandManagerPane}
 *
 * @see SunCORBATransportManagerAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class RCMSunCORBAPane extends AbstractTransportManagerPane
{
	/**
	 * Creates a new <code>RCMSunCORBAPane</code>.
	 *
	 * @param subjectHolder The holder of {@link SunCORBATransportManagerAdapter}
	 * @param contextHolder The holder of the context to be used by this pane
	 */
	public RCMSunCORBAPane(ValueModel subjectHolder,
								  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Initializes this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Remove Connection On Error check box
		JCheckBox removeConnectionOnErrorCheckBox = buildRemoveConnectionOnError();

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(removeConnectionOnErrorCheckBox, constraints);
	}
}
