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

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;

public class SingularAttributeProxyImpl<X, T> extends AttributeProxyImpl<X, T> implements SingularAttribute<X, T> {

    @Override
    public javax.persistence.metamodel.Bindable.BindableType getBindableType() {
        return ((SingularAttributeImpl<X, T>)getAttribute()).getBindableType();
    }

    @Override
    public Class<T> getBindableJavaType() {
        return ((SingularAttributeImpl<X, T>)getAttribute()).getBindableJavaType();
    }

    @Override
    public boolean isId() {
        return ((SingularAttributeImpl<X, T>)getAttribute()).isId();
    }

    @Override
    public boolean isVersion() {
        return ((SingularAttributeImpl<X, T>)getAttribute()).isVersion();
    }

    @Override
    public boolean isOptional() {
        return ((SingularAttributeImpl<X, T>)getAttribute()).isOptional();
    }

    @Override
    public Type<T> getType() {
        return ((SingularAttributeImpl<X, T>)getAttribute()).getType();
    }

}
