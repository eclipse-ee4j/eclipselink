/*
 * Copyright (c) 2021, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

module org.eclipse.persistence.jpa {
    requires java.rmi;
    requires transitive java.instrument;
    requires jdk.unsupported;

    requires transitive org.eclipse.persistence.core;
    requires org.eclipse.persistence.jpa.jpql;

    requires transitive jakarta.persistence;
    requires static jakarta.json;

    requires static jakarta.transaction;
    requires static jakarta.validation;
    requires static jakarta.xml.bind;
    requires static jakarta.cdi;

    exports org.eclipse.persistence.jpa;
    exports org.eclipse.persistence.jpa.config;
    exports org.eclipse.persistence.jpa.dynamic;
    exports org.eclipse.persistence.jpa.exceptions;
    exports org.eclipse.persistence.jpa.exceptions.i18n;
    exports org.eclipse.persistence.jpa.metadata;
    exports org.eclipse.persistence.tools.weaving.jpa;
    exports org.eclipse.persistence.tools.weaving.jpa.i18n;

    //exported through JPA PUBLIC API
    exports org.eclipse.persistence.internal.jpa;
    exports org.eclipse.persistence.internal.jpa.deployment; // exp metadata/metadata.objects
    exports org.eclipse.persistence.internal.jpa.metadata.xml;

    //exported through JPA INTERNAL API
    exports org.eclipse.persistence.internal.jpa.metadata;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.classes;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.objects;
    exports org.eclipse.persistence.internal.jpa.metadata.additionalcriteria;
    exports org.eclipse.persistence.internal.jpa.metadata.cache;
    exports org.eclipse.persistence.internal.jpa.metadata.changetracking;
    exports org.eclipse.persistence.internal.jpa.metadata.columns;
    exports org.eclipse.persistence.internal.jpa.metadata.converters;
    exports org.eclipse.persistence.internal.jpa.metadata.copypolicy;
    exports org.eclipse.persistence.internal.jpa.metadata.graphs;
    exports org.eclipse.persistence.internal.jpa.metadata.inheritance;
    exports org.eclipse.persistence.internal.jpa.metadata.listeners;
    exports org.eclipse.persistence.internal.jpa.metadata.locking;
    exports org.eclipse.persistence.internal.jpa.metadata.mappings;
    exports org.eclipse.persistence.internal.jpa.metadata.multitenant;
    exports org.eclipse.persistence.internal.jpa.metadata.nosql;
    exports org.eclipse.persistence.internal.jpa.metadata.partitioning;
    exports org.eclipse.persistence.internal.jpa.metadata.queries;
    exports org.eclipse.persistence.internal.jpa.metadata.sequencing;
    exports org.eclipse.persistence.internal.jpa.metadata.sop;
    exports org.eclipse.persistence.internal.jpa.metadata.structures;
    exports org.eclipse.persistence.internal.jpa.metadata.tables;
    exports org.eclipse.persistence.internal.jpa.metadata.transformers;

    opens org.eclipse.persistence.internal.jpa.deployment to org.eclipse.persistence.core;
    provides jakarta.persistence.spi.PersistenceProvider with org.eclipse.persistence.jpa.PersistenceProvider;

    uses org.eclipse.persistence.mappings.converters.spi.ConverterProvider;
}
