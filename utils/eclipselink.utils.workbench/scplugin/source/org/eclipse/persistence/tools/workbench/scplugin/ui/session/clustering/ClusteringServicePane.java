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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ClusteringServiceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.IPAddressFormatter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;

// Mapping Workbench

/**
 * This pane shows the information for Clustering Service.
 * <p>
 * Here the layout:</pre>
 * ________________________________________________
 * |                          ___________________ |
 * | Multicast Group Address: | XXX.XXX.XXX.XXX | |
 * |                          ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * |                          __________          |
 * | Multicast Port:          |      |I|          |
 * |                          ¯¯¯¯¯¯¯¯¯¯          |
 * |                          __________          |
 * | Packet Time to Live:     |      |I|          |
 * |                          ¯¯¯¯¯¯¯¯¯¯          |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯<pre>
 *
 * see ClusteringServiceAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class ClusteringServicePane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>ClusteringServicePane</code>.
	 *
	 * @param subjectHolder The holder of <code>RMIClusteringAdapter</code>
	 * @param context ...
	 */
	ClusteringServicePane(PropertyValueModel subjectHolder,
								 ApplicationContext context)
	{
		super(subjectHolder, context);
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
		return new PropertyAspectAdapter(getSubjectHolder(), ClusteringServiceAdapter.MULTICAST_GROUP_ADDRESS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ClusteringServiceAdapter adapter = (ClusteringServiceAdapter) subject;
				return adapter.getMulticastGroupAddress();
			}

			protected void setValueOnSubject(Object value)
			{
				ClusteringServiceAdapter adapter = (ClusteringServiceAdapter) subject;
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
	private PropertyValueModel buildMultiPortHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), ClusteringServiceAdapter.MULTICAST_PORT_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ClusteringServiceAdapter adapter = (ClusteringServiceAdapter) subject;
				return new Integer(adapter.getMulticastPort());
			}

			protected void setValueOnSubject(Object value)
			{
				ClusteringServiceAdapter adapter = (ClusteringServiceAdapter) subject;
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
	private SpinnerNumberModel buildMultiPortSpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildMultiPortHolder(), 0, 32768, 1);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Announcement Delay property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPacketTimeToLiveHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), ClusteringServiceAdapter.PACKET_TIME_TO_LIVE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ClusteringServiceAdapter adapter = (ClusteringServiceAdapter) subject;
				return new Integer(adapter.getPacketTimeToLive());
			}

			protected void setValueOnSubject(Object value)
			{
				ClusteringServiceAdapter adapter = (ClusteringServiceAdapter) subject;
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
		return new NumberSpinnerModelAdapter(buildPacketTimeToLiveHolder(), 0, Integer.MAX_VALUE, 1);
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Multicast Group Address widgets
		JFormattedTextField multicastGroupAddressField = new JFormattedTextField(new IPAddressFormatter());
		multicastGroupAddressField.setDocument(buildMulticastGroupAddressDocumentAdapter());
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
		addHelpTopicId(multicastGroupAddressWidgets, "session.clustering.csm.rmi.multicastGroupAddress");

		// Multi Port widgets
		JComponent multicastPortWidgets = buildLabeledSpinnerNumber
		(
			"RMI_MULTICAST_PORT_SPINNER",
			buildMultiPortSpinnerAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(multicastPortWidgets, constraints);
		addHelpTopicId(multicastPortWidgets, "session.clustering.csm.rmi.multicastPort");

		// Announcement Delay widgets
		JComponent packetTimeToLiveWidgets = buildLabeledSpinnerNumber
		(
			"RMI_PACKET_TIME_TO_LIVE_SPINNER",
			buildPacketTimeToLiveSpinnerAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(packetTimeToLiveWidgets, constraints);
		addHelpTopicId(packetTimeToLiveWidgets, "session.clustering.csm.rmi.packetTimeToLive");

		addHelpTopicId(this, "session.clustering.csm.rmi");
	}
}