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

package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;

/**
 * JUnit test case(s) for DDL generation.
 */
public class DDLGenerationTestBase extends JUnitTestCase {

    public DDLGenerationTestBase() {
        super();
    }

    public DDLGenerationTestBase(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "ddlGeneration";
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        // Trigger DDL generation
        EntityManager em = createEntityManager();
        closeEntityManager(em);
        clearCache();
    }
}
