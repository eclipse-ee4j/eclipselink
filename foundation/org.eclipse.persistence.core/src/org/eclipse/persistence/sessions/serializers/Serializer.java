/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Oracle - initial impl
 ******************************************************************************/
package org.eclipse.persistence.sessions.serializers;

import org.eclipse.persistence.sessions.Session;

/**
 * Generic serializer interface.
 * Allows for a plugable serializer for Remote, Cache Coordination, Converters.
 * @author James Sutherland
 */
public interface Serializer {
    byte[] serialize(Object object, Session session);
    Object deserialize(byte[] bytes, Session session);
}
