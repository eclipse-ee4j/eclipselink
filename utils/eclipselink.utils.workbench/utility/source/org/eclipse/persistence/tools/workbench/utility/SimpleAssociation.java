/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility;

import java.io.Serializable;

/**
 * Straightforward implementation of Association.
 */
public class SimpleAssociation
    extends AbstractAssociation
    implements Cloneable, Serializable
{
    private final Object key;
    private Object value;
    private static final long serialVersionUID = 1L;


    /**
     * Construct an association with the specified key
     * and a null value.
     */
    public SimpleAssociation(Object key) {
        super();
        this.key = key;
    }

    /**
     * Construct an association with the specified key and value.
     */
    public SimpleAssociation(Object key, Object value) {
        this(key);
        this.value = value;
    }


    /**
     * @see Association#getKey()
     */
    public Object getKey() {
        return this.key;
    }

    /**
     * @see Association#getValue()
     */
    public synchronized Object getValue() {
        return this.value;
    }

    /**
     * @see Association#setValue(Object)
     */
    public synchronized Object setValue(Object value) {
        Object old = this.value;
        this.value = value;
        return old;
    }

    /**
     * @see Object#clone()
     */
    public synchronized Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

}
