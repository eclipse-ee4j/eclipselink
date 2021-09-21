/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

/**
 * JPA-RS REST services.
 */
module org.eclipse.persistence.jpars {

    requires jakarta.persistence;
    requires jakarta.xml.bind;
    requires transitive jakarta.annotation;
    requires transitive jakarta.ws.rs;

    requires org.eclipse.persistence.jpa;
    requires transitive org.eclipse.persistence.jpars.server;
    requires org.eclipse.persistence.asm;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.jpa.jpql;
    requires org.eclipse.persistence.moxy;

    exports org.eclipse.persistence.jpa.rs.service;

    uses org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;

    provides org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider with
            org.eclipse.persistence.jpa.rs.service.JPARSPersistenceContextFactoryProvider;
}
