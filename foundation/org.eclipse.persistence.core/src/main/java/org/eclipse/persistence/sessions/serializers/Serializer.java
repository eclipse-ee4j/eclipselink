/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//      Oracle - initial impl
package org.eclipse.persistence.sessions.serializers;

import org.eclipse.persistence.sessions.Session;

import java.io.Serializable;

/**
 * Generic serializer interface.
 * Allows for a plugable serializer for Remote, Cache Coordination, Converters.
 * @author James Sutherland
 */
public interface Serializer extends Serializable, Cloneable {
    Object serialize(Object object, Session session);
    Object deserialize(Object bytes, Session session);
    Class<?> getType();
    void initialize(Class<?> serializeClass, String serializePackage, Session session);
}
