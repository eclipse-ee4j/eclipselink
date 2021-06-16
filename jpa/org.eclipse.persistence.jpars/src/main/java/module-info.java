/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

module org.eclipse.persistence.jpars {

    requires jakarta.persistence;
    requires jakarta.xml.bind;
    requires jakarta.activation;
    requires jakarta.annotation;
    requires jakarta.ws.rs;

    requires org.eclipse.persistence.dbws;
    requires org.eclipse.persistence.jpa;
    requires org.eclipse.persistence.asm;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.jpa.jpql;
    requires org.eclipse.persistence.moxy;

    exports org.eclipse.persistence.jpa.rs.service;

    provides org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider with
            org.eclipse.persistence.jpa.rs.service.JPARSPersistenceContextFactoryProvider;
}
