/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import jakarta.persistence.Persistence;
import junit.framework.Test;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

/**
 * Verify jakarta.persistence 3.2 changes in {@code jakarta.persistence.schema-generation.database.action} property.
 */
public class DatabaseActionVerifyTest extends AbstractSuite {

    public static Test suite() {
        return suite(
                "ScriptsActionVerify",
                new DatabaseActionVerifyTest("testVerify")
        );
    }

    public DatabaseActionVerifyTest() {
        super();
    }

    public DatabaseActionVerifyTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "persistence32";
    }

    // Create EntityManagerFactory with "jakarta.persistence.schema-generation.database.action" set to "verify"
    // Schema exists so validation shall pass.
    public void testVerify() {
        String persistenceUnitName = "persistence32_schemagen_verify";
        Persistence.createEntityManagerFactory(
                        persistenceUnitName,
                        JUnitTestCaseHelper.getDatabaseProperties(persistenceUnitName))
                .unwrap(EntityManagerFactoryImpl.class);
    }

}
