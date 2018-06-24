/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - initial implementation
package org.eclipse.persistence.internal.jpa.metamodel.proxy;

import java.util.Map;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.internal.jpa.metamodel.MapAttributeImpl;

/**
 * A proxy class that allows EclipseLink to trigger the deployment of a persistence unit
 * as an MapAttribute is accessed in the metamodel.
 * @author tware
 *
 * @param <X>
 * @param <K>
 * @param <V>
 */
public class MapAttributeProxyImpl<X, K, V> extends PluralAttributeProxyImpl<X, Map<K, V>, V> implements MapAttribute<X, K, V> {

    @Override
    public Class<K> getKeyJavaType() {
        return ((MapAttributeImpl)getAttribute()).getKeyJavaType();
    }

    @Override
    public Type<K> getKeyType() {
        return ((MapAttributeImpl)getAttribute()).getKeyType();
    }

}
