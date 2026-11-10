/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.entityagent;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorContext;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.AgentValidationBootstrapRecord;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(EmfRunner.class)
public class TestEntityAgentValidationBootstrap {
    public interface FirstChecks { }
    public interface SecondChecks { }

    @Test
    public void existingUnitWithoutValidationDoesNotDisableSuppliedFactory() throws Exception {
        try (BootstrapUnit unit = new BootstrapUnit();
                EntityManagerFactory disabled = unit.factory(Map.of("jakarta.persistence.validation.mode", "NONE"));
                EntityAgent disabledAgent = disabled.createEntityAgent()) {
            disabledAgent.insert(new AgentValidationBootstrapRecord(1));
            RecordingFactory recorder = new RecordingFactory();
            try (EntityManagerFactory enabled = unit.factory(configuration(recorder, FirstChecks.class));
                    EntityAgent agent = enabled.createEntityAgent()) {
                AgentValidationBootstrapRecord record = new AgentValidationBootstrapRecord(2);
                agent.insert(record);
                assertEquals("The supplied factory must receive the configured insert callback",
                        List.of(new ValidationCall(record, List.of(FirstChecks.class))), recorder.calls);
                disabledAgent.insert(new AgentValidationBootstrapRecord(3));
                assertEquals(1, recorder.calls.size());
            }
        }
    }

    @Test
    public void sameUnitDifferentFactoriesRetainTheirOwnValidationConfiguration() throws Exception {
        try (BootstrapUnit unit = new BootstrapUnit()) {
            RecordingFactory first = new RecordingFactory();
            RecordingFactory second = new RecordingFactory();
            try (EntityManagerFactory firstFactory = unit.factory(configuration(first, FirstChecks.class));
                    EntityAgent firstAgent = firstFactory.createEntityAgent()) {
                AgentValidationBootstrapRecord one = new AgentValidationBootstrapRecord(1);
                firstAgent.insert(one);
                try (EntityManagerFactory secondFactory = unit.factory(configuration(second, SecondChecks.class));
                        EntityAgent secondAgent = secondFactory.createEntityAgent()) {
                    AgentValidationBootstrapRecord two = new AgentValidationBootstrapRecord(2);
                    AgentValidationBootstrapRecord three = new AgentValidationBootstrapRecord(3);
                    secondAgent.insert(two);
                    firstAgent.insert(three);
                    assertEquals(List.of(new ValidationCall(one, List.of(FirstChecks.class)),
                            new ValidationCall(three, List.of(FirstChecks.class))), first.calls);
                    assertEquals(List.of(new ValidationCall(two, List.of(SecondChecks.class))), second.calls);
                }
            }
        }
    }

    @Test
    public void sameFactoryWithDifferentGroupsRetainsEachUnitConfiguration() throws Exception {
        try (BootstrapUnit unit = new BootstrapUnit()) {
            RecordingFactory recorder = new RecordingFactory();
            try (EntityManagerFactory firstFactory = unit.factory(configuration(recorder, FirstChecks.class));
                    EntityAgent firstAgent = firstFactory.createEntityAgent()) {
                AgentValidationBootstrapRecord one = new AgentValidationBootstrapRecord(1);
                firstAgent.insert(one);
                try (EntityManagerFactory secondFactory = unit.factory(configuration(recorder, SecondChecks.class));
                        EntityAgent secondAgent = secondFactory.createEntityAgent()) {
                    AgentValidationBootstrapRecord two = new AgentValidationBootstrapRecord(2);
                    AgentValidationBootstrapRecord three = new AgentValidationBootstrapRecord(3);
                    secondAgent.insert(two);
                    firstAgent.insert(three);
                    assertEquals(List.of(new ValidationCall(one, List.of(FirstChecks.class)),
                            new ValidationCall(two, List.of(SecondChecks.class)),
                            new ValidationCall(three, List.of(FirstChecks.class))), recorder.calls);
                }
            }
        }
    }

    @Test
    public void absentInsertGroupsRemainEnabledWhenEmptyFactoryIsCreatedSecond() throws Exception {
        assertAbsentAndEmptyGroups("pre-insert", true);
    }

    @Test
    public void emptyInsertGroupsRemainDisabledWhenDefaultFactoryIsCreatedSecond() throws Exception {
        assertAbsentAndEmptyGroups("pre-insert", false);
    }

    @Test
    public void absentUpdateGroupsRemainEnabledWhenEmptyFactoryIsCreatedSecond() throws Exception {
        assertAbsentAndEmptyGroups("pre-update", true);
    }

    @Test
    public void emptyUpdateGroupsRemainDisabledWhenDefaultFactoryIsCreatedSecond() throws Exception {
        assertAbsentAndEmptyGroups("pre-update", false);
    }

    @Test
    public void absentUpsertGroupsRemainEnabledWhenEmptyFactoryIsCreatedSecond() throws Exception {
        assertAbsentAndEmptyGroups("pre-upsert", true);
    }

    @Test
    public void emptyUpsertGroupsRemainDisabledWhenDefaultFactoryIsCreatedSecond() throws Exception {
        assertAbsentAndEmptyGroups("pre-upsert", false);
    }

    @Test
    public void firstThenSecondMergeGroupsRemainIsolated() throws Exception {
        assertMergeGroups(FirstChecks.class, SecondChecks.class);
    }

    @Test
    public void secondThenFirstMergeGroupsRemainIsolated() throws Exception {
        assertMergeGroups(SecondChecks.class, FirstChecks.class);
    }

    private static void assertAbsentAndEmptyGroups(String event, boolean absentFirst) throws Exception {
        try (BootstrapUnit unit = new BootstrapUnit()) {
            RecordingFactory recorder = new RecordingFactory();
            Map<String, Object> firstConfiguration = disabledGroups(recorder);
            Map<String, Object> secondConfiguration = disabledGroups(recorder);
            (absentFirst ? firstConfiguration : secondConfiguration)
                    .remove("jakarta.persistence.validation.group." + event);
            if (event.equals("pre-update")) {
                unit.seed(1, 2, 3);
            } else if (event.equals("pre-upsert")) {
                // Exercise both the absent-row and existing-row upsert paths.
                unit.seed(2);
            }
            AgentValidationBootstrapRecord one = new AgentValidationBootstrapRecord(1);
            AgentValidationBootstrapRecord two = new AgentValidationBootstrapRecord(2);
            AgentValidationBootstrapRecord three = new AgentValidationBootstrapRecord(3);
            try (EntityManagerFactory first = unit.factory(firstConfiguration);
                    EntityAgent firstAgent = first.createEntityAgent()) {
                write(firstAgent, event, one);
                try (EntityManagerFactory second = unit.factory(secondConfiguration);
                        EntityAgent secondAgent = second.createEntityAgent()) {
                    write(secondAgent, event, two);
                    write(firstAgent, event, three);
                    List<ValidationCall> expected = absentFirst
                            ? List.of(new ValidationCall(one, List.of(Default.class)),
                                    new ValidationCall(three, List.of(Default.class)))
                            : List.of(new ValidationCall(two, List.of(Default.class)));
                    assertEquals("Absent groups use Default; explicit empty groups disable only their factory",
                            expected, recorder.calls);
                }
            }
        }
    }

    private static void write(EntityAgent agent, String event, AgentValidationBootstrapRecord record) {
        record.name = "changed-" + record.id;
        switch (event) {
            case "pre-insert" -> agent.insert(record);
            case "pre-update" -> agent.update(record);
            case "pre-upsert" -> agent.upsert(record);
            default -> throw new IllegalArgumentException(event);
        }
    }

    private static void assertMergeGroups(Class<?> firstGroup, Class<?> secondGroup) throws Exception {
        try (BootstrapUnit unit = new BootstrapUnit()) {
            RecordingFactory recorder = new RecordingFactory();
            Map<String, Object> firstConfiguration = disabledGroups(recorder);
            Map<String, Object> secondConfiguration = disabledGroups(recorder);
            firstConfiguration.put("jakarta.persistence.validation.group.pre-merge", firstGroup.getName());
            secondConfiguration.put("jakarta.persistence.validation.group.pre-merge", secondGroup.getName());
            AgentValidationBootstrapRecord one = new AgentValidationBootstrapRecord(1);
            AgentValidationBootstrapRecord two = new AgentValidationBootstrapRecord(2);
            AgentValidationBootstrapRecord three = new AgentValidationBootstrapRecord(3);
            try (EntityManagerFactory first = unit.factory(firstConfiguration)) {
                merge(first, one);
                try (EntityManagerFactory second = unit.factory(secondConfiguration)) {
                    merge(second, two);
                    merge(first, three);
                    assertEquals(List.of(new ValidationCall(one, List.of(firstGroup)),
                            new ValidationCall(two, List.of(secondGroup)),
                            new ValidationCall(three, List.of(firstGroup))), recorder.calls);
                }
            }
        }
    }

    private static void merge(EntityManagerFactory factory, AgentValidationBootstrapRecord record) {
        try (EntityManager manager = factory.createEntityManager()) {
            manager.getTransaction().begin();
            manager.merge(record);
            manager.getTransaction().commit();
        }
    }

    private static Map<String, Object> disabledGroups(RecordingFactory recorder) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.validation.mode", "CALLBACK");
        properties.put("jakarta.persistence.validation.factory", recorder.factory);
        for (String event : List.of("pre-persist", "pre-merge", "pre-remove", "pre-insert",
                "pre-update", "pre-delete", "pre-upsert")) {
            properties.put("jakarta.persistence.validation.group." + event, "");
        }
        return properties;
    }

    private static Map<String, Object> configuration(RecordingFactory recorder, Class<?> group) {
        return Map.of("jakarta.persistence.validation.mode", "CALLBACK",
                "jakarta.persistence.validation.factory", recorder.factory,
                "jakarta.persistence.validation.group.pre-insert", group.getName());
    }

    private record ValidationCall(Object entity, List<Class<?>> groups) { }

    /** A supplied factory records public validation calls without depending on a discoverable provider. */
    private static final class RecordingFactory {
        private final List<ValidationCall> calls = new ArrayList<>();
        private final ValidatorFactory factory;

        private RecordingFactory() {
            ClassLoader loader = ValidatorFactory.class.getClassLoader();
            Validator validator = (Validator) Proxy.newProxyInstance(loader, new Class<?>[]{Validator.class},
                    (proxy, method, arguments) -> {
                        if (method.getName().equals("validate")) {
                            calls.add(new ValidationCall(arguments[0], List.copyOf(Arrays.asList((Class<?>[]) arguments[1]))));
                            return Set.of();
                        }
                        throw new UnsupportedOperationException(method.getName());
                    });
            ValidatorContext context = (ValidatorContext) Proxy.newProxyInstance(loader,
                    new Class<?>[]{ValidatorContext.class}, (proxy, method, arguments) -> {
                        if (method.getName().equals("getValidator")) {
                            return validator;
                        }
                        if (method.getReturnType() == ValidatorContext.class) {
                            return proxy;
                        }
                        throw new UnsupportedOperationException(method.getName());
                    });
            factory = (ValidatorFactory) Proxy.newProxyInstance(loader, new Class<?>[]{ValidatorFactory.class},
                    (proxy, method, arguments) -> switch (method.getName()) {
                        case "getValidator" -> validator;
                        case "usingContext" -> context;
                        case "close" -> null;
                        case "equals" -> proxy == arguments[0];
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "toString" -> "RecordingValidatorFactory@" + System.identityHashCode(proxy);
                        default -> throw new UnsupportedOperationException(method.getName());
                    });
        }
    }

    /** Owns one isolated H2 database and one persistence-unit identity for all factory configurations. */
    private static final class BootstrapUnit implements AutoCloseable {
        private final SEPersistenceUnitInfo info;
        private final Connection keeper;

        private BootstrapUnit() throws Exception {
            String name = "validation-bootstrap-" + UUID.randomUUID();
            Class<?> type = Class.forName("org.h2.jdbcx.JdbcDataSource");
            Object source = type.getConstructor().newInstance();
            type.getMethod("setURL", String.class).invoke(source, "jdbc:h2:mem:" + name);
            type.getMethod("setUser", String.class).invoke(source, "sa");
            type.getMethod("setPassword", String.class).invoke(source, "");
            DataSource dataSource = (DataSource) source;
            keeper = dataSource.getConnection();
            try (java.sql.Statement statement = keeper.createStatement()) {
                statement.execute("CREATE TABLE EA_VALIDATION_BOOTSTRAP (ID INTEGER PRIMARY KEY, NAME VARCHAR(255))");
            }
            info = new SEPersistenceUnitInfo();
            info.setPersistenceUnitName(name);
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(AgentValidationBootstrapRecord.class.getClassLoader());
            info.setNewTempClassLoader(AgentValidationBootstrapRecord.class.getClassLoader());
            info.setPersistenceUnitRootUrl(AgentValidationBootstrapRecord.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(List.of(AgentValidationBootstrapRecord.class.getName()));
            info.setExcludeUnlistedClasses(true);
            info.setNonJtaDataSource(dataSource);
            Properties properties = new Properties();
            properties.setProperty("jakarta.persistence.jdbc.user", "sa");
            properties.setProperty("jakarta.persistence.jdbc.password", "");
            properties.setProperty("eclipselink.weaving", "false");
            properties.setProperty("eclipselink.logging.level", "OFF");
            properties.setProperty("eclipselink.cache.shared.default", "false");
            info.setProperties(properties);
        }

        private void seed(int... identifiers) throws Exception {
            try (java.sql.PreparedStatement statement = keeper.prepareStatement(
                    "INSERT INTO EA_VALIDATION_BOOTSTRAP (ID, NAME) VALUES (?, ?)")) {
                for (int identifier : identifiers) {
                    statement.setInt(1, identifier);
                    statement.setString(2, "record-" + identifier);
                    statement.executeUpdate();
                }
            }
        }

        private EntityManagerFactory factory(Map<String, ?> configuration) {
            Map<String, Object> properties = new HashMap<>(configuration);
            properties.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
            return Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), properties);
        }

        @Override
        public void close() throws Exception {
            keeper.close();
        }
    }
}
