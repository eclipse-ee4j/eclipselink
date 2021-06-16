/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation
package org.eclipse.persistence.internal.jpa.metamodel.proxy;

import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.Type;

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
    public jakarta.persistence.metamodel.Bindable.BindableType getBindableType() {
        return ((PluralAttributeImpl)getAttribute()).getBindableType();
    }

    @Override
    public Class<V> getBindableJavaType() {
        return ((PluralAttributeImpl)getAttribute()).getBindableJavaType();
    }

    @Override
    public jakarta.persistence.metamodel.PluralAttribute.CollectionType getCollectionType() {
        return ((PluralAttributeImpl)getAttribute()).getCollectionType();
    }

    @Override
    public Type<V> getElementType() {
        return ((PluralAttributeImpl)getAttribute()).getElementType();
    }

}
