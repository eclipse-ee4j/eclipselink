/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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
//     11/13/2026 - Jonathan S. Fisher
//       - 3008: Session for ordered list change tracking tests
package org.eclipse.persistence.testing.tests.junit.changetracking;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;

final class OrderedListTestSession {

    static final class Owner {
        private Integer id;

        Owner() {
        }

        Owner(Integer id) {
            this.id = id;
        }

        Integer getId() {
            return id;
        }
    }

    static final class Element {
        private Integer id;

        Element() {
        }

        Element(Integer id) {
            this.id = id;
        }

        Integer getId() {
            return id;
        }
    }

    private OrderedListTestSession() {
    }

    static DatabaseSessionImpl create() {
        final Project project = new Project(new DatabaseLogin(new MySQLPlatform()));
        project.addDescriptor(buildDescriptor(Owner.class, "OWNERS"));
        project.addDescriptor(buildDescriptor(Element.class, "ELEMENTS"));
        final DatabaseSessionImpl session = (DatabaseSessionImpl) project.createDatabaseSession();
        session.setLogLevel(SessionLog.OFF);
        session.initializeDescriptors();
        return session;
    }

    private static ClassDescriptor buildDescriptor(Class<?> javaClass, String tableName) {
        final RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(javaClass);
        descriptor.setTableName(tableName);
        descriptor.setPrimaryKeyFieldName(tableName + ".ID");
        descriptor.addDirectMapping("id", tableName + ".ID");
        return descriptor;
    }
}
