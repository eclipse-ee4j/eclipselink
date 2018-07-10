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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

/**
 * Test named/predefined queries.
 */
public class PredefinedQueryTest extends ReadObjectTest {
    public Vector arguments;

    public PredefinedQueryTest() {
        super();
    }

    public PredefinedQueryTest(ReadObjectQuery query, Object originalObject, Vector arguments) {
        super(originalObject);
        setQuery(query);
        this.arguments = arguments;
    }

    protected void setup() {
    ClassDescriptor descriptor;
    if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
        descriptor = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(getQuery().getReferenceClass());
    } else {
        descriptor = getSession().getDescriptor(getQuery().getReferenceClass());
    }
        descriptor.getQueryManager().removeQuery(getQuery().getName());
        descriptor.getQueryManager().addQuery(getQuery().getName(), getQuery());
    }

    protected void test() {
        try {
            this.objectFromDatabase = getSession().executeQuery(getQuery().getName(), getQuery().getReferenceClass(), this.arguments);
        } catch (org.eclipse.persistence.exceptions.DatabaseException exception) {
            throw new TestWarningException("Function not supported on this database.");
        }
    }
}
