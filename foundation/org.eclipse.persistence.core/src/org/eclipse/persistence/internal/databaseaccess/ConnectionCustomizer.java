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
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - New file introduced for bug 224964: Provide support for Proxy Authentication through JPA. 
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * The base class for connection customization.
 * 
 * The extending class must not alter the attributes defined in this class.
 * 
 * Consider implementing equals method on the extending class so that
 * the two instances are consider equal if they apply exactly the same customizations.
 * This allows to skip unnecessary work when overriding customizer defined by
 * ServerSession by the one defined by ClientSession: in case the two customizers are
 * equal the old (ServerSession's) customizer is kept.
 * That may save considerable effort in internal connection pooling case:
 * if both ServerSession and ClientSession have exactly the same proxy properties
 * (that happens if ClientSession doesn't have any proxy properties at all and
 * just "inherits" all the properties from its parent ServerSession)
 * then without correct customizer equality check the proxy session opened
 * by ServerSession would have been closed, only to be re-opened again by ClientSession
 * customizer using exactly the same proxy properties (and then the same happens again
 * when ClientSession releases connection).  
 */
public abstract class ConnectionCustomizer implements Cloneable {
    protected Accessor accessor;
    protected Session session;
    private ConnectionCustomizer prevCustomizer;
    
    /**
     * INTERNAL:
     * Constructor accepts the accessor to which the customizer will be applied and the session that used for customization.
     * The accessor and the session couldn't be altered during the connector's lifetime,
     * the only exception is of the new accessor by DatasourceAccessor.clone method. 
     */
    public ConnectionCustomizer(Accessor accessor, Session session) {
        this.accessor = accessor;
        this.session = session;
    }
    
    /**
     * INTERNAL:
     * Used only by DatasourceAccessor.clone method.
     */
    void setAccessor(Accessor accessor) {
        this.accessor = accessor;
    }
    
    /**
     * INTERNAL:
     */
    public Accessor getAccessor() {
        return accessor;
    }
    
    /**
     * INTERNAL:
     */
    public Session getSession() {
        return session;
    }
    
    /**
     * INTERNAL:
     */
    public ConnectionCustomizer getPrevCustomizer() {
        return prevCustomizer;
    }
    
    /**
     * INTERNAL:
     */
    public void setPrevCustomizer(ConnectionCustomizer prevCustomizer) {
        this.prevCustomizer = prevCustomizer;
    }
    
    /**
     * INTERNAL:
     * Empty customizer that does nothing.
     * Used when ServerSession defines customizer and ClientSession
     * explicitly demands no customization.
     * Example: ServerSession has proxy properties, ClientSession
     * has PersistenceUnitProperties.ORACLE_PROXY_TYPE property with an empty String value.
     * Using Empty customizer allows to avoid customization as well as
     * cache the previous (ServerSession - created) customizer,
     * which is brought back after ClientSession releases the accessor.
     */
    static class Empty extends ConnectionCustomizer {
        public Empty(Accessor accessor, Session session) {
            super(accessor, session);
        }
        public void customize() {
        }
        public boolean isActive() {
            return false;
        }
        public void clear() {            
        }
        public boolean equals(Object obj) {
            return (obj instanceof Empty);
        }
    }
    public static ConnectionCustomizer createEmptyCustomizer(Session session) {
        return new Empty(null, session);
    }
    
    /**
     * INTERNAL:
     * Applies customization to connection.
     * Called only if connection is not already customized (isActive()==false). 
     * The method may throw SQLException wrapped into DatabaseException.
     * isActive method called after this method should return true only in case
     * the connection was actually customized. 
     */
    public abstract void customize();
    
    /**
     * INTERNAL:
     * Indicated whether the connection is currently customized.
     */
    public abstract boolean isActive();

    /**
     * INTERNAL:
     * Clears customization from connection.
     * Called only if connection is customized (isActive()==true). 
     * If the method fails due to SQLException it should "eat" it
     * (just like DatasourceAccessor.closeConnection method).
     * isActive method called after this method should always return false.
     */
    public abstract void clear();
}
