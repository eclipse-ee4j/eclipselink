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

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;

import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;

public class SingularAttributeProxyImpl<X, T> extends AttributeProxyImpl<X, T> implements SingularAttribute<X, T> {

    @Override
    public jakarta.persistence.metamodel.Bindable.BindableType getBindableType() {
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
