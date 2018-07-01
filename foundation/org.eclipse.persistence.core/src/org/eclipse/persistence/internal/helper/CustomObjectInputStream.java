/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.helper;

import java.lang.Class;
import java.lang.ClassNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectInputStream;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * INTERNAL:
 * Defines a custom ObjectInputStream that is used with SerializedObjectConverter
 * to ensure the correct class loader is used.
 * BUG# 2813583
 *
 * @author Guy Pelletier
 * @version 1.0 March 25/03
 */
public class CustomObjectInputStream extends ObjectInputStream {
    ConversionManager m_conversionManager;

    public CustomObjectInputStream(InputStream stream, Session session) throws IOException {
        super(stream);
        m_conversionManager = session.getDatasourceLogin().getDatasourcePlatform().getConversionManager();
    }

    public Class resolveClass(ObjectStreamClass classDesc) throws ClassNotFoundException, IOException {
        return m_conversionManager.convertClassNameToClass(classDesc.getName());
    }
}
