/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sessions.coordination;

import java.net.*;
import java.io.IOException;
import org.eclipse.persistence.exceptions.DiscoveryException;
import org.eclipse.persistence.internal.sessions.coordination.*;

/**
 * <p>
 * <b>Purpose</b>: Detects new members of a logical EclipseLink cluster.
 * <p>
 * <b>Description</b>: Each RemoteCommandManager has its own DiscoveryManager,
 * which handles the detection of other remote command services as they become available.
 * The DiscoveryManager is an active object (in that it extends Thread) and becomes
 * a separate thread when it is started using startDiscovery().
 * <p>
 * Discovery is done through the use of a multicast. Each discovery manager
 * joins the multicast group and announces itself to the group. As it receives
 * service announcements from other discovery managers it notifies the RCM to
 * establish connections to and from the new service.
 *
 * @see RemoteCommandManager
 * @see java.net.MulticastSocket
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class DiscoveryManager implements Runnable {

    /** Default value constants */
    public static final String DEFAULT_MULTICAST_GROUP = "226.10.12.64";
    public static final int DEFAULT_MULTICAST_PORT = 3121;
    public static final int DEFAULT_PACKET_TIME_TO_LIVE = 2;
    public static final int DEFAULT_ANNOUNCEMENT_DELAY = 1000;

    /** Defines the IP address of the multicast group */
    protected String multicastGroupAddress = DEFAULT_MULTICAST_GROUP;

    /** Defines the port the multicast socket will be using */
    protected int multicastPort = DEFAULT_MULTICAST_PORT;

    /** The multicast socket used for discovery */
    protected MulticastSocket communicationSocket;

    /**
     * Number of hops in the life of the datapacket
     * Default is 2, a hub and an interface card to prevent the data packets from leaving the localnetwork.
     */
    protected int packetTimeToLive = DEFAULT_PACKET_TIME_TO_LIVE;

    /** Indicates to the listening thread that it should stop */
    protected boolean stopListening = false;

    /** Delay time in millis between initialization and when the announcement is sent */
    protected int announcementDelay = DEFAULT_ANNOUNCEMENT_DELAY;

    /** The remote command manager responsible for this service */
    protected RemoteCommandManager rcm;

    /**
     * Constructors to create a discovery manager.
     */
    public DiscoveryManager(RemoteCommandManager mgr) {
        this.rcm = mgr;
    }

    public DiscoveryManager(String address, int port, RemoteCommandManager mgr) {
        this(mgr);
        this.multicastGroupAddress = address;
        this.multicastPort = port;
    }

    public DiscoveryManager(String address, int port, int delay, RemoteCommandManager mgr) {
        this(address, port, mgr);
        this.announcementDelay = delay;
    }

    /**
     * INTERNAL:
     * Send out an announcement that we are here.
     */
    public void announceSession() {
        rcm.logDebug("sending_announcement", (Object[])null);

        ServiceAnnouncement outMsg = new ServiceAnnouncement(rcm.getServiceId());
        byte[] outBytes = outMsg.toBytes();

        try {
            // Create a packet to send and send it out to everyone listening
            DatagramPacket sendPacket = new DatagramPacket(outBytes, outBytes.length, InetAddress.getByName(multicastGroupAddress), multicastPort);
            getCommunicationSocket().send(sendPacket);

            Object[] args = null;
            rcm.logInfo("announcement_sent", args);

        } catch (Exception ex) {
            // We got an exception. Map it to an RCM exception and call the handler
            DiscoveryException discoveryEx = DiscoveryException.errorSendingAnnouncement(ex);
            rcm.handleException(discoveryEx);
        }
    }

    /**
     * ADVANCED:
     * Announce the local service and join the cluster
     */
    public void startDiscovery() {
        if (rcm.isCommandProcessorASession()) {
            rcm.getCommandProcessor().processCommand(new ProfileDiscoveryStartedCommand());
        }

        // Only start if we are currently stopped
        if (this.isDiscoveryStopped()) {
            this.rcm.getServerPlatform().launchContainerRunnable(this);
        }
    }

    /**
     * ADVANCED:
     * Stop accepting announcements from other services becoming available.
     * Note that this does not remove the local service from the cluster.
     */
    public void stopDiscovery() {
        if (rcm.isCommandProcessorASession()) {
            this.rcm.getCommandProcessor().processCommand(new ProfileDiscoveryStoppedCommand());
        }
        stopListening();
        try {
            // Put in a sleep to give the listener thread a chance to stop
            Thread.sleep(500);
        } catch (InterruptedException exception) {
        }
        communicationSocket = null;
    }

    /**
     * ADVANCED:
     * Return true if discovery has been stopped at the time this method is called.
     * If false is returned then it is undefined whether discovery is started or
     * stopped. It may be started, or it may be in the process of starting or
     * stopping.
     */
    public boolean isDiscoveryStopped() {
        return (communicationSocket == null);
    }

    /**
     * INTERNAL:
     * Create the multicast socket and join the multicast group.
     */
    public void createCommunicationSocket() {
        Object[] args = { multicastGroupAddress, "" + multicastPort };
        rcm.logDebug("initializing_discovery_resources", args);
        if (communicationSocket == null) {
            try {
                communicationSocket = new MulticastSocket(multicastPort);
                communicationSocket.setTimeToLive(getPacketTimeToLive());
                communicationSocket.joinGroup(InetAddress.getByName(multicastGroupAddress));
            } catch (IOException ex) {
                // Either we couldn't create the socket or we couldn't join the group
                DiscoveryException discoveryEx = DiscoveryException.errorJoiningMulticastGroup(ex);
                rcm.handleException(discoveryEx);
            }
        }
    }

    /**
      * INTERNAL:
      * Return the socket that will be used for the multicast.
      */
    public MulticastSocket getCommunicationSocket() {
        return communicationSocket;
    }

    /**
     * INTERNAL:
     * This is the main execution method of discovery.  It will create a socket to
     * listen on, create a local connection for this service and announce that we are
     * open for business.
    */
    public void run() {
        //Initialize the communication socket
        createCommunicationSocket();

        // Create the local connection from which we will receive commands
        rcm.getTransportManager().createLocalConnection();

        // Announce to all other discovery managers that this service is up. The
        // delay allows time for posting of connections to the name service
        try {
            Thread.sleep(announcementDelay);
        } catch (InterruptedException exception) {
        }
        announceSession();

        // Listen for other sessions that are joining
        startListening();
    }

    /**
     * INTERNAL:
     * This method puts us into the listening mode loop. This thread blocks, waiting
     * on announcements that we receive from other discovery managers.
     */
    public void startListening() {
        byte[] recvBuf = new byte[128];

        // Only stop when we get the directive to stop
        stopListening = false;
        rcm.logInfo("discovery_manager_active", (Object[])null);
        while (!stopListening) {
            DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
            ServiceAnnouncement inMsg;

            // Block waiting for a message
            try {
                getCommunicationSocket().receive(recvPacket);
            } catch (IOException exception) {
                if (stopListening) {
                    // We caused the exception by closing the socket
                    rcm.logInfo("discovery_manager_stopped", (Object[])null);
                    return;
                } else {
                    // Exception was caused by something else (e.g. network error, etc.)
                    rcm.handleException(DiscoveryException.errorReceivingAnnouncement(exception));
                }
            }

            // We received a message, unmarshall it into an announcement
            try {
                inMsg = new ServiceAnnouncement(recvPacket.getData());
            } catch (Exception ex) {
                // Log a warning that we couldn't process the announcement
                Object[] args = { ex };
                rcm.logWarning("received_corrupt_announcement", args);
                continue;
            }

            // If the msg is not from ourselves, and is announcing a service on
            // the same channel as we are on then we should do something about it
            if (!rcm.getServiceId().getId().equals(inMsg.getServiceId().getId()) && (rcm.getServiceId().getChannel().equalsIgnoreCase(inMsg.getServiceId().getChannel()))) {
                receivedAnnouncement(inMsg.getServiceId());
            }
        }
    }

    /**
     * INTERNAL:
     * Signal this instance to stop listening.
     */
    public void stopListening() {
        this.stopListening = true;
        if (getCommunicationSocket() != null) {
            getCommunicationSocket().close();
        }
    }

    /**
     * INTERNAL:
     * Process the announcement that indicates that a new service is online
     */
    public void receivedAnnouncement(ServiceId serviceId) {
        Object[] args = { serviceId };
        rcm.logInfo("announcement_received", args);
        // Notify the RCM that a new service has been detected 
        rcm.newServiceDiscovered(serviceId);
    }

    /**
     * PUBLIC:
     * Set the amount of time in millis that the service should wait between the time
     * that this Remote Service is available and a session announcement is sent out
     * to other discovery managers. This may be needed to give some systems more time
     * to post their connections into the naming service. Takes effect the next time
     * listening is started.
     */
    public void setAnnouncementDelay(int millisecondsToDelay) {
        announcementDelay = millisecondsToDelay;
    }

    /**
     * PUBLIC:
     * Return the amount of time in millis that the service should wait between the time
     * that this Remote Service is available and a session announcement is sent out
     * to other discovery managers.
     */
    public int getAnnouncementDelay() {
        return announcementDelay;
    }

    /**
     * PUBLIC:
     * Return the host address of the multicast group.
     */
    public String getMulticastGroupAddress() {
        return (multicastGroupAddress);
    }

    /**
     * PUBLIC:
     * Set the host address of the multicast group. Takes effect the next time
     * listening is started.
     */
    public void setMulticastGroupAddress(String address) {
        this.multicastGroupAddress = address;
    }

    /**
     * PUBLIC:
     * Set the multicast port used for discovery. Takes effect the next time
     * listening is started.
     */
    public void setMulticastPort(int port) {
        this.multicastPort = port;
    }

    /**
     * PUBLIC:
     * Return the multicast port used for discovery.
     */
    public int getMulticastPort() {
        return (multicastPort);
    }

    /**
     * INTERNAL: invoke when the RCM shutdown. Subclass overrides this method if necessary.
     */
    protected void shallowCopy(DiscoveryManager dmgr) {
        this.multicastGroupAddress = dmgr.multicastGroupAddress;
        this.multicastPort = dmgr.multicastPort;
        this.announcementDelay = dmgr.announcementDelay;
        this.rcm = dmgr.rcm;
    }

    /**
     * PUBLIC:
     * Returns the number of hops the data packets of the session announcement will take before expiring.
     * The default is 2, a hub and an interface card to prevent the data packets from leaving the local network.
     */
    public int getPacketTimeToLive() {
        return this.packetTimeToLive;
    }

    /**
     * PUBLIC:
     * Set the number of hops the data packets of the session announcement will take before expiring.
     * The default is 2, a hub and an interface card to prevent the data packets from leaving the local network.
     *
     * Note that if sessions are hosted on different LANs that are part of WAN, the announcement sending by one session
     * may not reach other sessions.  In this case, consult your network administrator for the right time-to-live value
     * or test your network by increase the value until sessions receive announcement sent by others.
     */
    public void setPacketTimeToLive(int newPacketTimeToLive) {
        this.packetTimeToLive = newPacketTimeToLive;
    }
}
