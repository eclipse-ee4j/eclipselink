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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.internal.jaxb.many;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * This abstract class is used to support JAXBContext creation with an array or Collection class.
 *
 * Subclasses are CollectionValue, ObjectArrayValue and PrimitiveArrayValue
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public abstract class ManyValue<T, ITEM> {

    protected Collection<T> adaptedValue;

    public abstract boolean isArray();

    public abstract ITEM getItem();

    public abstract void setItem(ITEM item);

    public Collection<T> getAdaptedValue() {
        return adaptedValue;
    }

    public void setAdaptedValue(Collection<T> adaptedValue) {
        this.adaptedValue = adaptedValue;
    }

    public abstract Class<?> containerClass();

}
