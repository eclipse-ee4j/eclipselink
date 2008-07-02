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
package org.eclipse.persistence.platform.server.wls;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.services.mbean.MBeanDevelopmentServices;
import org.eclipse.persistence.services.mbean.MBeanRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebLogic10 specific behavior.
 * This includes WebLogic 10.3 behavior.
 *
 */
public class WebLogic_10_Platform extends WebLogic_9_Platform {
    // see http://e-docs.bea.com/wls/docs90/jmx/accessWLS.html#1119237
    /** This JNDI address is for JMX MBean registration */
    private static final String JMX_JNDI_RUNTIME_REGISTER = "java:comp/env/jmx/runtime";  
    /* 
     * If the cached MBeanServer is not used, then the unregister jndi address must be used to create a context
     * Note: the context must be explicitly closed after use or we may cache the user and get a
     * weblogic.management.NoAccessRuntimeException when trying to use the associated MBeanServer
     * see http://bugs.eclipse.org/238343
     * see http://e-docs.bea.com/wls/docs100/jndi/jndi.html#wp467275  
     */
    /** This JNDI address is for JMX MBean unregistration */    
    private static final String JMX_JNDI_RUNTIME_UNREGISTER = "java:comp/jmx/runtime";
    /** This is the prefix for all MBeans that are registered with their specific session name appended */
    private static final String JMX_REGISTRATION_PREFIX = "EclipseLink_Domain:Name=";
    // Secondary override properties can be set to disable MBean registration
    /** This System property "eclipselink.register.dev.mbean" when set to true will enable registration/unregistration of the DevelopmentServices MBean */
    public static final String  JMX_REGISTER_DEV_MBEAN_PROPERTY = "eclipselink.register.dev.mbean";
    /** This System property "eclipselink.register.run.mbean" when set to true will enable registration/unregistration of the RuntimeServices MBean */    
    public static final String  JMX_REGISTER_RUN_MBEAN_PROPERTY = "eclipselink.register.run.mbean";
    
    // Any value such as true will turn on the MBean
    protected boolean shouldRegisterDevelopmentBean = System.getProperty(JMX_REGISTER_DEV_MBEAN_PROPERTY) != null;
    protected boolean shouldRegisterRuntimeBean = System.getProperty(JMX_REGISTER_RUN_MBEAN_PROPERTY) != null;
    
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebLogic_10_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL: 
     * serverSpecificRegisterMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public void serverSpecificRegisterMBean() {
        MBeanServer mBeanServerRuntime = null;      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        Context initialContext = null;        
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
            try {
                initialContext = new InitialContext();
                mBeanServerRuntime = (MBeanServer) initialContext.lookup(JMX_JNDI_RUNTIME_REGISTER);
                // Attempt to register new mBean with the server
                if (shouldRegisterDevelopmentBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Development_" + sessionName + ",Type=Configuration");
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
                    }

                    MBeanDevelopmentServices mbean = new MBeanDevelopmentServices((Session)getDatabaseSession());
                    ObjectInstance info = null;
                    try {
                        info = mBeanServerRuntime.registerMBean(mbean, name);
                    } catch(InstanceAlreadyExistsException iaee) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", registrationProblem);
                    }
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "registered_mbean", info);
                }

                if (shouldRegisterRuntimeBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Runtime_" + sessionName + ",Type=Reporting");                        
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
                    }
                    MBeanRuntimeServices runtimeServices = new MBeanRuntimeServices((Session)getDatabaseSession());
                    ObjectInstance runtimeInstance = null;
                    try {
                        runtimeInstance = mBeanServerRuntime.registerMBean(runtimeServices, name);
                    } catch(InstanceAlreadyExistsException iaee) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", registrationProblem);
                    }
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "registered_mbean", runtimeInstance);          
                }
            } catch (NamingException ne) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "failed_to_find_mbean_server", ne);
            } catch (Exception exception) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
            } finally {
                // close the context
                // see http://forums.bea.com/thread.jspa?threadID=600004445
                // see http://e-docs.bea.com/wls/docs81/jndi/jndi.html#471919
                // see http://e-docs.bea.com/wls/docs100/jndi/jndi.html#wp467275
                try {
                    mBeanServerRuntime = null;
                    initialContext.close();                    
                } catch (NamingException ne) {
                    // exceptions on context close will be ignored, the context will be GC'd
                }
            }
        }
    }

    /**
     * INTERNAL: 
     * serverSpecificUnregisterMBean(): Server specific implementation of the
     * un-registration of the JMX MBean from its server during session logout.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     */
    public void serverSpecificUnregisterMBean() {
        MBeanServer mBeanServerRuntime = null;      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        Context initialContext = null;        
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
            try {
                initialContext = new InitialContext();
                mBeanServerRuntime = (MBeanServer) initialContext.lookup(JMX_JNDI_RUNTIME_UNREGISTER);
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
                        ((MBeanServer)mBeanServerRuntime).unregisterMBean(name);
                    } catch(InstanceNotFoundException inf) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", inf);
                    } catch (MBeanRegistrationException mbre) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mbre);                        
                    }                                        
                }

                if (shouldRegisterRuntimeBean) {
                    try {                        
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Runtime_" + sessionName + ",Type=Reporting");                        
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
                    }

                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "unregistering_mbean", name);
                    try {
                        ((MBeanServer)mBeanServerRuntime).unregisterMBean(name);
                    } catch(InstanceNotFoundException inf) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", inf);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", registrationProblem);
                    }                              
                }
            } catch (NamingException ne) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "failed_to_find_mbean_server", ne);
            } catch (Exception exception) {
                // Trap a possible WebLogic specific [weblogic.management.NoAccessRuntimeException]
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
            } finally {
                // close the context
                // see http://forums.bea.com/thread.jspa?threadID=600004445
                // see http://e-docs.bea.com/wls/docs81/jndi/jndi.html#471919
                // see http://e-docs.bea.com/wls/docs100/jndi/jndi.html#wp467275
                try {
                    mBeanServerRuntime = null;
                    initialContext.close();                    
                } catch (NamingException ne) {
                    // exceptions on context close will be ignored, the context will be GC'd
                }
            }
        }
    }

    /**
     * Remove JMX reserved characters from the session name
     * @param aSession
     * @return
     */
    private String getMBeanSessionName() {
        // Check for a valid session - should never occur though        
        if(null != getDatabaseSession()) {
            // remove any JMX reserved characters when the session name is file:/drive:/directory
            return getDatabaseSession().getName().replaceAll("[=,:]", "_");
        } else {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, "session_key_for_mbean_name_is_null");
            return null;
        }
    }
    
}
