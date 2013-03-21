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

import java.util.Collection;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.sessions.factories.SessionManager;


/**
 * Helper class to simplify the development and generation of code that accesses
 * TopLink through the SessionManager (sessions config XML).
 * Responsibilities:<ul>
 * <li> Lookup of a session by name using default or provided sessions config location
 * <li> Support lookup of active UnitOfWork and Session in JTA environments
 * <li> Hot/Re-deployment handling of applications
 * <li> Detachment helpers to simplify usage within a local session bean
 * </ul>
 *
 * Basic usage example:
 * <code>
 * SessionFactory = sessionFactory = new SessionFactory("session-name");
 *
 * ...
 *
 * public List read(Vector args) {
 *    Session session = sessionFactory.acquireSession();
 *
 *    List results = (List) session.executeQuery("query-name", MyClass.class, args);
 *
 *    session.release();
 *    return results;
 * }
 *
 * public void write(MyClass detachedInstance) {
 *    UnitOfWork uow = sessionFactory.acquireUnitOfWork();
 *
 *    MyClass workingCopy = (MyClass) uow.readObject(detachedInstance);
 *
 *    if (workingCopy == null) {
 *       throw new MyException("Cannot write changes. Object does not exist");
 *    }
 *
 *    uow.deepMergeClone(detachedInstance);
 *
 *    uow.commit();
 * }
 * </code>
 *
 * <b>Detachment</b>: The detach helper methods are provided to assist with the
 * construction of applications. This helper class was designed for use within
 * session beans (SB) and in the case of local SBs the objects returned are not
 * serialized. Since EclipseLink's default behavior is to return the shared instance
 * from the cache and rely on developers to only modify instances within a
 * UnitOfWork this may be an issue. The client to the local session bean may
 * try to modify the instance and thus corrupt the cache. By detaching the object
 * the client to the session bean gets its own isolated copy that it can freely
 * modify. This provides the same functionality as with a remote session bean
 * and allows the developer the choice in how/when objects are detached.
 * <i>Note</i>: The above code example shows how a detached instance can have
 * changes made to it persisted through use of the UnitOfWork merge API.
 *
 * @author Doug Clarke & John Braken
 * @version 10.1.3
 * @since Dec 10, 2006
 */
public class SessionFactory {
    /**
     * Location for the sessions.xml file. The default here is the most common.
     * If none is provided then EclipseLink's default locations of 'sessions.xml'
     * and 'META-INF/sessions.xml' will be tried.
     */
    private String sessionXMLPath;
    private String sessionName;

    /**
     * Constructor for creating a new EclipseLinkSessionHelper instance.
     *
     * @param sessionsXMLPath - resource path of the sessions configuration xml.
     * @param sessionName - name of the session to use.
     */
    public SessionFactory(String sessionsXMLPath, String sessionName) {
        this.sessionXMLPath = sessionsXMLPath;
        this.sessionName = sessionName;
    }

    public SessionFactory(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getSessionXMLPath() {
        return this.sessionXMLPath;
    }
    
    /**
     * The class-loader returned form this call will be used when loading the
     * EclipseLink configuration. By default this is the current thread's loader.
     * If this is not the case users can subclass this session factory and 
     * override this method to provide a different loader.
     */
    protected ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Helper method that looks up the singleton session and ensure that
     * if the application has been hot-deployed it gets a fresh version of the
     * server.
     */
    public DatabaseSession getSharedSession() {
        return getSharedSession(true, false);
    }

    /**
     * Used in place of getSharedSession() when the calling application needs 
     * access to the session prior to login or it wishes to force the session 
     * configuration to be re-loaded an applied. This also makes use of the 
     * current class-loader return from getClassLoader() and a SessionManager 
     * class-loader check to see if the application was loaded by another 
     * class-loader and is should this be refreshed as the application has been 
     * hot deployed.
     */
    public DatabaseSession getSharedSession(boolean login, boolean refresh) {
        XMLSessionConfigLoader xmlLoader;

        if (getSessionXMLPath() != null) {
            xmlLoader = new XMLSessionConfigLoader(getSessionXMLPath());
        } else {
            xmlLoader = new XMLSessionConfigLoader();
        }

        return (DatabaseSession)SessionManager.getManager().getSession(xmlLoader, 
                                                                       getSessionName(), 
                                                                       getClassLoader(), 
                                                                       login, 
                                                                       refresh, 
                                                                       true);
    }

    /**
     * Returns the Session active for this specified helper. If the EclipseLink
     * session does not have an external transaction controller or there is
     * not an active JTA transaction then a newly acquire client session is
     * returned on each call.
     *
     * This method also properly handles acquire a client session from a broker
     * as well as returning the shared session in the case it is a database
     * session.
     */
    public Session acquireSession() {
        Session sharedSession = getSharedSession();

        if (sharedSession.hasExternalTransactionController()) {
            UnitOfWork uow = sharedSession.getActiveUnitOfWork();
            if (uow != null) {
                return uow.getParent();
            }
        }

        if (sharedSession.isServerSession()) {
            return ((Server)sharedSession).acquireClientSession();
        }
        if (sharedSession.isSessionBroker()) {
            SessionBroker broker = (SessionBroker)sharedSession;
            if (broker.isServerSessionBroker()) {
                return broker.acquireClientSessionBroker();
            }
            return broker;
        }
        // Assume we have a database session and return it.
        return sharedSession;
    }

    /**
     * Looks up the active UnitOfWork using either the global JTA TX or acquires
     * a new one from the active session.
     */
    public UnitOfWork acquireUnitOfWork() {
        return acquireUnitOfWork(getSharedSession());
    }

    /**
     * Looks up the active UnitOfWork using either the global JTA TX or acquires
     * a new one from the active session. THis method should be used if a session
     * has already been acquired.
     */
    public UnitOfWork acquireUnitOfWork(Session session) {
        if (session.hasExternalTransactionController()) {
            return session.getActiveUnitOfWork();
        }

        return session.acquireUnitOfWork();
    }

    /**
     * Build a detached copy using a one-off UnitOfWork.
     * This simulates creation of a copy through serialization when using a
     * remote session bean if this copy process is not done the user of this
     * helper would return the shared copy from the cache that should not be
     * modified. These detachment methods *MUST* be used for local session
     * beans where the returned objects can be modified by the client.
     *
     * @param entity an existing persistent entity
     * @return a copy of the entity for use in a local client that may make changes to it
     */
    public Object detach(Object entity) {
        UnitOfWork uow = 
            ((org.eclipse.persistence.internal.sessions.AbstractSession)getSharedSession()).acquireNonSynchronizedUnitOfWork(null);

        Object copy = uow.registerObject(entity);
        uow.release();

        return copy;
    }

    public Collection detach(Collection entities) {
        UnitOfWork uow = 
            ((org.eclipse.persistence.internal.sessions.AbstractSession)getSharedSession()).acquireNonSynchronizedUnitOfWork(null);

        Collection copies = uow.registerAllObjects(entities);
        uow.release();

        return copies;
    }

}
