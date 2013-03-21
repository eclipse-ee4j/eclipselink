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
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/  
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