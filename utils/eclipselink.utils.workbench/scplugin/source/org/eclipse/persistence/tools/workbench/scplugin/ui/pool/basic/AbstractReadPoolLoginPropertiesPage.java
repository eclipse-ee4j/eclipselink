/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

// Mapping Workbench

/**
 * This page shows the information regarding the login.
 * <p>
 * Here the layout:
 * <pre>
 * _____________________________________________
 * |                                           |
 * | x Use Non-Transactional Read Login        |<- Enable/Disable the AbstractRdbmsLoginPane
 * |                                           |
 * |   --------------------------------------- |
 * |   |                                     | |
 * |   | {@link Sub pane}                            | |
 * |   |                                     | |
 * |   --------------------------------------- |
 * |                                           |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see ConnectionPoolAdapter
 * @see ReadConnectionPoolAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
abstract class AbstractReadPoolLoginPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>RdbmsReadPoolLoginPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link PoolNode}
	 * @param contextHolder
	 */
	AbstractReadPoolLoginPropertiesPage(PropertyValueModel nodeHolder,
													WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the <code>ComponentEnabler</code> that will keep the enable state
	 * of the components contained in the given collection in sync with the
	 * boolean value calculated by the enabled state holder.
	 *
	 * @param components The collection of components where their enable state
	 * will be updated when necessary
	 * @return A new <code>ComponentEnabler</code>
	 */
	private ComponentEnabler buildUseNonTransactionalReadLoginEnabler(Collection components)
	{
		return new ComponentEnabler(buildUseNonTransactionalReadLoginHolder(), components);
	}

	/**
	 * Creates the selection holder that will hold the user object to be edited
	 * by this page.
	 *
	 * @return The <code>PropertyValueModel</code> containing the {@link DatabaseLoginAdapter}
	 * to be edited by the {@link AbstractRdbmsLoginPane}
	 */
	protected final PropertyValueModel buildLoginHolder()
	{
		String[] propertyNames =
		{
			ConnectionPoolAdapter.LOGIN_CONFIG_PROPERTY,
			ReadConnectionPoolAdapter.USE_NON_TRANSACTIONAL_READ_LOGIN_PROPERTY
		};

		return new PropertyAspectAdapter(getSelectionHolder(), propertyNames)
		{
			protected Object getValueFromSubject()
			{
				ReadConnectionPoolAdapter pool = (ReadConnectionPoolAdapter) subject;

				if (pool.usesNonTransactionalReadLogin())
					return pool.getLogin();

				return null;
			}
		};
	}

	/**
	 * Creates the pane that will show the login information.
	 *
	 * @return The login pane
	 */
	protected abstract JComponent buildLoginPane();

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected final Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Vector components = new Vector();
		int offset = SwingTools.checkBoxIconWidth();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Exclusive Connections check box
		JCheckBox exclusiveCheckBox = buildCheckBox
		(
			"CONNECTION_READ_EXCLUSIVE_CONNECTIONS_CHECK_BOX",
			buildExclusiveConnectionsCheckBoxAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(exclusiveCheckBox, constraints);
		helpManager().addTopicID(exclusiveCheckBox, "connectionPool.exclusiveTransactions");

		// Use Non Transactional Read Login check box
		JCheckBox nonTransactionalReadLoginCheckBox = buildCheckBox
		(
			"CONNECTION_READ_USE_NON_TRANSACTIONAL_READ_LOGIN_CHECK_BOX",
			buildUseNonTransactionalReadLoginCheckBoxAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 5, 0);

		panel.add(nonTransactionalReadLoginCheckBox, constraints);
		helpManager().addTopicID(nonTransactionalReadLoginCheckBox, "connectionPool.nonTransactional");

		// Login pane
		JComponent loginPane = buildLoginPane();
		loginPane.setName("CONNECTION_READ_LOGIN_PANE");

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(0, offset, 0, 0);

		panel.add(loginPane, constraints);
		helpManager().addTopicID(loginPane, "session.login.database.connection");
		components.add(loginPane);

		buildUseNonTransactionalReadLoginEnabler(components);

		// Create the object responsible to keep the enable state of the components
		// in sync with the Session's property External Connection Pooling
		buildExclusiveConnectionsEnabler(exclusiveCheckBox);

		return panel;
	}

	/**
	 * Creates the <code>ButtonModel</code> responsible to handle enabled state
	 * of Use Non-Transactional Read Login check box.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildUseNonTransactionalReadLoginCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildUseNonTransactionalReadLoginHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Use Non-Transactional Read Login.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildUseNonTransactionalReadLoginHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), ReadConnectionPoolAdapter.USE_NON_TRANSACTIONAL_READ_LOGIN_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ReadConnectionPoolAdapter pool = (ReadConnectionPoolAdapter) subject;
				return Boolean.valueOf(pool.usesNonTransactionalReadLogin());
			}

			protected void setValueOnSubject(Object value)
			{
				ReadConnectionPoolAdapter pool = (ReadConnectionPoolAdapter) subject;
				pool.setUseNonTransactionalReadLogin(Boolean.TRUE.equals(value));
			}
		};
	}
	
	/**
	 * Creates the <code>ButtonModel</code> responsible to handle enabled state
	 * of Exclusive Connections check box.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildExclusiveConnectionsCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildExclusiveConnectionsHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Exclusive Connections.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildExclusiveConnectionsHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), ReadConnectionPoolAdapter.EXCLUSIVE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ReadConnectionPoolAdapter pool = (ReadConnectionPoolAdapter) subject;
				return Boolean.valueOf(pool.isExclusive());
			}

			protected void setValueOnSubject(Object value)
			{
				ReadConnectionPoolAdapter pool = (ReadConnectionPoolAdapter) subject;
				pool.setExclusive(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ComponentEnabler</code> that will keep the enable state
	 * of the components contained in the given collection in sync with the
	 * boolean value calculated by the enabled state holder.
	 *
	 * @param components The collection of components where their enable state
	 * will be updated when necessary
	 * @return A new <code>ComponentEnabler</code>
	 */
	private ComponentEnabler buildExclusiveConnectionsEnabler(JComponent component)
	{
		return new ComponentEnabler(buildEnableStateHolder(), Collections.singletonList(component));
	}
	
	/**
	 * Creates the <code>PropertyValueModel</code> responsible to retrieve the
	 * boolean flag used by the <code>ComponentEnabler</code> in order to keep
	 * the enable state of the components in sync with the underlying model's
	 * property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildEnableStateHolder()
	{
		// Holder of the ServerSessionAdapter
		PropertyAspectAdapter sessionHolder = new PropertyAspectAdapter(getSelectionHolder(), "")
		{
			protected Object getValueFromSubject()
			{
				// Pool->PoolsAdapter->ServerSessionAdapter
				ConnectionPoolAdapter pool = (ConnectionPoolAdapter) subject;
				return pool.getParent().getParent();
			}
		};

		// Holder of the LoginAdapter, just in case it changes
		PropertyAspectAdapter loginHolder = new PropertyAspectAdapter(sessionHolder, ServerSessionAdapter.LOGIN_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ServerSessionAdapter session = (ServerSessionAdapter) subject;
				return session.getLogin();
			}
		};

		// Holder of the LoginAdapter's property: External Connection Pooling
		PropertyAspectAdapter booleanHolder = new PropertyAspectAdapter(loginHolder, DatabaseSessionAdapter.EXTERNAL_CONNECTION_POOLING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LoginAdapter login = (LoginAdapter) subject;
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) login.getParent();
			
				return Boolean.valueOf(session.usesExternalConnectionPooling());
			}
		};

		// Convert the boolean from true to false and vice versa
		return new TransformationPropertyValueModel(booleanHolder)
		{
			protected Object transform(Object value)
			{
				// Reverse the value
				return Boolean.valueOf(Boolean.FALSE.equals(value));
			}
		};
	}

}