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
package org.eclipse.persistence.tools.workbench.scplugin.ui.login;

// JDK
import java.awt.GridLayout;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

// Mapping Workbench

/**
 * This page only shows two options as shown in the layout.
 * <p>
 * Here the layout:
 * <pre>
 * ___________________________________________
 * |                                         |
 * | x External Transaction Pooling          |
 * |                                         |
 * -------------------------------------------</pre>
 *
 * Known containers of this pane:<br>
 * - {@link EisConnectionPropertiesPage}<br>
 * - {@link RdbmsConnectionPropertiesPage}
 *
 * @see LoginAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class LoginExternalOptionsPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>LoginExternalOptionsPane</code>.
	 *
	 * @param subjectHolder The holder of {@link LoginAdapter}
	 * @param context The plug-in context to be used, such as <code>ResourceRepository</code>
	 */
	public LoginExternalOptionsPane(PropertyValueModel subjectHolder,
											  ApplicationContext context)
	{
		super(new GridLayout(1, 1, 0, 0), subjectHolder, context);
	}

	/**
	 * Creates the <code>JCheckBox</code> for the property External Connection
	 * Pooling.
	 *
	 * @return A new <code>JCheckBox</code>
	 */
	protected JCheckBox buildExternalConnectionPoolingCheckBox()
	{
		return buildCheckBox("CONNECTION_EXTERNAL_CONNECTION_POOLING_CHECK_BOX",
									buildExternalConnectionPoolingCheckBoxAdapter());
	}

	/**
	 * Creates the <code>ButtonModel</code> responsible to handle selected state
	 * of External Connection Pooling check box.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildExternalConnectionPoolingCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildExternalConnectionPoolingHolder(), false);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * External Connection Pooling property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildExternalConnectionPoolingHolder()
	{
		PropertyValueModel subjectHolder = new PropertyAspectAdapter(getSubjectHolder(), "")
		{
			protected Object getValueFromSubject()
			{
				LoginAdapter login = (LoginAdapter) subject;
				return login.getParent();
			}
		};

		return new PropertyAspectAdapter(subjectHolder, DatabaseSessionAdapter.EXTERNAL_CONNECTION_POOLING_PROPERTY)
		{
			protected Object getValueFromSubject() {
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				return Boolean.valueOf(session.usesExternalConnectionPooling());
			}

			protected void setValueOnSubject(Object value) {
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				session.setExternalConnectionPooling(((Boolean)value).booleanValue());
			}
		};
	}
	
	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		add(buildExternalConnectionPoolingCheckBox());
	}
}
