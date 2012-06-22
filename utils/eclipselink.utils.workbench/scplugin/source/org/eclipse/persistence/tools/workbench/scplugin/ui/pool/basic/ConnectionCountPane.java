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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;

// Mapping Worbench

/**
 * This pane shows the Connection count widgets. Those widgets are disabled if
 * {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter#usesExternalConnectionPooling() LoginAdapter.usesExternalConnectionPooling()}
 * returns <code>true<code>.
 * <p>
 * If the Login's External Connection Pooling flag is set to <code>true<code>,
 * then the components of this pane will be disabled.
 * <p>
 * Here the layout:
 * <pre>
 * _________________________________
 * |                               |
 * | _Connection Count____________ |
 * | |          ____________     | |
 * | | Maximum: | I      |I|     | |
 * | |          ¯¯¯¯¯¯¯¯¯¯¯¯     | |
 * | |          ____________     | |
 * | | Minimum: | I      |I|     | |
 * | |          ¯¯¯¯¯¯¯¯¯¯¯¯     | |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * Know containers of this pane:<br>
 * - {@link PoolGeneralPropertiesPage}<br>
 * - {@link ReadPoolGeneralPropertiesPage}<br>
 * - {@link org.eclipse.persistence.tools.workbench.scplugin.ui.pool.write.PoolGeneralPropertiesPage PoolGeneralPropertiesPage}
 *
 * @see ConnectionPoolAdapter
 * @see LoginAdapter#usesExternalConnectionPooling()
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public final class ConnectionCountPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>ConnectionCountPane</code>.
	 *
	 * @param subjectHolder The holder of {@link ConnectionPoolAdapter}
	 * @param context The context used to retrieve the <code>ResourceRepository</code>
	 */
	public ConnectionCountPane(PropertyValueModel subjectHolder,
										ApplicationContext context)
	{
		super(subjectHolder, context);
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
	private ComponentEnabler buildComponentEnabler()
	{
		return new ComponentEnabler(buildEnableStateHolder(), getComponents());
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
		PropertyAspectAdapter sessionHolder = new PropertyAspectAdapter(getSubjectHolder(), "")
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

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Maximum Connections Count property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildMaximumCountHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), ConnectionPoolAdapter.MAX_CONNECTIONS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ConnectionPoolAdapter pool = (ConnectionPoolAdapter) subject;
				return new Integer(pool.getMaxConnections());
			}

			protected void setValueOnSubject(Object value)
			{
				ConnectionPoolAdapter pool = (ConnectionPoolAdapter) subject;
				pool.setMaxConnections(((Integer) value).intValue());
			}
		};
	}

	/**
	 * Creates the <code>SpinnerModel</code> that keeps the value from the
	 * spinner in sync with the Maximum Connections value in the model and vice
	 * versa.
	 * 
	 * @return A new <code>SpinnerNumberModel</code>
	 */
	private SpinnerNumberModel buildMaximumCountSpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildMaximumCountHolder(), 0, Integer.MAX_VALUE, 1);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Minimum Connections property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildMinimumCountHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), ConnectionPoolAdapter.MIN_CONNECTIONS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ConnectionPoolAdapter pool = (ConnectionPoolAdapter) subject;
				return new Integer(pool.getMinConnections());
			}

			protected void setValueOnSubject(Object value)
			{
				ConnectionPoolAdapter pool = (ConnectionPoolAdapter) subject;
				pool.setMinConnections(((Integer) value).intValue());
			}
		};
	}

	/**
	 * Creates the <code>SpinnerModel</code> that keeps the value from the
	 * spinner in sync with the Maximum Connections value in the model and vice
	 * versa.
	 * 
	 * @return A new <code>SpinnerNumberModel</code>
	 */
	private SpinnerNumberModel buildMinimumCountSpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildMinimumCountHolder(), 0, Integer.MAX_VALUE, 1);
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		setBorder(buildTitledBorder("CONNECTION_POOL_CONNECTION_COUNT_TITLE"));
		setName("CONNECTION_POOL_CONNECTION_COUNT_PANE");

		GridBagConstraints constraints = new GridBagConstraints();

		// Minimum Connections label
		JLabel minimumConnectionsLabel = buildLabel("CONNECTION_POOL_MINIMUM_CONNECTIONS_SPINNER");

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 5);

		add(minimumConnectionsLabel, constraints);

		// Minimum Connections spinner
		JSpinner minimumConnectionsSpinner = SwingComponentFactory.buildSpinnerNumber(buildMinimumCountSpinnerAdapter());
		minimumConnectionsSpinner.setName("CONNECTION_POOL_MINIMUM_CONNECTIONS_SPINNER");

		constraints.gridx       = 1;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 5);

		add(minimumConnectionsSpinner, constraints);
		minimumConnectionsLabel.setLabelFor(minimumConnectionsSpinner);

		// Maximum Connections label
		JLabel maximumConnectionsLabel = buildLabel("CONNECTION_POOL_MAXIMUM_CONNECTIONS_SPINNER");

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 5, 5);

		add(maximumConnectionsLabel, constraints);

		// Maximum Connections spinner
		JSpinner maximumConnectionsSpinner = SwingComponentFactory.buildSpinnerNumber(buildMaximumCountSpinnerAdapter());
		maximumConnectionsSpinner.setName("CONNECTION_POOL_MAXIMUM_CONNECTIONS_SPINNER");

		constraints.gridx       = 1;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 5, 5);

		add(maximumConnectionsSpinner, constraints);
		maximumConnectionsLabel.setLabelFor(maximumConnectionsSpinner);

		// Create the object responsible to keep the enable state of the components
		// in sync with the Session's property External Connection Pooling
		buildComponentEnabler();
	}
}
