/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     pvijayaratnam - cache coordination test implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.performance;

/*import oracle.eclipselink.coherence.integrated.cache.CoherenceInterceptor;
import oracle.eclipselink.coherence.integrated.config.CoherenceReadCustomizer;
import oracle.eclipselink.coherence.integrated.querying.DeleteObjectThroughCoherence;
import oracle.eclipselink.coherence.integrated.querying.InsertObjectToCoherence;
import oracle.eclipselink.coherence.integrated.querying.ReadObjectFromCoherence;
import oracle.eclipselink.coherence.integrated.querying.UpdateObjectToCoherence;*/

import org.eclipse.persistence.config.SessionCustomizer;
//import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;
//import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;

//import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
//import org.eclipse.persistence.sessions.coordination.TransportManager;
//import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;

public class CacheCoordinationSessionCustomizer implements SessionCustomizer {

    public void customize(Session session) throws Exception {
        /*ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        descriptor.setCacheInterceptorClass(CoherenceInterceptor.class);
        descriptor.setDefaultReadObjectQueryRedirector(new ReadObjectFromCoherence());
        /*descriptor.setDefaultUpdateObjectQueryRedirector(new UpdateObjectToCoherence());
        descriptor.setDefaultInsertObjectQueryRedirector(new InsertObjectToCoherence());
        descriptor.setDefaultDeleteObjectQueryRedirector(new DeleteObjectThroughCoherence());*/
        
        /*descriptor = session.getDescriptor(Address.class);
        descriptor.setCacheInterceptorClass(CoherenceInterceptor.class);
        descriptor.setDefaultReadObjectQueryRedirector(new ReadObjectFromCoherence());
        /*descriptor.setDefaultUpdateObjectQueryRedirector(new UpdateObjectToCoherence());
        descriptor.setDefaultInsertObjectQueryRedirector(new InsertObjectToCoherence());
        descriptor.setDefaultDeleteObjectQueryRedirector(new DeleteObjectThroughCoherence());*/
        
        /*descriptor = session.getDescriptor(Project.class);
        descriptor.setCacheInterceptorClass(CoherenceInterceptor.class);
        descriptor.setDefaultReadObjectQueryRedirector(new ReadObjectFromCoherence());
        /*descriptor.setDefaultUpdateObjectQueryRedirector(new UpdateObjectToCoherence());
        descriptor.setDefaultInsertObjectQueryRedirector(new InsertObjectToCoherence());
        descriptor.setDefaultDeleteObjectQueryRedirector(new DeleteObjectThroughCoherence());*/
        
        /*descriptor = session.getDescriptor(SmallProject.class);
        descriptor.setCacheInterceptorClass(CoherenceInterceptor.class);
        descriptor.setDefaultReadObjectQueryRedirector(new ReadObjectFromCoherence());
        /*descriptor.setDefaultUpdateObjectQueryRedirector(new UpdateObjectToCoherence());
        descriptor.setDefaultInsertObjectQueryRedirector(new InsertObjectToCoherence());
        descriptor.setDefaultDeleteObjectQueryRedirector(new DeleteObjectThroughCoherence());*/
        
        /*descriptor = session.getDescriptor(LargeProject.class);
        descriptor.setCacheInterceptorClass(CoherenceInterceptor.class);
        descriptor.setDefaultReadObjectQueryRedirector(new ReadObjectFromCoherence());
        /*descriptor.setDefaultUpdateObjectQueryRedirector(new UpdateObjectToCoherence());
        descriptor.setDefaultInsertObjectQueryRedirector(new InsertObjectToCoherence());
        descriptor.setDefaultDeleteObjectQueryRedirector(new DeleteObjectThroughCoherence());*/
        
        /*descriptor = session.getDescriptor(PhoneNumber.class);
        descriptor.setCacheInterceptorClass(CoherenceInterceptor.class);
        descriptor.setDefaultReadObjectQueryRedirector(new ReadObjectFromCoherence());
        /*descriptor.setDefaultUpdateObjectQueryRedirector(new UpdateObjectToCoherence());
        descriptor.setDefaultInsertObjectQueryRedirector(new InsertObjectToCoherence());
        descriptor.setDefaultDeleteObjectQueryRedirector(new DeleteObjectThroughCoherence());*/
        
        /*String cachecoordinationProtocol = session.getProperty("cachecoordination.protocol").toString();

        RemoteCommandManager rcm = new RemoteCommandManager((DatabaseSessionImpl) session);

        // Cachecoordination configuration JMS-specific properties
        if (cachecoordinationProtocol.equalsIgnoreCase("jms")) {

            JMSTopicTransportManager tm = new JMSTopicTransportManager(rcm);
            tm.setTopicHostUrl(session.getProperty("rcm.wls.jms.topichost.url").toString());
            tm.setTopicName(session.getProperty("rcm.wls.jms.topicname").toString());
            tm.setTopicConnectionFactoryName(session.getProperty("rcm.wls.jms.topic.connectionfactory.name").toString());
            rcm.setTransportManager(tm);
        }
        // Cachecoordination configuration RMI-specific properties
        else if (cachecoordinationProtocol.equalsIgnoreCase("rmi")) {

            ((DatabaseSessionImpl) session).getCommandManager().getDiscoveryManager().setAnnouncementDelay(Integer.parseInt(session.getProperty("announcement.delay").toString()));
            ((DatabaseSessionImpl) session).getCommandManager().getDiscoveryManager().setMulticastGroupAddress(session.getProperty("rcm.multicastgroup").toString());
            ((DatabaseSessionImpl) session).getCommandManager().getDiscoveryManager().setMulticastPort(Integer.parseInt(session.getProperty("rcm.multicastport").toString()));
            ((DatabaseSessionImpl) session).getCommandManager().getDiscoveryManager().setPacketTimeToLive(Integer.parseInt(session.getProperty("rcm.packet.timetolive").toString()));

            rcm.setUrl(session.getProperty("current.server.url").toString());
        }

        rcm.getTransportManager().setUserName(session.getProperty("server.user").toString());
        rcm.getTransportManager().setPassword(session.getProperty("server.pwd").toString());
        rcm.getTransportManager().setNamingServiceType(TransportManager.JNDI_NAMING_SERVICE);
        rcm.getTransportManager().setInitialContextFactoryName("weblogic.jndi.WLInitialContextFactory");
        rcm.getTransportManager().setShouldRemoveConnectionOnError(false);
        rcm.setShouldPropagateAsynchronously(true);
        rcm.setServerPlatform(((org.eclipse.persistence.sessions.DatabaseSession) session).getServerPlatform());
        ((DatabaseSessionImpl) session).setCommandManager(rcm);
        ((DatabaseSessionImpl) session).setShouldPropagateChanges(true);
        rcm.initialize();

        // Sleep to allow RCM to startup and find each session.
        try {
            Thread.sleep(2000);
        } catch (Exception ignore) {
        }*/

    }

}
