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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.clustering;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic.AbstractSessionPanelTest;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JMSTopicTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JNDINamingService;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMIRegistryNamingServiceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RMITransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RemoteCommandManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.UserDefinedTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering.SessionClusteringPropertiesPage;


/**
 * @see SessionClusteringPropertiesPage
 *
 * @author Pascal Filion
 * @version 1.0a
 */
public class SessionClusteringPropertiesPageTest extends AbstractSessionPanelTest
{
	private int clusteringCount;
	private int transportCount;

	public SessionClusteringPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new SessionClusteringPropertiesPageTest("SessionClusteringPropertiesPageTest").execute(args);
	}

	protected void _testComponentEntryChannel() throws Exception
	{
		RemoteCommandManagerAdapter rcm = setClusteringToRemoteCommandManager();
		rcm.setTransportAsRMI();

		simulateMnemonic("CLUSTERING_CHANNEL_FIELD");
		simulateTextInput("My Channel");

		assertEquals("My Channel", rcm.getChannel());
	}

	protected void _testComponentEntryJMSTopicName() throws Exception
	{
		JMSTopicTransportManagerAdapter jms = setJMSTopicTransportManager();

		simulateMnemonic("RMI_JMS_TOPIC_NAME_FIELD");
		simulateTextInput("TopicName");

		assertEquals("TopicName", jms.getTopicName());
	}

	protected void _testComponentEntryJNDIPassword() throws Exception
	{
		JNDINamingService rmi = setJNDINamingService();

		simulateMnemonic("JNDI_PASSWORD_FIELD");
		simulateTextInput("*password*");

		assertEquals("*password*", rmi.getPassword());
	}

	protected void _testComponentEntryJNDIURL() throws Exception
	{
		RMIRegistryNamingServiceAdapter rmi = setRMIRegistryNamingService();

		simulateMnemonic("JNDI_URL_FIELD");
		simulateTextInput("file:////MyLibrary.jar");

		assertEquals("file:////MyLibrary.jar", rmi.getURL());
	}

	protected void _testComponentEntryJNDIUsername() throws Exception
	{
		JNDINamingService rmi = setJNDINamingService();

		simulateMnemonic("JNDI_USER_NAME_FIELD");
		simulateTextInput("USER_NAME");

		assertEquals("USER_NAME", rmi.getUserName());
	}

	protected void _testComponentEntryRemoveConnectionOnError() throws Exception
	{
		RMITransportManagerAdapter transport = setRMITransportManager();
		boolean oldState = transport.removeConnectionOnError();

		simulateMnemonic("CLUSTERING_REMOVE_CONNECTION_ON_ERROR_CHECK_BOX");

		assertTrue(oldState != transport.removeConnectionOnError());
	}

	protected void _testComponentEntryRMIMulticastGroupAddress() throws Exception
	{
		RMITransportManagerAdapter rmi = setRMITransportManager();

		simulateMnemonic("RMI_MULTICAST_GROUP_ADDRESS_FIELD");
		simulateFormattedTextInput("138.2.91.79");

		assertEquals("138.2.91.79", rmi.getMulticastGroupAddress());
	}

	protected void _testComponentEntryRMIMultiPort() throws Exception
	{
		RMITransportManagerAdapter rmi = setRMITransportManager();

		simulateMnemonic("RMI_MULTICAST_PORT_SPINNER");
		simulateSpinnerInput(4500);

		assertEquals(4500, rmi.getMulticastPort());
	}

	protected void _testFocusTransferChannel() throws Exception
	{
		setRMITransportManager();

		testFocusTransferByMnemonic("CLUSTERING_CHANNEL_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferJMSTopicName() throws Exception
	{
		setJMSTopicTransportManager();

		testFocusTransferByMnemonic("RMI_JMS_TOPIC_NAME_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferJNDIPassword() throws Exception
	{
		setJNDINamingService();

		testFocusTransferByMnemonic("JNDI_PASSWORD_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferJNDIURL() throws Exception
	{
		setJNDINamingService();

		testFocusTransferByMnemonic("JNDI_URL_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferJNDIUsername() throws Exception
	{
		setJNDINamingService();

		testFocusTransferByMnemonic("JNDI_USER_NAME_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferRemoveConnectionOnError() throws Exception
	{
		setClusteringToRemoteCommandManager();

		testFocusTransferByMnemonic("CLUSTERING_REMOVE_CONNECTION_ON_ERROR_CHECK_BOX",
											 COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferRMIAnnouncementDelay() throws Exception
	{
		setRMITransportManager();

		testFocusTransferByMnemonic("RMI_ANNOUNCEMENT_DELAY_SPINNER",
											 COMPONENT_SPINNER);
	}

	protected void _testFocusTransferRMIMulticastGroupAddress() throws Exception
	{
		setRMITransportManager();

		testFocusTransferByMnemonic("RMI_MULTICAST_GROUP_ADDRESS_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferRMIMultiPort() throws Exception
	{
		setRMITransportManager();

		testFocusTransferByMnemonic("RMI_MULTICAST_PORT_SPINNER",
											 COMPONENT_SPINNER);
	}

	protected void _testFocusTransferType() throws Exception
	{
		setClusteringToRemoteCommandManager();

		testFocusTransferByMnemonic("CLUSTERING_TYPE_COMBO_BOX",
											 COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferUseClusteringService() throws Exception
	{
		testFocusTransferByMnemonic("CLUSTERING_CLUSTERING_CHECK_BOX",
											 COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferUserDefinedTransportClass() throws Exception
	{
		setUserDefinedTransportManager();
		sleep();

		testFocusTransferByMnemonic("RCM_USER_DEFINED_TRANSPORT_CLASS_FIELD",
											 COMPONENT_TEXT_FIELD);
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(SessionClusteringPropertiesPage.class, getNodeHolder());
	}

	protected boolean canContinueTestingDuplicateMnemonic()
	{
		SessionAdapter session = getSession();

		// This will add the Remote Command Manager panel
		if (session.hasNoClusteringService())
		{
			session.setClusteringToRemoteCommandManager();
			this.clusteringCount++;
			return true;
		}

		// This will test the Remote Command Manager panel
		if (session.hasRemoteCommandManager())
		{
			RemoteCommandManagerAdapter remoteCommandManager = session.getRemoteCommandManager();
			TransportManagerAdapter transportManager = remoteCommandManager.getTransportManager();

			// This will change a sub-pane to show the information about RMI and RMI/IIOP
			if (transportManager instanceof JMSTopicTransportManagerAdapter)
			{
				if (this.transportCount < 3)
				{
					remoteCommandManager.setTransportAsRMI();
					this.transportCount++;
					return true;
				}
			}

			// This will change a sub-pane to show the information about User Defined
			if (transportManager instanceof RMITransportManagerAdapter)
			{
				if (this.transportCount < 3)
				{
					remoteCommandManager.setTransportAsUserDefined();
					this.transportCount++;
					return true;
				}
			}

			// This will change a sub-pane to show the information about JMS
			if (transportManager instanceof UserDefinedTransportManagerAdapter)
			{
				if (this.transportCount < 3)
				{
					remoteCommandManager.setTransportAsJMSTopic();
					this.transportCount++;
					return true;
				}
			}
		}

		// Now test the Cache Synchronization Manager panel
		if (session.hasRemoteCommandManager())
		{
			session.setClusteringToCacheSynchronizationManager();
			this.clusteringCount++;
			return true;
		}

		// The test started with Cache Synchronization Manager
		if (this.clusteringCount < 2)
		{
			session.setClusteringToNothing();
			this.clusteringCount++;
			return true;
		}

		return false;
	}

	protected void printModel()
	{
	}

	protected void resetProperty()
	{
	}

	private RemoteCommandManagerAdapter setClusteringToRemoteCommandManager()
	{
		SessionAdapter session = (SessionAdapter) getSelection();
		return session.setClusteringToRemoteCommandManager();
	}

	private JMSTopicTransportManagerAdapter setJMSTopicTransportManager()
	{
		RemoteCommandManagerAdapter adapter = setClusteringToRemoteCommandManager();
		return adapter.setTransportAsJMSTopic();
	}

	private JNDINamingService setJNDINamingService()
	{
		RMITransportManagerAdapter rmi = setRMITransportManager();

		if (rmi.getNamingServiceType() == RMITransportManagerAdapter.JNDI_NAMING_SERVICE_PROPERTY)
			return rmi.getJNDINamingService();

		return rmi.setNamingServiceToJNDINamingService();
	}

	private RMIRegistryNamingServiceAdapter setRMIRegistryNamingService()
	{
		RMITransportManagerAdapter rmi = setRMITransportManager();

		if (rmi.getNamingServiceType() == RMITransportManagerAdapter.RMI_REGISTRY_NAMING_SERVICE_PROPERTY)
			return rmi.getRMIRegistryNamingService();

		return rmi.setNamingServiceToRMIRegistryNamingService();
	}

	private RMITransportManagerAdapter setRMITransportManager()
	{
		RemoteCommandManagerAdapter adapter = setClusteringToRemoteCommandManager();
		return adapter.setTransportAsRMI();
	}

	private UserDefinedTransportManagerAdapter setUserDefinedTransportManager()
	{
		RemoteCommandManagerAdapter adapter = setClusteringToRemoteCommandManager();
		return adapter.setTransportAsUserDefined();
	}

	protected String windowTitle()
	{
		return "Testing Session Clustering Page";
	}
}