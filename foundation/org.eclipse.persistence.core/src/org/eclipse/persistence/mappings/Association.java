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
package org.eclipse.persistence.mappings;

import java.util.*;

/**
 * <p><b>Purpose</b>: Generic association object.
 * This can be used to map hashtable/map containers where the key and value primitives or independent objects.
 *
 * @author James Sutherland
 * @since TOPLink/Java 3.0
 */
public class Association implements Map.Entry {
    protected Object key;
    protected Object value;

    /**
     * Default constructor.
     */
    public Association() {
        super();
    }

    /**
     * PUBLIC:
     * Create an association.
     */
    public Association(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * PUBLIC:
     * Return the key.
     */
    public Object getKey() {
        return key;
    }

    /**
     * PUBLIC:
     * Return the value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * PUBLIC:
     * Set the key.
     */
    public void setKey(Object key) {
        this.key = key;
    }

    /**
     * PUBLIC:
     * Set the value.
     */
    public Object setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        return oldValue;
    }
}
