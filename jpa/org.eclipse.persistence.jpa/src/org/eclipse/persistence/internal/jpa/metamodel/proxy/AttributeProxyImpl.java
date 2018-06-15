/*
 * Copyright (c)  201, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial implementation
package org.eclipse.persistence.internal.jpa.metamodel.proxy;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Member;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;

/**
 * A proxy class that allows EclipseLink to trigger the deployment of a persistence unit
 * as an Attribute is accessed in the metamodel.
 * @author tware
 *
 * @param <X>
 * @param <T>
 */
public class AttributeProxyImpl<X, T> implements Attribute<X, T>, Serializable {

    protected Attribute<X, T> attribute = null;
    protected transient Set<WeakReference<EntityManagerFactoryImpl>> factories = new HashSet<WeakReference<EntityManagerFactoryImpl>>();

    public synchronized Attribute<X, T> getAttribute(){
        if (attribute == null){
            for (WeakReference<EntityManagerFactoryImpl> factoryRef: factories){
                EntityManagerFactoryImpl factory = factoryRef.get();
                if (factory != null){
                    factory.getDatabaseSession();
                } else {
                    factories.remove(factoryRef);
                }
            }
        }
        return attribute;
    }

    public synchronized Attribute<X, T> getAttributeInternal(){
        return attribute;
    }

    public synchronized void setAttribute(Attribute<X, T> attribute){
        this.attribute = attribute;
    }

    public synchronized void addFactory(EntityManagerFactoryImpl factory){
        for (WeakReference<EntityManagerFactoryImpl> factoryRef: factories){
            EntityManagerFactoryImpl fImpl = factoryRef.get();
            if (fImpl != null && fImpl.equals(factory)) {
                return;
            }
        }
        factories.add(new WeakReference<EntityManagerFactoryImpl>(factory));
    }

    @Override
    public String getName() {
        return getAttribute().getName();
    }

    @Override
    public javax.persistence.metamodel.Attribute.PersistentAttributeType getPersistentAttributeType() {
        return getAttribute().getPersistentAttributeType();
    }

    @Override
    public ManagedType<X> getDeclaringType() {
        return getAttribute().getDeclaringType();
    }

    @Override
    public Class<T> getJavaType() {
        return getAttribute().getJavaType();
    }

    @Override
    public Member getJavaMember() {
        return getAttribute().getJavaMember();
    }

    @Override
    public boolean isAssociation() {
        return getAttribute().isAssociation();
    }

    @Override
    public boolean isCollection() {
        return getAttribute().isCollection();
    }

}
