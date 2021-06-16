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

module org.eclipse.persistence.jpa {
    requires java.rmi;

    requires org.eclipse.persistence.asm;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.jpa.jpql;

    requires jakarta.persistence;

    requires static jakarta.transaction;
    requires static jakarta.validation;
    requires static jakarta.xml.bind;

    exports org.eclipse.persistence.jpa;
    exports org.eclipse.persistence.jpa.config;
    exports org.eclipse.persistence.jpa.dynamic;
    exports org.eclipse.persistence.jpa.metadata;
    exports org.eclipse.persistence.tools.weaving.jpa;

    // modelgen
    exports org.eclipse.persistence.internal.jpa.deployment;
    exports org.eclipse.persistence.internal.jpa.metadata;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.classes;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.objects;
    exports org.eclipse.persistence.internal.jpa.metadata.xml;

    // dbws
    exports org.eclipse.persistence.internal.jpa;
    exports org.eclipse.persistence.internal.jpa.weaving;

    // dbws builder
    exports org.eclipse.persistence.internal.jpa.metadata.accessors;
    exports org.eclipse.persistence.internal.jpa.metadata.columns;
    exports org.eclipse.persistence.internal.jpa.metadata.converters;
    exports org.eclipse.persistence.internal.jpa.metadata.partitioning;
    exports org.eclipse.persistence.internal.jpa.metadata.queries;
    exports org.eclipse.persistence.internal.jpa.metadata.sequencing;
    exports org.eclipse.persistence.internal.jpa.metadata.structures;
    exports org.eclipse.persistence.internal.jpa.metadata.tables;

    provides jakarta.persistence.spi.PersistenceProvider with org.eclipse.persistence.jpa.PersistenceProvider;
}
