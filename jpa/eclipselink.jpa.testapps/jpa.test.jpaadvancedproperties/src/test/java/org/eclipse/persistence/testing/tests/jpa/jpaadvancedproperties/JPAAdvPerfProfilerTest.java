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

package org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.tools.profiler.PerformanceProfiler;

public class JPAAdvPerfProfilerTest extends JUnitTestCase {

    public JPAAdvPerfProfilerTest() {
    }

    public JPAAdvPerfProfilerTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "JPAADVProperties2";
    }

    public void testPerformanceProfilerTyperProperty(){
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        try {
            assertTrue("Profiler type set to PerformanceProfiler, it however has been detected as different type Profiler.",
                    session.getProfiler() instanceof PerformanceProfiler);
        } finally {
            closeEntityManager(em);
        }
    }
}