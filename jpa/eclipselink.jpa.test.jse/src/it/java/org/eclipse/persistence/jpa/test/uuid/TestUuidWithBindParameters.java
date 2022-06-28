/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.test.uuid;

import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDUUIDEntity;
import org.junit.runner.RunWith;

// Run TestUuidCommon tests with eclipselink.jdbc.bind-parameters property set to true
@RunWith(EmfRunner.class)
public class TestUuidWithBindParameters extends TestUuidCommon {

    @Emf(
            name = "TestUuidWithBindParameters",
            createTables = DDLGen.DROP_CREATE,
            classes = { UUIDUUIDEntity.class },
            properties = {
            // Uncomment following line to log SQL statements
            //        @Property(name = "eclipselink.logging.level.sql", value = "FINE"),
                    @Property(name = "eclipselink.jdbc.bind-parameters", value = "true")
            }
    )
    private EntityManagerFactory emf;

    EntityManagerFactory getEmf() {
        return emf;
    }

}
