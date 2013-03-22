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
 *     ailitchev - bug  235433: Can't customize ConnectionPolicy through JPA + some comments.
 ******************************************************************************/  
package org.eclipse.persistence.sessions.server;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.internal.localization.*;

/**
 * <p>
 * <b>Purpose</b>: Used to specify how a client session's should be allocated.
 * <p>
 * <b>Description</b>: The ConnectionPolicy is used to indicate how a client
 * session will interact with the internal or external JDBC connection pool/data
 * source. The default ConnectionPolicy is held on the ServerSession but this
 * can be overridden for any specific client session when it is acquired.
 * 
 * @see ServerSession#getDefaultConnectionPolicy()
 * @see ServerSession#acquireClientSession(ConnectionPolicy)
 * @see ServerSession#acquireClientSession(ConnectionPolicy, Map)
 * @see ClientSession#getConnectionPolicy()
 */
public class ConnectionPolicy implements Cloneable, Serializable {
    /**
     * The login information used to create a JDBC connection or acquire one
     * from an external pool/data-source. Typically this is constant within a
     * single persistence unit but in some advanced usages users can customize
     * connections for each client session.
     */
    protected Login login;
    
    /**
     * Name of the pool to be used.
     * If neither pool name nor login provided then default pool will be used.
     * If no pool name is provided but there's a login then the login is used to
     * create connection which the ClientSession will use.
     */
    protected String poolName;
    
    /**
     * Determines if the write/exclusive connection is acquired only when first
     * requested (lazy, this is the default) or immediately when the client
     * session is acquired.
     * After write/exclusive connection is acquired
     * if isLazy is true and exclusiveMode is Transactional
     * then it's held until transaction is committed or rolled back,
     * otherwise until the client session is released.
     */
    protected boolean isLazy;

    /**
     * Default value Transactional causes creation of ClientSession,
     * the other two values - ExclusiveIsolatedClientSession.
     * ExclusiveMode values correspond to ExclusiveConnectionMode values,
     * the latter class has extensive comments explaining the differences between
     * the values.
     * @see ExclusiveConnectionMode
     */
    public enum ExclusiveMode {
        Transactional,
        Isolated,
        Always
    }

    /**
     * This attribute is used by the ServerSession to determine if a client
     * session with an exclusive connection should be built and how the exclusive
     * connection should be used.
     */
    protected ExclusiveMode exclusiveMode;

    /**
     * This attribute will provide a mechanism by which customers will be able
     * to pass connection information to events to enable further customization.
     */
    protected Map properties;

    /**
     * PUBLIC:
     * A connection policy is used to define how the client session connection should be acquired.
     */
    public ConnectionPolicy() {
        this.isLazy = true;
        this.exclusiveMode = ExclusiveMode.Transactional;
    }

    /**
     * PUBLIC:
     * A connection policy is used to define how the client session connection should be acquired.
     */
    public ConnectionPolicy(String poolName) {
        this.isLazy = true;
        this.poolName = poolName;
        this.exclusiveMode = ExclusiveMode.Transactional;
    }

    /**
     * PUBLIC:
     * A connection policy is used to define how the client session connection should be acquired.
     */
    public ConnectionPolicy(Login login) {
        this.isLazy = false;
        this.login = login;
        this.exclusiveMode = ExclusiveMode.Transactional;
    }

    /**
     * INTERNAL:
     * Clone the query
     */
    public Object clone() {
        try {
            ConnectionPolicy clone = (ConnectionPolicy)super.clone();
            if (clone.hasLogin()) {
                clone.setLogin(clone.getLogin().clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * PUBLIC:
     * A lazy connection only acquires a physical connection
     * when a transaction is started and releases the connection when the transaction completes.
     */
    public void dontUseLazyConnection() {
        setIsLazy(false);
    }

    /**
     * PUBLIC:
     * Return the login to use for this connection.
     * Client sessions support using a separate user login for database modification.
     */
    public Login getLogin() {
        return login;
    }

    /**
     * PUBLIC:
     * Return the pool name or null if not part of a pool.
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * ADVANCED:
     * This method will return the collection of custom properties set on the Connection
     * policy.  Note that this will cause the lazy initialization of the HashMap.
     */
    public Map getProperties() {
        if (this.properties == null) {
            this.properties = new HashMap();
        }
        return this.properties;
    }

    /**
     * PUBLIC:
     * Returns the property associated with the corresponding key.  These properties will be available to
     * connection events.
     */
    public Object getProperty(Object object) {
        if (this.hasProperties()) {
            return this.getProperties().get(object);
        }
        return null;
    }

    /**
     * PUBLIC:
     * Return if a login is used, only one of login and pool can be used.
     */
    public boolean hasLogin() {
        return login != null;
    }

    /**
     * PUBLIC:
     * Returns true if properties are available on the Connection Policy
     */
    public boolean hasProperties() {
        return (this.properties != null) && (!this.properties.isEmpty());
    }

    /**
     * PUBLIC:
     * Indicates whether exclusiveMode is Isolated.
     */
    public boolean isExclusiveIsolated() {
        return this.exclusiveMode == ExclusiveMode.Isolated;
    }

    /**
     * PUBLIC:
     * Indicates whether exclusiveMode is Always.
     */
    public boolean isExclusiveAlways() {
        return this.exclusiveMode == ExclusiveMode.Always;
    }

    /**
     * PUBLIC:
     * Indicates whether ExclusiveIsolatedClientSession should be created.
     */
    public boolean isExclusive() {
        return isExclusiveIsolated() || isExclusiveAlways();
    }

    /**
     * PUBLIC:
     * Return if a lazy connection should be used, a lazy connection only acquire a physical connection
     * when a transaction is started and releases the connection when the transaction completes.
     */
    public boolean isLazy() {
        return isLazy;
    }

    /**
     * INTERNAL:
     * Return if part of a connection pool.
     */
    public boolean isPooled() {
        return poolName != null;
    }

    /**
     * INTERNAL:
     * Return if part of a connection pool.
     */
    public boolean isUserDefinedConnection() {
        return poolName == null;
    }

    /**
     * PUBLIC:
     * This method is used to remove a custom property from the Connection Policy.
     * This method will return the propery removed.  If it was not found then null
     * will be returned.
     */
    public Object removeProperty(Object key) {
        if (this.hasProperties()) {
            return getProperties().remove(key);
        }
        return null;
    }

    /**
     * PUBLIC:
     * Set if a lazy connection should be used, a lazy connection only acquire a physical connection
     * when a transaction is started and releases the connection when the transaction completes.
     */
    public void setIsLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    /**
     * PUBLIC:
     * Set the login to use for this connection.
     * Client sessions support using a separate user login for database modification.
     * Pooled connections must use the pool's login and cannot define their own.
     */
    public void setLogin(Login login) {
        this.login = login;
    }

    /**
     * PUBLIC:
     * Set the pool name or null if not part of a pool.
     */
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    /**
     * PUBLIC:
     * Use this method to set custom properties on the Connection Policy.  These
     * properties will be available from within connection events but have no
     * effect on the connection directly.
     */
    public void setProperty(Object key, Object property) {
        getProperties().put(key, property);
    }

    /**
     * PUBLIC:
     * Returns exclusive mode.
     */
    public ExclusiveMode getExclusiveMode() {
        return exclusiveMode;
    }

    /**
     * PUBLIC:
     * Sets exclusive mode, if null is passed sets the default value.
     */
    public void setExclusiveMode(ExclusiveMode exclusiveMode) {
        if(exclusiveMode == null) {
            this.exclusiveMode = ExclusiveMode.Transactional;
        } else {
            this.exclusiveMode = exclusiveMode;
        }
    }

    /**
     * OBSOLETE:
     * If set to true the acquired client session should acquire an exclusive connection
     * for all database interaction.  Currently this is only supported with Isolated
     * data, but required for Oracle VPD support.
     * 
     * This method has been replaced with setExclusiveMode method:
     * true corresponds to ExclusiveMode.Isolated,
     * false - to ExclusiveMode.Transactional.
     * @deprecated
     */
    public void setShouldUseExclusiveConnection(boolean useExclusiveConnection) {
        if(useExclusiveConnection) {
            exclusiveMode = ExclusiveMode.Isolated;
        } else {
            exclusiveMode = ExclusiveMode.Transactional;
        }
    }

    /**
     * OBSOLETE:
     * Returns true if the acquired client session should acquire an exclusive connection
     * for all database interaction.  Currently this is only supported with Isolated
     * data, but required for Oracle VPD support.
     * 
     * This method has been replaced with isExclusiveIsolated method.
     * @deprecated
     */
    public boolean shouldUseExclusiveConnection() {
        return isExclusiveIsolated();
    }

    /**
     * INTERNAL:
     * return a string representation of this ConnectionPolicy
     */
    public String toString() {
        String type = "";
        if (isPooled()) {
            type = "(" + ToStringLocalization.buildMessage("pooled", (Object[])null) + ": " + getPoolName();
        } else {
            type = "(" + ToStringLocalization.buildMessage("login", (Object[])null) + ": " + getLogin();
        }
        if (isLazy()) {
            type = type + "," + ToStringLocalization.buildMessage("lazy", (Object[])null) + ")";
        } else {
            type = type + "," + ToStringLocalization.buildMessage("non-lazy", (Object[])null) + ")";
        }

        return Helper.getShortClassName(getClass()) + type;
    }

    /**
     * PUBLIC:
     * A lazy connection only acquires a physical connection
     * when a transaction is started and releases the connection when the transaction completes.
     */
    public void useLazyConnection() {
        setIsLazy(true);
    }
}
