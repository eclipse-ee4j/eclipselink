/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.namedgraphs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.namedgraphs.model.BasicGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.CyclicGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.DuplicateGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.MissingGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.UnrelatedGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.WrongTypeGraphRoot;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Invalid graph metadata must fail before publication, without poisoning the next factory. */
public class TestDistributedNamedEntityGraphValidation {
    @Test(timeout = 30000)
    public void missingSubgraphNamesAreRejected() {
        assertInvalid(MissingGraphRoot.class, "invalid-missing-orm.xml", "DngMissing");
    }

    @Test(timeout = 30000)
    public void wrongAssociationTargetTypesAreRejected() {
        assertInvalid(WrongTypeGraphRoot.class, "invalid-wrong-type-orm.xml", "DngWrongType");
    }

    @Test(timeout = 30000)
    public void basicAttributesCannotDeclareDistributedSubgraphs() {
        assertInvalid(BasicGraphRoot.class, "invalid-basic-orm.xml", "DngBasic");
    }

    @Test(timeout = 30000)
    public void cyclesTerminateWithoutPublishingPartialGraphs() {
        assertInvalid(CyclicGraphRoot.class, "invalid-cyclic-orm.xml", "DngCycle");
    }

    @Test(timeout = 30000)
    public void sameOwnerDuplicatesAreRejected() {
        assertInvalid(DuplicateGraphRoot.class, "invalid-duplicate-orm.xml", "DngDuplicate");
    }

    @Test(timeout = 30000)
    public void unrelatedDuplicateRootsAreRejected() {
        assertInvalid(UnrelatedGraphRoot.class, "invalid-unrelated-orm.xml", "DngChild");
    }

    private static void assertInvalid(Class<?> invalid, String mapping, String graph) {
        // A second failure followed by success catches stale registry state as well as one-off validation.
        for (int attempt = 0; attempt < 2; attempt++) {
            RuntimeException failure = assertThrows(RuntimeException.class, () -> {
                try (EntityManagerFactory factory = TestDistributedNamedEntityGraphs.configuration("invalid")
                        .managedClass(invalid).mappingFile("META-INF/distributed-named-graphs/" + mapping).createEntityManagerFactory()) {
                    factory.getMetamodel();
                }
            });
            StringBuilder diagnostic = new StringBuilder();
            for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                diagnostic.append(cause.getMessage()).append('\n');
            }
            assertTrue("Diagnostic must identify graph " + graph + ": " + diagnostic, diagnostic.toString().contains(graph));
            assertTrue("Diagnostic must identify owner " + invalid.getSimpleName() + ": " + diagnostic,
                    diagnostic.toString().contains(invalid.getSimpleName()));
            try (EntityManagerFactory factory = TestDistributedNamedEntityGraphs.configuration("after-invalid").createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                assertEquals("DngRoot", manager.getEntityGraph("DngRoot").getName());
                assertEquals("DngShared", manager.getEntityGraph("DngShared").getName());
            }
        }
    }
}
