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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMIIIOPTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMITransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RemoteCommandManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SunCORBATransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.UserDefinedTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ComboBoxSelection;
import org.eclipse.persistence.tools.workbench.uitools.ComponentVisibilityEnabler;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.Transformer;

// Mapping Workbench

/**
 * This is a sub-pane of {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.SessionClusteringPropertiesPage
 * SessionClusteringPropertiesPage} and it is shown when the Clustering Type is
 * Remote Command.
 * <p>
 * Here the layout:<pre>
 * ______________________________________
 * |          _________________________ |  ________________
 * | Type:    |                     |v| |<-| CORBA        |
 * |          ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |  | JMS          |
 * |          _________________________ |  | RMI          |
 * | Channel: | I                     | |  | RMI/IIOP     |
 * |          ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |  | User Defined |
 * |                                    |  ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * | x Remove Connection on Error       |<- This check box is placed within the
 * |                                    |   Transport Manager pane for better
 * | -Options-------------------------- |   layout
 * |                                    |
 * |  {@link RCMJMSPane}                        |
 * |   or                               |
 * |  {@link RCMRMIPane}                        |
 * |   or                               |
 * |  {@link RCMUserDefinedPane}                |
 * |                                    |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * </pre>
 *
 * @see RemoteCommandManagerAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class RemoteCommandManagerPane extends AbstractSubjectPanel
{
	/**
	 * The pane containing JMS Topic specific information.
	 */
	private RCMJMSPane jmsPane;

	/**
	 * The pane containing RMI/IIOP Topic specific information.
	 */
	private RCMRMIPane rmiIIOPPane;

	/**
	 * The pane containing RMI Topic specific information.
	 */
	private RCMRMIPane rmiPane;

	/**
	 * The pane containing sun CORBA specific information.
	 */
	private RCMSunCORBAPane sunCORBAPane;
	
	/**
	 * The pane containing User Defined specific information.
	 */
	private RCMUserDefinedPane userDefinedPane;

	/**
	 * Creates a new <code>CacheSynchronizationManagerPane</code>.
	 *
	 * @param subjectHolder The holder of {@link RemoteCommandManagerAdapter}
	 * @param context
	 */
	RemoteCommandManagerPane(ValueModel subjectHolder,
									 WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates a new <code>ComponentVisibilityEnabler</code> that is responsible
	 * to keep the visible state of the given list of <code>Component</code>s in
	 * sync with the type of {@link TransportManagerAdapter} used.
	 *
	 * @param channelWidget The widget used for editing the Channel property
	 * @return A new <code>ComponentVisibilityEnabler</code>
	 */
	private ComponentVisibilityEnabler buildChannelComponentVisibilityUpdater(Component channelWidget)
	{
		return new ComponentVisibilityEnabler(buildChannelVisibilityHolder(),
														  Collections.singleton(channelWidget));
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the value in the model and vice versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildChannelDocumentAdapter()
	{
		return new DocumentAdapter(buildChannelHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Channel property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildChannelHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.CHANNEL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
				return adapter.getChannel();
			}

			protected void setValueOnSubject(Object value)
			{
				RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
				adapter.setChannel((String) value);
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * boolean holder used by the <code>ComponentVisibilityEnabler</code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildChannelVisibilityHolder()
	{
		return new TransformationPropertyValueModel(buildTransportManagerHolder())
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(! (value instanceof JMSTopicTransportManagerAdapter));
			}
		};
	}

	/**
	 * Creates the <code>SwitcherPanel</code> ...
	 *
	 * @return A new <code>SwitcherPanel</code>
	 */
	private SwitcherPanel buildClusteringSwitcherPanel(ComboBoxSelectionManager manager)
	{
		return new SwitcherPanel(buildTransportManagerHolder(),
										 buildTransportManagerTransformer(manager));
	}

	/**
	 * Creates the subject holder where it is two children down from the value
	 * contained in the given node holder.
	 *
	 * @return The <code>PropertyValueModel</code> containing the subject holder
	 * required by {@link RCMJMSPane}
	 */
	private PropertyValueModel buildJMSTransportManagerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.TRANSPORT_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
				TransportManagerAdapter transportManager = adapter.getTransportManager();

				if (transportManager instanceof JMSTopicTransportManagerAdapter)
					return transportManager;

				return null;
			}
		};
	}

	/**
	 * Creates the subject holder where it is two children down from the value
	 * contained in the given node holder.
	 *
	 * @return The <code>PropertyValueModel</code> containing the subject holder
	 * required by {@link RCMRMIPane}
	 */
	private PropertyValueModel buildRMIIIOPTransportManagerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.TRANSPORT_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter rcm = (RemoteCommandManagerAdapter) subject;
				TransportManagerAdapter transportManager = rcm.getTransportManager();

				if (transportManager.getClass().equals(RMIIIOPTransportManagerAdapter.class))
					return transportManager;

				return null;
			}
		};
	}

	/**
	 * Creates the subject holder where it is two children down from the value
	 * contained in the given node holder.
	 *
	 * @return The <code>PropertyValueModel</code> containing the subject holder
	 * required for this pane
	 */
	private PropertyValueModel buildRMITransportManagerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.TRANSPORT_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter rcm = (RemoteCommandManagerAdapter) subject;
				TransportManagerAdapter transportManager = rcm.getTransportManager();

				if (transportManager.getClass().equals(RMITransportManagerAdapter.class))
					return transportManager;

				return null;
			}
		};
	}

	/**
	 * Initializes all the sub-panes.
	 */
	private void buildSubPanes()
	{
		jmsPane = new RCMJMSPane(buildJMSTransportManagerHolder(), getWorkbenchContextHolder());
		addPaneForAlignment(jmsPane);

		rmiPane = new RCMRMIPane(buildRMITransportManagerHolder(), getWorkbenchContextHolder());
		addPaneForAlignment(rmiPane);

		rmiIIOPPane = new RCMRMIPane(buildRMIIIOPTransportManagerHolder(), getWorkbenchContextHolder());
		addPaneForAlignment(rmiIIOPPane);

		sunCORBAPane = new RCMSunCORBAPane(buildSunCORBATransportManagerHolder(), getWorkbenchContextHolder());
		addPaneForAlignment(jmsPane);
		
		userDefinedPane = new RCMUserDefinedPane(buildUserDefinedTransportManagerHolder(), getWorkbenchContextHolder());
		addPaneForAlignment(userDefinedPane);
	}

	/**
	 * Creates the subject holder where it is two children down from the value
	 * contained in the given node holder.
	 *
	 * @return The <code>PropertyValueModel</code> containing the subject holder
	 * required by {@link RCMSunCORBAPane}
	 */
	private PropertyValueModel buildSunCORBATransportManagerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.TRANSPORT_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
				TransportManagerAdapter transportManager = adapter.getTransportManager();

				if (transportManager instanceof SunCORBATransportManagerAdapter)
					return transportManager;

				return null;
			}
		};
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the Type combo box.
	 *
	 * @param manager This manager is containing all the items to be contained
	 * in the <code>CollectionValueModel</code> required by the ComboBoxAdapter
	 * <code>ComboBoxModelAdapter</code>
	 * @return A new <code>PropertyValueModel</code>
	 */
	private CollectionValueModel buildTransportManagerCollectionHolder(final ComboBoxSelectionManager manager)
	{
		return new AbstractReadOnlyCollectionValueModel()
		{
			public Object getValue()
			{
				return manager.choices();
			}

			public int size()
			{
				return manager.size();
			}
		};
	}

	/**
	 * Creates the <code>ComboBoxModelAdapter</code> that keeps the selected
	 * item from the combo box in sync with the value in the model and vice
	 * versa.
	 * 
	 * @param manager This manager is containing all the items to be contained
	 * in the <code>CollectionValueModel</code> required by the
	 * <code>ComboBoxModelAdapter</code>
	 * @return A new <code>ComboBoxModelAdapter</code>
	 */
	private ComboBoxModel buildTransportManagerComboBoxAdapter(final ComboBoxSelectionManager manager)
	{
		return new ComboBoxModelAdapter(buildTransportManagerCollectionHolder(manager),
												  buildTransportManagerSelectionHolder(manager));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> that will be the value
	 * holder of the <code>TransformationPropertyValueModel</code> given to the
	 * <code>PanelSwitcherAdapter</code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTransportManagerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.TRANSPORT_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter remoteCommandManager = (RemoteCommandManagerAdapter) subject;
				return remoteCommandManager.getTransportManager();
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Transport Manager property. The value returned is not the actual
	 * Transport Manager but a <code>ComboBoxSelection</code> which is one of the
	 * items of the combo box.
	 *
	 * @param manager This manager responsible for retrieving the proper
	 * <code>ComboBoxSelection</code> for a <code>TransportManager</code>
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTransportManagerSelectionHolder(final ComboBoxSelectionManager manager)
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.TRANSPORT_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
				return manager.retrieveComboBoxSelection(adapter.getTransportManager());
			}

			protected void setValueOnSubject(Object value)
			{
				ComboBoxSelection selection = (ComboBoxSelection) value;
				selection.setPropertyOn(subject);
			}
		};
	}

	/**
	 * Creates the ...
	 * 
	 * @param manager The manager responsible to retrieve the proper ...
	 * @return The <code>Transformer</code> responsible to return a <code>JComponent</code>
	 * for a value
	 */
	private Transformer buildTransportManagerTransformer(final ComboBoxSelectionManager manager)
	{
		return new Transformer()
		{
			public Object transform(Object value)
			{
				if (value == null)
					return null;

				return manager.retrieveConverter(value).transform(value);
			}
		};
	}

	/**
	 * Creates the subject holder where it is two children down from the value
	 * contained in the given node holder.
	 *
	 * @return The <code>PropertyValueModel</code> containing the subject holder
	 * required for this pane
	 */
	private PropertyValueModel buildUserDefinedTransportManagerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RemoteCommandManagerAdapter.TRANSPORT_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
				TransportManagerAdapter transportManager = adapter.getTransportManager();

				if (transportManager instanceof UserDefinedTransportManagerAdapter)
					return transportManager;

				return null;
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		ComboBoxSelectionManager manager = new ComboBoxSelectionManager();

		buildSubPanes();

		// Type widgets
		Component typeWidgets = buildLabeledComboBox
		(
			"CLUSTERING_TYPE_COMBO_BOX",
			buildTransportManagerComboBoxAdapter(manager)
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(typeWidgets, constraints);
		addHelpTopicId(typeWidgets, "session.clustering.rcm");

		// Channel label
		JComponent channelWidgets = buildLabeledTextField
		(
			"CLUSTERING_CHANNEL_FIELD",
			buildChannelDocumentAdapter()
		);
		channelWidgets.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(channelWidgets, constraints);
		buildChannelComponentVisibilityUpdater(channelWidgets);
		addHelpTopicId(channelWidgets, "session.clustering.rcm.channel");

		// Options pane container
		SwitcherPanel transportManagerPaneContainer = buildClusteringSwitcherPanel(manager);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(transportManagerPaneContainer, constraints);

		addHelpTopicId(this, "session.clustering.rcm");
	}

	/**
	 * This manager is responsible to return the associated <code>ComboBoxSelection</code>
	 * or <code>ObjectToComponentConverter</code> based on
	 * the <code>RemoteCommandManager</code> passed to it.
	 */
	private class ComboBoxSelectionManager
	{
		/**
		 * The table of registered choices shown in the Type combo box.
		 */
		private Hashtable selections;

		/**
		 * Creates a new <code>ComboBoxSelectionManager</code>.
		 */
		private ComboBoxSelectionManager()
		{
			super();
			initialize();
		}

		/**
		 * Returns the <code>Iterator</code> over the <code>ComboBoxSelection</code>.
		 *
		 * @return The <code>Iterator</code> over the <code>ComboBoxSelection</code>
		 */
		public Iterator choices()
		{
			List sortedChoices = (List) selections.get("sorted-choices");
			return sortedChoices.iterator();
		}

		/**
		 * Initializes this <code>ComboBoxSelectionManager</code>.
		 */
		private void initialize()
		{
			selections = new Hashtable();
			selections.put(SunCORBATransportManagerAdapter.class,    new SunCORBAChoice());
			selections.put(JMSTopicTransportManagerAdapter.class,    new JMSChoice());
			selections.put(RMITransportManagerAdapter.class,         new RMIChoice());
			selections.put(RMIIIOPTransportManagerAdapter.class,     new RMIIIOPChoice());
			selections.put(UserDefinedTransportManagerAdapter.class, new UserDefinedChoice());

			sortChoices();
		}

		/**
		 * Retrieves the <code>ComboBoxSelection</code> that is wrapping the type
		 * value's class.
		 *
		 * @param value An instance of <code>RemoteCommandManager</code>
		 * @return The associated <code>ComboBoxSelection</code>
		 */
		public ComboBoxSelection retrieveComboBoxSelection(Object value)
		{
			return (ComboBoxSelection) selections.get(value.getClass());
		}

		/**
		 * Retrieves the <code>ObjectToComponentConverter</code> that is wrapping the type
		 * value's class.
		 *
		 * @param value An instance of <code>RemoteCommandManager</code>
		 * @return The associated <code>ObjectToComponentConverter</code>
		 */
		public Transformer retrieveConverter(Object value)
		{
			return (Transformer) selections.get(value.getClass());
		}

		/**
		 * Returns the count of choices this manager handles.
		 *
		 * @return The number of choice
		 */
		public int size()
		{
			return selections.size() - 1; // -1 for the cached sorted list
		}

		/**
		 * Sorts the keys in an alphabetical order based on the display string.
		 */
		private void sortChoices()
		{
			Vector choicesValues = new Vector(selections.values());
			Vector choices = new Vector(choicesValues.size());
			Set choicesSet = selections.entrySet();

			Collections.sort(choicesValues);

			for (int index = choicesValues.size(); --index >=0; )
			{
				Object value = choicesValues.get(index);

				for (Iterator iter = choicesSet.iterator(); iter.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) iter.next();

					if (entry.getValue() == value)
					{
						choices.add(0, entry.getValue());
						break;
					}
				}
			}

			selections.put("sorted-choices", choices);
		}
	}

	/**
	 * This is one of the choice for Transport Manager type.
	 */
	private class JMSChoice extends ComboBoxSelection
									implements Transformer
	{
		/**
		 * Creates a new <code>JMSChoice</code>.
		 */
		private JMSChoice()
		{
			super(resourceRepository().getString("JMS"));
		}

		/**
		 * Updates the Transport Manager in the {@link RemoteCommandManagerAdapter}
		 * to be {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
			adapter.setTransportAsJMSTopic();
		}

		/**
		 * Based on the given object, requests the associated component.
		 *
		 * @param value The value used to retrieve a pane
		 * @return The pane associated with the given value
		 */
		public Object transform(Object value)
		{
			return jmsPane;
		}
	}

	/**
	 * This is one of the choice for Transport Manager type.
	 */
	private class RMIChoice extends ComboBoxSelection
											  implements Transformer
	{
		/**
		 * Creates a new <code>RMIChoice</code>.
		 */
		private RMIChoice()
		{
			super(resourceRepository().getString("RMI"));
		}

		/**
		 * Updates the Transport Manager in the {@link RemoteCommandManagerAdapter}
		 * to be {@link RMITransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
			adapter.setTransportAsRMI();
		}

		/**
		 * Based on the given object, requests the associated component.
		 *
		 * @param value The value used to retrieve a pane
		 * @return The pane associated with the given value
		 */
		public Object transform(Object value)
		{
			return rmiPane;
		}
	}

	/**
	 * This is one of the choice for Transport Manager type.
	 */
	private class RMIIIOPChoice extends ComboBoxSelection
										  implements Transformer
	{
		/**
		 * Creates a new <code>UserDefinedChoice</code>.
		 */
		private RMIIIOPChoice()
		{
			super(resourceRepository().getString("RMI/IIOP"));
		}

		/**
		 * Updates the Transport Manager in the {@link RemoteCommandManagerAdapter}
		 * to be {@link RMIIIOPTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
			adapter.setTransportAsRMIIIOP();
		}

		/**
		 * Based on the given object, requests the associated component.
		 *
		 * @param value The value used to retrieve a pane
		 * @return The pane associated with the given value
		 */
		public Object transform(Object value)
		{
			return rmiIIOPPane;
		}
	}

	/**
	 * This is one of the choice for Transport Manager type.
	 */
	private class SunCORBAChoice extends ComboBoxSelection
										  implements Transformer
	{
		/**
		 * Creates a new <code>SunCORBAChoice</code>.
		 */
		private SunCORBAChoice()
		{
			super(resourceRepository().getString("CORBA"));
		}

		/**
		 * Updates the Transport Manager in the {@link RemoteCommandManagerAdapter}
		 * to be {@link CORBATransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
			adapter.setTransportAsSunCORBA();
		}

		/**
		 * Based on the given object, requests the associated component.
		 *
		 * @param value The value used to retrieve a pane
		 * @return The pane associated with the given value
		 */
		public Object transform(Object value)
		{
			return sunCORBAPane;
		}
	}

	/**
	 * This is one of the choice for Transport Manager type.
	 */
	private class UserDefinedChoice extends ComboBoxSelection
											  implements Transformer
	{
		/**
		 * Creates a new <code>UserDefinedChoice</code>.
		 */
		private UserDefinedChoice()
		{
			super(resourceRepository().getString("USER_DEFINED"));
		}

		/**
		 * Updates the Transport Manager in the {@link RemoteCommandManagerAdapter}
		 * to be {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			RemoteCommandManagerAdapter adapter = (RemoteCommandManagerAdapter) subject;
			adapter.setTransportAsUserDefined();
		}

		/**
		 * Based on the given object, requests the associated component.
		 *
		 * @param value The value used to retrieve a pane
		 * @return The pane associated with the given value
		 */
		public Object transform(Object value)
		{
			return userDefinedPane;
		}
	}
}
