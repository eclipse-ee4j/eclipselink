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
