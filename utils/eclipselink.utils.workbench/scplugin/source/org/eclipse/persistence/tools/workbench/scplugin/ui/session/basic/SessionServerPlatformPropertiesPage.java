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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.ServerPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.CustomServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NullServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.ComponentVisibilityEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

// TopLink
import org.eclipse.persistence.platform.server.CustomServerPlatform;

// Mapping Workbench

/**
 * @version 10.1.3
 * @author Pascal Filion
 */
public class SessionServerPlatformPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>SessionServerPlatformPropertiesPage</code>.
	 * 
	 * @param nodeHolder The holder of {@link SessionAdapter}
	 * @param contextHolder The holder of <code>WorkbenchContext</code>
	 */
	public SessionServerPlatformPropertiesPage(PropertyValueModel nodeHolder,
															 WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
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

		// Server Platform pane
		JCheckBox useServerPlatformCheckBox = buildCheckBox
		(
			"SERVER_PLATFORM_CHECK_BOX",
			buildUseServerPlatformButtonModel()
		);
		useServerPlatformCheckBox.addActionListener(buildServerPlatformAction());

		ServerPlatformPane subPane = new ServerPlatformPane(buildServerPlatformPaneSubjectHolder());
		new ComponentEnabler(buildServerPlatformBooleanHolder(), subPane.getComponents());

		GroupBox groupBox = new GroupBox(useServerPlatformCheckBox, subPane);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(groupBox, constraints);

		addHelpTopicId(container, "session.serverPlatform");
		return container;
	}

	/**
	 * Creates the <code>ActionListener</code> responsible to change the Server
	 * Platform from <code>NoServerPlatform</code> to the default retrieved from
	 * the preferences.
	 *
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildServerPlatformAction()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SessionAdapter session = (SessionAdapter) selection();
				boolean checked = ((JCheckBox) e.getSource()).isSelected();
				String serverClassName;

				String noServerPlatformClassName = NullServerPlatformAdapter.instance().getServerClassName();
				noServerPlatformClassName = ClassTools.shortNameForClassNamed(noServerPlatformClassName);

				if (!checked)
				{
					serverClassName = noServerPlatformClassName;
				}
				else
				{
					// Retrieve the default value from the preferences
					serverClassName = preferences().get(SCPlugin.SERVER_PLATFORM_PREFERENCE,
																	SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT);

					// If in the preferences we have NoServerPlatform, then we use
					// the default value
					if (noServerPlatformClassName.equals(serverClassName))
					{
						serverClassName = SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT;
					}
				}

				session.setServerPlatform(new ServerPlatform(serverClassName));
			}
		};
	}

	private PropertyValueModel buildServerPlatformBooleanHolder()
	{
		return new TransformationPropertyValueModel(buildServerPlatformHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) value;
				return Boolean.valueOf(! serverPlatform.isNull());
			}
		};
	}

	private PropertyValueModel buildServerPlatformHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.SERVER_PLATFORM_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter session = (SessionAdapter) subject;
				return session.getServerPlatform();
			}

			protected void setValueOnSubject(Object value)
			{
				// It has to be done through an ActionListener since this method
				// won't work properly with a Boolean value coming from the check
				// box nor when this pane is populated, this method is called, which
				// should not!
			}
		};
	}

	private PropertyValueModel buildServerPlatformPaneSubjectHolder()
	{
		return new TransformationPropertyValueModel(buildServerPlatformHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) value;
				return serverPlatform.isNull() ? null : value;
			}
		};
	}

	private ButtonModel buildUseServerPlatformButtonModel()
	{
		return new CheckBoxModelAdapter(buildUseServerPlatformHolder());
	}

	private PropertyValueModel buildUseServerPlatformHolder()
	{
		return new TransformationPropertyValueModel(buildServerPlatformHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) value;
				return Boolean.valueOf(! serverPlatform.isNull());
			}
		};
	}

	/**
	 * 
	 */
	private class CustomServerPlatformSubPane extends AbstractSubjectPanel
	{
		/**
		 * Creates a new <code>CustomServerPlatformSubPane</code>.
		 */
		private CustomServerPlatformSubPane(PropertyValueModel subjectHolder)
		{
			super(subjectHolder,
					SessionServerPlatformPropertiesPage.this.getWorkbenchContextHolder());
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to return the
		 * Class Repository.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private ValueModel buildClassRepositoryHolder()
		{
			return new PropertyAspectAdapter(getSelectionHolder())
			{
				protected Object getValueFromSubject()
				{
					SCAdapter adapter = (SCAdapter) subject;
					return adapter.getClassRepository();
				}
			};
		}

		/**
		 * Creates a Browse button that will take care to show the class chooser.
		 *
		 * @return A new <code>JButton</code>
		 */
		private JButton buildExternalTransactionControllerBrowseButton()
		{
			return ClassChooserTools.buildBrowseButton
			(
				getWorkbenchContextHolder(),
				"SERVER_PLATFORM_CUSTOM_EXTERNAL_TRANSACTION_CONTROLLER_BROWSE_BUTTON",
				buildClassRepositoryHolder(),
				buildExternalTransactionControllerHolder()
			);
		}

		private Document buildExternalTransactionControllerDocument()
		{
			return new DocumentAdapter(buildExternalTransactionControllerHolder());
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * External Transaction Controller property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildExternalTransactionControllerHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), CustomServerPlatformAdapter.EXTERNAL_TRANSACTION_CONTROLLER_CLASS_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					CustomServerPlatformAdapter customServerPlatform = (CustomServerPlatformAdapter) subject;
					return customServerPlatform.getExternalTransactionControllerClass();
				}

				protected void setValueOnSubject(Object value)
				{
					CustomServerPlatformAdapter customServerPlatform = (CustomServerPlatformAdapter) subject;
					customServerPlatform.setExternalTransactionControllerClass((String) value);
				}
			};
		}

		/**
		 * Creates a Browse button that will take care to show the class chooser.
		 *
		 * @return A new <code>JButton</code>
		 */
		private JButton buildServerClassBrowseButton()
		{
			return ClassChooserTools.buildBrowseButton
			(
				getWorkbenchContextHolder(),
				"SERVER_PLATFORM_CUSTOM_SERVER_CLASS_BROWSE_BUTTON",
				buildClassRepositoryHolder(),
				buildServerClassHolder()
			);
		}

		private Document buildServerClassDocument()
		{
			return new DocumentAdapter(buildServerClassHolder());
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * Exception Handler property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildServerClassHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), CustomServerPlatformAdapter.SERVER_CLASS_NAME_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					CustomServerPlatformAdapter customServerPlatform = (CustomServerPlatformAdapter) subject;
					return customServerPlatform.getServerClassName();
				}

				protected void setValueOnSubject(Object value)
				{
					CustomServerPlatformAdapter customServerPlatform = (CustomServerPlatformAdapter) subject;
					customServerPlatform.setServerClassName((String) value);
				}
			};
		}

		/**
		 * Initializes the layout of this pane.
		 */
		protected void initializeLayout()
		{
			GridBagConstraints constraints = new GridBagConstraints();

			// Server Class widgets
			JComponent serverPlatformWidgets = buildLabeledTextField
			(
				"SERVER_PLATFORM_CUSTOM_SERVER_CLASS_CHOOSER",
				buildServerClassDocument(),
				buildServerClassBrowseButton()
			);

			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(0, 0, 0, 0);

			add(serverPlatformWidgets, constraints);

			// External Transaction Controller widgets
			JComponent externalTransactionControllerWidgets = buildLabeledTextField
			(
				"SERVER_PLATFORM_CUSTOM_EXTERNAL_TRANSACTION_CONTROLLER_CHOOSER",
				buildExternalTransactionControllerDocument(),
				buildExternalTransactionControllerBrowseButton()
			);

			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(5, 0, 0, 0);

			add(externalTransactionControllerWidgets, constraints);
		}
	}

	private class ServerPlatformPane extends AbstractSubjectPanel
	{
		private ServerPlatformPane(PropertyValueModel subjectHolder)
		{
			super(subjectHolder,
					SessionServerPlatformPropertiesPage.this.getWorkbenchContextHolder());
		}

		/**
		 * Creates a new <code>ComponentVisibilityEnabler</code> that is responsible
		 * to keep the visible state of the given list of <code>Component</code>s in
		 * sync with the type of {@link TransportManagerAdapter} used.
		 *
		 * @param channelWidget The widget used for editing the Channel property
		 * @return A new <code>ComponentVisibilityEnabler</code>
		 */
		private ComponentVisibilityEnabler buildCustomServerPlatformComponentVisibilityUpdater(Component channelWidget)
		{
			return new ComponentVisibilityEnabler(buildCustomServerPlatformVisibilityHolder(),
															  Collections.singleton(channelWidget));
		}

		private PropertyValueModel buildCustomServerPlatformHolder()
		{
			return new TransformationPropertyValueModel(buildServerPlatformHolder())
			{
				protected Object transform(Object value)
				{
					if (value == null)
						return null;

					ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) value;
					return serverPlatform.isCustom() ? value : null;
				}
			};
		}

		private JComponent buildCustomServerPlatformSubPane()
		{
			return new CustomServerPlatformSubPane(buildCustomServerPlatformHolder());
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * boolean holder used by the <code>ComponentVisibilityEnabler</code>.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildCustomServerPlatformVisibilityHolder()
		{
			return new TransformationPropertyValueModel(buildServerPlatformHolder())
			{
				protected Object transform(Object value)
				{
					if (value == null)
						return null;

					ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) value;
					return Boolean.valueOf(serverPlatform.isCustom());
				}
			};
		}

		private ButtonModel buildEnableExternalTransactionControllerButtonModel()
		{
			return new CheckBoxModelAdapter(buildEnableExternalTransactionControllerHolder());
		}

		private PropertyValueModel buildEnableExternalTransactionControllerHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), ServerPlatformAdapter.ENABLE_JTA_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) subject;
					return Boolean.valueOf(serverPlatform.getEnableJTA());
				}

				protected void setValueOnSubject(Object value)
				{
					ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) subject;
					serverPlatform.setEnableJTA(Boolean.TRUE.equals(value));
				}
			};
		}

		private ButtonModel buildEnableRuntimeServicesButtonModel()
		{
			return new CheckBoxModelAdapter(buildEnableRuntimeServicesHolder());
		}

		private PropertyValueModel buildEnableRuntimeServicesHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), ServerPlatformAdapter.ENABLE_RUNTIME_SERVICES_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) subject;
					return Boolean.valueOf(serverPlatform.getEnableRuntimeServices());
				}

				protected void setValueOnSubject(Object value)
				{
					ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) subject;
					serverPlatform.setEnableRuntimeServices(Boolean.TRUE.equals(value));
				}
			};
		}

		private CollectionValueModel buildServerPlatformCollectionHolder()
		{
			return new SimpleCollectionValueModel(buildServerPlatformNames());
		}

		private ComboBoxModel buildServerPlatformComboBoxModel()
		{
			return new ComboBoxModelAdapter(buildServerPlatformCollectionHolder(),
													  buildServerPlatformSelectionHolder());
		}

		private Collection buildServerPlatformNames()
		{
			Collection servers = CollectionTools.sortedSet(ServerPlatformManager.instance().platformShortNames());
			String serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
			servers.remove(ClassTools.shortNameForClassNamed(serverClassName));
			return servers;
		}

		private ListCellRenderer buildServerPlatformRenderer()
		{
			return new SimpleListCellRenderer()
			{
				protected String buildText(Object value)
				{
					if (value == null)
						return "";

					return resourceRepository().getString((String) value);
				}
			};
		}

		private PropertyValueModel buildServerPlatformSelectionHolder()
		{
			PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.SERVER_PLATFORM_CONFIG_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					SessionAdapter session = (SessionAdapter) subject;
					return session.getServerPlatform();
				}

				protected void setValueOnSubject(Object value)
				{
					SessionAdapter session = (SessionAdapter) subject;
					ServerPlatformAdapter serverPlatform = session.getServerPlatform();
					String shortClassName = (String) value;
					String serverClassName;

					if (serverPlatform.isCustom())
					{
						serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
					}
					else
					{
						serverClassName = serverPlatform.getServerClassName();
					}

					serverClassName = ClassTools.shortNameForClassNamed(serverClassName);

					if (!serverClassName.equals(shortClassName))
					{
						session.setServerPlatform(new ServerPlatform(shortClassName));
					}
				}
			};

			return new TransformationPropertyValueModel(adapter)
			{
				protected Object transform(Object value)
				{
					if (value == null)
						return null;

					ServerPlatformAdapter serverPlatform = (ServerPlatformAdapter) value;
					String serverClassName = serverPlatform.getServerClassName();

					if (serverPlatform.isCustom())
						serverClassName = CustomServerPlatform.class.getName();

					if (serverPlatform.isNull())
						return null;

					return ClassTools.shortNameForClassNamed(serverClassName);
				}
			};
		}

		protected void initializeLayout()
		{
			GridBagConstraints constraints = new GridBagConstraints();

			// Platform combo box
			JComponent serverPlatformComboBox = buildLabeledComboBox
			(
				"SERVER_PLATFORM_COMBO_BOX",
				buildServerPlatformComboBoxModel(),
				buildServerPlatformRenderer()
			);

			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(0, 0, 0, 0);

			add(serverPlatformComboBox, constraints);

			// Enable Runtime Services check box
			JComponent enableRuntimeServicesCheckBox = buildCheckBox
			(
				"SERVER_PLATFORM_ENABLE_RUNTIME_SERVICES_CHECK_BOX",
				buildEnableRuntimeServicesButtonModel()
			);

			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(5, 0, 0, 0);

			add(enableRuntimeServicesCheckBox, constraints);

			// Enable External Transaction Controller check box
			JComponent enableExternalTransactionControllerCheckBox = buildCheckBox
			(
				"SERVER_PLATFORM_ENABLE_EXTERNAL_TRANSACTION_CONTROLLER_CHECK_BOX",
				buildEnableExternalTransactionControllerButtonModel()
			);

			constraints.gridx      = 0;
			constraints.gridy      = 2;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(0, 0, 0, 0);

			add(enableExternalTransactionControllerCheckBox, constraints);

			// Custom pane
			JComponent customServerPlatformSubPane = buildCustomServerPlatformSubPane();

			constraints.gridx      = 0;
			constraints.gridy      = 3;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 1;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.PAGE_START;
			constraints.insets     = new Insets(5, 0, 0, 0);

			add(customServerPlatformSubPane, constraints);
			buildCustomServerPlatformComponentVisibilityUpdater(customServerPlatformSubPane);
		}
	}
}
