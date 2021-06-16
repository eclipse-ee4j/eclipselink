/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      Oracle - initial impl
package org.eclipse.persistence.sessions.serializers;

import java.io.Serializable;

import org.eclipse.persistence.sessions.Session;

/**
 * Generic serializer interface.
 * Allows for a plugable serializer for Remote, Cache Coordination, Converters.
 * @author James Sutherland
 */
public interface Serializer extends Serializable, Cloneable {
    Object serialize(Object object, Session session);
    Object deserialize(Object bytes, Session session);
    Class getType();
    void initialize(Class serializeClass, String serializePackage, Session session);
}
