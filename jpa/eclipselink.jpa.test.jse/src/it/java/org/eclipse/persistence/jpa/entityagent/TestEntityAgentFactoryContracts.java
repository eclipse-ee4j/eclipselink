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
package org.eclipse.persistence.jpa.entityagent;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitTransactionType;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Owns an H2 database so factory disposal cannot affect another test's persistence unit. */
@RunWith(EmfRunner.class)
public class TestEntityAgentFactoryContracts {
    @Test
    public void closedFactoryRejectsBothCreationAndTypedHelperRoutes() throws Exception {
        try (FactoryUnit unit = new FactoryUnit()) {
            EntityManagerFactory factory = unit.factory;
            factory.close();
            AtomicBoolean invoked = new AtomicBoolean();
            assertThrows(IllegalStateException.class, () -> factory.createEntityAgent());
            assertThrows(IllegalStateException.class, () -> factory.createEntityAgent(Map.of()));
            assertThrows(IllegalStateException.class,
                    () -> factory.runInTransaction(EntityAgent.class, agent -> invoked.set(true)));
            assertThrows(IllegalStateException.class,
                    () -> factory.callInTransaction(EntityAgent.class, agent -> invoked.getAndSet(true)));
            assertFalse(invoked.get());
            assertTrue(unit.clients.acquired.isEmpty());
        }
    }

    @Test
    public void invalidFactoryInputsDoNotAcquireAClientOrInvokeWork() throws Exception {
        try (FactoryUnit unit = new FactoryUnit()) {
            AtomicBoolean invoked = new AtomicBoolean();
            EntityManagerFactory factory = unit.factory;
            assertThrows(IllegalArgumentException.class,
                    () -> factory.callInTransaction(EntityHandler.class, agent -> invoked.getAndSet(true)));
            assertThrows(IllegalArgumentException.class,
                    () -> factory.runInTransaction(EntityHandler.class, agent -> invoked.set(true)));
            assertThrows(IllegalArgumentException.class,
                    () -> factory.callInTransaction(null, agent -> invoked.getAndSet(true)));
            assertThrows(IllegalArgumentException.class,
                    () -> factory.callInTransaction(EntityAgent.class, null));
            assertThrows(IllegalArgumentException.class,
                    () -> factory.createEntityAgent(new EntityAgent.CreationOption() { }));
            assertFalse(invoked.get());
            assertTrue(unit.clients.acquired.isEmpty());
            // M7 marks work nonnull but does not prescribe its null-input exception type.
            assertThrows(RuntimeException.class,
                    () -> factory.runInTransaction(EntityAgent.class, null));
            assertEquals(unit.clients.acquired, unit.clients.released);
            try (EntityAgent peer = factory.createEntityAgent()) {
                peer.insert(new AgentTarget(1, "valid after invalid input"));
                assertEquals(1, unit.count(1));
            }
        }
    }

    @Test
    public void failedInitializationReleasesItsClientAndPreservesPeerCommit() throws Exception {
        try (FactoryUnit unit = new FactoryUnit(); EntityAgent peer = unit.factory.createEntityAgent()) {
            ClientSession peerSession = peer.unwrap(ClientSession.class);
            peer.getTransaction().begin();
            peer.insert(new AgentTarget(2, "peer pending"));
            assertEquals(0, unit.count(2));
            assertThrows(IllegalArgumentException.class, () -> unit.factory.createEntityAgent(
                    Map.of(QueryHints.CACHE_RETRIEVE_MODE, "not-a-cache-mode")));
            assertEquals(2, unit.clients.acquired.size());
            ClientSession failed = unit.clients.acquired.get(1);
            assertNotSame(peerSession, failed);
            assertEquals(List.of(failed), unit.clients.released);
            assertFalse(failed.isActive());
            assertTrue(failed.getWriteConnections().isEmpty());
            assertTrue(peerSession.isActive());
            assertTrue(peer.getTransaction().isActive());
            assertFalse(peer.getTransaction().getRollbackOnly());
            assertTrue(unit.server.isConnected());
            peer.getTransaction().commit();
            assertEquals(1, unit.count(2));
            try (EntityAgent next = unit.factory.createEntityAgent()) {
                next.insert(new AgentTarget(3, "fresh client"));
                assertEquals(1, unit.count(3));
            }
        }
    }

    private static final class Clients extends SessionEventAdapter {
        final List<ClientSession> acquired = new ArrayList<>();
        final List<ClientSession> released = new ArrayList<>();
        @Override public void postAcquireClientSession(SessionEvent event) {
            acquired.add((ClientSession) event.getSession());
        }
        @Override public void postReleaseClientSession(SessionEvent event) {
            released.add((ClientSession) event.getSession());
        }
    }

    private static final class FactoryUnit implements AutoCloseable {
        final Connection keeper;
        final EntityManagerFactory factory;
        final ServerSession server;
        final Clients clients = new Clients();

        FactoryUnit() throws Exception {
            String name = "agent-factory-contract-" + UUID.randomUUID();
            Class<?> type = Class.forName("org.h2.jdbcx.JdbcDataSource");
            Object source = type.getConstructor().newInstance();
            type.getMethod("setURL", String.class).invoke(source, "jdbc:h2:mem:" + name);
            type.getMethod("setUser", String.class).invoke(source, "sa");
            type.getMethod("setPassword", String.class).invoke(source, "");
            DataSource dataSource = (DataSource) source;
            keeper = dataSource.getConnection();
            try (java.sql.Statement statement = keeper.createStatement()) {
                statement.execute("CREATE TABLE EA_TARGET (ID BIGINT PRIMARY KEY, LABEL VARCHAR(255))");
            }
            SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
            info.setPersistenceUnitName(name);
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(AgentTarget.class.getClassLoader());
            info.setNewTempClassLoader(AgentTarget.class.getClassLoader());
            info.setPersistenceUnitRootUrl(AgentTarget.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(List.of(AgentTarget.class.getName()));
            info.setExcludeUnlistedClasses(true);
            info.setNonJtaDataSource(dataSource);
            Properties properties = new Properties();
            properties.setProperty("jakarta.persistence.jdbc.user", "sa");
            properties.setProperty("jakarta.persistence.jdbc.password", "");
            properties.setProperty("jakarta.persistence.validation.mode", "NONE");
            properties.setProperty("eclipselink.weaving", "false");
            properties.setProperty("eclipselink.logging.level", "OFF");
            properties.setProperty("eclipselink.cache.shared.default", "false");
            info.setProperties(properties);
            factory = Persistence.createEntityManagerFactory(name,
                    Map.of(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info));
            server = factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
            server.getEventManager().addListener(clients);
        }

        int count(long id) throws Exception {
            try (java.sql.PreparedStatement query = keeper.prepareStatement("SELECT COUNT(*) FROM EA_TARGET WHERE ID = ?")) {
                query.setLong(1, id);
                try (java.sql.ResultSet rows = query.executeQuery()) {
                    assertTrue(rows.next());
                    return rows.getInt(1);
                }
            }
        }

        @Override public void close() throws Exception {
            try {
                if (factory.isOpen()) {
                    factory.close();
                }
            } finally {
                keeper.close();
            }
        }
    }
}
