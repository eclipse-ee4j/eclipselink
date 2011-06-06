/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

// JDK
import java.awt.Component;
import java.util.Collections;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the {@link PropertyPane} which edits the login's properties.
 * <p>
 * Here the layout of this pane:
 * <pre>
 * _______________________
 * |                     |
 * |    {@link PropertyPane}     |
 * |                     |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see LoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class RdbmsPropertiesPropertiesPage extends LoginPropertiesPropertiesPage
{
	/**
	 * Creates a new <code>LoginPropertiesPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link DatabaseLoginAdapter}
	 * @param context
	 */
	public RdbmsPropertiesPropertiesPage(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildPage()
	{
		JComponent subPane = (JComponent) super.buildPage();
		buildPropertyPaneEnabler(subPane);
		return subPane;
	}

	/**
	 * Attaches the given pane with a {@link ComponentEnabler} in order update
	 * the pane's enable state when the check box Use Properties is selected
	 * (located in the Options - Advanced tab).
	 *
	 * @param pane The pane to have its enabled state (along with its children)
	 * in sync with the Use Properties property.
	 */
	private void buildPropertyPaneEnabler(JComponent pane)
	{
		new ComponentEnabler(buildUsePropertiesHolder(), Collections.singleton(pane));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Use Properties property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildUsePropertiesHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.USE_PROPERTIES_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.usesProperties());
			}
		};
	}
}
