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

package org.eclipse.persistence.testing.tests.jpa.puwithspaces;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;

public class PUWithSpacesTest extends JUnitTestCase {

    /**
     * See bug# 210280: verify that the URL encoding for spaces and multibyte chars is handled properly in the EMSetup map lookup
     * UC1 - EM has no spaces or multi-byte chars in name or path
     * UC2 - EM has spaces hex(20) in EM name but not in path
     * UC3/4 are fixed by 210280 - the other UC tests are for regression
     * UC3 - EM has spaces in path but not in the EM name
     * UC4 - EM has spaces in path and EM name
     * UC5 - EM has multi-byte hex(C3A1) chars in EM name but not in path
     * Keep resource with spaces and multibyte chars separate
     * UC6 - EM has multi-byte chars in path but not EM name
     * UC7 - EM has multi-byte chars in path and EM name
     * UC8 - EM has spaces and multi-byte chars in EM name but not in path
     * UC9 - EM has spaces and multi-byte chars in path and EM name
     */
    // UC2 - EM has spaces in EM name but not in path
    public void test210280EntityManagerFromPUwithSpaceInNameButNotInPath() {
        // This EM is defined in a persistence.xml that is off eclipselink-advanced-properties (no URL encoded chars in path)
        privateTest210280EntityManagerWithPossibleSpacesInPathOrName(//
                "A JPAADVProperties pu with spaces in the name", //
                "with a name containing spaces was not found.");
    }

    // UC3 - EM has spaces in path but not in the EM name
    public void test210280EntityManagerFromPUwithSpaceInPathButNotInName() {
        // This EM is defined in a persistence.xml that is off [eclipselink-pu with spaces] (with URL encoded chars in path)
        privateTest210280EntityManagerWithPossibleSpacesInPathOrName(//
                "eclipselink-pu-with-spaces-in-the-path-but-not-the-name", //
                "with a path containing spaces was not found.");
    }

    // UC4 - EM has spaces in the path and name
    public void test210280EntityManagerFromPUwithSpaceInNameAndPath() {
        // This EM is defined in a persistence.xml that is off [eclipselink-pu with spaces] (with URL encoded chars in path)
        privateTest210280EntityManagerWithPossibleSpacesInPathOrName(//
                "eclipselink-pu with spaces in the path and name", //
                "with a path and name both containing spaces was not found.");
    }

    private void privateTest210280EntityManagerWithPossibleSpacesInPathOrName(String puName, String failureMessagePostScript) {
        EntityManager em = null;
        try {
            em = createEntityManager(puName);
        } catch (Exception exception) {
            throw new RuntimeException("A Persistence Unit [" + puName + "] " + failureMessagePostScript, exception);
        } finally {
            if (null != em) {
                closeEntityManager(em);
            }
        }
    }
}
