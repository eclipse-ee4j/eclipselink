/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.mappings;


/**
 * <p><b>Purpose</b>: Generic association object.
 * This can be used to map hashtable/map containers where the key and value are non-typed primitives.
 *
 * @author James Sutherland
 * @since TOPLink/Java 3.0
 */
public class TypedAssociation extends Association {
    protected Class keyType;
    protected Class valueType;

    /**
     * Default constructor.
     */
    public TypedAssociation() {
        super();
    }

    /**
     * PUBLIC:
     * Create an association.
     */
    public TypedAssociation(Object key, Object value) {
        super(key, value);
        if (key != null) {
            this.keyType = key.getClass();
        }
        this.value = value;
        if (value != null) {
            this.valueType = value.getClass();
        }
    }

    /**
     * PUBLIC:
     * Return the class of the key.
     */
    public Class getKeyType() {
        return keyType;
    }

    /**
     * PUBLIC:
     * Return the class of the value.
     */
    public Class getValueType() {
        return valueType;
    }

    /**
     * INTERNAL:
     * Handler for the descriptor post build event.
     * Convert the key and values to their appropriate type.
     */
    public void postBuild(org.eclipse.persistence.descriptors.DescriptorEvent event) {
        setKey(event.getSession().getDatasourceLogin().getDatasourcePlatform().getConversionManager().convertObject(getKey(), getKeyType()));
        setValue(event.getSession().getDatasourceLogin().getDatasourcePlatform().getConversionManager().convertObject(getValue(), getValueType()));
    }

    /**
     * PUBLIC:
     * Set the class of the key.
     */
    public void setKeyType(Class keyType) {
        this.keyType = keyType;
    }

    /**
     * PUBLIC:
     * Set the class of the value.
     */
    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }
}
