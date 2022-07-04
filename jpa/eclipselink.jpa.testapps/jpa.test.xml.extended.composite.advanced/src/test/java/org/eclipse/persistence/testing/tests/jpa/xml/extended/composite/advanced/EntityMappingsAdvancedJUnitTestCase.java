/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/29/2011 - 2.3 Andrei Ilitchev
//       - Bug 328404 - JPA Persistence Unit Composition
//         Adapted org.eclipse.persistence.testing.tests.jpa.xml.advanced.EntityMappingsAdvancedJUnitTestCase
//         for composite persistence unit.
//         Try to keep one-to-one correspondence between the two in the future, too.
//         The tests that could not (or not yet) adapted for composite persistence unit
//         are commented out, the quick explanation why the test can't run is provided.
package org.eclipse.persistence.testing.tests.jpa.xml.extended.composite.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsAdvancedJUnitTestCase extends org.eclipse.persistence.testing.tests.jpa.xml.composite.advanced.EntityMappingsAdvancedJUnitTestCase {

    public EntityMappingsAdvancedJUnitTestCase() {
        super();
    }

    public EntityMappingsAdvancedJUnitTestCase(String name) {
        super(name);
    }

    public EntityMappingsAdvancedJUnitTestCase(String name, String persistenceUnit) {
        super(name, persistenceUnit);
    }

    public static Test suite() {
        return suite("xml-extended-composite-advanced");
    }

}
