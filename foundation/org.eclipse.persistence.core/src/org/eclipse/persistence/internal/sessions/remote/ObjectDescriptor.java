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
package org.eclipse.persistence.internal.sessions.remote;

import java.io.*;

/**
 * Stores object description. The object descriptor is constructed and serialized as part of transporter.
 */
public class ObjectDescriptor implements Serializable {

    /** A primary key */
    protected Object key;

    /** A write lock value */
    protected Object writeLockValue;

    /** The domain object */
    protected Object object;

    /** The read time for the object */
    protected long readTime = 0;

    /**
     * Return primary key values of an associated object
     */
    public Object getKey() {
        return key;
    }

    /**
     * Return the domain object
     */
    public Object getObject() {
        return object;
    }

    /**
     * Return the read time of the associated object
     */
    public long getReadTime() {
        return readTime;
    }

    /**
     * Return the write lock value of an associated object
     */
    public Object getWriteLockValue() {
        return writeLockValue;
    }

    /**
     * Set primary key values of an associated object
     */
    public void setKey(Object key) {
        this.key = key;
    }

    /**
     * Set an object
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * Set the read time
     */
    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    /**
     * Set write lock value of an associated object
     */
    public void setWriteLockValue(Object writeLockValue) {
        this.writeLockValue = writeLockValue;
    }
}
