/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.CacheSynchronizationManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSClusteringAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JNDIClusteringServiceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMIClusteringAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMIIIOPJNDIClusteringAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMIJNDIClusteringAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SunCORBAJNDIClusteringAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ComboBoxSelection;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.Transformer;

// Mapping Workbench

/**
 * This is a sub-pane of {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.SessionClusteringPropertiesPage
 * SessionClusteringPropertiesPage} and it is shown when the Cache
 * Synchronization Manager is selected.
 * <p>
 * Here the layout:<pre>
 * _______________________________________________________
 * |                     _______________________________ |  _________________
 * | Clustering Service: |                           |v| |<-| JMS           |
 * |                     ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |  | RMI           |
 * |                                                     |  | RMI JNDI      |
 * | -ClusteringServicePane----------------------------- |  | RMI IIOP JNDI |
 * | |                                                 | |  | Sun CORBA     |
 * | | JMSClusteringServicePane                        | |  ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * | |  or                                             | |
 * | | RMIClusteringServicePane                        | |
 * | |  or                                             | |
 * | | RMIJNDIClusteringPane                           | |
 * | |  or                                             | |
 * | | RMIIIOPJNDIClusteringPane                       | |
 * | |  or                                             | |
 * | | SunCORBAJNDIClusteringPane                      | |
 * | |                                                 | |
 * | --------------------------------------------------- |
 * |                                                     |
 * | x Asynchronous                                      |
 * |                                                     |
 * | x Remove Connection on Error                        |
 * |                                                     |
 * | _JNDI Naming Service_______________________________ |
 * | |                                                 | |
 * | | {@link JNDINamingServicePane}                   | |
 * | |                                                 | |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see CacheSynchronizationManagerAdapter
 * @see ClusteringServiceAdapter
 * @see JMSClusteringAdapter
 * @see RMIClusteringAdapter
 * @see RMIJNDIClusteringAdapter
 * @see RMIIIOPJNDIClusteringAdapter
 * @see SunCORBAClusteringAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class CacheSynchronizationManagerPane extends AbstractSubjectPanel
{
	/**
	 * The pane containing JMS Clustering Service specific information.
	 */
	private JMSClusteringPane jmsClusteringServicePane;

	/**
	 * The pane containing RMI JNDI/RMI IIOP JNDI/Sun CORBA Clustering Service
	 * specific information.
	 */
	private JNDIClusteringServicePane jndiClusteringServicePane;

	/**
	 * The pane containing RMI Clustering Service specific information.
	 */
	private ClusteringServicePane rmiClusteringServicePane;

	/**
	 * Creates a new <code>CacheSynchronizationManagerPane</code>.
	 *
	 * @param subjectHolder The holder of {@link CacheSynchronizationManagerAdapter}
	 * @param context
	 */
	CacheSynchronizationManagerPane(ValueModel subjectHolder,
											  ApplicationContext context)
	{
		super(subjectHolder, context);
		addHelpTopicId(this, "session.clustering.csm");
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildAsynchronousAdapter()
	{
		return new CheckBoxModelAdapter(buildAsynchronousHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Asynchronous property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildAsynchronousHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), CacheSynchronizationManagerAdapter.CACHE_SYNC_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) this.subject;
				return Boolean.valueOf(adapter.isAsynchronous());
			}

			protected void setValueOnSubject(Object value)
			{
				CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) this.subject;
				adapter.setAsynchronous(Boolean.TRUE.equals(value));
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
	private ComboBoxModel buildClusteringServiceAdapter(final ComboBoxSelectionManager manager)
	{
		return new ComboBoxModelAdapter(buildClusteringServiceCollectionHolder(manager),
												  buildClusteringServiceSelectionHolder(manager));
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the Clustering Service combo box.
	 *
	 * @param manager This manager is containing all the items to be contained
	 * in the <code>CollectionValueModel</code> required by the
	 * <code>ComboBoxModelAdapter</code>
	 * @return A new <code>CollectionValueModel</code>
	 */
	private CollectionValueModel buildClusteringServiceCollectionHolder(ComboBoxSelectionManager manager)
	{
		return new ReadOnlyCollectionValueModel(manager.choices());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> that will be the value
	 * holder of the <code>TransformationPropertyValueModel</code> given to the
	 * <code>PanelSwitcherAdapter</code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildClusteringServiceHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), CacheSynchronizationManagerAdapter.CLUSTERING_SERVICE_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) this.subject;
				return adapter.getClusteringService();
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Clustering Service property. The value returned is not the actual
	 * Clustering Service but a <code>ComboBoxSelection</code> which is one of the
	 * items of the combo box.
	 *
	 * @param manager This manager responsible for retrieving the proper
	 * <code>ComboBoxSelection</code> for a <code>TransportManager</code>
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildClusteringServiceSelectionHolder(final ComboBoxSelectionManager manager)
	{
		return new PropertyAspectAdapter(getSubjectHolder(), CacheSynchronizationManagerAdapter.CLUSTERING_SERVICE_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) this.subject;
				return manager.retrieveComboBoxSelection(adapter.getClusteringService());
			}

			protected void setValueOnSubject(Object value)
			{
				ComboBoxSelection selection = (ComboBoxSelection) value;
				selection.setPropertyOn(this.subject);
			}
		};
	}

	/**
	 * Creates the <code>TransformationPropertyValueModel</code> required to
	 * properly keep the content of the <code>PanelSwitcherAdapter</code> in sync
	 * with the value contained in the model.
	 *
	 * @param manager The manager responsible to retrieve the proper
	 * <code>PanelSwitcherAdapter.ObjectToComponentConverter</code> that knows
	 * how to transform an object to a <code>JComponent</code>
	 * @return The <code>TransformationPropertyValueModel</code> responsible to
	 * return a <code>JComponent</code> for a value
	 */
	private SwitcherPanel buildClusteringServiceSwitcherPanel(ComboBoxSelectionManager manager)
	{
		return new SwitcherPanel(buildClusteringServiceHolder(),
										 buildClusteringServiceTransformer(manager));
	}

	/**
	 * Creates
	 *
	 * @param manager The manager responsible to retrieve the proper
	 * <code>PanelSwitcherAdapter.ObjectToComponentConverter</code> that knows
	 * how to transform an object to a <code>JComponent</code>
	 * @return
	 */
	private Transformer buildClusteringServiceTransformer(final ComboBoxSelectionManager manager)
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
	 * required for a sub-pane
	 */
	private PropertyValueModel buildJMSClustingServiceHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSubjectHolder(), CacheSynchronizationManagerAdapter.CLUSTERING_SERVICE_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				CacheSynchronizationManagerAdapter cacheSync = (CacheSynchronizationManagerAdapter) this.subject;
				return cacheSync.getClusteringService();
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				return (value instanceof JMSClusteringAdapter) ? value : null;
			}
		};
	}

	/**
	 * Creates the subject holder where it is two children down from the value
	 * contained in the given node holder.
	 *
	 * @return The <code>PropertyValueModel</code> containing the subject holder
	 * required for a sub-pane
	 */
	private PropertyValueModel buildJNDIClusteringServiceHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSubjectHolder(), CacheSynchronizationManagerAdapter.CLUSTERING_SERVICE_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				CacheSynchronizationManagerAdapter cacheSync = (CacheSynchronizationManagerAdapter) this.subject;
				return cacheSync.getClusteringService();
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				if ((value instanceof JNDIClusteringServiceAdapter) &&
					!(value instanceof JMSClusteringAdapter))
				{
					return value;
				}

				return null;
			}
		};
	}

	/**
	 * Creates the subject holder where it is two children down from the value
	 * contained in the given node holder.
	 *
	 * @return The <code>PropertyValueModel</code> containing the subject holder
	 * required for a sub-pane
	 */
	private PropertyValueModel buildRMIClusteringServiceHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSubjectHolder(), CacheSynchronizationManagerAdapter.CLUSTERING_SERVICE_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				CacheSynchronizationManagerAdapter cacheSync = (CacheSynchronizationManagerAdapter) this.subject;
				return cacheSync.getClusteringService();
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				return (value instanceof RMIClusteringAdapter) ? value : null;
			}
		};
	}

	/**
	 * Creates and caches the sub-panes used to show the properties for all the
	 * Clustering Service choices.
	 */
	private void buildPanes()
	{
		this.jmsClusteringServicePane = new JMSClusteringPane(buildJMSClustingServiceHolder(), getApplicationContext());
		addPaneForAlignment(this.jmsClusteringServicePane);

		this.jndiClusteringServicePane = new JNDIClusteringServicePane(buildJNDIClusteringServiceHolder(), getApplicationContext());
		addPaneForAlignment(this.jndiClusteringServicePane);

		this.rmiClusteringServicePane = new ClusteringServicePane(buildRMIClusteringServiceHolder(), getApplicationContext());
		addPaneForAlignment(this.rmiClusteringServicePane);
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>CheckBoxModelAdapter</code>
	 */
	private ButtonModel buildRemoveConnectionOnErrorCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildRemoveConnectionOnErrorHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Remove Connection On Error property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildRemoveConnectionOnErrorHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), CacheSynchronizationManagerAdapter.ON_CONNECTION_ERROR_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) this.subject;
				return Boolean.valueOf(adapter.removeConnectionOnError());
			}

			protected void setValueOnSubject(Object value)
			{
				CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) this.subject;
				adapter.setRemoveConnectionOnError(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		buildPanes();

		ComboBoxSelectionManager manager = new ComboBoxSelectionManager();
		GridBagConstraints constraints = new GridBagConstraints();

		// Clustering Service widgets
		Component clusteringServiceWidgets = buildLabeledComboBox
		(
			"CSM_CLUSTERING_SERVICE_COMBO_BOX",
			buildClusteringServiceAdapter(manager)
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

		add(clusteringServiceWidgets, constraints);
		addHelpTopicId(clusteringServiceWidgets, "session.clustering.csm.clusteringService");

		// Clustering Service pane
		SwitcherPanel clusteringServicePane = buildClusteringServiceSwitcherPanel(manager);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(clusteringServicePane, constraints);

		// Asynchronous check box
		JCheckBox asynchronousCheckBox =
			buildCheckBox("CSM_ASYNCHRONOUS_CHECK_BOX",
							  buildAsynchronousAdapter());

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(asynchronousCheckBox, constraints);
		addHelpTopicId(asynchronousCheckBox, "session.clustering.csm.asynchronous");

		// Remove Connection On Error check box
		JCheckBox removeConnectionOnErrorCheckBox =
			buildCheckBox("CSM_REMOVE_CONNECTION_ON_ERROR_CHECK_BOX",
							  buildRemoveConnectionOnErrorCheckBoxAdapter());

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(removeConnectionOnErrorCheckBox, constraints);
		addHelpTopicId(removeConnectionOnErrorCheckBox, "session.clustering.csm.removeConnectionOnError");
	}

	/**
	 * The root class for all the Clustering Service choices.
	 */
	private abstract class ClusteringServiceChoice extends ComboBoxSelection
																  implements Comparable,
														 						 Transformer
	{
		/**
		 * The pane to be returned by this choice.
		 */
		private final JComponent pane;

		/**
		 * Creates a new <code>ClusteringChoice</code>.
		 *
		 * @param pane The pane to be shown on screen when this choice is used
		 * @param displayString The display string to be shown to represent this
		 * choice
		 */
		protected ClusteringServiceChoice(JComponent pane, String displayString)
		{
			super(displayString);
			this.pane = pane;
		}

		/**
		 * Compares this <code>ComboBoxSelection</code> with the given
		 * <code>ComboBoxSelection</code>.
		 *
		 * @param object The <code>ComboBoxSelection</code> to be compared with
		 * @return The comparison results from this object with the given object
		 */
		public int compareTo(Object object)
		{
			ComboBoxSelection selection = (ComboBoxSelection) object;
			return displayString().compareTo(selection.displayString());
		}

		/**
		 * Based on the given object, requests the associated component.
		 *
		 * @param value The value used to retrieve a pane
		 * @return The pane associated with the given value
		 */
		public Object transform(Object value)
		{
			return this.pane;
		}
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
		private final Hashtable selections;

		/**
		 * Creates a new <code>ComboBoxSelectionManager</code>.
		 */
		private ComboBoxSelectionManager()
		{
			super();

			this.selections = new Hashtable();
			this.selections.put(JMSClusteringAdapter.class,          new JMSClusteringChoice());
			this.selections.put(RMIClusteringAdapter.class,          new RMIClusteringChoice());
			this.selections.put(RMIJNDIClusteringAdapter.class,      new RMIJNDIClusteringChoice());
			this.selections.put(RMIIIOPJNDIClusteringAdapter.class,  new RMIIIOPJNDIClusteringChoice());
			this.selections.put(SunCORBAJNDIClusteringAdapter.class, new SunCORBAJNDIClusteringChoice());
		}

		/**
		 * Returns the list of <code>ComboBoxSelection</code>.
		 *
		 * @return An ordored list
		 */
		public List choices()
		{
			Vector choices = new Vector(5);

			choices.add(this.selections.get(JMSClusteringAdapter.class));
			choices.add(this.selections.get(RMIClusteringAdapter.class));
			choices.add(this.selections.get(RMIJNDIClusteringAdapter.class));
			choices.add(this.selections.get(RMIIIOPJNDIClusteringAdapter.class));
			choices.add(this.selections.get(SunCORBAJNDIClusteringAdapter.class));

			Collections.sort(choices);

			return choices;
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
			return (ComboBoxSelection) this.selections.get(value.getClass());
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
			return (Transformer) this.selections.get(value.getClass());
		}

		/**
		 * Returns the count of choices this manager handles.
		 *
		 * @return The number of choice
		 */
		public int size()
		{
			return this.selections.size();
		}
	}

	/**
	 * This is one of the choice for Clustering Service type.
	 */
	private class JMSClusteringChoice extends ClusteringServiceChoice
	{
		/**
		 * Creates a new <code>JMSClusteringChoice</code>.
		 */
		private JMSClusteringChoice()
		{
			super(CacheSynchronizationManagerPane.this.jmsClusteringServicePane,
					resourceRepository().getString("CSM_JMS_CLUSTERING_CHOICE"));
		}

		/**
		 * Updates the Transport Manager in the {@link RemoteCommandManagerAdapter}
		 * to be {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) subject;
			adapter.setClusteringAsJMS();
		}
	}

	/**
	 * This is one of the choice for Clustering Service type.
	 */
	private class RMIClusteringChoice extends ClusteringServiceChoice
	{
		/**
		 * Creates a new <code>RMIClusteringChoice</code>.
		 */
		private RMIClusteringChoice()
		{
			super(CacheSynchronizationManagerPane.this.rmiClusteringServicePane,
					resourceRepository().getString("CSM_RMI_CLUSTERING_CHOICE"));
		}

		/**
		 * Updates the Ser in the {@link RemoteCommandManagerAdapter}
		 * to be {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) subject;
			adapter.setClusteringAsRMI();
		}
	}

	/**
	 * This is one of the choice for Clustering Service type.
	 */
	private class RMIIIOPJNDIClusteringChoice extends ClusteringServiceChoice
	{
		/**
		 * Creates a new <code>RMIIIOPClusteringChoice</code>.
		 */
		private RMIIIOPJNDIClusteringChoice()
		{
			super(CacheSynchronizationManagerPane.this.jndiClusteringServicePane,
					resourceRepository().getString("CSM_RMI_IIOP_JNDI_CLUSTERING_CHOICE"));
		}

		/**
		 * Updates the Transport Manager in the {@link RemoteCommandManagerAdapter}
		 * to be {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) subject;
			adapter.setClusteringAsRMIIIOPJNDI();
		}
	}

	/**
	 * This is one of the choice for Clustering Service type.
	 */
	private class RMIJNDIClusteringChoice extends ClusteringServiceChoice
	{
		/**
		 * Creates a new <code>RMIJNDIClusteringChoice</code>.
		 */
		private RMIJNDIClusteringChoice()
		{
			super(CacheSynchronizationManagerPane.this.jndiClusteringServicePane,
					resourceRepository().getString("CSM_RMI_JNDI_CLUSTERING_CHOICE"));
		}

		/**
		 * Updates the Ser in the {@link RemoteCommandManagerAdapter}
		 * to be {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) subject;
			adapter.setClusteringAsRMIJNDI();
		}
	}

	/**
	 * This is one of the choice for Clustering Service type.
	 */
	private class SunCORBAJNDIClusteringChoice extends ClusteringServiceChoice
	{
		/**
		 * Creates a new <code>SunCORBAJNDIClusteringChoice</code>.
		 */
		private SunCORBAJNDIClusteringChoice()
		{
			super(CacheSynchronizationManagerPane.this.jndiClusteringServicePane,
					resourceRepository().getString("CSM_SUN_CORBA_JNDI_CLUSTERING_CHOICE"));
		}

		/**
		 * Updates the Ser in the {@link RemoteCommandManagerAdapter}
		 * to be {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter}.
		 *
		 * @param subject The <code>RemoteCommandManagerAdapter</code>
		 */
		public void setPropertyOn(Object subject)
		{
			CacheSynchronizationManagerAdapter adapter = (CacheSynchronizationManagerAdapter) subject;
			adapter.setClusteringAsSunCORBAJNDI();
		}
	}
}