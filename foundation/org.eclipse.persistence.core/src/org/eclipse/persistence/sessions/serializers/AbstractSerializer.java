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
//      Oracle - initial impl
package org.eclipse.persistence.sessions.serializers;

import org.eclipse.persistence.sessions.Session;

/**
 * Abstract Serializer class.  All serializers should subclass this class to be backward compatible.
 * @author James Sutherland
 */
public abstract class AbstractSerializer implements Serializer {

    public Class getType() {
        return byte[].class;
    }

    public void initialize(Class serializeClass, String serializePackage, Session session) { }

    public String toString() {
        return getClass().getSimpleName();
    }
}
