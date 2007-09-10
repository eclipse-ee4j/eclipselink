/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.exceptions.*;
import java.net.*;
import java.io.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>:To Provide a framework for offering customers the ability to automatically
 * connect multiple sessions for synchrnization.
 * <p>
 * <b>Descripton</b>:This thread object will place a remote dispatcher in a globally available space.
 * it will also monitor the specified multicast socket to allow other sessions to connect.
 * 
 * @author Gordon Yorke
 * @see org.eclipse.persistence.sessions.remote.CacheSynchronizationManager
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by 
 * 		{@link org.eclipse.persistence.sessions.coordination.TransportManager}
 */
public abstract class AbstractClusteringService extends Thread
{
	/**
	 * controlls the listening thread
	 */
	 protected boolean stopListening = false;
/**
	 * Defines what port the multicast socket will be monitoring on
	 */
	protected int multicastPort;
	 
	/**
	 * Number of hops in the life of the datapacket
	 * Default is 2, a hub and an interface card to prevent the datapackets from leaving the local
	 *  network
	 */
	protected int timeToLive = 2;

	/**
	 * Number of milliseconds to delay between the time that the RemoteService is made available and
	 * this session announces its existence.
	 */
	protected int announcementDelay = 0;
	
	/**
	 * This is the multiclass socket that will be used for communication 
	 */
	protected MulticastSocket communicationSocket;
	 
	/**
	 * Defines the IP address of the multicast group
	 */
	protected String multicastGroupAddress;
	
	/**
	 * This is the instantiated remote object that will be placed in the JNDI service and provides
	 * synchronization services
	 */
	protected Object dispatcher;
	/**
	 * The URL of the JNDI host where the Dispatcher will be placed
	 */
	protected String localHostURL;
	/**
	 * This is the session that will be synchronized to
	 */
	protected Session session;
	
	protected static int DEFAULT_RECV_BUFFER_SIZE = 128;
	protected static String DEFAULT_MULTICAST_GROUP = "226.18.6.18";
	protected static int DEFAULT_MULTICAST_PORT = 6018;
	/**
	 * Stores the unique ID for this sessio
	 */
	protected String sessionId;
	
	/**
	 * This method stores the reconnect policy for reconnecting to remote Sessions
	 * when connections are dropped.  Currently only technically useful with JMS connections
	 */
	protected DistributedSessionReconnectPolicy reconnectionPolicy;

	//This will allow customers to specify the application name for deployment
	//within an application server.
	protected String applicationName;

	/**
	 * PUBLIC:
	 * 	Creates an AbstractClusteringService
	 * @SBGen Constructor
	 */
	public AbstractClusteringService(Session session) {
	    this(DEFAULT_MULTICAST_GROUP,DEFAULT_MULTICAST_PORT, session);
	} 
	/**
	 * ADVANCED:
	 * 	Creates an AbstractClusteringService
	 * @param multicastAddress The address of the multicast group
	 * @param multicastPort The port the multicast group is listening on
	 * @SBGen Constructor
	 */
	public AbstractClusteringService(String multicastAddress, int multicastPort, Session session) {
	    this.multicastPort = multicastPort;
	    this.multicastGroupAddress = multicastAddress;
	    this.session = session;
	    this.reconnectionPolicy = new DistributedSessionReconnectPolicy();
	} 

   /**
    * INTERNAL:
    * Initializes the clustering service and starts again
    */
    public void initialize(){
        this.stopListening();
        if (getCommunicationSocket() != null) {
            getCommunicationSocket().close();
        }
        try{
            Thread.sleep(500);
            //ensure that the listening socket stops
        }catch (InterruptedException exception){
        }
        this.communicationSocket = null;
        this.start();
    }
    
    /**
     * INTERNAL:
     * This is the main execution method of this class.  It will create a socket to listen to and
     * register the dispatcher for this class in JNDI
     */
    public void run()
    {
		//Initialize the communication socket
		((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "initializing_local_discovery_communication_socket");
		getCommunicationSocket();
		//Place the remote dispatcher in JNDI
    	setSessionId(buildSessionId());
    	((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "place_local_remote_session_dispatcher_into_naming_service");
		registerDispatcher();
		
		getSession().getCacheSynchronizationManager().setSessionRemoteConnection(getLocalRemoteConnection());
    	//Announce to all other servers that the session is available to synchronize with
    	((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "connecting_to_other_sessions");
    	//Delay announcing existence.  This allows time for the Remote Service to propagate to the JNDI
    	//Service if required
    	try{
    		sleep(getAnnouncementDelay());
    	}catch (InterruptedException exception){
    	}
    	announceSession(getSessionId());
    	//Listen for other sessions that are joining
    	((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINEST, SessionLog.PROPAGATION, "done");
    	listen();     
     }

	/**
	 * ADVANCED:
	 * This method will register the dispatcher for this session in JNDI
	 * on the specified host.  It must register the dispatcher under the SessionId
	 * @param jndiHostURL This is the URL that will be used to register the synchronization service
	 */
	public abstract void registerDispatcher();
	/**
	 * ADVANCED:
	 * This method will deregister the dispatcher for this session from JNDI
	 * on the specified host.  It must deregister the dispatcher under the SessionId
	 */
	//BUG 2700381: deregister from JNDI
	public abstract void deregisterDispatcher();

	/**
	 * ADVANCED:
	 * 	This method should return a remote connection of the appropraite type for
	 * use in the synchronizatio
	 */
	public abstract RemoteConnection createRemoteConnection(String sessionId, String jndiHostURL); 

	/**
	 * PUBLIC:
	 *      Use this method to set the Multicast Port that this system will use to communicate
	 * 
	 * @param port This is the port that the multicast socket will listen on
	 * @SBGen Method set port
	 */
	public void setMulticastPort(int port) {
		// SBgen: Assign variable
		this.multicastPort = port;
	} 

	/**
	 * PUBLIC:
	 *  	Return the port that the Service will be using for the multicast socket
	 * @return the port that the multicast socket will listen on
	 * @SBGen Method get port
	 */
	public int getMulticastPort() {
		// SBgen: Get variable
		return(multicastPort);
	} 
    /**
     * INTERNAL:
     * Returns the reconnectionPolicy for this Service.  Only currently useful in the JMS Service
     */
      public DistributedSessionReconnectPolicy getReconnectPolicy(){
        return this.reconnectionPolicy;
      }
    /**
     * INTERNAL:
     * Returns the reconnectionPolicy for this Service.  Only currently useful in the JMS Service
     */
      public void setReconnectPolicy(DistributedSessionReconnectPolicy reconnectPolicy){
        this.reconnectionPolicy = reconnectPolicy;
      }
	/**
	 * PUBLIC:
	 * Set the host address of the naming service url.
	 * @param url jndi host url
	 * @SBGen Method set jndi host url
	 */
	
	public void setLocalHostURL(String url) {
		localHostURL = url;
	}
	/**
	 * PUBLIC:
	 *  	Use this method to set the number of hops that the Data packet will make until expiring.
	 * @param timeToLive 
	 * @SBGen Method set timeToLive
	 */
	public void setTimeToLive(int timeToLive) {
		// SBgen: Assign variable
		this.timeToLive = timeToLive;
	} 

	/**
	 * PUBLIC:
	 * 	    Returns the number of hops the data packet will take before expiring
	 * @SBGen Method get timeToLive
	 */
	public int getTimeToLive() {
		// SBgen: Get variable
		return(timeToLive);
	} 


	/**
	 * ADVANCED:
	 *  	Returns the socket that will be used for the multicast communication.
	 * By default this will be java.net.MulticastSocket
	 * @SBGen Method get communicationSocket
	 */
	public abstract MulticastSocket getCommunicationSocket() ;
	

	/**
	 * This method will return the Host adddress of the Multicast Group.  Used to 
	 * determine what group of servers will connect to each other for synchronization
	 * @SBGen Method get multicastHostAddress
	 */
	public String getMulticastGroupAddress() {
		// SBgen: Get variable
		return(multicastGroupAddress);
	} 

	/**
	 * This method will set the Host adddress of the Multicast Group.  Used to 
	 * determine what group of servers will connect to each other for synchronization
	 * @param multicastHostAddress 
	 * @SBGen Method set multicastHostAddress
	 */
	public void setMulticastGroupAddress(String multicastGroupAddress) {
		// SBgen: Assign variable
		this.multicastGroupAddress = multicastGroupAddress;
	} 

	/**
	 * This is the object that will be placed in JNDI to provide remote synchronization services
	 * @SBGen Method get dispatcher
	 */
	public abstract Object getDispatcher() throws java.rmi.RemoteException;

	/**
	 * ADVANCED:
	 * This method should return a Remote Connection of the appropriate type that references the 
	 * Remote dispatcher for this Session
	 */
	 public abstract RemoteConnection getLocalRemoteConnection();
	 
	/**
	 * INTERNAL:
	 */
	 public Session getSession() {
		// SBgen: Get variable
		return(session);
	} 

	/**
	 * This is the object that will be placed in JNDI to provide remote synchronization services
	 * @param dispatcher 
	 * @SBGen Method set dispatcher
	 */
	public void setDispatcher(Object dispatcher) {
		// SBgen: Assign variable
		this.dispatcher = dispatcher;
	} 

	/**
	 * This method will get the Host address of the JNDI service
	 * @param jndiHostURL 
	 * @SBGen Method set jndiHostURL
	 */
	public String getLocalHostURL() {
		return this.localHostURL;
	} 
	/**
	 * INTERNAL:
	 * THis method is called by the cache synchronization manager when this server should
	 * connect back ('handshake') to the server from which this remote connection came.
	 */
	 public void connectBackToRemote(RemoteConnection connection) throws Exception {
   		ConnectToSessionCommand command = new ConnectToSessionCommand();
   		command.setRemoteConnection(getLocalRemoteConnection());
   		connection.processCommand(command);
     }
   /**
	 * INTERNAL:
	 * 	This method will generate a unique key for identifying this Session from all other sessions on the network
	 */
	public String buildSessionId() {
		String tmpSessionId = new java.rmi.server.UID().toString();
		try{
		    tmpSessionId += java.net.InetAddress.getLocalHost().getHostAddress().replace('.', '-') + "-";
		}catch (IOException exception){
			getSession().handleException(SynchronizationException.errorLookingUpLocalHost(exception));
		}

		tmpSessionId += System.identityHashCode(getSession());
    return tmpSessionId.replace(':', '-');
	} 

	/**
	* INTERNAL:
	* Send out an announcement that we are here
	*/
	public void announceSession(String sessionIdentifier) {
		SessionAnnouncement outMsg = new SessionAnnouncement(
		sessionIdentifier, getLocalHostURL(), (getApplicationName() == null ? "" : getApplicationName()));
		byte[] outBytes = outMsg.toBytes();
		try{
			DatagramPacket sendPacket = new DatagramPacket(
				outBytes, outBytes.length, 
				InetAddress.getByName(getMulticastGroupAddress()), getMulticastPort());
			getCommunicationSocket().send(sendPacket);
		}catch (Exception exception){
			SynchronizationException topException = SynchronizationException.errorNotifyingCluster(exception);
			getSession().handleException(topException);
		}
		((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINER, SessionLog.PROPAGATION, "announcement_sent_from", getSessionId());
	}
	
	/**
	 * INTERNAL:
	 * 	This method provides the functionality of listening for synchronization requests from other servers
	 * @param listenSocket The Multicast socket to listen on
	 */
	public void listen() {
        this.stopListening = false;
		byte[] recvBuf = new byte[DEFAULT_RECV_BUFFER_SIZE];

		// Only stop when we get an exception
		while (! stopListening) {
			DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
			SessionAnnouncement inMsg;	

			// Block waiting for a message
			try{
				getCommunicationSocket().receive(recvPacket);
			}catch(IOException exception){
				if (!stopListening){
					getSession().handleException(SynchronizationException.errorReceivingAnnouncement(getSessionId(), exception));
				}else{
					return;
				}
			}		
			// We received a message, unmarshall it into an announcement
			try {
				inMsg = new SessionAnnouncement(recvPacket.getData());
			} catch (SynchronizationException syncEx) {
				((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.WARNING, SessionLog.PROPAGATION, "corrupted_session_announcement", getSessionId());
				continue;
			}
			
			// If the msg is not from this server then notify the server sync controller
			if (! getSessionId().equals(inMsg.getSessionId()))
				receivedAnnouncement(inMsg.getSessionId(), inMsg.getJNDIHostURL(), inMsg.getApplicationName());
		}
	}
	
	/**
	 * INTERNAL:
	 * Use this method to notify the SynchronizationManager that we have to connect to a new Session
	 * that has just joined the network
	 */
	public void receivedAnnouncement(String sessionId, String jndiHostURL, String applicationName){
		((org.eclipse.persistence.internal.sessions.AbstractSession)getSession()).log(SessionLog.FINER, SessionLog.PROPAGATION, "announcement_received_from", sessionId);
		StringBuffer buffer = new StringBuffer();
		buffer.append(jndiHostURL);
		buffer.append(applicationName);
		RemoteConnection newConnection = createRemoteConnection(sessionId, buffer.toString());
		getSession().getCacheSynchronizationManager().addRemoteConnection(newConnection);
	}
	
	/**
	 * INTERNAL:
	 * This method will be called by the CacheSynchronizationManager when a connection fails.
	 * it will redirect the call to the included reconnection policy
	 */
	 public RemoteConnection reconnect(RemoteConnection oldConnection){
	    return getReconnectPolicy().reconnect(oldConnection);
	 }

	/**
	 * ADVANCED:
	 * 	Set the Unique identifier for the session.  This attribute will be used to store
	 * the service in JNDI.
	 * @param sessionId 
	 * @SBGen Method set sessionId
	 */
	public void setSessionId(String sessionId) {
		// SBgen: Assign variable
		this.sessionId = sessionId;
	} 

	/**
	 * ADVANCED:
	 * 	Get the Unique identifier for the session.  This attribute will be used to store
	 * the service in JNDI.
	 * @SBGen Method get sessionId
	 */
	public String getSessionId() {
		// SBgen: Get variable
		return(sessionId);
	} 


	/**
	 * ADVANCED:
	 * Uses to stop the Listener thread for a dropped session
	 */
	public void stopListening() {
		this.stopListening = true;
		if (getCommunicationSocket() != null){
			getCommunicationSocket().close();
		}
	} 
	/**
	 * INTERNAL:
	 */
	public void setSession(Session session)
	    {
	        this.session = session;
	    }
	    
	/**
	 * PUBLIC:
	 * Sets the amount of time in millis that the service should wait between the time
	 * that the Remote Service is made available and this session's existences is announced.
	 */
	 public void setAnnouncementDelay(int millisecondsToDelay){
		this.announcementDelay = millisecondsToDelay;
	 }
	 
	/**
	 * PUBLIC:
	 * Use this method to set the application name if required when deploying within
	 * an application server
	 */
	public void setApplicationName(String name){
		this.applicationName = name;
	}

	/**
	 * PUBLIC:
	 * Returns the amount of time in milliseconds that the service will wait between the time
	 * that the Remote Service is made available and this session's existences is announced.
	 */
	 public int getAnnouncementDelay(){
		return this.announcementDelay;
	 }

	/**
	 * PUBLIC:
	 * Use this method to get the application name 
	 */
	public String getApplicationName(){
		return this.applicationName;
	}
}
