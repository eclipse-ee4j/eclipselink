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

package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultToOneFetchTypeTest {

    @ParameterizedTest(name = "{0}, PU={1}, mapping={2}")
    @MethodSource("standardMappings")
    void standardToOnePolicyAndIndirectionAgree(String kind, FetchType defaultFetch, String declaredFetch) {
        ObjectAccessor accessor = kind.equals("many-to-one") ? new ManyToOneAccessor() : new OneToOneAccessor();
        accessor.setProject(project(unit(defaultFetch), false));
        accessor.setFetch(declaredFetch);
        boolean lazy = "LAZY".equals(declaredFetch)
                || ((declaredFetch == null || "DEFAULT".equals(declaredFetch)) && defaultFetch == FetchType.LAZY);
        assertEquals(lazy, accessor.isLazy());
        assertEquals(lazy, accessor.usesIndirection());
        assertEquals(declaredFetch, accessor.getFetch(), "Resolving the policy must not normalize raw metadata");
    }

    static Stream<Arguments> standardMappings() {
        return Stream.of("many-to-one", "one-to-one").flatMap(kind ->
                Stream.of(FetchType.LAZY, FetchType.EAGER, null).flatMap(defaultFetch ->
                        Stream.of(null, "DEFAULT", "LAZY", "EAGER").map(fetch -> Arguments.of(kind, defaultFetch, fetch))));
    }

    @Test
    void metadataWithoutPersistenceUnitKeepsEagerFallback() {
        for (ObjectAccessor accessor : new ObjectAccessor[] {new ManyToOneAccessor(), new OneToOneAccessor()}) {
            accessor.setProject(project(null, false));
            assertFalse(accessor.isLazy());
            accessor.setFetch("DEFAULT");
            assertFalse(accessor.isLazy());
            assertFalse(accessor.usesIndirection());
        }
    }

    @Test
    void eagerWeavingEnablesIndirectionWithoutChangingFetchPolicy() {
        OneToOneAccessor accessor = new OneToOneAccessor();
        accessor.setProject(project(unit(FetchType.EAGER), true));
        assertFalse(accessor.isLazy());
        assertTrue(accessor.usesIndirection());
    }

    @Test
    void variableToOneRetainsItsOwnDefault() {
        VariableOneToOneAccessor accessor = new VariableOneToOneAccessor();
        accessor.setProject(project(unit(FetchType.LAZY), false));
        assertEquals("EAGER", accessor.getDefaultFetchType());
        assertFalse(accessor.isLazy());
        assertFalse(accessor.usesIndirection());
        accessor.setFetch("LAZY");
        assertTrue(accessor.isLazy());
    }

    @Test
    void collectionAndBasicDefaultsAreUnaffected() {
        for (FetchType policy : new FetchType[] {FetchType.LAZY, FetchType.EAGER}) {
            MetadataProject project = project(unit(policy), false);
            for (RelationshipAccessor accessor : new RelationshipAccessor[] {new OneToManyAccessor(), new ManyToManyAccessor()}) {
                accessor.setProject(project);
                assertTrue(accessor.isLazy());
                assertTrue(accessor.usesIndirection());
            }
            BasicAccessor basic = new BasicAccessor();
            basic.setProject(project);
            assertFalse(basic.usesIndirection());
            ElementCollectionAccessor elements = new ElementCollectionAccessor();
            elements.setProject(project);
            assertTrue(elements.usesIndirection());
        }
    }

    @Test
    void nonwovenClassesKeepMetadataPolicyAndFallBackToDirectReferences() {
        assertFalse(PersistenceWeaved.class
                .isAssignableFrom(UnwovenOwner.class));
        assertFalse(PersistenceWeaved.class
                .isAssignableFrom(UnwovenTarget.class));
        for (FetchType policy : new FetchType[] {null, FetchType.EAGER, FetchType.LAZY}) {
            SEPersistenceUnitInfo info = (SEPersistenceUnitInfo) unit(policy);
            String name = "nonwoven-to-one-default-" + policy;
            info.setPersistenceUnitName(name);
            info.setClassLoader(getClass().getClassLoader());
            info.setNewTempClassLoader(getClass().getClassLoader());
            info.setPersistenceUnitRootUrl(getClass().getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(List.of(UnwovenOwner.class.getName(), UnwovenTarget.class.getName()));
            info.setExcludeUnlistedClasses(true);
            info.setValidationMode("NONE");
            EntityManagerSetupImpl setup =
                    new EntityManagerSetupImpl(name, name);
            setup.setIsInContainerMode(true);
            try {
                setup.predeploy(info, Map.of(
                        PersistenceUnitProperties.WEAVING, "false",
                        PersistenceUnitProperties.LOGGING_LEVEL, "OFF"));
                ServerSession session = (ServerSession) setup.getSession();
                assertFalse(session.isConnected(), "Metadata processing must not connect to a database");
                session.getProject().convertClassNamesToClasses(getClass().getClassLoader());
                for (String attribute : List.of("many", "one")) {
                    ForeignReferenceMapping mapping = (ForeignReferenceMapping) session
                            .getDescriptor(UnwovenOwner.class).getMappingForAttributeName(attribute);
                    assertEquals(policy == FetchType.LAZY, mapping.isLazy(),
                            "Metadata retains the requested policy before the weaving capability check");
                    assertEquals(policy == FetchType.LAZY, mapping.usesIndirection());
                }
                // This is the existing initialization boundary that detects absent weaving.
                session.initializeDescriptors();
                for (String attribute : List.of("many", "one")) {
                    ForeignReferenceMapping mapping = (ForeignReferenceMapping) session
                            .getDescriptor(UnwovenOwner.class).getMappingForAttributeName(attribute);
                    assertFalse(mapping.usesIndirection(),
                            "A nonwoven to-one uses the existing direct-reference fallback");
                }
                assertFalse(session.isConnected(), "Descriptor initialization must not acquire JDBC resources");
            } finally {
                setup.undeploy();
            }
        }
    }

    @Entity
    public static class UnwovenOwner {
        @Id
        public int id;
        @ManyToOne
        public UnwovenTarget many;
        @OneToOne
        public UnwovenTarget one;
    }

    @Entity
    public static class UnwovenTarget {
        @Id
        public int id;
    }

    private static PersistenceUnitInfo unit(FetchType policy) {
        // Exercise the SPI contract independently of the SE configuration producer.
        return new SEPersistenceUnitInfo() {
            @Override
            public FetchType getDefaultToOneFetchType() {
                return policy;
            }
        };
    }

    private static MetadataProject project(PersistenceUnitInfo unit, boolean eagerWeaving) {
        return new MetadataProject(unit, new ServerSession(), true, eagerWeaving, false, false, false);
    }
}
