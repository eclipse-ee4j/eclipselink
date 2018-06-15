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

import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.internal.jpa.metamodel.PluralAttributeImpl;

/**
 * A proxy class that allows EclipseLink to trigger the deployment of a persistence unit
 * as an PluralAttribute is accessed in the metamodel.
 * @author tware
 *
 * @param <X>
 * @param <C>
 * @param <V>
 */
public class PluralAttributeProxyImpl<X, C, V> extends AttributeProxyImpl<X, C> implements PluralAttribute<X, C, V>{

    @Override
    public javax.persistence.metamodel.Bindable.BindableType getBindableType() {
        return ((PluralAttributeImpl)getAttribute()).getBindableType();
    }

    @Override
    public Class<V> getBindableJavaType() {
        return ((PluralAttributeImpl)getAttribute()).getBindableJavaType();
    }

    @Override
    public javax.persistence.metamodel.PluralAttribute.CollectionType getCollectionType() {
        return ((PluralAttributeImpl)getAttribute()).getCollectionType();
    }

    @Override
    public Type<V> getElementType() {
        return ((PluralAttributeImpl)getAttribute()).getElementType();
    }

}
