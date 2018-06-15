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

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 *
 * This abstract class is used to support JAXBContext creation with a List class.
 * Classes which extend this class will be dynamically generated when a List class is given to JAXBContext
 */

@XmlTransient
public abstract class CollectionValue<T> extends ManyValue<T, Object> {

    public CollectionValue() {
        try {
            setItem(PrivilegedAccessHelper.newInstanceFromClass(containerClass()));
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public Object getItem() {
        return adaptedValue;
    }

    @Override
    public void setItem(Object collection) {
        adaptedValue = (Collection<T>) collection;
    }

}
