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
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.platform.server;

import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.services.mbean.MBeanDevelopmentServices;
import org.eclipse.persistence.services.mbean.MBeanRuntimeServicesMBean;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 * Subclass of org.eclipse.persistence.platform.server.ServerPlatformBase 
 * in support of the JMXEnabledPlatform interface
 * <p>
 * This is the abstract superclass of all platforms for all servers that contain a subclass
 * that implements the JMXEnabledPlatform interface. 
 * Each DatabaseSession
 * contains an instance of the receiver, to help the DatabaseSession determine:
 * <p><ul>
 * <li> Whether or not to enable JTA (external transaction control)
 * <li> How to register/unregister for runtime services (JMX/MBean)
 * <li> Whether or not to enable runtime services
 * </ul><p>
 * Subclasses already exist to provide configurations for Oc4J, WebLogic, JBoss, NetWeaver, GlassFish and WebSphere.
 * <p>
 * If the versioned platform subclass is JMX enabled by EclipseLink (registers MBeans) then that 
 * server platform must implement the JMXEnabledPlatform interface
 * To provide some different behavior than the provided ServerPlatform(s), we recommend
 * subclassing org.eclipse.persistence.platform.server.JMXServerPlatformBase (or a subclass),
 * and overriding:
 * <ul>
 * <li>JMXEnabledPlatform.prepareServerSpecificServicesMBean()
 * </ul>
 * for the desired behavior.
 *
 * @see org.eclipse.persistence.platform.server.ServerPlatformBase
 * @since EclipseLink 2.1.1
 */
public abstract class JMXServerPlatformBase extends ServerPlatformBase {

    /** This is the prefix for all MBeans that are registered with their specific session name appended */
    public static final String JMX_REGISTRATION_PREFIX = "TopLink:Name=";

    /** Cache the ServerPlatform MBeanServer for performance */
    private MBeanServer mBeanServer = null;
    
    /** cache the RuntimeServices MBean - during platform construction for JMXEnabledPlatform implementors */
    private MBeanRuntimeServicesMBean runtimeServicesMBean = null;

    /** moduleName determination is available during MBean registration only */
    private String moduleName = null;

    /** applicationName determination is available during MBean registration only */
    private String applicationName = null;
    
    /**
     * INTERNAL:
     * Default Constructor: Initialize so that runtime services and JTA are enabled. 
     */
    public JMXServerPlatformBase(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        // Answer "unknown" as a default for platforms that do not implement getModuleName()
        applicationName = DEFAULT_SERVER_NAME_AND_VERSION;
        moduleName = DEFAULT_SERVER_NAME_AND_VERSION;        
    }
    
    /** 
     * INTERNAL:
     * 
     * @return the JMX specification MBeanServer
     */
    public MBeanServer getMBeanServer() {
        // lazy initialize the MBeanServer reference
        if(null == mBeanServer) {
            try {
                // Attempt to get the first MBeanServer we find - usually there is only one - when agentId == null we return a List of them
                List<MBeanServer> mBeanServerList = MBeanServerFactory.findMBeanServer(null);
                if(null == mBeanServerList || mBeanServerList.isEmpty()) {
                    // Unable to acquire a JMX specification List of MBeanServer instances
                    //System.out.println("Unable to get an MBeanServer list");
                } else {
                    // Use the first MBeanServer by default - there may be multiple domains each with their own MBeanServer
                    mBeanServer = mBeanServerList.get(0);
                    if(mBeanServerList.size() > 1) {
                        // There are multiple MBeanServerInstances - just warn for now
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, 
                                "jmx_mbean_runtime_services_registration_encountered_multiple_mbeanserver_instances",
                                mBeanServerList.size(), mBeanServer);
                        // Check the domain if it exists and use a non-null one
                        
                        /*for(MBeanServer anMBeanServer : mBeanServerList) {
                            if(null != anMBeanServer.getDefaultDomain()) {
                                mBeanServer = anMBeanServer;
                            }
                        }*/
                    }
                    // iterate and print the names of the MBeanServer instances
                    for(MBeanServer anMBeanServer : mBeanServerList) {
                        AbstractSessionLog.getLog().log(SessionLog.INFO, 
                                "jmx_mbean_runtime_services_registration_mbeanserver_print",
                                anMBeanServer, anMBeanServer.getMBeanCount());
                    }
                }                
            } catch (Exception e) {
                // TODO: Warning required
                e.printStackTrace();
            }
        }
        return mBeanServer;
    } 
    
    /**
     * INTERNAL: serverSpecificRegisterMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * Default is to do nothing. This should be subclassed if required.
     * For platform classes that override the JMXEnabledPlatform - the services MBean 
     * is created at platform construction for use during MBean registration here.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public void serverSpecificRegisterMBean() {
        
        // get and cache module and application name during registration
        MBeanServer mBeanServerRuntime = getMBeanServer();      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
            try {                    
                // Attempt to register new mBean with the server
                if (null != mBeanServerRuntime && shouldRegisterDevelopmentBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Development-" + sessionName + ",Type=Configuration");
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
                    }

                    // Currently the to be deprecated development MBean is generic to all server platforms
                    MBeanDevelopmentServices developmentMBean = new MBeanDevelopmentServices(getDatabaseSession());
                    ObjectInstance info = null;
                    try {
                        info = mBeanServerRuntime.registerMBean(developmentMBean, name);
                    } catch(InstanceAlreadyExistsException iaee) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", registrationProblem);
                    } catch (Exception e) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", e);
                    }
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "registered_mbean", info);
                }

                if (null != mBeanServerRuntime && shouldRegisterRuntimeBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Session(" + sessionName + ")");                        
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
                    }
                    
                    ObjectInstance runtimeInstance = null;
                    try {
                        // The cached runtimeServicesMBean is a server platform specific instance
                        runtimeInstance = mBeanServerRuntime.registerMBean(runtimeServicesMBean, name);
                        setRuntimeServicesMBean(runtimeServicesMBean);
                    } catch(InstanceAlreadyExistsException iaee) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", registrationProblem);
                    } catch (Exception e) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", e);
                    }
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "registered_mbean", runtimeInstance);          
                }
            } catch (Exception exception) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
            }
        }
    }
    
    /**
     * INTERNAL: 
     * serverSpecificUnregisterMBean(): Server specific implementation of the
     * de-registration of the JMX MBean from its server during session logout.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     */
    public void serverSpecificUnregisterMBean() {
        MBeanServer mBeanServerRuntime = getMBeanServer();      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
            try {
                
                // Attempt to register new mBean with the server
                if (shouldRegisterDevelopmentBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Development_" + sessionName + ",Type=Configuration");
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
                    }

                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "unregistering_mbean", name);
                    try {
                        mBeanServerRuntime.unregisterMBean(name);
                    } catch(InstanceNotFoundException inf) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", inf);
                    } catch (MBeanRegistrationException mbre) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mbre);                        
                    }                                        
                }

                if (shouldRegisterRuntimeBean) {
                    try {                        
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Session(" + sessionName + ")");                        
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
                    }

                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "unregistering_mbean", name);
                    try {
                        mBeanServerRuntime.unregisterMBean(name);
                    } catch(InstanceNotFoundException inf) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", inf);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", registrationProblem);
                    }                              
                }
            } catch (Exception exception) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
            } finally {
                // de reference the mbean
                this.setRuntimeServicesMBean(null);
            }
        }
    }    
    
    /**
     * Remove JMX reserved characters from the session name
     * @param aSession
     * @return
     */
    protected String getMBeanSessionName() {
        // Check for a valid session - should never occur though        
        if(null != getDatabaseSession() && null != getDatabaseSession().getName()) {
            // remove any JMX reserved characters when the session name is file:/drive:/directory
            return getDatabaseSession().getName().replaceAll("[=,:]", "_");
        } else {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, "session_key_for_mbean_name_is_null");
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Return the cached server specific services MBean 
     */
    protected MBeanRuntimeServicesMBean getRuntimeServicesMBean() {
        return runtimeServicesMBean;
    }

    /**
     * INTERNAL:
     * Set the cached server specific services MBean
     * @param runtimeServicesMBean
     */
    protected void setRuntimeServicesMBean(MBeanRuntimeServicesMBean runtimeServicesMBean) {
        this.runtimeServicesMBean = runtimeServicesMBean;
    }

    /**
     * INTERNAL: 
     * getModuleName(): Answer the name of the context-root of the application that this session is associated with.
     * Answer "unknown" if there is no module name available.
     * Default behavior is to return "unknown" - we override this behavior here for JBoss.
     * 
     * There are 4 levels of implementation.
     * 1) use the property override jboss.moduleName, or
     * 2) perform a reflective jboss.work.executeThreadRuntime.getModuleName() call, or
     * 3) extract the moduleName:persistence_unit from the jboss classloader string representation, or
     * 3) defer to superclass - usually return "unknown"
     *
     * @return String moduleName
     */
    @Override
    public String getModuleName() {        
        return this.moduleName;
    }
    
    protected void setModuleName(String aName) {
        moduleName = aName;
    }
    
    /**
     * INTERNAL: 
     * getApplicationName(): Answer the name of the module (EAR name) that this session is associated with.
     * Answer "unknown" if there is no application name available.
     * Default behavior is to return "unknown" - we override this behavior here for JBoss.
     * 
     * There are 4 levels of implementation.
     * 1) use the property override weblogic.applicationName, or
     * 2) perform a reflective weblogic.work.executeThreadRuntime.getApplicationName() call, or
     * 3) extract the moduleName:persistence_unit from the weblogic classloader string representation, or
     * 3) defer to superclass - usually return "unknown"
     *
     * @return String applicationName
     */
    public String getApplicationName() {
        return this.applicationName;
    }
    
    
    public void setApplicationName(String aName) {
        applicationName = aName;
    }

}
