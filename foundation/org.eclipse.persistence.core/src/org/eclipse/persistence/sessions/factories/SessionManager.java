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
package org.eclipse.persistence.sessions.factories;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.logging.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Global session location.</p>
 *
 * <p><b>Description</b>: This allows for a global session local which can
 * be accessed globally from other classes.  This is needed for EJB data stores
 * as they must have a globally accessible place to access the session.
 * This can be by EJB session beans, BMP beans and CMP beans as well as Servlets and
 * other three-tier services.</p>
 *
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Store a global session.
 * <li> Allow the storage of alternative sessions as well.
 * </ul></p>
 *
 * @author James Sutherland
 * @since TOPLink/Java 3.0
 */
public class SessionManager {
    /** Allow for usage of schema validation to be configurable. */
    protected static boolean shouldUseSchemaValidation = true;
    
    protected static SessionManager manager = initializeManager();
    protected AbstractSession defaultSession;
    protected ConcurrentMap<String, Session> sessions = null;
    protected static boolean shouldPerformDTDValidation;

    /**
     * PUBLIC:
     * Return if schema validation will be used when parsing the 10g (10.1.3) sessions XML.
     */
    public static boolean shouldUseSchemaValidation() {
        return shouldUseSchemaValidation;
    }

    /**
     * PUBLIC:
     * Set if schema validation will be used when parsing the 10g (10.1.3) sessions XML.
     * By default schema validation is on, but can be turned off if validation problems occur,
     * or to improve parsing performance.
     */
    public static void setShouldUseSchemaValidation(boolean value) {
        shouldUseSchemaValidation = value;
    }
    
    /**
     * PUBLIC:
     * The default constructor to create a new session manager.
     */
    public SessionManager() {
        sessions = new ConcurrentHashMap<String, Session>(5);
    }

    /**
       * INTERNAL:
       * add an named session to the hashtable.
       * session must have a name prior to setting into session Manager
       */
    public void addSession(Session session) {
        getSessions().put(session.getName(), session);
    }

    /**
     * ADVANCED:
     * add an named session to the hashtable.
     */
    public void addSession(String sessionName, Session session) {
        session.setName(sessionName);
        getSessions().put(sessionName, session);
    }

    /**
     * PUBLIC:
     * Return the default session.
     * The session configuration is stored in a sessions.xml file in a
    * directory on your classpath. Other sessions are supported through the
    * getSession by name API.
     */
    public Session getDefaultSession() {
        if (defaultSession == null) {
            defaultSession = getSession("default");
        }
        return defaultSession;
    }

    /**
     * INTERNAL:
     * Destroy the session defined by sessionName on this manager.
     */
    public void destroySession(String sessionName) {
        DatabaseSession session = (DatabaseSession)getSessions().get(sessionName);

        if (session != null) {
            destroy(session);
        } else {
            logAndThrowException(SessionLog.WARNING, ValidationException.noSessionRegisteredForName(sessionName));
        }
    }

    private void destroy(DatabaseSession session) {
        try {
            if (session.isConnected()) {
                session.logout();
            }
        } catch (Throwable ignore) {
            // EL Bug 321843 - Must handle errors from logout.
            AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, AbstractSessionLog.CONNECTION, ignore);
        }

        sessions.remove(session.getName());
        session = null;
    }

    /**
     * INTERNAL:
     * Destroy all sessions held onto by this manager.
     */
    public void destroyAllSessions() {
        Iterator toRemoveSessions = new ArrayList(getSessions().values()).iterator();

        while (toRemoveSessions.hasNext()) {
            destroy((DatabaseSession)toRemoveSessions.next());
        }
    }

    /**
     * INTERNAL:
     */
    public synchronized SessionConfigs getInternalMWConfigObjects(String resourceName, ClassLoader objectClassLoader) {
        return getInternalMWConfigObjects(resourceName, objectClassLoader, true);
    }

    /**
     * INTERNAL:
     */
    public synchronized SessionConfigs getInternalMWConfigObjects(String resourceName, ClassLoader objectClassLoader, boolean validate) {
        return new XMLSessionConfigLoader(resourceName).loadConfigsForMappingWorkbench(objectClassLoader, validate);
    }

    /**
     * PUBLIC:
     * Return the singleton session manager.
     * This allow global access to a set of named sessions.
     */
    public static SessionManager getManager() {
        if (manager == null) {
            initializeManager();
        }

        return manager;
    }
    
    /**
     * INTERNAL:
     * Initialize the singleton session manager.
     */
    protected static SessionManager initializeManager() {
        return new SessionManager();
    }

    /**
     * PUBLIC:
     * Return the session by name.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getSession(String sessionName) {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();
        loader.setSessionName(sessionName);
        return getSession(loader);
    }

    /**
     * PUBLIC:
     * Return the session by name.
     * Log the session in only if specified.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getSession(String sessionName, boolean shouldLoginSession) {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();
        loader.setSessionName(sessionName);
        loader.setShouldLogin(shouldLoginSession);
        return getSession(loader);
    }

    /**
     * PUBLIC:
     * Return the session by name.
     * Log the session in only if specified.
     * Refresh the session only if specified.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getSession(String sessionName, boolean shouldLoginSession, boolean shouldRefreshSession) {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();
        loader.setSessionName(sessionName);
        loader.setShouldLogin(shouldLoginSession);
        loader.setShouldRefresh(shouldRefreshSession);
        return getSession(loader);
    }

    /**
     * PUBLIC:
     * Return the session by name, using the classloader of the Object specified.
     * This method is used in older versions of BMP support
     * This method will cause the class loader of the provided object to be
     * compared with the classloader used to load the original session of this
     * name, with this classloader.  If they are not the same then the session
     * will be refreshed.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getSession(String sessionName, Object objectBean) {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();
        loader.setSessionName(sessionName);
        ClassLoader classLoader = null;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                classLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(objectBean.getClass()));
            }catch (PrivilegedActionException ex){
                throw (RuntimeException) ex.getCause();
            }
        }else{
            classLoader = PrivilegedAccessHelper.getClassLoaderForClass(objectBean.getClass());
        }
        loader.setClassLoader(classLoader);
        return getSession(loader);
    }

    /**
     * PUBLIC:
     * Return the session by name, in the file specified.
     * Login the session.
     */
    public org.eclipse.persistence.internal.sessions.AbstractSession getSession(String sessionName, String filename) {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();
        loader.setSessionName(sessionName);
        loader.setResourceName(filename);
        return getSession(loader);
    }
    
    /**
     * PUBLIC:
     * Return the session by name, in the file specified, using the class loader to find the resource.
     * This method will cause the class loader to be compared with the classloader
     * used to load the original session of this name.
     * If they are not the same then the session will be refreshed.
     */
    public AbstractSession getSession(String sessionName, String filename, ClassLoader classLoader) {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();
        loader.setSessionName(sessionName);
        loader.setResourceName(filename);
        loader.setClassLoader(classLoader);
        loader.setShouldCheckClassLoader(true);
        return getSession(loader);
    }

    /**
     * PUBLIC:
     * Return the session by name.
     * Provide the class loader for loading the project, the configuration file
     * and the deployed classes.
     * E.g. SessionManager.getManager().getSession("mySession", MySessionBean.getClassLoader());
     * This method will cause the class loader to be compared with the classloader
     * used to load the original session of this name.
     * If they are not the same then the session will be refreshed.
     */
    public AbstractSession getSession(String sessionName, ClassLoader objectClassLoader) {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader();
        loader.setSessionName(sessionName);
        loader.setClassLoader(objectClassLoader);
        loader.setShouldCheckClassLoader(true);
        return getSession(loader);
    }

    /**
     * PUBLIC:
     * Return the session by name, loading the configuration from the file
     * specified in the XMLSessionConfigLoader. Provide the class loader for
     * loading the project, the configuration file and the deployed classes.
     * This method will cause the class loader to be compared with the classloader
     * used to load the original session of this name.
     * If they are not the same then the session will be refreshed.
     */
    public AbstractSession getSession(XMLSessionConfigLoader loader, String sessionName, ClassLoader objectClassLoader) {
        if (loader == null){
            loader = new XMLSessionConfigLoader();
        }
        loader.setSessionName(sessionName);
        loader.setClassLoader(objectClassLoader);
        loader.setShouldCheckClassLoader(true);
        return getSession(loader);
    }


    /**
     * PUBLIC:
     * Return the session by name, loading the configuration from the file
     * specified in the loader. Provide the class loader for loading the
     * project, the configuration file and the deployed classes. Pass in true for
     * shouldLoginSession if the session returned should be logged in before
     * returned otherwise false. Pass in true for shouldRefreshSession if the
     * XMLSessionConfigLoader should reparse the configuration file for new
     * sessions. False, will cause the XMLSessionConfigLoader not to parse the
     * file again.
     */
    public AbstractSession getSession(XMLSessionConfigLoader loader, String sessionName, ClassLoader objectClassLoader, boolean shouldLoginSession, boolean shouldRefreshSession) {
        if (loader == null){
            loader = new XMLSessionConfigLoader();
        }
        loader.setSessionName(sessionName);
        loader.setClassLoader(objectClassLoader);
        loader.setShouldLogin(shouldLoginSession);
        loader.setShouldRefresh(shouldRefreshSession);
        return getSession(loader);
    }

    /**
     * PUBLIC:
     * Return the session by name, loading the configuration from the file
     * specified in the loader. Provide the class loader for loading the
     * project, the configuration file and the deployed classes. Pass in true for
     * shouldLoginSession if the session returned should be logged in before
     * returned otherwise false. Pass in true for shouldRefreshSession if the
     * XMLSessionConfigLoader should reparse the configuration file for new
     * sessions. False, will cause the XMLSessionConfigLoader not to parse the
     * file again.
     * Pass true for shouldCheckClassLoader will cause the class loader to be compared with the classloader
     * used to load the original session of this name.
     * If they are not the same then the session will be refreshed, this can be used for re-deployment.
     */
    public AbstractSession getSession(XMLSessionConfigLoader loader, String sessionName, ClassLoader objectClassLoader, boolean shouldLoginSession, boolean shouldRefreshSession, boolean shouldCheckClassLoader) {
        if (loader == null){
            loader = new XMLSessionConfigLoader();
        }
        loader.setSessionName(sessionName);
        loader.setClassLoader(objectClassLoader);
        loader.setShouldLogin(shouldLoginSession);
        loader.setShouldRefresh(shouldRefreshSession);
        loader.setShouldCheckClassLoader(shouldCheckClassLoader);
        return getSession(loader);
    }
        
    /**
     * PUBLIC:
     * Return the session by name, loading the configuration from the file
     * specified in the loader, using the loading options provided on the loader.
     */
    public AbstractSession getSession(XMLSessionConfigLoader loader) { 
        AbstractSession session = (AbstractSession)getSessions().get(loader.getSessionName());
        boolean shouldRefreshSession = loader.shouldRefresh();
        if (loader.shouldCheckClassLoader() && (session != null) && !session.getDatasourcePlatform().getConversionManager().getLoader().equals(loader.getClassLoader())) {
            //bug 3766808  if a different classloader is being used then a reload of the session should
            //be completed otherwise failures may occur
            shouldRefreshSession = true;
        }
        if ((session == null) || shouldRefreshSession) {
            // PERF: Avoid synchronization for normal get.
            synchronized (this) {
                // Must re-assert checks inside synchronization.
                session = (AbstractSession)getSessions().get(loader.getSessionName());
                if (loader.shouldCheckClassLoader() && (session != null) && !session.getDatasourcePlatform().getConversionManager().getLoader().equals(loader.getClassLoader())) {
                    //bug 3766808  if a different classloader is being used then a reload of the session should
                    //be completed otherwise failures may occur
                    shouldRefreshSession = true;
                }
                if ((session == null) || shouldRefreshSession) {
                    if (session != null) {
                        if (session.isDatabaseSession() && session.isConnected()) {
                            // Must handles errors from logout as session maybe hosed.
                            try {
                                ((DatabaseSession)session).logout();
                            } catch (Throwable ignore) {
                                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, AbstractSessionLog.CONNECTION, ignore);
                            }
                        }        
                        getSessions().remove(loader.getSessionName());
                    }
        
                    if (loader.load(this, loader.getClassLoader())) {
                        session = (AbstractSession)getSessions().get(loader.getSessionName());
                    }
                }
            }
        }

        // Connect the session if specified.
        if (session == null) {
            logAndThrowException(SessionLog.WARNING, ValidationException.noSessionFound(loader.getSessionName(), loader.getResourcePath()));
        } else if (loader.shouldLogin() && !session.isConnected()) {
            // PERF: Avoid synchronization for normal get.
            synchronized (this) {
                // Must re-assert checks inside synchronization.
                if (loader.shouldLogin() && !session.isConnected()) {
                    ((DatabaseSession)session).login();
                }
            }
        }

        return session;
    }

    /**
     * INTERNAL:
     * Log exceptions to the default log then throw them.
     */
    private void logAndThrowException(int level, RuntimeException exception) throws RuntimeException {
        AbstractSessionLog.getLog().logThrowable(level, AbstractSessionLog.CONNECTION, exception);
        throw exception;
    }


    /**
     * INTERNAL:
     * Set a hashtable of all sessions
     */
    public void setSessions(ConcurrentMap sessions) {
        this.sessions = sessions;
    }

    /**
     * INTERNAL:
     * Return a hashtable on all sessions.
     */
    public ConcurrentMap getSessions() {
        return sessions;
    }

    /**
     * PUBLIC:
     * Set the default session.
     * If not set the session configuration is stored in a sessions.xml 
    * file in a TopLink directory on your classpath.
     * Other sessions are supported through the getSession by name API.
     */
    public void setDefaultSession(Session defaultSession) {
        this.defaultSession = (org.eclipse.persistence.internal.sessions.AbstractSession)defaultSession;
        addSession("default", defaultSession);
    }

    /**
     * INTERNAL:
     * Set the singleton session manager.
     * This allows global access to a set of named sessions.
     */
    public static void setManager(SessionManager theManager) {
        manager = theManager;
    }

}
