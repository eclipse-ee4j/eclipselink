/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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

import junit.framework.Test;

import org.eclipse.persistence.testing.models.jpa.persistence32.Trainer;

/**
 * Verify jakarta.persistence 3.2 API - new @EnumeratedValue annotation used in Java enum.
 */
public class EnumTest extends AbstractPokemonSuite {

    public static Test suite() {
        return suite(
                "EnumTest",
                new EnumTest("testGetTrainer"),
                new EnumTest("testGetTrainerEnumOrdinalValue"),
                new EnumTest("testGetTrainerEnumStringValue")
        );
    }

    public EnumTest() {
    }

    public EnumTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    public void testGetTrainer() {
               Trainer trainer = emf.callInTransaction(em -> em
                .createQuery("SELECT e FROM Trainer e WHERE e.id=:id",
                             Trainer.class)
                .setParameter("id", TRAINERS[1].getId())
                .getSingleResult());
        assertEquals(TRAINERS[1], trainer);
    }

    public void testGetTrainerEnumOrdinalValue() {
        Number trainerStatusOrdinal = (Number) emf.callInTransaction(em -> em
                .createNativeQuery("SELECT STATUS_ORDINAL FROM PERSISTENCE32_TRAINER WHERE ID=?1")
                .setParameter(1, TRAINERS[1].getId())
                .getSingleResult());
        assertEquals(TRAINERS[1].getStatusOrdinal().getIntValue(), trainerStatusOrdinal.intValue());
    }

    public void testGetTrainerEnumStringValue() {
        String trainerStatusString = (String) emf.callInTransaction(em -> em
                .createNativeQuery("SELECT STATUS_STRING FROM PERSISTENCE32_TRAINER WHERE ID=?1")
                .setParameter(1, TRAINERS[1].getId())
                .getSingleResult());
        assertEquals(TRAINERS[1].getStatusString().getStringValue(), trainerStatusString);
    }
}
