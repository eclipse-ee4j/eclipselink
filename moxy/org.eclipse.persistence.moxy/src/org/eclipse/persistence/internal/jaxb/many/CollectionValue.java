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