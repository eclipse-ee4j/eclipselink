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

package org.eclipse.persistence.internal.jpa.deployment;

import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SEPersistenceUnitInfoTest {

    @Test
    void omittedDefaultRemainsEager() {
        assertEquals(FetchType.EAGER, new SEPersistenceUnitInfo().getDefaultToOneFetchType());
        assertEquals(FetchType.EAGER, info(new PersistenceConfiguration("defaults")).getDefaultToOneFetchType());
    }

    @ParameterizedTest
    @EnumSource(value = FetchType.class, names = {"LAZY", "EAGER"})
    void configuredDefaultIsSnapshotted(FetchType fetchType) {
        PersistenceConfiguration configuration = new PersistenceConfiguration("defaults").defaultToOneFetchType(fetchType);
        SEPersistenceUnitInfo first = info(configuration);
        String originalHash = first.getConfigHash();
        FetchType opposite = fetchType == FetchType.LAZY ? FetchType.EAGER : FetchType.LAZY;
        configuration.defaultToOneFetchType(opposite);
        SEPersistenceUnitInfo second = info(configuration);
        assertEquals(fetchType, first.getDefaultToOneFetchType());
        assertEquals(opposite, second.getDefaultToOneFetchType());
        assertEquals(originalHash, first.getConfigHash());
        assertNotEquals(first.getConfigHash(), second.getConfigHash());
    }

    @Test
    void omittedAndExplicitEagerHaveSameIdentity() {
        assertEquals(info(new PersistenceConfiguration("defaults")).getConfigHash(),
                info(new PersistenceConfiguration("defaults").defaultToOneFetchType(FetchType.EAGER)).getConfigHash());
    }

    private static SEPersistenceUnitInfo info(PersistenceConfiguration configuration) {
        return new SEPersistenceUnitInfo(configuration, null);
    }
}
