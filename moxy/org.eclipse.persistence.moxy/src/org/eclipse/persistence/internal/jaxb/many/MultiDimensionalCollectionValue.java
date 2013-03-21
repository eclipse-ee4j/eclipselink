/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

@XmlTransient
public abstract class MultiDimensionalCollectionValue<T extends ManyValue<?, Object>> extends MultiDimensionalManyValue<T> {

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public Object getItem() {
        Class<?> containerClass = containerClass();
        ContainerPolicy containerPolicy;
        if(Modifier.isAbstract(containerClass.getModifiers())) {
            containerPolicy = ContainerPolicy.buildPolicyFor(CoreClassConstants.ArrayList_class);
        } else {
            containerPolicy = ContainerPolicy.buildPolicyFor(containerClass());
        }
        Object container = containerPolicy.containerInstance();
        for(ManyValue<?, Object> containerValue : adaptedValue) {
            containerPolicy.addInto(containerValue.getItem(), container, null);
        }
        return container;
    }

    @Override
    public void setItem(Object item) {
        try {
            Collection<T> collection = (Collection<T>) item;
            adaptedValue = new ArrayList<T>(collection.size());
            for(Object stringArray : collection) {
                ManyValue<?, Object> stringArrayValue = (ManyValue<?, Object>) PrivilegedAccessHelper.newInstanceFromClass(componentClass());
                stringArrayValue.setItem(stringArray);
                adaptedValue.add((T) stringArrayValue);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}