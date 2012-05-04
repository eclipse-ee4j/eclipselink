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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.LoginExternalOptionsPane;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.RdbmsLoginPane;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

// Mapping Workbench

/**
 * This page shows the information regarding the Database login which is
 * specific for {@link DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * ___________________________________________
 * |                    ____________________ |
 * | Database Platform: |                |V| |
 * |                    -------------------- |
 * | --------------------------------------- |
 * | |                                     | |
 * | | {@link RdbmsLoginPane}                           | |
 * | |                                     | |
 * | --------------------------------------- |
 * |                                         |
 * | --------------------------------------- |
 * | |                                     | |
 * | | {@link CustomizedExternalOptionsPane}       | |<- {@link LoginExternalOptionsPane}
 * | |                                     | |
 * | --------------------------------------- |
 * |                                         |
 * -------------------------------------------</pre>
 *
 * @see LoginAdapter
 * @see DatabaseLoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class RdbmsConnectionPropertiesPage extends AbstractLoginPropertiesPage
{
	/**
	 * This model is used to transform 3 properties into a boolean property.
	 */
	private PseudoExternalConnectionPoolingModel pseudoModel;

	/**
	 * Creates a new <code>RdbmsConnectionPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.DatabaseSessionNode DatabaseSessionNode}
	 */
	public RdbmsConnectionPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
		addHelpTopicId(this, "session.login.database.connection");
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Platform Class property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildDatabasePlatformHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.PLATFORM_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				String platformClass = login.getPlatformClass();

				if (platformClass == null)
					return null;

				try
				{
					return DatabasePlatformRepository.getDefault().platformForRuntimePlatformClassNamed(platformClass);
				}
				catch (IllegalArgumentException e)
				{
					return null;
				}
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				DatabasePlatform platform = (DatabasePlatform) value;

				if (platform != null)
				{
					login.setPlatformClass(platform.getRuntimePlatformClassName());
				}
			}
		};
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

		// Platform widgets
		JComponent platformWidgets = buildLabeledComponent
		(
			"CONNECTION_RDBMS_DATABASE_PLATFORM_FIELD",
			PlatformComponentFactory.buildPlatformChooser(buildDatabasePlatformHolder())
		);

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		panel.add(platformWidgets, constraints);
		addHelpTopicId(platformWidgets, "session.login.database.connection.platform");

		// Login pane
		RdbmsLoginPane loginPane = new RdbmsLoginPane(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(loginPane, constraints);
		addPaneForAlignment(loginPane);

		// External Connection Pooling check box
		CustomizedExternalOptionsPane optionsPane = new CustomizedExternalOptionsPane();

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(optionsPane, constraints);

		return panel;
	}

	/**
	 * Creates the <code>PropertyChangeListener</code> to keep the parent object
	 * of the {@link PseudoPreallocationSizeModel} in sync with the selection
	 * object.
	 *
	 * @return A new <code>PropertyChangeListener</code>
	 */
	private PropertyChangeListener buildSelectionHolderListener ()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				pseudoModel.setParentNode((AbstractNodeModel) e.getNewValue());
			}
		};
	}

	/**
	 * Initializes this <code>RdbmsConnectionPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of the subject, which is <code>DatabaseLoginAdapter</code>
	 */
	protected void initialize(PropertyValueModel nodeHolder)
	{
		super.initialize(nodeHolder);

		getSelectionHolder().addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectionHolderListener());

		pseudoModel = new PseudoExternalConnectionPoolingModel();
		pseudoModel.setParentNode((AbstractNodeModel) selection());
	}

	/**
	 * This customized <code>LoginExternalOptionsPane</code> disables and selects
	 * the External Transaction Pooling check box if the Database Driver is J2EE
	 * Data Source;
	 */
	private class CustomizedExternalOptionsPane extends LoginExternalOptionsPane
	{
		/**
		 * Creates a new <code>CustomizedExternalOptionsPane</code>.
		 */
		protected CustomizedExternalOptionsPane()
		{
			super(getSelectionHolder(),
					RdbmsConnectionPropertiesPage.this.getApplicationContext());
		}

		/**
		 * Creates the <code>JCheckBox</code> for the property External Connection
		 * Pooling.
		 *
		 * @return A new <code>JCheckBox</code>
		 */
		protected JCheckBox buildExternalConnectionPoolingCheckBox()
		{
			JCheckBox checkBox = super.buildExternalConnectionPoolingCheckBox();
			buildExternalConnectionPoolingEnabler(checkBox);
			return checkBox;
		}

		/**
		 * Creates the <code>ComponentEnabler</code> that will keep the enable state
		 * of the components contained in the given collection in sync with the
		 * boolean value calculated by the enabled state holder.
		 *
		 * @param checkBox The External Connection Pooling check box
		 * @return A new <code>ComponentEnabler</code>
		 */
		private ComponentEnabler buildExternalConnectionPoolingEnabler(JCheckBox checkBox)
		{
			return new ComponentEnabler(buildExternalConnectionPoolingEnableStateHolder(),
												 Collections.singleton(checkBox));
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to listen to
		 * changes made in the model that will determine if the check box needs to
		 * be enabled or not.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildExternalConnectionPoolingEnableStateHolder()
		{
			return new PropertyAspectAdapter(PseudoExternalConnectionPoolingModel.ENABLED_PROPERTY, pseudoModel)
			{
				protected Object getValueFromSubject()
				{
					PseudoExternalConnectionPoolingModel model = (PseudoExternalConnectionPoolingModel) subject;
					return Boolean.valueOf(model.isEnabled());
				}
			};
		}
	}

	/**
	 * A pseudo model merging two properties into a single property. Basically,
	 * the enable state of the External Connection Pooling is dedictated by the
	 * Database Driver type and the External Transaction Controller.
	 */
	private class PseudoExternalConnectionPoolingModel extends AbstractNodeModel
	{
		private boolean enabled;
		private PropertyChangeListener listener;
		private PropertyChangeListener managedListener;
		private PropertyChangeListener serverPlatformListener;
		private SessionBrokerAdapter sessionBroker;

		public static final String ENABLED_PROPERTY = "enabled";

		private PropertyChangeListener buildManagedPropertyChangeListener()
		{
			return new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					DatabaseLoginAdapter login = (DatabaseLoginAdapter) getParent();
					SessionAdapter session = (SessionAdapter) login.getParent();

					// Disengage the listener for the Server Platform that was
					// registered on the broker's Server Platform
					if (sessionBroker != null)
					{
						ServerPlatformAdapter serverPlatform = sessionBroker.getServerPlatform();
						serverPlatform.removePropertyChangeListener(ServerPlatformAdapter.ENABLE_JTA_PROPERTY, listener);
					}

					// Keep track of the broker
					DatabaseSessionAdapter databaseSession = (DatabaseSessionAdapter) session;
					sessionBroker = databaseSession.getBroker();

					// Reinstall the listener on the Server Platform
					ServerPlatformAdapter serverPlatform = session.getServerPlatform();
					serverPlatform.addPropertyChangeListener(ServerPlatformAdapter.ENABLE_JTA_PROPERTY, listener);

					updateEnableState();
				}
			};
		}

		private PropertyChangeListener buildPropertyChangeListener()
		{
			return new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					updateEnableState();
				}
			};
		}

		private PropertyChangeListener buildServerPlatformPropertyChangeListener()
		{
			return new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					ServerPlatformAdapter oldServerPlatform = (ServerPlatformAdapter) e.getOldValue();
					oldServerPlatform.removePropertyChangeListener(ServerPlatformAdapter.ENABLE_JTA_PROPERTY, listener);

					ServerPlatformAdapter newServerPlatform = (ServerPlatformAdapter) e.getNewValue();
					newServerPlatform.addPropertyChangeListener(ServerPlatformAdapter.ENABLE_JTA_PROPERTY, listener);

					updateEnableState();
				}
			};
		}

		protected void checkParent(Node parent)
		{
			// The parent is set/unset dynamically
		}

		private void disengageListeners()
		{
			// Database Driver: disable when it's J2EE Data Source
			DatabaseLoginAdapter login = (DatabaseLoginAdapter) getParent();
			login.removePropertyChangeListener(DatabaseLoginAdapter.USE_DRIVER_MANAGER_PROPERTY, listener);

			// Server Platform
			SessionAdapter session = (SessionAdapter) login.getParent();
			session.removePropertyChangeListener(SessionAdapter.SERVER_PLATFORM_CONFIG_PROPERTY, serverPlatformListener);

			// External Transaction Controller (JTA): disable if there is an entry
			ServerPlatformAdapter serverPlatform;

			if (sessionBroker != null)
				serverPlatform = sessionBroker.getServerPlatform();
			else
				serverPlatform = session.getServerPlatform();

			serverPlatform.removePropertyChangeListener(ServerPlatformAdapter.ENABLE_JTA_PROPERTY, listener);

			// Managed session, needed to keep track of the session broker
			if (!session.isBroker())
			{
				DatabaseSessionAdapter databaseSession = (DatabaseSessionAdapter) session;
				databaseSession.removePropertyChangeListener(DatabaseSessionAdapter.MANAGED_BY_BROKER, managedListener);

				sessionBroker = null;
			}
		}

		public String displayString()
		{
			return null;
		}

		private void engageListeners()
		{
			// Database Driver: disable when it's J2EE Data Source
			DatabaseLoginAdapter login = (DatabaseLoginAdapter) getParent();
			login.addPropertyChangeListener(DatabaseLoginAdapter.USE_DRIVER_MANAGER_PROPERTY, listener);

			// Server Platform
			SessionAdapter session = (SessionAdapter) login.getParent();
			session.addPropertyChangeListener(SessionAdapter.SERVER_PLATFORM_CONFIG_PROPERTY, serverPlatformListener);

			// External Transaction Controller: disable if there is an entry
			ServerPlatformAdapter serverPlatform = session.getServerPlatform();
			serverPlatform.addPropertyChangeListener(ServerPlatformAdapter.ENABLE_JTA_PROPERTY, listener);

			// Managed session, need to keep track of the session broker
			if (!session.isBroker())
			{
				DatabaseSessionAdapter databaseSession = (DatabaseSessionAdapter) session;
				databaseSession.addPropertyChangeListener(DatabaseSessionAdapter.MANAGED_BY_BROKER, managedListener);

				sessionBroker = databaseSession.getBroker();
			}
		}

		/**
		 * Initializes this pseudo model.
		 */
		protected void initialize()
		{
			super.initialize();

			listener = buildPropertyChangeListener();
			managedListener = buildManagedPropertyChangeListener();
			serverPlatformListener = buildServerPlatformPropertyChangeListener();
		}

		public boolean isEnabled()
		{
			return enabled;
		}

		public void setEnabled(boolean enabled)
		{
			boolean oldEnabled = isEnabled();
			this.enabled = enabled;
			firePropertyChanged(ENABLED_PROPERTY, oldEnabled, enabled);
		}

		public void setParentNode(Node parentNode)
		{
			if (getParent() != null)
			{
				disengageListeners();
			}

			super.setParent(parentNode);

			if (parentNode != null)
			{
				engageListeners();
				updateEnableState();
			}
		}

		/**
		 * Updates the enable state based on certain criteras.
		 */
		private void updateEnableState()
		{
			DatabaseLoginAdapter login = (DatabaseLoginAdapter) getParent();
			DatabaseSessionAdapter session = (DatabaseSessionAdapter) login.getParent();
			boolean enabled = login.databaseDriverIsDriverManager() && !session.hasJTA();

			if (session.isManaged() && session.getBroker().hasJTA())
			{
				enabled = false;
			}

			setEnabled(enabled);
		}
	}
}
