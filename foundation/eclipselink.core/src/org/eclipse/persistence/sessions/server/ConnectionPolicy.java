/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.server;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.internal.localization.*;

/**
 * <p>
 * <b>Purpose</b>: Used to specify how a client session's should be allocated.
 * @see ServerSession
 */
public class ConnectionPolicy implements Cloneable, Serializable {
    protected Login login;
    protected String poolName;
    protected boolean isLazy;

    // this attribute is used by the ServerSession to determine if a client session
    // with an exclusive connection should be built.
    protected boolean shouldUseExclusiveConnection;

    // this attribute will provide a mechanism by which customers will be able to pass connection
    // information to events
    protected Map properties;

    /**
     * PUBLIC:
     * A connection policy is used to define how the client session connection should be acquired.
     */
    public ConnectionPolicy() {
        this.isLazy = true;
    }

    /**
     * PUBLIC:
     * A connection policy is used to define how the client session connection should be acquired.
     */
    public ConnectionPolicy(String poolName) {
        this.isLazy = true;
        this.poolName = poolName;
    }

    /**
     * PUBLIC:
     * A connection policy is used to define how the client session connection should be acquired.
     */
    public ConnectionPolicy(Login login) {
        this.isLazy = false;
        this.login = login;
    }

    /**
     * INTERNAL:
     * Clone the query
     */
    public Object clone() {
        try {
            ConnectionPolicy clone = (ConnectionPolicy)super.clone();
            if (clone.hasLogin()) {
                clone.setLogin((Login)clone.getLogin().clone());
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
     * If set to true the acquired client session should acquire an exclusive connection
     * for all database interaction.  Currently this is only supported with Isolated
     * data, but required for Oracle VPD support.
     */
    public void setShouldUseExclusiveConnection(boolean useExclusiveConnection) {
        this.shouldUseExclusiveConnection = useExclusiveConnection;
    }

    /**
     * PUBLIC:
     * Returns true if the acquired client session should acquire an exclusive connection
     * for all database interaction.  Currently this is only supported with Isolated
     * data, but required for Oracle VPD support.
     */
    public boolean shouldUseExclusiveConnection() {
        return this.shouldUseExclusiveConnection;
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