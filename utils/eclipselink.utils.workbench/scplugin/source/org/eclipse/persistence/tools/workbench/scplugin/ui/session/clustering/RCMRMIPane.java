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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMIRegistryNamingServiceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMITransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.IPAddressFormatter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;

// Mapping Workbench

/**
 * This page shows the information about the {@link RMITransportManagerAdapter}.
 * <p>
 * Here the layout of this pane:<pre>
 * ________________________________________________
 * |                          ___________________ |
 * | Multicast Group Address: | XXX.XXX.XXX.XXX | |
 * |                          ------------------- |
 * |                          __________          |
 * | Multicast Port:          |      |I|          |
 * |                          ----------          |
 * |                          __________          |
 * | Announcement Delay:      |      |I|          |
 * |                          ----------          |
 * |                                              |
 * |  x Synchronous                               |
 * |                                              |
 * |  x Remove Connection on Error                |
 * |                                              |
 * | _o Registry Naming Service__________________ |
 * | |           ______________________         | |
 * | | URL:      | I                  |         | |
 * | |           ----------------------         | |
 * | -------------------------------------------- |
 * | _o JNDI Naming Service______________________ |
 * | |                                          | |
 * | | {@link JNDINamingServicePane}                    | |
 * | |                                          | |
 * | -------------------------------------------- |
 * ------------------------------------------------</pre>
 *
 * Known container of this pane:<br>
 * - {@link RemoteCommandManagerPane}
 *
 * @see RMITransportManagerAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class RCMRMIPane extends AbstractTransportManagerPane
{
	/**
	 * Creates a new <code>RCMRMIPane</code>.
	 *
	 * @param subjectHolder The holder of {@link RMITransportManagerAdapter}
	 * @param context The current context
	 */
	RCMRMIPane(ValueModel subjectHolder,
				  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Announcement Delay property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildAnnouncementDelayHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.DISCOVER_ANNOUNCEMENT_DELAY_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				return new Integer(adapter.getAnnouncementDelay());
			}

			protected void setValueOnSubject(Object value)
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				adapter.setAnnouncementDelay(((Integer) value).intValue());
			}
		};
	}

	/**
	 * Creates the <code>SpinnerModel</code> that keeps the value from the
	 * spinner in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>SpinnerNumberModel</code>
	 */
	private SpinnerNumberModel buildAnnouncementDelaySpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildAnnouncementDelayHolder(), 0, Integer.MAX_VALUE, 1);
	}

	private void buildJNDINamingComponentEnabler(PropertyValueModel subjectHolder,
																JComponent pane)
	{
		new ComponentEnabler(buildJNDINamingEnablerHolder(subjectHolder),
									pane.getComponents());
	}

	private PropertyValueModel buildJNDINamingEnablerHolder(PropertyValueModel subjectHolder)
	{
		return new TransformationPropertyValueModel(subjectHolder)
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return Boolean.valueOf(RMITransportManagerAdapter.JNDI_NAMING_SERVICE_PROPERTY.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * JNDI Naming Service property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildJNDINamingServiceHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.JNDI_NAMING_SERVICE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter rmi = (RMITransportManagerAdapter) subject;
				return rmi.getJNDINamingService();
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the value in the model and vice versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildMulticastGroupAddressDocumentAdapter()
	{
		return new DocumentAdapter(buildMulticastGroupAddressHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Multicast Group Address property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildMulticastGroupAddressHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.DISCOVER_MULTICAST_GROUP_ADDRESS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				return adapter.getMulticastGroupAddress();
			}

			protected void setValueOnSubject(Object value)
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				adapter.setMulticastGroupAddress((String) value);
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Multi Port property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildMultiCastPortHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.DISCOVER_MULTICAST_PORT_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				return new Integer(adapter.getMulticastPort());
			}

			protected void setValueOnSubject(Object value)
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				adapter.setMulticastPort(((Integer) value).intValue());
			}
		};
	}

	/**
	 * Creates the <code>SpinnerModel</code> that keeps the value from the
	 * spinner in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>SpinnerNumberModel</code>
	 */
	private SpinnerNumberModel buildMultiCastPortSpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildMultiCastPortHolder(), 0, 32768, 1);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * PacketTimeToLive property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPacketTimeToLiveHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.DISCOVER_PACKET_TIME_TO_LIVE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				return new Integer(adapter.getPacketTimeToLive());
			}

			protected void setValueOnSubject(Object value)
			{
				RMITransportManagerAdapter adapter = (RMITransportManagerAdapter) subject;
				adapter.setPacketTimeToLive(((Integer) value).intValue());
			}
		};
	}

	/**
	 * Creates the <code>SpinnerModel</code> that keeps the value from the
	 * spinner in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>SpinnerNumberModel</code>
	 */
	private SpinnerNumberModel buildPacketTimeToLiveSpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildPacketTimeToLiveHolder(), 0, 32768, 1);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Naming Service property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildNamingServiceTypeHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.NAMING_SERVICE_TYPE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter rmi = (RMITransportManagerAdapter) subject;
				return rmi.getNamingServiceType();
			}

			protected void setValueOnSubject(Object value)
			{
				RMITransportManagerAdapter rmi = (RMITransportManagerAdapter) subject;

				if (rmi.getNamingServiceType().equals(value))
				{
					return; // This prevents from reapplying the same naming service
				}
				else if (RMITransportManagerAdapter.JNDI_NAMING_SERVICE_PROPERTY.equals(value))
				{
					rmi.setNamingServiceToJNDINamingService();
				}
				else if (RMITransportManagerAdapter.RMI_REGISTRY_NAMING_SERVICE_PROPERTY.equals(value))
				{
					rmi.setNamingServiceToRMIRegistryNamingService();
				}
			}
		};
	}

	private void buildRMIRegistryNamingComponentEnabler(PropertyValueModel subjectHolder,
																		 JComponent pane)
	{
		new ComponentEnabler(buildRMIRegistryNamingEnablerHolder(subjectHolder),
									pane.getComponents());
	}

	private PropertyValueModel buildRMIRegistryNamingEnablerHolder(PropertyValueModel subjectHolder)
	{
		return new TransformationPropertyValueModel(subjectHolder)
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return Boolean.valueOf(RMITransportManagerAdapter.RMI_REGISTRY_NAMING_SERVICE_PROPERTY.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * RMI Registery Naming Service property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildRMIRegistryNamingServiceHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.RMI_REGISTRY_NAMING_SERVICE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter rmi = (RMITransportManagerAdapter) subject;
				return rmi.getRMIRegistryNamingService();
			}
		};
	}

	private ButtonModel buildSynchronousButtonModelAdapter()
	{
		return new CheckBoxModelAdapter(buildSynchronousHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Synchronous property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyAspectAdapter buildSynchronousHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), RMITransportManagerAdapter.SEND_MODE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				RMITransportManagerAdapter transport = (RMITransportManagerAdapter) subject;
				return Boolean.valueOf(transport.isSynchronous());
			}

			protected void setValueOnSubject(Object value)
			{
				RMITransportManagerAdapter transport = (RMITransportManagerAdapter) subject;
				transport.setSynchronous(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel namingServiceHolder = buildNamingServiceTypeHolder();

		// Multicast Group Address widgets
		JFormattedTextField multicastGroupAddressField = new JFormattedTextField(new IPAddressFormatter());
		multicastGroupAddressField.setDocument(buildMulticastGroupAddressDocumentAdapter());
		multicastGroupAddressField.setName("RMI_MULTICAST_GROUP_ADDRESS_FIELD");
		multicastGroupAddressField.setColumns(9);

		JComponent multicastGroupAddressWidgets = buildLabeledComponent
		(
			"RMI_MULTICAST_GROUP_ADDRESS_FIELD",
			multicastGroupAddressField
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

		add(multicastGroupAddressWidgets, constraints);
		addHelpTopicId(multicastGroupAddressWidgets, "session.clustering.rcm.rmi.multicastGroupAddress");

		// Multicast Port widget
		JComponent multicastPortWidgets = buildLabeledSpinnerNumber
		(
			"RMI_MULTICAST_PORT_SPINNER",
			buildMultiCastPortSpinnerAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(multicastPortWidgets, constraints);
		addHelpTopicId(multicastPortWidgets, "session.clustering.rcm.rmi.multicastPort");

		// PacketTimeToLive widget
		JComponent packetTimeToLiveWidgets = buildLabeledSpinnerNumber
		(
			"RMI_PACKET_TIME_TO_LIVE_SPINNER",
			buildPacketTimeToLiveSpinnerAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(packetTimeToLiveWidgets, constraints);
		addHelpTopicId(packetTimeToLiveWidgets, "session.clustering.rcm.rmi.packetTimeToLive");

		// Announcement Delay label
		JComponent announcementDelayWidgets = buildLabeledSpinnerNumber
		(
			"RMI_ANNOUNCEMENT_DELAY_SPINNER",
			buildAnnouncementDelaySpinnerAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(announcementDelayWidgets, constraints);
		addHelpTopicId(announcementDelayWidgets, "session.clustering.rcm.rmi.announcementDelay");

		// Remove Connection On Error check box
		JCheckBox removeConnectionOnErrorCheckBox = buildRemoveConnectionOnError();

		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);

		add(removeConnectionOnErrorCheckBox, constraints);

		// Synchronous check box
		JCheckBox synchronousCheckBox = buildCheckBox
		(
			"RMI_SYNCHRONOUS_CHECK_BOX",
			buildSynchronousButtonModelAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);

		add(synchronousCheckBox, constraints);
		addHelpTopicId(synchronousCheckBox, "session.clustering.rcm.rmi.synchronous");

		// RMI Registry Naming Service pane
		JRadioButton rmiRegistryNamingServiceRadioButton = buildRadioButton
		(
			"RMI_REGISTRY_NAMING_SERVICE_RADIO_BUTTON",
			new RadioButtonModelAdapter(namingServiceHolder, RMITransportManagerAdapter.RMI_REGISTRY_NAMING_SERVICE_PROPERTY)
		);
		addHelpTopicId(rmiRegistryNamingServiceRadioButton, "session.clustering.rcm.rmi.rmiURL");

		RMIRegistryNamingServicePane registryNamingServicePane = new RMIRegistryNamingServicePane();
		addHelpTopicId(registryNamingServicePane, "session.clustering.rcm.rmi.rmiURL");
		buildRMIRegistryNamingComponentEnabler(namingServiceHolder, registryNamingServicePane);

		// JNDI Naming Service pane
		JRadioButton jndiNamingServiceRadioButton = buildRadioButton
		(
			"RMI_JNDI_NAMING_SERVICE_RADIO_BUTTON",
			new RadioButtonModelAdapter(namingServiceHolder, RMITransportManagerAdapter.JNDI_NAMING_SERVICE_PROPERTY)
		);
		addHelpTopicId(jndiNamingServiceRadioButton, "session.clustering.rcm.jndiNamingService");

		JNDINamingServicePane jndiNamingServicePane = new JNDINamingServicePane(buildJNDINamingServiceHolder(), getWorkbenchContextHolder());
		jndiNamingServicePane.setBorder(BorderFactory.createEmptyBorder());
		buildJNDINamingComponentEnabler(namingServiceHolder, jndiNamingServicePane);
		registryNamingServicePane.addPaneForAlignment(jndiNamingServicePane);

		// Add the widgets to the container
		GroupBox groupBox = new GroupBox
		(
			rmiRegistryNamingServiceRadioButton,
			registryNamingServicePane,
			jndiNamingServiceRadioButton,
			jndiNamingServicePane
		);

		constraints.gridx       = 0;
		constraints.gridy       = 6;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(groupBox, constraints);
		addHelpTopicId(this, "session.clustering.rcm.rmi");
	}

	/**
	 * This sub-pane simply shows the Naming Service URL widgets.
	 */
	private class RMIRegistryNamingServicePane extends AbstractSubjectPanel
	{
		/**
		 * Creates a new <code>RMIRegistryNamingServicePane</code>.
		 */
		private RMIRegistryNamingServicePane()
		{
			super(buildRMIRegistryNamingServiceHolder(),
					RCMRMIPane.this.getApplicationContext());
		}

		/**
		 * Give a public access to another pane for alignment only between them.
		 *
		 * @param pane The pane to align with this one
		 */
		public void addPaneForAlignment(JNDINamingServicePane pane)
		{
			super.addPaneForAlignment(pane);
		}

		/**
		 * Creates the <code>DocumentAdapter</code> that keeps the value from the
		 * text field in sync with the URL value in the model and vice versa.
		 *
		 * @return A new <code>DocumentAdapter</code>
		 */
		private Document buildRMIURLDocumentAdapter()
		{
			return new DocumentAdapter(buildRMIURLHolder());
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * URL property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildRMIURLHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), RMIRegistryNamingServiceAdapter.URL_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					RMIRegistryNamingServiceAdapter rmi = (RMIRegistryNamingServiceAdapter) subject;
					return rmi.getURL();
				}

				protected void setValueOnSubject(Object value)
				{
					RMIRegistryNamingServiceAdapter rmi = (RMIRegistryNamingServiceAdapter) subject;
					rmi.setURL((String) value);
				}
			};
		}

		/**
		 * Initialize the layout of this pane.
		 */
		protected void initializeLayout()
		{
			GridBagConstraints constraints = new GridBagConstraints();

			JComponent rmiURLWidgets = buildLabeledTextField
			(
				"RMI_REGISTRY_NAMING_SERVICE_URL_FIELD",
				buildRMIURLDocumentAdapter()
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

			add(rmiURLWidgets, constraints);
		}
	}
}
